package com.benkkstudios.downloader.database

import com.benkkstudios.database.BeeModel
import com.benkkstudios.downloader.DownloadStatus


data class DownloadItem(
    override var id: String,
    var url: String,
    var thumbnail: String? = null,
    var directory: String = "",
    var filename: String = "",
    var progress: Int = 0,
    var total: Long = 0,
    var downloaded: Long = 0,
    var lastModifiedAt: Long = 0,
    var scanToGallery: Boolean = false,
    var status: String = DownloadStatus.QUEUED,
) : BeeModel