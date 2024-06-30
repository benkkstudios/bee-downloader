
# Bee Downloader

Mutiple download manager with workmanager
Jetpack compose support


## Installation

add this to gradle
implementation 'com.github.benkkstudios:bee-downloader:<latest relase>'


## Add To Gradle


## Initialize downloader

```kotlin
        val downloadConfig = DownloadConfig(
            notificationEnable = false, // required android.permission.POST_NOTIFICATIONS
            notificationIcon = R.mipmap.ic_launcher,
            connectTimeoutMs = 30000,
            readTimeoutMs = 30000
        )
        BeeDownloader.create(this, downloadConfig)
    }

```

## How to add download task

```kotlin
    BeeDownloader.enqueue(
        url = "download url",
        directory = context.cacheDir.absolutePath,
        filename = "example.jpg", // filename for saving
        thumbnail = "thumbnail url", // optional
        scanToGallery = true //scan downloaded file to gallery
    )

```


## How to start download

```kotlin
    BeeDownloader.start(context)
```
## Listen task on activity

```kotlin
          BeeDownloader.observe(this@MainActivity){ state ->
                when(state){
                    DownloadState.Canceled -> TODO()
                    is DownloadState.Complete -> TODO()
                    is DownloadState.Failed -> TODO()
                    DownloadState.Paused -> TODO()
                    is DownloadState.Progress -> TODO()
                    DownloadState.Queued -> TODO()
                    DownloadState.Removed -> TODO()
                }
            }
```

## Listen task on Compose

```kotlin
    val state by BeeDownloader.state().collectAsState()
```

## Get all item from database

```kotlin
    val state by BeeDownloader.getAllDownload()
```
