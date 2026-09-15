package com.example.reports

import java.io.File

/**
 * Data structures representing a full, official, professional report
 * built from real case, client, evidence, and financial data.
 */
data class TimelineEventItem(
    val time: String,
    val event: String
)

data class ReportEvidenceItem(
    val name: String,
    val fileType: String,
    val originalFilename: String,
    val md5Hash: String,
    val sha256Hash: String,
    val fileSizeFormatted: String,
    val chainOfCustody: String
)

data class ReportSourceItem(
    val name: String,
    val category: String,
    val officialUrl: String,
    val notes: String = ""
)

data class ReportPaymentSummary(
    val totalAmount: Double,
    val paidAmount: Double,
    val remainingAmount: Double,
    val currency: String,
    val paymentStatus: String,
    val paymentsCount: Int
)

enum class LogoPosition(val displayName: String) {
    RIGHT("يمين"),
    CENTER("وسط"),
    LEFT("يسار")
}

enum class LogoSize(val displayName: String, val heightPx: Float) {
    SMALL("صغير", 40f),
    MEDIUM("متوسط", 58f),
    LARGE("كبير", 76f)
}

enum class ReportSection(val id: String, val titleAr: String) {
    CASE_DETAILS("case_details", "بيانات القضية والعميل"),
    EXECUTIVE_SUMMARY("exec_summary", "المقدمة والملخص التنفيذي"),
    TECHNICAL_ANALYSIS("tech_analysis", "المحتوى والتحليل الفني الجنائي"),
    EVIDENCE_LEDGER("evidence_ledger", "سجل الأدلة والمرفقات الرقمية"),
    CUSTOM_NOTES("custom_notes", "الملاحظات والتوجيهات الإضافية"),
    RECOMMENDATIONS("recommendations", "التوصيات والإجراءات الأمنية"),
    FINANCIAL_SUMMARY("financial_summary", "الملخص المالي والمستحقات"),
    SIGNATURE("signature", "التوقيع والخاتمة المعتمدة")
}

data class FullForensicReport(
    val reportId: String,
    val title: String,
    val caseNumber: String,
    val generatedDate: String,
    val systemName: String = "منظومة جعفر بدران للتحقيق الرقمي والأمن السيبراني",
    val systemLogoText: String = "JB FORENSICS",
    val investigatorName: String = "جعفر بدران",
    val investigatorTitle: String = "خبير ومستشار معتمد في التحقيق الرقمي والأمن السيبراني",
    val contactPhone: String = "+966 50 000 0000",
    val contactEmail: String = "forensics@jaffarbadran.com",
    val clientName: String,
    val clientPhone: String,
    val clientEmail: String,
    val priority: String,
    val threatType: String,
    val status: String,
    val dueDate: String,
    val executiveSummary: String,
    val timelineEvents: List<TimelineEventItem>,
    val evidenceList: List<ReportEvidenceItem>,
    val technicalAnalysis: String,
    val officialSourcesUsed: List<ReportSourceItem>,
    val finalOutcome: String,
    val securityRecommendations: List<String>,
    val paymentsSummary: ReportPaymentSummary? = null,
    val digitalVerificationHash: String = "",
    val logoPath: String? = null,
    val showLogo: Boolean = true,
    val logoPosition: LogoPosition = LogoPosition.RIGHT,
    val logoSize: LogoSize = LogoSize.MEDIUM,
    val visibleSections: List<ReportSection> = ReportSection.entries.toList(),
    val customNotesList: List<String> = emptyList(),
    val customConclusion: String = "تم فحص واعتماد هذا التقرير الجنائي وفق الضوابط والمعايير الرقمية المعتمدة."
)

enum class ReportExportFormat(
    val extension: String,
    val mimeType: String,
    val displayName: String,
    val iconName: String
) {
    PDF("pdf", "application/pdf", "مستند PDF رسمي", "PictureAsPdf"),
    DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "مستند Word (DOCX)", "Description"),
    HTML("html", "text/html", "صفحة ويب تفاعلية (HTML)", "Html"),
    TXT("txt", "text/plain", "ملف نصي مدقق (TXT)", "TextFields");

    companion object {
        fun fromExtension(ext: String): ReportExportFormat? {
            return entries.firstOrNull { it.extension.equals(ext, ignoreCase = true) }
        }
    }
}

data class ExportResult(
    val file: File,
    val format: ReportExportFormat,
    val fileSizeFormatted: String,
    val sha256Checksum: String
)
