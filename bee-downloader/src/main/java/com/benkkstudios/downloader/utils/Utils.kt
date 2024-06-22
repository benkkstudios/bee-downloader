package com.benkkstudios.downloader.utils

import com.benkkstudios.downloader.encoder.Encoder

internal fun getUniqueId(url: String, directory: String, filename: String): String {
    return Encoder.encode("$url$directory$filename")
}

