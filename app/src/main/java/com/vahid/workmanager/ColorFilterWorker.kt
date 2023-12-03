package com.vahid.workmanager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.LightingColorFilter
import android.graphics.Paint
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ColorFilterWorker(
    private val context: Context,
    private val workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        val imageUri = workerParameters.inputData.getString(WorkerKeys.IMAGE_URI)
            ?.toUri()
            ?.toFile()
        delay(5000L)
        return imageUri?.let { file ->
            val bmp = BitmapFactory.decodeFile(file.absolutePath)
            val resultBmp = bmp.copy(bmp.config, true)
            val paint = Paint()
            paint.colorFilter = LightingColorFilter(0x08FF04, 1)
            val canvas = Canvas(resultBmp)
            canvas.drawBitmap(resultBmp, 0f, 0f, paint)
            withContext(Dispatchers.IO) {
                val resultImageFile = File(context.cacheDir, "new_image.jpg")
                val outputStream = FileOutputStream(resultImageFile)
                val success = resultBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                if (success) {
                    Result.success(
                        workDataOf(
                            WorkerKeys.FILTER_URI to resultImageFile.toUri().toString()
                        )
                    )
                } else {
                    Result.failure()
                }
            }
        } ?: Result.failure()
    }
}