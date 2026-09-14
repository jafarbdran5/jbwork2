package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Entity representing an authoritative official portal, support gateway,
 * law enforcement portal, trusted partner program, OSINT verification tool,
 * or fact-checking source.
 */
@Entity(tableName = "official_sources")
data class OfficialSourceEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val companyOrEntity: String,
    val sectionType: String, // One of 14 standard sections
    val category: String, // Specific topic: e.g. "Hacked Account", "Law Enforcement", "OSINT"
    val portalType: String, // e.g. "بوابة قانونية مشفرة", "نموذج استرجاع عاجل", "أداة تقصي رقمي"
    val officialUrl: String,
    val description: String,
    val region: String = "عالمي (Global)",
    val requirements: String = "متاح للعامة",
    val verificationStatus: String = "معتمد ورسمي", // "معتمد ورسمي", "يتطلب اعتماد شريك", "متاح للعامة"
    val lastVerifiedDate: String = "سبتمبر 2026",
    val isFavorite: Boolean = false,
    val notes: String = "",
    val isVisible: Boolean = true,
    val sortOrder: Int = 0
)
