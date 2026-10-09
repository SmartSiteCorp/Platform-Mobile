package com.smartsite.app.data.mock

import com.smartsite.app.R
import com.smartsite.app.data.model.Site
import com.smartsite.app.data.model.SiteStatus
import com.smartsite.app.data.repository.SiteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.util.UUID

/**
 * In-memory replacement for the Base44 SDK `Site` entity.
 * All mutations update the StateFlow immediately; nothing is persisted.
 */
object MockSiteRepository : SiteRepository {

    private val _sites = MutableStateFlow(
        listOf(
            Site(
                id = "site-jardins",
                name = "Résidence Les Jardins",
                address = "12 rue des Lilas, 69003 Lyon",
                description = "Construction d'une résidence de 24 logements collectifs avec parking souterrain et espaces verts.",
                status = SiteStatus.IN_PROGRESS,
                startDate = LocalDate.of(2025, 9, 1),
                endDate = LocalDate.of(2026, 6, 30),
                imageRes = R.drawable.site_jardins,
                planRes = listOf(R.drawable.plan_rdc, R.drawable.plan_r1, R.drawable.plan_r2)
            ),
            Site(
                id = "site-horizon",
                name = "Tour Horizon",
                address = "8 quai du Commerce, 44000 Nantes",
                description = "Tour de bureaux de 12 étages, certification HQE visée.",
                status = SiteStatus.IN_PROGRESS,
                startDate = LocalDate.of(2025, 6, 15),
                endDate = LocalDate.of(2027, 2, 28),
                imageRes = R.drawable.site_horizon,
                planRes = listOf(R.drawable.plan_rdc, R.drawable.plan_r2)
            ),
            Site(
                id = "site-olympe",
                name = "Parc d'activités Olympe",
                address = "ZA des Oliviers, 13011 Marseille",
                description = "Aménagement d'un parc d'activités de 8 cellules industrielles.",
                status = SiteStatus.PLANNING,
                startDate = LocalDate.of(2026, 11, 1),
                endDate = LocalDate.of(2027, 9, 30),
                imageRes = R.drawable.site_olympe,
                planRes = listOf(R.drawable.plan_rdc)
            ),
            Site(
                id = "site-saint-michel",
                name = "Îlot Saint-Michel",
                address = "3 place Saint-Michel, 33000 Bordeaux",
                description = "Rénovation lourde d'un îlot ancien : reprise en sous-œuvre et surélévation.",
                status = SiteStatus.ON_HOLD,
                startDate = LocalDate.of(2025, 3, 1),
                endDate = LocalDate.of(2026, 12, 15),
                imageRes = R.drawable.site_saint_michel,
                planRes = listOf(R.drawable.plan_r1, R.drawable.plan_r2)
            ),
            Site(
                id = "site-verdun",
                name = "Halle des sports de Verdun",
                address = "25 avenue Foch, 59120 Loos",
                description = "Construction d'une halle sportive de 2 400 m² avec tribunes.",
                status = SiteStatus.COMPLETED,
                startDate = LocalDate.of(2024, 11, 4),
                endDate = LocalDate.of(2026, 4, 30),
                imageRes = R.drawable.site_verdun,
                planRes = listOf(R.drawable.plan_rdc, R.drawable.plan_r1)
            ),
            Site(
                id = "site-ecole",
                name = "École primaire Jean Moulin",
                address = "17 rue de la République, 31000 Toulouse",
                description = "Extension de l'école : 6 classes, une cantine et une cour végétalisée.",
                status = SiteStatus.IN_PROGRESS,
                startDate = LocalDate.of(2026, 1, 12),
                endDate = LocalDate.of(2026, 12, 20),
                imageRes = R.drawable.site_ecole,
                planRes = listOf(R.drawable.plan_r1)
            )
        )
    )
    override val sites: StateFlow<List<Site>> = _sites.asStateFlow()

    override fun getById(id: String): Site? = _sites.value.firstOrNull { it.id == id }

    override fun create(
        name: String,
        address: String,
        description: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Site {
        val site = Site(
            id = "site-${UUID.randomUUID()}",
            name = name,
            address = address,
            description = description,
            status = SiteStatus.PLANNING,
            startDate = startDate,
            endDate = endDate,
            imageRes = null,
            planRes = emptyList()
        )
        _sites.value = _sites.value + site
        return site
    }
}
