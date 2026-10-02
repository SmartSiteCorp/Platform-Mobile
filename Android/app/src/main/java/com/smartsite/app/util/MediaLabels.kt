package com.smartsite.app.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.ui.graphics.vector.ImageVector
import com.smartsite.app.data.model.MediaType

/** French label for a media type. */
fun MediaType.frenchLabel(): String = when (this) {
    MediaType.PHOTO -> "Photo"
    MediaType.DRONE_CAPTURE -> "Capture drone"
    MediaType.LIDAR_SCAN -> "Scan LiDAR"
    MediaType.PLAN -> "Plan"
    MediaType.DOCUMENT -> "Document"
}

/** Icon per media type (mirrors the mockup's lucide mapping). */
fun MediaType.icon(): ImageVector = when (this) {
    MediaType.PHOTO -> Icons.Filled.PhotoCamera
    MediaType.DRONE_CAPTURE -> Icons.Filled.PhotoCamera
    MediaType.LIDAR_SCAN -> Icons.Filled.CropFree
    MediaType.PLAN -> Icons.Filled.Image
    MediaType.DOCUMENT -> Icons.Filled.Description
}
