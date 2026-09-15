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
    val clientEmail: String = "",
    val threatType: String, // نوع القضية / المشكلة
    val description: String = "", // وصف وتفاصيل القضية
    val priority: String, // حرجة, عالية, متوسطة, منخفضة
    val status: String, // جديدة, قيد المتابعة, إحالة للجهات المختصة, مكتملة, مغلقة, مؤرشفة
    val assignedInvestigator: String, // المسؤول عنها
    val dueDate: String = "", // الموعد النهائي / تاريخ الاستحقاق
    val source: String = "يدوي", // المصدر: يدوي, Google Sheet, نموذج دعم, طلب خارجي
    val linkedRequestId: String? = null, // الطلب الخارجي المرتبط
    val timelineEventsJson: String = "[]", // Serialized list of timeline updates
    val notes: String = "",
    val createdDate: Long = System.currentTimeMillis(),
    val updatedDate: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val isArchived: Boolean = false,
    val syncStatus: String = "SYNCED", // PENDING_SYNC, SYNCED
    // Financial & Payments
    val totalAmount: Double = 0.0, // القيمة الإجمالية للقضية
    val paidAmount: Double = 0.0, // إجمالي المبالغ المدفوعة
    val remainingAmount: Double = 0.0, // المبلغ المتبقي
    val currency: String = "SAR", // العملة
    val paymentStatus: String = "غير مدفوع", // غير مدفوع, مدفوع جزئيًا, مدفوع بالكامل, معفى, مؤجل
    // Official & External Identifiers
    val externalPlatformCaseId: String = "", // رقم أو معرف البلاغ بالمنصة الخارجية (Meta, Google, X, إلخ)
    val supportTicketId: String = "", // رقم تذكرة الدعم الفني
    val targetIdentifier: String = "" // معرف الحساب أو الرابط المستهدف
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
    val fileType: String, // Screenshot, Audio, Video, Network Dump, Chat Export, Forensic Image, Document, Archive
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
    val syncStatus: String = "SYNCED",
    // Offline Storage & File System
    val localFilePath: String = "", // مسار الحفظ المحلي في ذاكرة التطبيق
    val fileSizeBytes: Long = 0L, // حجم الملف بالبايت
    val fileSizeFormatted: String = "0 KB", // حجم الملف منسق
    val mimeType: String = "", // نوع MIME للملف
    val category: String = "مستندات", // صور، مستندات، مراسلات، تقارير، مرفقات العميل، مرفقات المنصة، فواتير، أخرى
    val description: String = "" // وصف تفصيلي للملف ومحتواه
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
    val relatedClientId: String? = null,
    val relatedContentId: String? = null,
    val assignedUser: String = "جعفر بدران",
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

@Entity(tableName = "case_payments")
data class CasePaymentEntity(
    @PrimaryKey val id: String,
    val caseId: String,
    val caseNumber: String,
    val amount: Double,
    val currency: String = "SAR",
    val paymentMethod: String, // نقدي, تحويل بنكي, بطاقة مدى/ائتمان, STC Pay, PayPal, شيك, أخرى
    val paymentDate: String,
    val notes: String = "",
    val receiptNumber: String = "",
    val createdDate: Long = System.currentTimeMillis(),
    val syncStatus: String = "SYNCED"
)

@Entity(tableName = "case_financial_logs")
data class CaseFinancialLogEntity(
    @PrimaryKey val id: String,
    val caseId: String,
    val logType: String, // تحديد السعر, تعديل السعر, تسجيل دفعة, استرداد, إعفاء
    val oldValue: Double,
    val newValue: Double,
    val currency: String = "SAR",
    val notes: String = "",
    val performedBy: String = "جعفر بدران",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "case_audit_logs")
data class CaseAuditLogEntity(
    @PrimaryKey val id: String,
    val caseId: String,
    val caseNumber: String,
    val operation: String, // إنشاء القضية, تعديل السعر, تغيير الحالة, إضافة دفعة, تعديل بيانات, إضافة مرفق, إغلاق القضية, إعادة فتح, أرشفة
    val oldValue: String = "",
    val newValue: String = "",
    val performedBy: String = "جعفر بدران",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "video_ideas")
data class VideoIdeaEntity(
    @PrimaryKey val id: String,
    val title: String,
    val concept: String,
    val platform: String, // YouTube, TikTok, Instagram, X, LinkedIn, أخرى
    val contentType: String, // توعية أمنية, شرح تقني, كشف احتيال, نصيحة سريعة, بودكاست, تحليل قضية
    val targetAudience: String, // عامة المستخدمين, تقنيون, شركات ورواد أعمال, صناع محتوى
    val goal: String, // زيادة الوعي, بناء الثقة, توجيه المتابعين, جذب عملاء
    val hook: String = "",
    val keyPoints: String = "",
    val status: String = "فكرة جديدة", // فكرة جديدة, قيد التطوير, جاهزة للتصوير, تم التصوير, قيد المونتاج, جاهزة للنشر, تم النشر, مؤرشفة
    val priority: String = "متوسطة", // حرجة, عالية, متوسطة, منخفضة
    val notes: String = "",
    val createdDate: Long = System.currentTimeMillis(),
    val updatedDate: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val syncStatus: String = "SYNCED"
)

@Entity(tableName = "video_scripts")
data class VideoScriptEntity(
    @PrimaryKey val id: String,
    val ideaId: String? = null,
    val title: String,
    val hook: String = "",
    val intro: String = "",
    val mainContent: String = "",
    val outro: String = "",
    val callToAction: String = "",
    val estimatedDuration: String = "60 ثانية",
    val platform: String = "YouTube",
    val toneAndStyle: String = "احترافي ومباشر",
    val referencesAndSources: String = "",
    val notes: String = "",
    val status: String = "مسودة", // مسودة, قيد المراجعة, معتمد للتصوير, تم الإنتاج, مؤرشف
    val createdDate: Long = System.currentTimeMillis(),
    val updatedDate: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val syncStatus: String = "SYNCED"
)

@Entity(tableName = "case_custom_links")
data class CaseCustomLinkEntity(
    @PrimaryKey val id: String,
    val caseId: String,
    val caseNumber: String,
    val title: String,
    val url: String,
    val linkType: String = "رابط خارجي", // حساب, صفحة, منشور, فيديو, صورة, مجموعة, قناة, موقع, رابط دعم, رابط بلاغ, رابط تذكرة, رابط مراسلة, رابط Google Sheet, رابط خارجي, أخرى
    val notes: String = "",
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "generated_reports")
data class GeneratedReportEntity(
    @PrimaryKey val id: String,
    val caseId: String = "",
    val caseNumber: String = "",
    val clientName: String = "",
    val templateId: String = "",
    val title: String,
    val subtitle: String = "",
    val sectionsJson: String = "[]",
    val executiveSummary: String = "",
    val completionNotes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val format: String = "PDF", // PDF, DOCX, HTML, TXT
    val filePath: String = "",
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null
)

@Entity(tableName = "report_templates")
data class ReportTemplateEntity(
    @PrimaryKey val id: String,
    val templateName: String,
    val title: String,
    val subtitle: String,
    val organizationName: String = "منظومة جعفر بدران للأدلة الرقمية والاستشارات السيبرانية",
    val primaryColorHex: String = "#00E5FF",
    val accentColorHex: String = "#7C4DFF",
    val introText: String = "",
    val outroText: String = "",
    val signatureTitle: String = "المسؤول والخبير الجنائي",
    val signatureName: String = "جعفر بدران",
    val footerText: String = "وثيقة عمل رسمية صادرة ومعتمدة - منظومة جعفر بدران للأدلة الرقمية",
    val visibleSectionsJson: String = "[]",
    val sectionsOrderJson: String = "[]",
    val isDefault: Boolean = false
)


