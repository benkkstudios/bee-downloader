package com.benkkstudios.downloader.internal

import android.content.Context
import android.media.MediaScannerConnection
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.benkkstudios.downloader.DownloadStatus
import com.benkkstudios.downloader.database.DownloadDatabase
import com.benkkstudios.downloader.database.DownloadItem
import com.benkkstudios.downloader.notification.NotificationManager
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

internal class DownloadWorker(
    private val context: Context, workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), DownloadTask.Listener {
    private val database = DownloadDatabase.getInstance(context)
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    private val notificationManager: NotificationManager = NotificationManager(context)

    companion object {
        const val KEY_ITEM = "KEY_ITEM"
        const val KET_ERROR = "KET_ERROR"
        const val KEY_STATUS = "KEY_STATUS"
        const val KEY_PROGRESS = "KEY_PROGRESS"
        private const val WORKER_NAME = "BENKKSTUDIOS_DOWNLOADER"
        private lateinit var item: DownloadItem
        private lateinit var workManager: WorkManager
        private var internalObserver: Observer<List<WorkInfo?>?>? = null

        fun start(context: Context, workManager: WorkManager) {
            this.workManager = workManager
            removeObserver(context)
            val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            val downloadRequest = OneTimeWorkRequest.Builder(DownloadWorker::class.java).setConstraints(constraints).build()
            workManager.enqueueUniqueWork(
                WORKER_NAME, ExistingWorkPolicy.REPLACE, downloadRequest
            )
            addObserver(context, internalObserver)
        }

        fun addObserver(context: Context, observer: Observer<List<WorkInfo?>?>? = null) {
            internalObserver = observer
            internalObserver?.let {
                WorkManager.getInstance(context).getWorkInfosForUniqueWorkLiveData(WORKER_NAME).observeForever(it)
            }
        }

        fun removeObserver(context: Context) {
            internalObserver?.let {
                WorkManager.getInstance(context).getWorkInfosForUniqueWorkLiveData(WORKER_NAME).removeObserver(it)
            }
        }

        fun cancel() {
            DownloadTask.cancel()
        }
    }


    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        runCatching {
            performWork()
        }.onSuccess {
            return@withContext Result.success()
        }.onFailure {
            return@withContext Result.failure()
        }
        return@withContext Result.failure()
    }

    private suspend fun performWork() {
        while (database.getPendingDownloads().isNotEmpty()) {
            item = database.getPendingDownload()
            DownloadTask.download(item, this)
            delay(1000)
        }
        onAllComplete()
    }

    override fun onProgress(progress: Int) {
        notificationManager.onProgress(item, progress)
        updateProgress(DownloadStatus.PROGRESS, progress)
    }

    override fun onComplete() {
        notificationManager.onComplete(item)
        updateProgress(DownloadStatus.COMPLETED, 100)
        if (item.scanToGallery) scanFile(item)
    }

    override fun onError(error: String) {
        notificationManager.onError(item)
        updateProgress(DownloadStatus.FAILED, progress = 0, error = error)
    }

    override fun onCancel() {
        notificationManager.onCancel(item)
        updateProgress(DownloadStatus.CANCELLED)
    }

    private fun onAllComplete() {
        notificationManager.onAllComplete()
    }

    override fun onStarted() {
        notificationManager.onProgress(item)
        updateProgress(DownloadStatus.PROGRESS)
    }

    private fun updateProgress(
        downloadStatus: String,
        progress: Int = -1,
        error: String = "Unknown Error"
    ) {
        val data = Data.Builder().apply {
            putInt(KEY_PROGRESS, progress)
            putString(KEY_STATUS, downloadStatus)
            putString(KET_ERROR, error)
            putString(KEY_ITEM, Gson().toJson(item))
        }
        val saveItem = item.copy(status = downloadStatus, progress = progress)
        database.update(saveItem)
        scope.launch { setProgress(data.build()) }
    }

    private fun scanFile(item: DownloadItem) {
        MediaScannerConnection.scanFile(
            context, arrayOf(File(item.directory, item.filename).absolutePath),
            null
        ) { _, _ -> }
    }
}