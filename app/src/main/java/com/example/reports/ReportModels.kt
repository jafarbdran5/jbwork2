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
    val digitalVerificationHash: String = ""
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
