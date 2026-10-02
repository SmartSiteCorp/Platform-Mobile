package com.smartsite.app.navigation

object Routes {
    const val DASHBOARD = "dashboard"
    const val SITES = "sites"
    const val SITE_DETAIL = "site_detail/{siteId}"
    const val TASKS = "tasks"
    const val TASK_DETAIL = "task_detail/{taskId}"
    const val DRONE = "drone"
    const val MEDIA = "media"
    const val ANNOTATIONS = "annotations"
    const val PROFILE = "profile"

    fun siteDetail(siteId: String) = "site_detail/$siteId"
    fun taskDetail(taskId: String) = "task_detail/$taskId"
}
