package com.benkkstudios.downloader

import androidx.annotation.DrawableRes

data class DownloadConfig(
    val notificationEnable: Boolean = false,
    @DrawableRes val notificationIcon: Int = R.mipmap.ic_launcher_foreground,
    val connectTimeoutMs: Long = 30000L,
    val readTimeoutMs: Long = 30000L
)