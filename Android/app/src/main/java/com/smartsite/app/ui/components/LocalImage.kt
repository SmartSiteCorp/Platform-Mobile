package com.smartsite.app.ui.components

import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Displays an image that is either a bundled drawable ([res]) or a local
 * content/file URI ([uri]) picked by the user. No network loading — the alpha
 * is fully offline.
 */
@Composable
fun LocalImage(
    modifier: Modifier = Modifier,
    @DrawableRes res: Int? = null,
    uri: String? = null,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop
) {
    when {
        res != null -> Image(
            painter = painterResource(res),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
        uri != null -> {
            val context = LocalContext.current
            val bitmap by produceState<ImageBitmap?>(initialValue = null, uri) {
                value = withContext(Dispatchers.IO) {
                    runCatching {
                        context.contentResolver.openInputStream(android.net.Uri.parse(uri))?.use { input ->
                            BitmapFactory.decodeStream(input)?.asImageBitmap()
                        } ?: runCatching {
                            // plain file path (own cache files)
                            BitmapFactory.decodeFile(android.net.Uri.parse(uri).path)?.asImageBitmap()
                        }.getOrNull()
                    }.getOrNull()
                }
            }
            bitmap?.let {
                Image(
                    bitmap = it,
                    contentDescription = contentDescription,
                    modifier = modifier,
                    contentScale = contentScale
                )
            } ?: Box(modifier)
        }
        else -> Box(modifier)
    }
}
