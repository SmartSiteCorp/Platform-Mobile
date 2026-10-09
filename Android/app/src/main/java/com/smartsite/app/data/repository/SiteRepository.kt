package com.smartsite.app.data.repository

import com.smartsite.app.data.model.Site
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

interface SiteRepository {
    val sites: StateFlow<List<Site>>
    fun getById(id: String): Site?
    fun create(
        name: String,
        address: String,
        description: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Site
}
