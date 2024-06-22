package com.benkkstudios.downloader.internal

import com.benkkstudios.downloader.DownloadStatus
import com.benkkstudios.downloader.database.DownloadItem

sealed class DownloadState(
    val item: DownloadItem = DownloadItem("", ""),
    val progress: Int = 0,
    val error: String = "Unknown Error",
    val status: String = DownloadStatus.UNKNOWN
) {
    class Complete(item: DownloadItem) : DownloadState(item, status = DownloadStatus.COMPLETED, progress = 100)
    class Progress(item: DownloadItem, progress: Int) : DownloadState(item, progress = progress, status = DownloadStatus.PROGRESS)
    class Failed(item: DownloadItem, error: String = "Unknown Error") : DownloadState(
        item, error = error, status = DownloadStatus.FAILED,
        progress = 0
    )

    data object Paused : DownloadState(status = DownloadStatus.PAUSED)
    data object Canceled : DownloadState(status = DownloadStatus.CANCELLED, progress = 0)
    data object Removed : DownloadState()
    data object Queued : DownloadState()
}

