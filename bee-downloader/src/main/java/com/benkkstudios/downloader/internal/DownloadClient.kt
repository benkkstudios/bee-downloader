package com.benkkstudios.downloader.internal

import com.benkkstudios.downloader.BeeDownloader
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.util.concurrent.TimeUnit


internal object DownloadClient {
    fun createService(): DownloadServices {
        val client = OkHttpClient.Builder()
            .connectTimeout(BeeDownloader.config.connectTimeoutMs, TimeUnit.MILLISECONDS)
            .readTimeout(BeeDownloader.config.readTimeoutMs, TimeUnit.MILLISECONDS)
            .addInterceptor(DownloadInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl("https://google.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DownloadServices::class.java)
    }

    class DownloadInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var tryCount = 0
            val maxTries = 3
            val request = chain.request()
            var response = chain.proceed(request)
            while (!response.isSuccessful && tryCount < maxTries) {
                tryCount++
                response.close()
                response = chain.proceed(request)
            }
            return response
        }
    }

    interface DownloadServices {
        @GET
        @Streaming
        suspend fun download(@Url url: String): ResponseBody
    }
}