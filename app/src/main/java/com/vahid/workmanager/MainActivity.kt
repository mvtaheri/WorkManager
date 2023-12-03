package com.vahid.workmanager

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import coil.compose.rememberImagePainter
import com.vahid.workmanager.ui.theme.WorkManagerTheme
import java.time.Duration

class MainActivity : ComponentActivity() {
    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()
        val colorFilterRequest = OneTimeWorkRequestBuilder<ColorFilterWorker>()
            .build()
        val workManager = WorkManager.getInstance(applicationContext)
        setContent {
            WorkManagerTheme {
                // A surface container using the 'background' color from the theme
                val workInfo = workManager.getWorkInfosForUniqueWorkLiveData("download")
                    .observeAsState()
                    .value
                val downloadInfo = remember(key1 = workInfo) {
                    workInfo?.find { it.id == downloadRequest.id }
                }
                val filterInfo = remember(key1 = workInfo) {
                    workInfo?.find { it.id == colorFilterRequest.id }
                }
                val imageUri by derivedStateOf {
                    val downloadUri = downloadInfo?.outputData?.getString(WorkerKeys.IMAGE_URI)
                        ?.toUri()
                    val filterUri = filterInfo?.outputData?.getString(WorkerKeys.FILTER_URI)
                        ?.toUri()
                    filterUri ?: downloadUri
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    imageUri?.let { uri ->
                        Image(
                            painter = rememberImagePainter(data = uri), contentDescription = null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Button(
                        onClick = {
                            workManager.beginUniqueWork(
                                "download",
                                ExistingWorkPolicy.KEEP,
                                downloadRequest
                            )
                                .then(colorFilterRequest)
                                .enqueue()
                        },
                        enabled = downloadInfo?.state != WorkInfo.State.RUNNING
                    ) {
                        Text(text = "Start Download")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    when (downloadInfo?.state) {
                        WorkInfo.State.RUNNING -> Text(text = "Downloading ... ")
                        WorkInfo.State.SUCCEEDED -> Text(text = "Download Succeeded ")
                        WorkInfo.State.FAILED -> Text(text = "Download FAILED ")
                        WorkInfo.State.CANCELLED -> Text(text = "Download CANCELLED  ")
                        WorkInfo.State.ENQUEUED -> Text(text = "Download ENQUEUED  ")
                        WorkInfo.State.BLOCKED -> Text(text = "Download BLOCKED ")
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WorkManagerTheme {
        Greeting("Android")
    }
}