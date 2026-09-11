package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cases")
data class CaseEntity(
    @PrimaryKey val id: String, // e.g. case_1725968400000_uuid
    val caseNumber: String, // e.g. JB-2026-0814
    val title: String,
    val clientName: String,
    val clientPhone: String,
    val threatType: String, // Extortion, Impersonation, Blackmail, Account Takeover, Harassment, Doxxing, Phishing, Defamation
    val priority: String, // Critical, High, Medium, Low
    val status: String, // New, Investigation, In Progress, Transferred to Police, Resolved, Closed
    val assignedInvestigator: String,
    val timelineEventsJson: String, // Serialized list of timeline updates
    val notes: String,
    val createdDate: Long = System.currentTimeMillis(),
    val updatedDate: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val syncStatus: String = "SYNCED" // PENDING_SYNC, SYNCED
)

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val phoneNumber: String,
    val encryptedEmail: String,
    val riskLevel: String, // Urgent, Active Blackmail, High Risk, Follow-up
    val notes: String,
    val activeCaseId: String? = null,
    val createdDate: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val syncStatus: String = "SYNCED"
)

@Entity(tableName = "evidence")
data class EvidenceEntity(
    @PrimaryKey val id: String,
    val caseId: String,
    val caseNumber: String,
    val evidenceName: String,
    val fileType: String, // Screenshot, Audio, Video, Network Dump, Chat Export, Forensic Image
    val originalFilename: String,
    val md5Hash: String,
    val sha256Hash: String,
    val exifDeviceModel: String,
    val exifSoftware: String,
    val exifGpsCoords: String,
    val exifTimestamp: String,
    val chainOfCustodyLog: String, // Log of custody and investigator who sealed it
    val notes: String,
    val createdDate: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val syncStatus: String = "SYNCED"
)

@Entity(tableName = "content_studio")
data class ContentEntity(
    @PrimaryKey val id: String, // Deterministic cnt_timestamp_uuid
    val title: String,
    val body: String,
    val platform: String, // Instagram, Facebook, X, Telegram, TikTok, YouTube, LinkedIn, Snapchat, Website
    val status: String, // Idea, Draft, Scheduled, Published, Archived
    val tagsJson: String, // CSV or JSON hashtags
    val scheduledTimestamp: Long? = null,
    val createdDate: Long = System.currentTimeMillis(),
    val updatedDate: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val syncStatus: String = "SYNCED"
)

@Entity(tableName = "knowledge_base")
data class KnowledgeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // Platform Policies, OSINT, Psychological Casework, Investigator Resilience, Operational SOPs
    val summary: String,
    val content: String,
    val officialUrl: String = "",
    val isOfficialGuide: Boolean = true,
    val tags: String = "",
    val updatedDate: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val syncStatus: String = "SYNCED"
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val actionType: String, // LOGIN, VIEW, CREATE, EDIT, DELETE, EXPORT, RELOAD_LIBRARY
    val module: String, // CASES, EVIDENCE, STUDIO, KNOWLEDGE, CLIENTS, AUTH, TASKS, SETTINGS
    val entityId: String,
    val performedBy: String,
    val userRole: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val priority: String, // حرجة, عالية, متوسطة, منخفضة
    val dueDate: String,
    val status: String, // جديدة, قيد التنفيذ, مكتملة, مؤجلة
    val relatedCaseId: String? = null,
    val relatedCaseNumber: String? = null,
    val createdDate: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val syncStatus: String = "SYNCED"
)

@Entity(tableName = "sync_queue")
data class SyncOperationEntity(
    @PrimaryKey val id: String,
    val entityType: String, // CASES, EVIDENCE, STUDIO, KNOWLEDGE, CLIENTS, TASKS
    val entityId: String,
    val operation: String, // INSERT, UPDATE, DELETE
    val payloadJson: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val retryCount: Int = 0,
    val status: String = "PENDING", // PENDING, SUCCESS, FAILED
    val errorMessage: String? = null
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val key: String,
    val value: String
)

@Entity(tableName = "support_forms")
data class SupportFormEntity(
    @PrimaryKey val id: String,
    val company: String,
    val platform: String,
    val category: String,
    val problemType: String,
    val formName: String,
    val formUrl: String,
    val urlType: String, // DIRECT_FORM, REPORT_FORM, RECOVERY_FORM, LEGAL_FORM, SUPPORT_PAGE
    val requiresLogin: Boolean = false,
    val requiresIdentity: Boolean = false,
    val requiresEvidence: Boolean = false,
    val region: String = "Global",
    val language: String = "ar, en",
    val officialDomain: String,
    val verified: Boolean = true,
    val lastVerifiedAt: String = "2026-09-10",
    val status: String = "ACTIVE", // ACTIVE, INACTIVE
    val notes: String = "",
    val tags: String = "",
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val syncStatus: String = "SYNCED"
)

@Entity(tableName = "investigation_tools")
data class InvestigationToolEntity(
    @PrimaryKey val id: String,
    val name: String,
    val url: String,
    val category: String,
    val subcategory: String,
    val description: String,
    val officialDomain: String,
    val freeOrPaid: String, // مجاني, مجاني جزئياً, مدفوع
    val requiresAccount: Boolean = false,
    val requiresUpload: Boolean = false,
    val privacyRisk: String = "منخفض", // منخفض, متوسط, مرتفع
    val language: String = "ar, en",
    val verified: Boolean = true,
    val lastVerifiedAt: String = "2026-09-10",
    val status: String = "ACTIVE", // ACTIVE, INACTIVE
    val tags: String = "",
    val isFavorite: Boolean = false,
    val lastUsedAt: Long? = null,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val syncStatus: String = "SYNCED"
)

@Entity(tableName = "case_linked_items")
data class CaseLinkedItemEntity(
    @PrimaryKey val id: String,
    val caseId: String,
    val itemType: String, // SUPPORT_FORM, INVESTIGATION_TOOL
    val itemId: String,
    val itemTitle: String,
    val itemUrl: String,
    val itemPlatformOrCategory: String,
    val linkedAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)


