package com.smartsite.app.data.mock

import com.smartsite.app.R
import com.smartsite.app.data.model.Media
import com.smartsite.app.data.model.MediaType
import com.smartsite.app.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.util.UUID

/**
 * In-memory replacement for the Base44 SDK `Media` entity.
 */
object MockMediaRepository : MediaRepository {

    private val _media = MutableStateFlow(
        listOf(
            Media(
                id = "media-dalle",
                fileRes = R.drawable.media_dalle,
                type = MediaType.PHOTO,
                caption = "Coulage de la dalle — Bâtiment A",
                siteId = "site-jardins",
                createdDate = LocalDate.of(2026, 9, 24)
            ),
            Media(
                id = "media-facade",
                fileRes = R.drawable.media_facade_drone,
                type = MediaType.DRONE_CAPTURE,
                caption = "Vue drone façade ouest — Tour Horizon",
                siteId = "site-horizon",
                createdDate = LocalDate.of(2026, 9, 30)
            ),
            Media(
                id = "media-terrain",
                fileRes = R.drawable.media_terrain_drone,
                type = MediaType.DRONE_CAPTURE,
                caption = "Survol terrain — parcelle B",
                siteId = "site-olympe",
                createdDate = LocalDate.of(2026, 9, 18)
            ),
            Media(
                id = "media-lidar",
                fileRes = R.drawable.media_lidar_toiture,
                type = MediaType.LIDAR_SCAN,
                caption = "Relevé LiDAR charpente — Îlot Saint-Michel",
                siteId = "site-saint-michel",
                createdDate = LocalDate.of(2026, 8, 12)
            ),
            Media(
                id = "media-plan-ecole",
                fileRes = R.drawable.plan_r1,
                type = MediaType.PLAN,
                caption = "Plan R+1 — École Jean Moulin",
                siteId = "site-ecole",
                createdDate = LocalDate.of(2026, 1, 20)
            ),
            Media(
                id = "media-plan-jardins",
                fileRes = R.drawable.plan_rdc,
                type = MediaType.PLAN,
                caption = "Plan RDC — Résidence Les Jardins",
                siteId = "site-jardins",
                createdDate = LocalDate.of(2025, 8, 22)
            ),
            Media(
                id = "media-halle",
                fileRes = R.drawable.site_verdun,
                type = MediaType.PHOTO,
                caption = "Halle livrée — vue extérieure",
                siteId = "site-verdun",
                createdDate = LocalDate.of(2026, 4, 28)
            )
        )
    )
    override val media: StateFlow<List<Media>> = _media.asStateFlow()

    override fun create(
        fileUri: String?,
        type: MediaType,
        caption: String,
        siteId: String
    ): Media {
        val item = Media(
            id = "media-${UUID.randomUUID()}",
            fileUri = fileUri,
            fileRes = null,
            type = type,
            caption = caption,
            siteId = siteId,
            createdDate = LocalDate.now()
        )
        _media.value = _media.value + item
        return item
    }
}
