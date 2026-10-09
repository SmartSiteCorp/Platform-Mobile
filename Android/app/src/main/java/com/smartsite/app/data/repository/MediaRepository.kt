package com.smartsite.app.data.repository

import com.smartsite.app.data.model.Media
import com.smartsite.app.data.model.MediaType
import kotlinx.coroutines.flow.StateFlow

interface MediaRepository {
    val media: StateFlow<List<Media>>
    fun create(
        fileUri: String?,
        type: MediaType,
        caption: String,
        siteId: String
    ): Media
}
