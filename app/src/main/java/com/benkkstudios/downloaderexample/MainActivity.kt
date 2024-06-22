package com.benkkstudios.downloaderexample

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.benkkstudios.downloader.BeeDownloader
import com.benkkstudios.downloader.internal.DownloadState
import com.benkkstudios.downloaderexample.ui.theme.BeeDownloaderExampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BeeDownloaderExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting()
                }
            }
        }
    }
}

fun downloadAllImages(context: Context) {
    BeeDownloader.clear()
    BeeDownloader.enqueue(
        url = "https://i.pinimg.com/564x/38/c8/6e/38c86eb987d1ab5cbb969507eb4c6589.jpg",
        directory = context.cacheDir.absolutePath,
        filename = "example1.jpg",
        thumbnail = "https://i.pinimg.com/564x/38/c8/6e/38c86eb987d1ab5cbb969507eb4c6589.jpg",
        scanToGallery = true
    )
    BeeDownloader.enqueue(
        url = "https://i.pinimg.com/564x/04/9a/5f/049a5fdd0ebb5c7f225b3202a1724dce.jpg",
        directory = context.cacheDir.absolutePath,
        filename = "example2.jpg",
        thumbnail = "https://i.pinimg.com/564x/04/9a/5f/049a5fdd0ebb5c7f225b3202a1724dce.jpg"
    )
    BeeDownloader.enqueue(
        url = "https://i.pinimg.com/564x/62/a9/d8/62a9d85482f143f922f01725a348d4e4.jpg",
        directory = context.cacheDir.absolutePath,
        filename = "example3.jpg",
        thumbnail = "https://i.pinimg.com/564x/62/a9/d8/62a9d85482f143f922f01725a348d4e4.jpg",
        scanToGallery = true
    )
    BeeDownloader.start(context)
}

@Composable
fun Greeting() {
    val context = LocalContext.current
    val state by BeeDownloader.state().collectAsState()
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF242936))
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(Color(0xFF242936))
        ) {
            Text(
                text = "Bee Downloader",
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            )
        }

        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 30.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xff2C313F)),
        ) {
            Column(Modifier.padding(vertical = 20.dp, horizontal = 30.dp)) {
                Text(
                    text = "Download Information",
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    Text(
                        text = "File Name : ",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = state.item.filename,
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                    )
                }
                Row {
                    Text(
                        text = "Progress Percent : ",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = state.progress.toString() + "%",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                    )
                }
                Row {
                    Text(
                        text = "Status : ",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = state.status,
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                    )
                }
                if (state is DownloadState.Failed) Row {
                    Text(
                        text = "Error Message : ",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = state.error,
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                    )
                }
            }

        }
        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xff8D63EE)),
            onClick = {
                downloadAllImages(context)
            },
        ) {
            Text(
                text = "Download Now",
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
            )
        }
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            Text(
//                text = "Bee Downloader",
//                modifier = modifier
//            )
//            Spacer(modifier = Modifier.height(10.dp))
//            Button(onClick = {
//                downloadAllImages(context)
//            }) {
//                Text(text = "Download Now")
//            }
//        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BeeDownloaderExampleTheme {
        Greeting()
    }
}