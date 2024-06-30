package com.benkkstudios.downloader.internal

import android.content.Context
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import com.benkkstudios.downloader.DownloadStatus
import com.benkkstudios.downloader.database.DownloadItem
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest

internal class DownloadObserver {
    private val _downloadState: MutableStateFlow<DownloadState> = MutableStateFlow(DownloadState.Queued)
    val downloadState get() = _downloadState.asStateFlow()

    fun setState(state: DownloadState) = apply { _downloadState.tryEmit(state) }
    private val _observer = Observer { workers: List<WorkInfo?>? ->
        if (!workers.isNullOrEmpty()) {
            onWorkInfoChanged(workers.first())
        }
    }

    fun reset() {
        _downloadState.tryEmit(DownloadState.Unknown)
    }

    fun get() = _observer

    suspend fun observe(context: Context, callback: (DownloadState) -> Unit) {
        DownloadWorker.addObserver(context, _observer)
        downloadState.collectLatest {
            callback.invoke(it)
        }
    }

    fun remove(context: Context) {
        DownloadWorker.removeObserver(context)
    }

    private fun onWorkInfoChanged(workInfo: WorkInfo?) {
        workInfo?.let {
            when (it.state) {
                WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING -> {
                    kotlin.runCatching {
                        val status = it.progress.getString(DownloadWorker.KEY_STATUS)
                        it.progress.getString(DownloadWorker.KEY_ITEM)?.let { json ->
                            val item = Gson().fromJson(json, DownloadItem::class.java)
                            when (status) {
                                DownloadStatus.COMPLETED -> _downloadState.tryEmit(DownloadState.Complete(item))
                                DownloadStatus.PROGRESS -> {
                                    val progress = it.progress.getInt(DownloadWorker.KEY_PROGRESS, -1)
                                    _downloadState.tryEmit(DownloadState.Progress(item, progress))
                                }

                                DownloadStatus.FAILED -> {
                                    val error = it.progress.getString(DownloadWorker.KET_ERROR)
                                    _downloadState.tryEmit(DownloadState.Failed(item, error ?: "Unknown Error"))
                                }

                                DownloadStatus.CANCELLED -> _downloadState.tryEmit(DownloadState.Canceled)
                                DownloadStatus.PAUSED -> _downloadState.tryEmit(DownloadState.Paused)
                                else -> {}
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }
}