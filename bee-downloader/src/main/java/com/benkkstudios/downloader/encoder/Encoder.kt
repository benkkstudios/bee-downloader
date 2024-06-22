package com.benkkstudios.downloader.encoder

import android.util.Base64
import java.nio.charset.Charset

internal object Encoder {
    fun encode(input: String): String {
        return Base64.encodeToString(input.toByteArray(), Base64.DEFAULT)
    }

    fun decode(input: String): String {
        return String(Base64.decode(input, Base64.DEFAULT), Charset.forName("UTF-8"))
    }
}