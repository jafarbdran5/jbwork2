package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * جميع الأقسام والوجهات الفعلية المعتمدة في منظومة جعفر بدران
 */
enum class ScreenDestination(
    val id: Int,
    val title: String,
    val icon: ImageVector,
    val defaultOrder: Int,
    val defaultInBottomBar: Boolean
) {
    DASHBOARD(0, "الرئيسية", Icons.Default.Home, 0, true),
    CASES(1, "القضايا", Icons.Default.Folder, 1, true),
    EXTERNAL_REQUESTS(14, "الطلبات الخارجية", Icons.Default.CloudDownload, 2, false),
    EVIDENCE(2, "المرفقات والملفات", Icons.Default.AttachFile, 3, true),
    TASKS(3, "المهام", Icons.Default.Assignment, 4, true),
    SUPPORT_FORMS(12, "نماذج الدعم المباشرة", Icons.Default.ContactSupport, 5, false),
    INVESTIGATION_HUB(13, "أدوات ومصادر العمل", Icons.Default.TravelExplore, 6, false),
    STUDIO(4, "استوديو المحتوى", Icons.Default.AutoAwesome, 7, true),
    CLIENTS(5, "العملاء", Icons.Default.People, 8, false),
    KNOWLEDGE(6, "الموسوعة المعرفية", Icons.Default.MenuBook, 9, false),
    REPORTS(7, "تقارير العمل والمتابعة", Icons.Default.Assessment, 10, false),
    SEARCH(8, "البحث الشامل", Icons.Default.Search, 11, false),
    TRASH(9, "سلة المحذوفات", Icons.Default.Delete, 12, false),
    SETTINGS(10, "الإعدادات والمزامنة", Icons.Default.Settings, 13, false),
    SECURITY(11, "سجل الأمان والتدقيق", Icons.Default.Security, 14, false),
    ADMIN_MANAGEMENT(15, "إدارة المنظومة", Icons.Default.AdminPanelSettings, 15, false),
    HYBRID_HUB(16, "بوابة الأداء السريع (Hybrid)", Icons.Default.Speed, 16, false);

    companion object {
        fun fromId(id: Int): ScreenDestination? = entries.find { it.id == id }

        /**
         * القائمة الافتراضية لعناصر الشريط السفلي
         */
        val defaultBottomNavDestinations: List<ScreenDestination>
            get() = entries.filter { it.defaultInBottomBar }.sortedBy { it.defaultOrder }
    }
}
