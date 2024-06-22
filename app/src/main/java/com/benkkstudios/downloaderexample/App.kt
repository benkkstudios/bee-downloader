package com.benkkstudios.downloaderexample

import android.app.Application
import com.benkkstudios.downloader.BeeDownloader
import com.benkkstudios.downloader.DownloadConfig

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val downloadConfig = DownloadConfig(
            notificationEnable = false,
            notificationIcon = R.mipmap.ic_launcher,
            connectTimeoutMs = 30000,
            readTimeoutMs = 30000
        )
        BeeDownloader.create(this, downloadConfig)
    }
}