package com.krishnaZyala.faceRecognition.ui.composable

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp


@Composable
fun FaceView(bitmap: Bitmap, modifier: Modifier = Modifier) = Box(modifier.padding(5.dp)) {
    Image(
        bitmap.asImageBitmap(),
        "Face Bitmap",
        Modifier.fillMaxSize().clip(CircleShape),
        contentScale = ContentScale.FillHeight
    )
}