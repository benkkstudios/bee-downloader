package com.benkkstudios.downloader

import android.content.Context
import androidx.work.WorkManager
import com.benkkstudios.database.ExistMode
import com.benkkstudios.downloader.database.DownloadDatabase
import com.benkkstudios.downloader.database.DownloadItem
import com.benkkstudios.downloader.internal.DownloadObserver
import com.benkkstudios.downloader.internal.DownloadState
import com.benkkstudios.downloader.internal.DownloadWorker
import com.benkkstudios.downloader.utils.getUniqueId
import kotlinx.coroutines.flow.flow
import java.io.File

object BeeDownloader {
    private lateinit var workManager: WorkManager
    private lateinit var database: DownloadDatabase
    private val downloadObserver: DownloadObserver = DownloadObserver()
    internal var config: DownloadConfig = DownloadConfig()

    fun create(context: Context, downloadConfig: DownloadConfig = DownloadConfig()) {
        database = DownloadDatabase.getInstance(context)
        workManager = WorkManager.getInstance(context)
        this.config = downloadConfig
    }

    fun enqueue(url: String, directory: String, filename: String, thumbnail: String? = null) = runCatching {
        database.insert(createDownloadItem(url, directory, filename, thumbnail), ExistMode.NOTHING)
    }.onFailure {
        it.printStackTrace()
    }

    fun state() = downloadObserver.downloadState

    fun getAllDownload() = flow { emit(database.get()) }

    fun cancel() = DownloadWorker.cancel()

    fun delete(item: DownloadItem, includeFile: Boolean = true) {
        database.delete(item)
        if (includeFile) File(item.directory, item.filename).delete()
        downloadObserver.setState(DownloadState.Removed)
    }

    fun retry(item: DownloadItem) = database.update(item.copy(status = DownloadStatus.QUEUED))

    fun start(context: Context) {
        DownloadWorker.addObserver(context, downloadObserver.get())
        DownloadWorker.start(context, workManager)
    }

    suspend fun observe(context: Context, callback: (DownloadState) -> Unit) = downloadObserver.observe(context, callback)

    fun removeObserver(context: Context) = downloadObserver.remove(context)

    fun clear() {
        database.clear()
        workManager.cancelAllWork()
    }

    private fun createDownloadItem(url: String, directory: String, filename: String, thumbnail: String? = null) = DownloadItem(
        id = getUniqueId(url = url, directory = directory, filename = filename),
        thumbnail = thumbnail,
        directory = directory,
        filename = filename,
        url = url,
        lastModifiedAt = System.currentTimeMillis()
    )
}