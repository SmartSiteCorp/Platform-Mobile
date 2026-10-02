package com.smartsite.app.data.model

import androidx.annotation.DrawableRes
import java.time.LocalDate

enum class SiteStatus { PLANNING, IN_PROGRESS, COMPLETED, ON_HOLD }

data class Site(
    val id: String,
    val name: String,
    val address: String,
    val description: String,
    val status: SiteStatus,
    val startDate: LocalDate,
    val endDate: LocalDate,
    // Bundled placeholder images — no network URLs in the alpha.
    @DrawableRes val imageRes: Int? = null,
    @DrawableRes val planRes: List<Int> = emptyList()
)
