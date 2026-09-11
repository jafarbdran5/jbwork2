package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data Source definition for external Google Sheets.
 * Supports multiple Google Sheets as independent data sources.
 */
@Entity(tableName = "external_request_sources")
data class ExternalRequestSourceEntity(
    @PrimaryKey val id: String, // e.g. source_1725968400000_abcd
    val name: String, // e.g. "طلبات العملاء العامة"
    val publicUrl: String, // Full public Google Sheet URL
    val spreadsheetId: String, // Extracted Google Sheet ID
    val sourceType: String = "GOOGLE_SHEETS", // GOOGLE_SHEETS, MANUAL, etc.
    val enabled: Boolean = true,
    val status: String = "متصل", // متصل, خطأ اتصال, قيد الفحص, معطل
    val lastSync: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Individual sheet tab within a Google Spreadsheet source.
 * Discovered automatically and can be enabled, disabled, or ignored.
 */
@Entity(tableName = "external_sheets")
data class ExternalSheetEntity(
    @PrimaryKey val id: String, // e.g. ${sourceId}_${sheetId}
    val sourceId: String,
    val sheetId: String, // gid or sheet name identifier
    val sheetName: String, // e.g. "الطلبات الجديدة", "أرشيف"
    val enabled: Boolean = true,
    val ignored: Boolean = false,
    val rowCount: Int = 0,
    val columnCount: Int = 0,
    val lastSync: Long? = null,
    val customDisplayName: String? = null,
    val columnMappingJson: String = "{}"
)

/**
 * An individual external request/case row from a Google Sheet.
 * Each row is an independent case and does not deduplicate by client name.
 */
@Entity(tableName = "external_requests")
data class ExternalRequestEntity(
    @PrimaryKey val id: String, // Unique composite: ${sourceId}_${spreadsheetId}_${sheetId}_${rowId}
    val sourceId: String,
    val spreadsheetId: String,
    val sheetId: String,
    val sheetName: String,
    val rowId: String, // Row index or row key
    val requestNumber: String, // e.g. EXT-2026-001
    val clientName: String,
    val clientPhone: String,
    val clientEmail: String,
    val description: String,
    val urgency: String = "متوسطة", // حرجة, عالية, متوسطة, منخفضة
    val status: String = "جديد", // جديد, قيد المراجعة, قيد المعالجة, تم تحويله إلى قضية, مكتمل, مرفوض, مؤرشف
    val receivedAt: String = "",
    val internalNotes: String = "",
    val associatedCaseId: String? = null, // Linked CaseEntity id when converted
    val rawData: String = "{}", // Full raw row JSON
    val additionalFields: String = "{}", // Extra/unknown columns JSON
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
