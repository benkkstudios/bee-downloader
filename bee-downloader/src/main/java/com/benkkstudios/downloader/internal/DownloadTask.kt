package com.benkkstudios.downloader.internal

import com.benkkstudios.downloader.database.DownloadItem
import java.io.File
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

internal object DownloadTask {
    private val downloadServices: DownloadClient.DownloadServices = DownloadClient.createService()
    private var total: Long = 0L
    private var downloaded: Long = 0L
    private val cancelled = AtomicBoolean(false)
    fun cancel() = apply { cancelled.set(true) }
    suspend fun download(item: DownloadItem, listener: Listener) {
        cancelled.set(false)
        runCatching {
            val originalFile = createOriginalFile(item)
            var tempFile = createTempFile(item)

            if (originalFile.exists()) {
                if (!tempFile.delete()) tempFile = originalFile
                total = item.total
                downloaded = item.downloaded
            }

            downloadServices.download(item.url).run {
                byteStream().use { inputStream ->
                    tempFile.outputStream().use { outputStream ->
                        total = contentLength()
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        var bytes = inputStream.read(buffer)
                        while (bytes >= 0) {
                            if (cancelled.get()) {
                                listener.onCancel()
                                break
                            }
                            outputStream.write(buffer, 0, bytes)
                            downloaded += bytes
                            bytes = inputStream.read(buffer)
                            val progress = ((downloaded * 100) / total).toInt()
                            listener.onProgress(progress)
                        }
                        inputStream.close()
                        outputStream.close()
                    }
                }
            }
            if (!cancelled.get()) {
                createRealFile(tempFile, originalFile)
                listener.onComplete()
            } else {
                deleteTempFile(item)
            }
        }.onFailure {
            listener.onError(it.message.toString())
        }
    }

    private fun deleteTempFile(item: DownloadItem) = File(item.directory, item.filename + ".temp").run {
        if (!exists()) delete()
        this
    }

    private fun createTempFile(item: DownloadItem) = File(item.directory, item.filename + ".temp").run {
        createDirectory(item)
        if (!exists()) createNewFile()
        this
    }

    private fun createOriginalFile(item: DownloadItem) = File(item.directory, item.filename).run {
        createDirectory(item)
        this
    }

    private fun createDirectory(item: DownloadItem) = File(item.directory).run {
        if (!exists()) if (!mkdir()) mkdirs()
        this
    }

    @Throws(IOException::class)
    fun createRealFile(tempFile: File, originalFile: File) {
        tempFile.copyTo(originalFile, overwrite = true)
        tempFile.delete()
    }

    interface Listener {
        fun onStarted()
        fun onProgress(progress: Int)
        fun onComplete()
        fun onError(error: String)
        fun onCancel()
    }
}

