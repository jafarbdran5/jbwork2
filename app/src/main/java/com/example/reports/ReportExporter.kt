package com.example.reports

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * High-performance, zero-external-dependency report generation and export engine.
 * Generates:
 * 1. PDF (using native Android PdfDocument with styled cards, tables, hashes & stamp)
 * 2. DOCX (genuine Microsoft Word OpenXML (.docx) generated via ZipOutputStream)
 * 3. HTML (self-contained, beautifully styled RTL Arabic executive report with print-ready CSS)
 * 4. TXT (clean, structured, cryptographic-ready forensic ledger)
 */
object ReportExporter {

    private const val AUTHORITY_SUFFIX = ".fileprovider"

    suspend fun generateAndSaveReport(
        context: Context,
        report: FullForensicReport,
        format: ReportExportFormat
    ): Result<ExportResult> = withContext(Dispatchers.IO) {
        try {
            val exportDir = File(context.cacheDir, "reports").apply { mkdirs() }
            val sanitizedTitle = report.caseNumber.replace("[^a-zA-Z0-9_\\-]".toRegex(), "_")
                .ifBlank { "report" }
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
            val filename = "Forensic_${sanitizedTitle}_${timestamp}.${format.extension}"
            val targetFile = File(exportDir, filename)

            when (format) {
                ReportExportFormat.PDF -> generatePdf(targetFile, report)
                ReportExportFormat.DOCX -> generateDocx(targetFile, report)
                ReportExportFormat.HTML -> generateHtml(targetFile, report)
                ReportExportFormat.TXT -> generateTxt(targetFile, report)
            }

            val checksum = calculateSha256(targetFile)
            val sizeFormatted = formatFileSize(targetFile.length())

            Result.success(
                ExportResult(
                    file = targetFile,
                    format = format,
                    fileSizeFormatted = sizeFormatted,
                    sha256Checksum = checksum
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getShareIntent(context: Context, exportResult: ExportResult): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}$AUTHORITY_SUFFIX",
            exportResult.file
        )
        return Intent(Intent.ACTION_SEND).apply {
            type = exportResult.format.mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "تقرير تحقيق رقمي رسمي: ${exportResult.file.name}")
            putExtra(
                Intent.EXTRA_TEXT,
                "مرفق التقرير الجنائي الرقمي الرسمي الصادر عن ${exportResult.file.name}\nبصمة SHA-256 للتحقق: ${exportResult.sha256Checksum}"
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun getViewIntent(context: Context, exportResult: ExportResult): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}$AUTHORITY_SUFFIX",
            exportResult.file
        )
        return Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, exportResult.format.mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    // ==========================================
    // 1. PDF GENERATION (Native Android PdfDocument)
    // ==========================================
    private fun generatePdf(outputFile: File, report: FullForensicReport) {
        val document = PdfDocument()
        val pageWidth = 595 // Standard A4 width at 72dpi
        val pageHeight = 842 // Standard A4 height at 72dpi

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        // Header Background Banner
        val headerPaint = Paint().apply {
            color = Color.rgb(18, 30, 49) // Deep Navy
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 110f, headerPaint)

        // Accent gold line
        val goldPaint = Paint().apply {
            color = Color.rgb(212, 160, 23)
            strokeWidth = 4f
            style = Paint.Style.STROKE
        }
        canvas.drawLine(0f, 110f, pageWidth.toFloat(), 110f, goldPaint)

        // Title Paint
        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 15f
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText(report.systemName, (pageWidth - 24).toFloat(), 35f, titlePaint)

        val subTitlePaint = Paint().apply {
            color = Color.rgb(220, 225, 230)
            textSize = 11f
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText(report.title, (pageWidth - 24).toFloat(), 60f, subTitlePaint)

        val metaHeaderPaint = Paint().apply {
            color = Color.rgb(180, 195, 210)
            textSize = 9.5f
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("رقم القضية: ${report.caseNumber}  |  تاريخ الإصدار: ${report.generatedDate}", (pageWidth - 24).toFloat(), 85f, metaHeaderPaint)

        // Left Logo text
        val logoPaint = Paint().apply {
            color = Color.rgb(212, 160, 23)
            textSize = 14f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText(report.systemLogoText, 24f, 45f, logoPaint)

        val logoSubPaint = Paint().apply {
            color = Color.rgb(170, 185, 200)
            textSize = 8.5f
            isAntiAlias = true
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText("OFFICIAL FORENSIC DOSSIER", 24f, 65f, logoSubPaint)

        // Content Area setup
        var currentY = 135f
        val bodyLabelPaint = Paint().apply {
            color = Color.rgb(18, 30, 49)
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT
        }
        val bodyTextPaint = Paint().apply {
            color = Color.rgb(40, 45, 55)
            textSize = 9.5f
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT
        }

        // Section: Case & Subject Details
        drawSectionHeader(canvas, "1. بيانات القضية والطرف المعني", currentY, pageWidth)
        currentY += 22f

        canvas.drawText("العميل / الجهة: ${report.clientName} (هاتف: ${report.clientPhone})", (pageWidth - 28).toFloat(), currentY, bodyTextPaint)
        currentY += 16f
        canvas.drawText("نوع التهديد: ${report.threatType} | درجة الأولوية: ${report.priority} | الحالة: ${report.status}", (pageWidth - 28).toFloat(), currentY, bodyTextPaint)
        currentY += 24f

        // Section: Executive Summary
        drawSectionHeader(canvas, "2. الملخص التنفيذي لنتائج الفحص", currentY, pageWidth)
        currentY += 22f

        val summaryLines = wrapText(report.executiveSummary, 90)
        for (line in summaryLines.take(5)) {
            canvas.drawText(line, (pageWidth - 28).toFloat(), currentY, bodyTextPaint)
            currentY += 15f
        }
        currentY += 10f

        // Section: Evidence & Hashes
        drawSectionHeader(canvas, "3. المرفقات والأدلة والبصمات الرقمية (Chain of Custody)", currentY, pageWidth)
        currentY += 22f

        if (report.evidenceList.isEmpty()) {
            canvas.drawText("لم يتم تسجيل مرفقات رقمية خارجية مباشرة في هذا التقرير.", (pageWidth - 28).toFloat(), currentY, bodyTextPaint)
            currentY += 18f
        } else {
            for ((idx, ev) in report.evidenceList.take(3).withIndex()) {
                canvas.drawText("[${idx + 1}] ${ev.name} (${ev.fileType}) - الحجم: ${ev.fileSizeFormatted}", (pageWidth - 28).toFloat(), currentY, bodyLabelPaint)
                currentY += 14f
                val hashText = "SHA-256: ${if (ev.sha256Hash.isNotBlank()) ev.sha256Hash.take(32) + "..." else "N/A"}"
                canvas.drawText(hashText, (pageWidth - 28).toFloat(), currentY, bodyTextPaint)
                currentY += 16f
            }
        }
        currentY += 8f

        // Section: Technical Findings & Outcome
        drawSectionHeader(canvas, "4. التحليل التقني والنتيجة الفنية", currentY, pageWidth)
        currentY += 22f

        val outcomeLines = wrapText(report.finalOutcome.ifBlank { "اكتمل الفحص الفني واستخلاص القرائن الرقمية بنجاح." }, 90)
        for (line in outcomeLines.take(4)) {
            canvas.drawText(line, (pageWidth - 28).toFloat(), currentY, bodyTextPaint)
            currentY += 15f
        }
        currentY += 10f

        // Section: Recommendations
        drawSectionHeader(canvas, "5. التوصيات والإجراءات الفنية الموصى بها", currentY, pageWidth)
        currentY += 22f

        val recs = if (report.securityRecommendations.isNotEmpty()) report.securityRecommendations else listOf(
            "تفعيل المصادقة الثنائية (2FA) عبر تطبيقات توليد الرموز لجميع الحسابات الحيوية.",
            "عزل الأجهزة المشتبه بإصابتها عن الشبكات المحلية وإجراء فحص بذاكرة التخزين الحي.",
            "الاحتفاظ بنسخ احتياطية غير متصلة بالإنترنت (Cold Backups) للبيانات الحساسة."
        )
        for ((idx, rec) in recs.take(3).withIndex()) {
            canvas.drawText("• (${idx + 1}) $rec", (pageWidth - 28).toFloat(), currentY, bodyTextPaint)
            currentY += 16f
        }
        currentY += 15f

        // Official Stamp & Verification Footer
        val stampBoxPaint = Paint().apply {
            color = Color.rgb(245, 247, 250)
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(24f, pageHeight - 115f, (pageWidth - 24).toFloat(), pageHeight - 30f, 8f, 8f, stampBoxPaint)

        val stampBorderPaint = Paint().apply {
            color = Color.rgb(212, 160, 23)
            style = Paint.Style.STROKE
            strokeWidth = 1.2f
        }
        canvas.drawRoundRect(24f, pageHeight - 115f, (pageWidth - 24).toFloat(), pageHeight - 30f, 8f, 8f, stampBorderPaint)

        val stampTextPaint = Paint().apply {
            color = Color.rgb(18, 30, 49)
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("المحقق المعتمد: ${report.investigatorName} - ${report.investigatorTitle}", (pageWidth - 36).toFloat(), pageHeight - 95f, stampTextPaint)

        val stampSubPaint = Paint().apply {
            color = Color.rgb(90, 100, 115)
            textSize = 8.5f
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("تواصل: ${report.contactPhone} | ${report.contactEmail}", (pageWidth - 36).toFloat(), pageHeight - 78f, stampSubPaint)

        val verificationHash = if (report.digitalVerificationHash.isNotBlank()) report.digitalVerificationHash else generateVerificationHash(report)
        val hashPaint = Paint().apply {
            color = Color.rgb(120, 130, 145)
            textSize = 7.5f
            typeface = Typeface.MONOSPACE
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("بصمة التحقق المشفرة: $verificationHash", (pageWidth - 36).toFloat(), pageHeight - 55f, hashPaint)

        // Stamp badge on left
        val badgePaint = Paint().apply {
            color = Color.rgb(212, 160, 23)
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText("[VERIFIED DOSSIER]", 36f, pageHeight - 80f, badgePaint)

        document.finishPage(page)
        FileOutputStream(outputFile).use { out ->
            document.writeTo(out)
        }
        document.close()
    }

    private fun drawSectionHeader(canvas: android.graphics.Canvas, title: String, y: Float, pageWidth: Int) {
        val bgPaint = Paint().apply {
            color = Color.rgb(238, 242, 246)
            style = Paint.Style.FILL
        }
        canvas.drawRect(24f, y - 13f, (pageWidth - 24).toFloat(), y + 4f, bgPaint)

        val barPaint = Paint().apply {
            color = Color.rgb(18, 30, 49)
            style = Paint.Style.FILL
        }
        canvas.drawRect((pageWidth - 28).toFloat(), y - 13f, (pageWidth - 24).toFloat(), y + 4f, barPaint)

        val textPaint = Paint().apply {
            color = Color.rgb(18, 30, 49)
            textSize = 10.5f
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText(title, (pageWidth - 34).toFloat(), y, textPaint)
    }

    // ==========================================
    // 2. DOCX GENERATION (Native OpenXML ZIP)
    // ==========================================
    private fun generateDocx(outputFile: File, report: FullForensicReport) {
        val docXml = buildDocxXmlContent(report)
        val contentTypesXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
    <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
    <Default Extension="xml" ContentType="application/xml"/>
    <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
</Types>"""

        val relsXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
    <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
</Relationships>"""

        ZipOutputStream(FileOutputStream(outputFile)).use { zip ->
            // [Content_Types].xml
            zip.putNextEntry(ZipEntry("[Content_Types].xml"))
            zip.write(contentTypesXml.toByteArray(Charsets.UTF_8))
            zip.closeEntry()

            // _rels/.rels
            zip.putNextEntry(ZipEntry("_rels/.rels"))
            zip.write(relsXml.toByteArray(Charsets.UTF_8))
            zip.closeEntry()

            // word/document.xml
            zip.putNextEntry(ZipEntry("word/document.xml"))
            zip.write(docXml.toByteArray(Charsets.UTF_8))
            zip.closeEntry()
        }
    }

    private fun buildDocxXmlContent(report: FullForensicReport): String {
        fun escapeXml(text: String): String {
            return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;")
        }

        val verificationHash = if (report.digitalVerificationHash.isNotBlank()) report.digitalVerificationHash else generateVerificationHash(report)

        return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
    <w:body>
        <!-- Header -->
        <w:p>
            <w:pPr>
                <w:jc w:val="right"/>
                <w:bidi/>
            </w:pPr>
            <w:r>
                <w:rPr>
                    <w:b/>
                    <w:sz w:val="36"/>
                    <w:color w:val="121E31"/>
                </w:rPr>
                <w:t>${escapeXml(report.systemName)}</w:t>
            </w:r>
        </w:p>
        <w:p>
            <w:pPr>
                <w:jc w:val="right"/>
                <w:bidi/>
            </w:pPr>
            <w:r>
                <w:rPr>
                    <w:b/>
                    <w:sz w:val="28"/>
                    <w:color w:val="D4A017"/>
                </w:rPr>
                <w:t>${escapeXml(report.title)}</w:t>
            </w:r>
        </w:p>
        <w:p>
            <w:pPr>
                <w:jc w:val="right"/>
                <w:bidi/>
            </w:pPr>
            <w:r>
                <w:rPr>
                    <w:sz w:val="20"/>
                    <w:color w:val="666666"/>
                </w:rPr>
                <w:t>رقم القضية: ${escapeXml(report.caseNumber)} | تاريخ الإصدار: ${escapeXml(report.generatedDate)}</w:t>
            </w:r>
        </w:p>

        <!-- Divider -->
        <w:p><w:r><w:t>__________________________________________________________________________</w:t></w:r></w:p>

        <!-- Section 1: Case Details -->
        <w:p>
            <w:pPr><w:jc w:val="right"/><w:bidi/></w:pPr>
            <w:r>
                <w:rPr><w:b/><w:sz w:val="24"/><w:color w:val="121E31"/></w:rPr>
                <w:t>1. بيانات القضية والطرف المعني</w:t>
            </w:r>
        </w:p>
        <w:p>
            <w:pPr><w:jc w:val="right"/><w:bidi/></w:pPr>
            <w:r>
                <w:rPr><w:sz w:val="22"/></w:rPr>
                <w:t>العميل / المؤسسة: ${escapeXml(report.clientName)} (هاتف: ${escapeXml(report.clientPhone)} | بريد: ${escapeXml(report.clientEmail)})</w:t>
            </w:r>
        </w:p>
        <w:p>
            <w:pPr><w:jc w:val="right"/><w:bidi/></w:pPr>
            <w:r>
                <w:rPr><w:sz w:val="22"/></w:rPr>
                <w:t>نوع التهديد: ${escapeXml(report.threatType)} | الأولوية: ${escapeXml(report.priority)} | الحالة: ${escapeXml(report.status)} | موعد الإنجاز: ${escapeXml(report.dueDate)}</w:t>
            </w:r>
        </w:p>

        <!-- Section 2: Executive Summary -->
        <w:p>
            <w:pPr><w:jc w:val="right"/><w:bidi/></w:pPr>
            <w:r>
                <w:rPr><w:b/><w:sz w:val="24"/><w:color w:val="121E31"/></w:rPr>
                <w:t>2. الملخص التنفيذي</w:t>
            </w:r>
        </w:p>
        <w:p>
            <w:pPr><w:jc w:val="right"/><w:bidi/></w:pPr>
            <w:r>
                <w:rPr><w:sz w:val="22"/></w:rPr>
                <w:t>${escapeXml(report.executiveSummary)}</w:t>
            </w:r>
        </w:p>

        <!-- Section 3: Technical Analysis -->
        <w:p>
            <w:pPr><w:jc w:val="right"/><w:bidi/></w:pPr>
            <w:r>
                <w:rPr><w:b/><w:sz w:val="24"/><w:color w:val="121E31"/></w:rPr>
                <w:t>3. الفحص الفني والتحليل الجنائي</w:t>
            </w:r>
        </w:p>
        <w:p>
            <w:pPr><w:jc w:val="right"/><w:bidi/></w:pPr>
            <w:r>
                <w:rPr><w:sz w:val="22"/></w:rPr>
                <w:t>${escapeXml(report.technicalAnalysis.ifBlank { "تم إجراء الفحص الفني على جميع الأدلة الرقمية المسجلة وتأكيد البصمات التشفيرية." })}</w:t>
            </w:r>
        </w:p>

        <!-- Section 4: Digital Evidence -->
        <w:p>
            <w:pPr><w:jc w:val="right"/><w:bidi/></w:pPr>
            <w:r>
                <w:rPr><w:b/><w:sz w:val="24"/><w:color w:val="121E31"/></w:rPr>
                <w:t>4. سجل المرفقات والأدلة الرقمية (Chain of Custody)</w:t>
            </w:r>
        </w:p>
        ${report.evidenceList.joinToString("") { ev ->
            """<w:p><w:pPr><w:jc w:val="right"/><w:bidi/></w:pPr><w:r><w:rPr><w:b/><w:sz w:val="20"/></w:rPr><w:t>• ${escapeXml(ev.name)} (${escapeXml(ev.fileType)}) - ${escapeXml(ev.fileSizeFormatted)}</w:t></w:r></w:p>
            <w:p><w:pPr><w:jc w:val="right"/><w:bidi/></w:pPr><w:r><w:rPr><w:sz w:val="18"/><w:color w:val="666666"/></w:rPr><w:t>   MD5: ${escapeXml(ev.md5Hash)} | SHA-256: ${escapeXml(ev.sha256Hash)}</w:t></w:r></w:p>"""
        }}

        <!-- Section 5: Recommendations -->
        <w:p>
            <w:pPr><w:jc w:val="right"/><w:bidi/></w:pPr>
            <w:r>
                <w:rPr><w:b/><w:sz w:val="24"/><w:color w:val="121E31"/></w:rPr>
                <w:t>5. التوصيات والإجراءات الفورية</w:t>
            </w:r>
        </w:p>
        ${report.securityRecommendations.joinToString("") { rec ->
            """<w:p><w:pPr><w:jc w:val="right"/><w:bidi/></w:pPr><w:r><w:rPr><w:sz w:val="22"/></w:rPr><w:t>• ${escapeXml(rec)}</w:t></w:r></w:p>"""
        }}

        <!-- Official Sign-off Box -->
        <w:p><w:r><w:t>__________________________________________________________________________</w:t></w:r></w:p>
        <w:p>
            <w:pPr><w:jc w:val="right"/><w:bidi/></w:pPr>
            <w:r>
                <w:rPr><w:b/><w:sz w:val="24"/><w:color w:val="121E31"/></w:rPr>
                <w:t>الاعتماد والختم الرقمي</w:t>
            </w:r>
        </w:p>
        <w:p>
            <w:pPr><w:jc w:val="right"/><w:bidi/></w:pPr>
            <w:r><w:rPr><w:sz w:val="20"/></w:rPr><w:t>المحقق المعتمد: ${escapeXml(report.investigatorName)} (${escapeXml(report.investigatorTitle)})</w:t></w:r>
        </w:p>
        <w:p>
            <w:pPr><w:jc w:val="right"/><w:bidi/></w:pPr>
            <w:r><w:rPr><w:sz w:val="20"/></w:rPr><w:t>قنوات التواصل: ${escapeXml(report.contactPhone)} | ${escapeXml(report.contactEmail)}</w:t></w:r>
        </w:p>
        <w:p>
            <w:pPr><w:jc w:val="right"/><w:bidi/></w:pPr>
            <w:r><w:rPr><w:sz w:val="18"/><w:color w:val="555555"/></w:rPr><w:t>رمز البصمة الرقمية الموحدة: $verificationHash</w:t></w:r>
        </w:p>
    </w:body>
</w:document>"""
    }

    // ==========================================
    // 3. HTML GENERATION (Standalone Web Document)
    // ==========================================
    private fun generateHtml(outputFile: File, report: FullForensicReport) {
        val verificationHash = if (report.digitalVerificationHash.isNotBlank()) report.digitalVerificationHash else generateVerificationHash(report)
        val html = """<!DOCTYPE html>
<html lang="ar" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${report.title} - ${report.caseNumber}</title>
    <style>
        :root {
            --primary: #121e31;
            --accent: #d4a017;
            --bg: #f8fafc;
            --card-bg: #ffffff;
            --text-main: #0f172a;
            --text-muted: #64748b;
            --border: #e2e8f0;
            --success: #10b981;
            --danger: #ef4444;
        }
        @media print {
            body { background: #fff !important; }
            .no-print { display: none !important; }
            .page-break { page-break-after: always; }
        }
        body {
            font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Noto Kufi Arabic', 'Cairo', sans-serif;
            background-color: var(--bg);
            color: var(--text-main);
            margin: 0;
            padding: 24px 16px;
            line-height: 1.6;
        }
        .container {
            max-width: 900px;
            margin: 0 auto;
            background: var(--card-bg);
            border-radius: 16px;
            box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05), 0 8px 10px -6px rgba(0, 0, 0, 0.01);
            overflow: hidden;
            border: 1px solid var(--border);
        }
        .header-banner {
            background: linear-gradient(135deg, #0d1522 0%, #1e293b 100%);
            color: #fff;
            padding: 32px 28px;
            border-bottom: 4px solid var(--accent);
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 16px;
        }
        .system-title {
            font-size: 20px;
            font-weight: 800;
            margin: 0 0 6px 0;
            color: #ffffff;
        }
        .report-title {
            font-size: 16px;
            font-weight: 600;
            color: var(--accent);
            margin: 0 0 4px 0;
        }
        .header-meta {
            font-size: 12.5px;
            color: #cbd5e1;
        }
        .brand-badge {
            background: rgba(212, 160, 23, 0.15);
            border: 1px solid var(--accent);
            padding: 8px 16px;
            border-radius: 10px;
            text-align: center;
        }
        .brand-badge .logo {
            font-weight: 900;
            font-size: 16px;
            letter-spacing: 2px;
            color: var(--accent);
        }
        .content {
            padding: 32px 28px;
        }
        .section-box {
            margin-bottom: 28px;
        }
        .section-title {
            font-size: 15px;
            font-weight: 700;
            color: var(--primary);
            border-right: 4px solid var(--accent);
            padding-right: 12px;
            margin: 0 0 14px 0;
            display: flex;
            align-items: center;
        }
        .grid-2 {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 16px;
            background: #f8fafc;
            padding: 16px;
            border-radius: 12px;
            border: 1px solid var(--border);
        }
        .meta-item strong {
            color: var(--primary);
            font-size: 13px;
        }
        .meta-item span {
            color: var(--text-main);
            font-size: 13.5px;
        }
        .badge {
            display: inline-block;
            padding: 3px 10px;
            border-radius: 9999px;
            font-size: 12px;
            font-weight: 600;
        }
        .badge-danger { background: #fee2e2; color: #991b1b; }
        .badge-warning { background: #fef3c7; color: #92400e; }
        .badge-success { background: #d1fae5; color: #065f46; }
        .summary-card {
            background: #f8fafc;
            border-right: 4px solid var(--primary);
            padding: 16px 20px;
            border-radius: 8px;
            font-size: 14px;
            color: #334155;
            white-space: pre-line;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 13px;
            margin-top: 8px;
        }
        th {
            background: #f1f5f9;
            color: var(--primary);
            padding: 10px 12px;
            text-align: right;
            border-bottom: 2px solid var(--border);
        }
        td {
            padding: 10px 12px;
            border-bottom: 1px solid var(--border);
            color: var(--text-main);
        }
        .hash-code {
            font-family: monospace;
            font-size: 11px;
            background: #e2e8f0;
            padding: 2px 6px;
            border-radius: 4px;
            word-break: break-all;
        }
        .rec-item {
            display: flex;
            align-items: flex-start;
            gap: 10px;
            margin-bottom: 10px;
            background: #f8fafc;
            padding: 10px 14px;
            border-radius: 8px;
            font-size: 13.5px;
        }
        .rec-item span {
            color: var(--accent);
            font-weight: bold;
        }
        .footer-stamp {
            background: #0f172a;
            color: #e2e8f0;
            padding: 24px;
            border-radius: 12px;
            margin-top: 36px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 16px;
        }
        .stamp-info h4 {
            margin: 0 0 4px 0;
            color: #fff;
            font-size: 15px;
        }
        .stamp-info p {
            margin: 0;
            font-size: 12.5px;
            color: #94a3b8;
        }
        .verification-code {
            font-family: monospace;
            font-size: 11.5px;
            color: var(--accent);
            background: rgba(255, 255, 255, 0.05);
            padding: 8px 12px;
            border-radius: 6px;
            border: 1px dashed rgba(212, 160, 23, 0.4);
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header-banner">
            <div>
                <div class="system-title">${report.systemName}</div>
                <div class="report-title">${report.title}</div>
                <div class="header-meta">
                    رقم القضية: <strong>${report.caseNumber}</strong> &nbsp;|&nbsp;
                    تاريخ الإصدار: <strong>${report.generatedDate}</strong>
                </div>
            </div>
            <div class="brand-badge">
                <div class="logo">${report.systemLogoText}</div>
                <div style="font-size: 10px; color: #e2e8f0;">OFFICIAL CERTIFIED</div>
            </div>
        </div>

        <div class="content">
            <!-- Case & Subject Details -->
            <div class="section-box">
                <div class="section-title">1. بيانات القضية والطرف المعني</div>
                <div class="grid-2">
                    <div class="meta-item">
                        <strong>العميل / المؤسسة:</strong>
                        <span>${report.clientName}</span>
                    </div>
                    <div class="meta-item">
                        <strong>رقم الهاتف:</strong>
                        <span dir="ltr">${report.clientPhone.ifBlank { "غير مسجل" }}</span>
                    </div>
                    <div class="meta-item">
                        <strong>نوع التهديد:</strong>
                        <span>${report.threatType}</span>
                    </div>
                    <div class="meta-item">
                        <strong>درجة الأولوية:</strong>
                        <span class="badge ${if (report.priority.contains("عاجل") || report.priority.contains("حرجة")) "badge-danger" else "badge-warning"}">${report.priority}</span>
                    </div>
                    <div class="meta-item">
                        <strong>حالة القضية:</strong>
                        <span class="badge badge-success">${report.status}</span>
                    </div>
                    <div class="meta-item">
                        <strong>موعد التسليم:</strong>
                        <span>${report.dueDate}</span>
                    </div>
                </div>
            </div>

            <!-- Executive Summary -->
            <div class="section-box">
                <div class="section-title">2. الملخص التنفيذي</div>
                <div class="summary-card">${report.executiveSummary}</div>
            </div>

            <!-- Evidence Chain of Custody -->
            <div class="section-box">
                <div class="section-title">3. سجل المرفقات والأدلة الرقمية (Chain of Custody)</div>
                ${if (report.evidenceList.isEmpty()) {
                    "<p style='color: var(--text-muted); font-size: 13.5px;'>لم يتم تسجيل مرفقات رقمية مباشرة في هذا التقرير.</p>"
                } else {
                    """<table>
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>اسم الملف / الدليل</th>
                                <th>النوع</th>
                                <th>الحجم</th>
                                <th>بصمة SHA-256 للتحقق</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${report.evidenceList.mapIndexed { i, ev ->
                                """<tr>
                                    <td>${i + 1}</td>
                                    <td><strong>${ev.name}</strong><br><small style="color: var(--text-muted);">${ev.originalFilename}</small></td>
                                    <td>${ev.fileType}</td>
                                    <td>${ev.fileSizeFormatted}</td>
                                    <td><span class="hash-code">${ev.sha256Hash.ifBlank { "غير محسوب" }}</span></td>
                                </tr>"""
                            }.joinToString("")}
                        </tbody>
                    </table>"""
                }}
            </div>

            <!-- Technical Findings -->
            <div class="section-box">
                <div class="section-title">4. التحليل التقني والنتائج</div>
                <div class="summary-card" style="border-right-color: var(--accent);">${report.technicalAnalysis.ifBlank { report.finalOutcome }}</div>
            </div>

            <!-- Recommendations -->
            <div class="section-box">
                <div class="section-title">5. التوصيات والإجراءات الفنية الموصى بها</div>
                ${report.securityRecommendations.mapIndexed { idx, rec ->
                    """<div class="rec-item">
                        <span>[${idx + 1}]</span>
                        <div>${rec}</div>
                    </div>"""
                }.joinToString("")}
            </div>

            <!-- Financial Summary if available -->
            ${if (report.paymentsSummary != null) {
                """<div class="section-box">
                    <div class="section-title">6. ملخص المستحقات والتحصيل المالي</div>
                    <div class="grid-2">
                        <div class="meta-item">
                            <strong>إجمالي الأتعاب:</strong>
                            <span>${report.paymentsSummary.totalAmount} ${report.paymentsSummary.currency}</span>
                        </div>
                        <div class="meta-item">
                            <strong>المبلغ المسدد:</strong>
                            <span style="color: var(--success); font-weight: bold;">${report.paymentsSummary.paidAmount} ${report.paymentsSummary.currency}</span>
                        </div>
                        <div class="meta-item">
                            <strong>المتبقي:</strong>
                            <span style="color: var(--danger); font-weight: bold;">${report.paymentsSummary.remainingAmount} ${report.paymentsSummary.currency}</span>
                        </div>
                        <div class="meta-item">
                            <strong>حالة السداد:</strong>
                            <span class="badge ${if (report.paymentsSummary.remainingAmount <= 0) "badge-success" else "badge-warning"}">${report.paymentsSummary.paymentStatus}</span>
                        </div>
                    </div>
                </div>"""
            } else ""}

            <!-- Official Stamp -->
            <div class="footer-stamp">
                <div class="stamp-info">
                    <h4>الخبير المعتمد: ${report.investigatorName}</h4>
                    <p>${report.investigatorTitle}</p>
                    <p>هاتف: ${report.contactPhone} &nbsp;|&nbsp; بريد: ${report.contactEmail}</p>
                </div>
                <div>
                    <div style="font-size: 11px; color: #94a3b8; margin-bottom: 4px;">بصمة التحقق الرقمية الصادرة عن المنظومة:</div>
                    <div class="verification-code">$verificationHash</div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>"""
        outputFile.writeText(html, Charsets.UTF_8)
    }

    // ==========================================
    // 4. TXT GENERATION (Cryptographic Plain Text)
    // ==========================================
    private fun generateTxt(outputFile: File, report: FullForensicReport) {
        val verificationHash = if (report.digitalVerificationHash.isNotBlank()) report.digitalVerificationHash else generateVerificationHash(report)
        val sb = StringBuilder()
        sb.appendLine("================================================================================")
        sb.appendLine("                     ${report.systemName}")
        sb.appendLine("                    ${report.systemLogoText} - OFFICIAL DOSSIER")
        sb.appendLine("================================================================================")
        sb.appendLine("عنوان التقرير : ${report.title}")
        sb.appendLine("رقم القضية    : ${report.caseNumber}")
        sb.appendLine("تاريخ الإصدار : ${report.generatedDate}")
        sb.appendLine("المحقق المعتمد: ${report.investigatorName} (${report.investigatorTitle})")
        sb.appendLine("بيانات التواصل: ${report.contactPhone} | ${report.contactEmail}")
        sb.appendLine("--------------------------------------------------------------------------------")
        sb.appendLine("1. بيانات القضية والطرف المعني:")
        sb.appendLine("   - العميل      : ${report.clientName}")
        sb.appendLine("   - الهاتف      : ${report.clientPhone}")
        sb.appendLine("   - البريد      : ${report.clientEmail}")
        sb.appendLine("   - نوع التهديد : ${report.threatType}")
        sb.appendLine("   - درجة الخطر  : ${report.priority}")
        sb.appendLine("   - الحالة      : ${report.status}")
        sb.appendLine("   - موعد التسليم: ${report.dueDate}")
        sb.appendLine("--------------------------------------------------------------------------------")
        sb.appendLine("2. الملخص التنفيذي:")
        sb.appendLine("   ${report.executiveSummary}")
        sb.appendLine("--------------------------------------------------------------------------------")
        sb.appendLine("3. التحليل الفني والتقني:")
        sb.appendLine("   ${report.technicalAnalysis.ifBlank { report.finalOutcome }}")
        sb.appendLine("--------------------------------------------------------------------------------")
        sb.appendLine("4. سجل الأدلة الرقمية وسلسلة الحيازة (Chain of Custody):")
        if (report.evidenceList.isEmpty()) {
            sb.appendLine("   (لا توجد مرفقات رقمية خارجية مباشرة مسجلة)")
        } else {
            report.evidenceList.forEachIndexed { idx, ev ->
                sb.appendLine("   [${idx + 1}] ${ev.name} | نوع: ${ev.fileType} | الحجم: ${ev.fileSizeFormatted}")
                sb.appendLine("       MD5    : ${ev.md5Hash}")
                sb.appendLine("       SHA-256: ${ev.sha256Hash}")
            }
        }
        sb.appendLine("--------------------------------------------------------------------------------")
        sb.appendLine("5. التوصيات والإجراءات الفنية:")
        report.securityRecommendations.forEachIndexed { idx, rec ->
            sb.appendLine("   [${idx + 1}] $rec")
        }
        if (report.paymentsSummary != null) {
            sb.appendLine("--------------------------------------------------------------------------------")
            sb.appendLine("6. الملخص المالي:")
            sb.appendLine("   - الإجمالي : ${report.paymentsSummary.totalAmount} ${report.paymentsSummary.currency}")
            sb.appendLine("   - المسدد   : ${report.paymentsSummary.paidAmount} ${report.paymentsSummary.currency}")
            sb.appendLine("   - المتبقي  : ${report.paymentsSummary.remainingAmount} ${report.paymentsSummary.currency}")
            sb.appendLine("   - الحالة   : ${report.paymentsSummary.paymentStatus}")
        }
        sb.appendLine("================================================================================")
        sb.appendLine("بصمة التحقق التشفيرية المعتمدة للمستند:")
        sb.appendLine("SHA-256 VERIFICATION HASH: $verificationHash")
        sb.appendLine("================================================================================")

        outputFile.writeText(sb.toString(), Charsets.UTF_8)
    }

    // ==========================================
    // UTILITIES
    // ==========================================
    private fun calculateSha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { stream ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (stream.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    private fun generateVerificationHash(report: FullForensicReport): String {
        val raw = "${report.reportId}|${report.caseNumber}|${report.clientName}|${report.generatedDate}|${report.investigatorName}"
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(raw.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 -> "%.2f ميجابايت".format(bytes.toDouble() / (1024 * 1024))
            bytes >= 1024 -> "%.1f كيلوبايت".format(bytes.toDouble() / 1024)
            else -> "$bytes بايت"
        }
    }

    private fun wrapText(text: String, maxCharsPerLine: Int): List<String> {
        val words = text.split("\\s+".toRegex())
        val lines = mutableListOf<String>()
        var currentLine = StringBuilder()

        for (word in words) {
            if (currentLine.length + word.length + 1 > maxCharsPerLine) {
                if (currentLine.isNotEmpty()) lines.add(currentLine.toString())
                currentLine = StringBuilder(word)
            } else {
                if (currentLine.isNotEmpty()) currentLine.append(" ")
                currentLine.append(word)
            }
        }
        if (currentLine.isNotEmpty()) lines.add(currentLine.toString())
        return lines
    }
}
