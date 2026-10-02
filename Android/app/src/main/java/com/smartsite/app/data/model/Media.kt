package com.smartsite.app.data.model

import androidx.annotation.DrawableRes
import java.time.LocalDate

enum class MediaType { PHOTO, DRONE_CAPTURE, LIDAR_SCAN, PLAN, DOCUMENT }

data class Media(
    val id: String,
    // User uploads are local content URIs; bundled mock entries use [fileRes] instead.
    val fileUri: String? = null,
    @DrawableRes val fileRes: Int? = null,
    val type: MediaType,
    val caption: String,
    val siteId: String,
    val createdDate: LocalDate
)
