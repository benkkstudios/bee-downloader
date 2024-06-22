package com.benkkstudios.downloader.database

import android.content.Context
import com.benkkstudios.database.BeeTable
import com.benkkstudios.downloader.DownloadStatus

internal class DownloadDatabase(context: Context) : BeeTable<DownloadItem>(context) {
    companion object {
        private var repo: DownloadDatabase? = null
        fun getInstance(context: Context): DownloadDatabase {
            if (repo == null) repo = DownloadDatabase(context)
            return repo!!
        }
    }

    fun getPendingDownloads() = filter { it.status == DownloadStatus.QUEUED }

    fun getPendingDownload() = first { it.status == DownloadStatus.QUEUED }
}