package com.example.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

data class DiscoveredSheet(
    val sheetId: String,
    val sheetName: String
)

data class SpreadsheetDiagnosticReport(
    val spreadsheetId: String,
    val isAccessible: Boolean,
    val isReadOnly: Boolean = true,
    val accessMode: String = "المصدر للقراءة فقط (READ-ONLY)",
    val statusMessage: String,
    val discoveredSheets: List<DiscoveredSheet> = emptyList(),
    val sampleHeaders: List<String> = emptyList(),
    val sampleRowCount: Int = 0
)

data class ParsedRequestRow(
    val rowId: String,
    val clientName: String,
    val clientPhone: String,
    val clientEmail: String,
    val description: String,
    val urgency: String,
    val receivedAt: String,
    val internalNotes: String,
    val rawDataJson: String,
    val additionalFieldsJson: String
)

data class SheetReadResult(
    val sheetName: String,
    val rowCount: Int,
    val columnCount: Int,
    val rows: List<ParsedRequestRow>,
    val detectedHeaders: List<String>,
    val rawTable: List<Map<String, String>> = emptyList()
)

class GoogleSheetsPublicService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    companion object {
        private const val TAG = "SheetsPublicService"

        /**
         * Extracts spreadsheet ID from any Google Sheets link format.
         */
        fun extractSpreadsheetId(url: String): String {
            val trimmed = url.trim()
            val matcher1 = Pattern.compile("/spreadsheets/(?:u/\\d+/)?d/(?:e/)?([a-zA-Z0-9-_]+)").matcher(trimmed)
            if (matcher1.find()) {
                return matcher1.group(1) ?: trimmed
            }
            val matcher2 = Pattern.compile("/d/([a-zA-Z0-9-_]+)").matcher(trimmed)
            if (matcher2.find()) {
                return matcher2.group(1) ?: trimmed
            }
            val matcher3 = Pattern.compile("[?&]id=([a-zA-Z0-9-_]+)").matcher(trimmed)
            if (matcher3.find()) {
                return matcher3.group(1) ?: trimmed
            }
            val matcher4 = Pattern.compile("key=([a-zA-Z0-9-_]+)").matcher(trimmed)
            if (matcher4.find()) {
                return matcher4.group(1) ?: trimmed
            }
            // If user directly pasted the alphanumeric ID
            if (trimmed.matches(Regex("^[a-zA-Z0-9-_]{15,}$"))) {
                return trimmed
            }
            return trimmed
        }

        /**
         * Extracts initial sheet gid from URL query or fragment if present.
         */
        fun extractSheetGid(url: String): String? {
            val gidPattern = Pattern.compile("[#?&]gid=([0-9]+)")
            val matcher = gidPattern.matcher(url)
            return if (matcher.find()) matcher.group(1) else null
        }
    }

    /**
     * Extracts the real document title from the public Google Sheet HTML.
     */
    suspend fun extractDocumentTitle(publicUrl: String): String = withContext(Dispatchers.IO) {
        try {
            val spreadsheetId = extractSpreadsheetId(publicUrl)
            if (spreadsheetId.isBlank()) return@withContext ""

            val htmlUrl = "https://docs.google.com/spreadsheets/d/$spreadsheetId/htmlview"
            val req = Request.Builder()
                .url(htmlUrl)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .build()
            val resp = client.newCall(req).execute()
            val html = resp.body?.string() ?: ""

            // 1. Check meta og:title
            val mOg = Pattern.compile("<meta\\s+property=[\"']og:title[\"']\\s+content=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE).matcher(html)
            if (mOg.find()) {
                val t = mOg.group(1)?.replace("- Google Sheets", "")?.replace("- جداول بيانات Google", "")?.trim()
                if (!t.isNullOrBlank()) return@withContext t
            }

            // 2. Check doc-title div
            val docTitlePattern = Pattern.compile("<div[^>]*id=[\"']doc-title[\"'][^>]*>.*?<span[^>]*class=[\"']name[\"'][^>]*>([^<]+)</span>", Pattern.CASE_INSENSITIVE or Pattern.DOTALL)
            val m0 = docTitlePattern.matcher(html)
            if (m0.find()) {
                val full = m0.group(1)?.trim() ?: ""
                val docName = if (full.contains(":")) full.substringBefore(":").trim() else full
                if (docName.isNotBlank()) return@withContext docName
            }

            // 3. Check HTML <title> tag
            val mTitle = Pattern.compile("<title>([^<]+)</title>", Pattern.CASE_INSENSITIVE).matcher(html)
            if (mTitle.find()) {
                val t = mTitle.group(1)?.replace("- Google Sheets", "")?.replace("- جداول بيانات Google", "")?.trim()
                if (!t.isNullOrBlank()) return@withContext t
            }
            ""
        } catch (e: Exception) {
            Log.w(TAG, "Failed to extract document title", e)
            ""
        }
    }

    /**
     * Tests public readability of the Google Sheet URL without requiring Google OAuth.
     */
    suspend fun testConnection(publicUrl: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val spreadsheetId = extractSpreadsheetId(publicUrl)
            if (spreadsheetId.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("رابط Google Sheet غير صالح"))
            }

            val testUrl = "https://docs.google.com/spreadsheets/d/$spreadsheetId/gviz/tq?tqx=out:json&tq=limit%201"
            val request = Request.Builder()
                .url(testUrl)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .build()

            val response = client.newCall(request).execute()
            val code = response.code
            val body = response.body?.string() ?: ""

            val isGvizOk = code in 200..299 && (body.contains("google.visualization") || body.contains("table") || body.contains("\"status\":\"ok\""))

            if (code == 404) {
                return@withContext Result.failure(Exception("الملف غير موجود (404). يرجى التأكد من صحة الرابط."))
            } else if (code == 401 || code == 403 || body.contains("Sign in") || body.contains("accounts.google.com")) {
                return@withContext Result.failure(Exception("الملف خاص ويتطلب إذن وصول. يرجى تفعيل خيار 'أي شخص لديه الرابط يمكنه العرض' (Anyone with link can view)."))
            }

            if (!isGvizOk) {
                // Fallback test via htmlview
                val htmlUrl = "https://docs.google.com/spreadsheets/d/$spreadsheetId/htmlview"
                val htmlReq = Request.Builder().url(htmlUrl).build()
                val htmlResp = client.newCall(htmlReq).execute()
                if (!htmlResp.isSuccessful) {
                    return@withContext Result.failure(Exception("تعذر القراءة من الرابط. كود الاستجابة: $code"))
                }
            }

            // Discover actual sheets and title
            val docTitle = extractDocumentTitle(publicUrl)
            val discoveredResult = discoverSheets(publicUrl)
            val discoveredTabs = discoveredResult.getOrNull() ?: emptyList()

            val titleDesc = if (docTitle.isNotBlank()) "«$docTitle»" else "الملف العام"
            val tabsSummary = if (discoveredTabs.isNotEmpty()) {
                "تم اكتشاف ${discoveredTabs.size} أوراق عمل: ${discoveredTabs.joinToString("، ") { it.sheetName }.take(80)}"
            } else {
                "القراءة متاحة بدون تسجيل دخول."
            }

            Result.success("تم الاتصال بنجاح بـ $titleDesc\n$tabsSummary")
        } catch (e: Exception) {
            Log.e(TAG, "Connection test failed", e)
            Result.failure(Exception("فشل الاتصال: ${e.localizedMessage ?: "تحقق من اتصالك بالإنترنت"}"))
        }
    }

    /**
     * Automatically discovers all sheet tabs inside the public Google Sheet.
     */
    suspend fun discoverSheets(publicUrl: String): Result<List<DiscoveredSheet>> = withContext(Dispatchers.IO) {
        try {
            val spreadsheetId = extractSpreadsheetId(publicUrl)
            val sheets = mutableListOf<DiscoveredSheet>()
            val seenNames = mutableSetOf<String>()
            val seenGids = mutableSetOf<String>()

            fun addSheet(gid: String, name: String) {
                val cleanGid = gid.trim().ifBlank { "0" }
                val cleanName = android.text.Html.fromHtml(name.trim(), android.text.Html.FROM_HTML_MODE_LEGACY).toString().trim()
                if (cleanName.isNotBlank() && !seenNames.contains(cleanName)) {
                    seenNames.add(cleanName)
                    seenGids.add(cleanGid)
                    sheets.add(DiscoveredSheet(sheetId = cleanGid, sheetName = cleanName))
                }
            }

            // Extract initial GID from publicUrl if provided
            val initialGid = extractSheetGid(publicUrl)

            // Step 1: Query htmlview
            val htmlUrl = "https://docs.google.com/spreadsheets/d/$spreadsheetId/htmlview"
            val req1 = Request.Builder()
                .url(htmlUrl)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .build()

            var html = ""
            try {
                val resp1 = client.newCall(req1).execute()
                html = resp1.body?.string() ?: ""
            } catch (e: Exception) {
                Log.w(TAG, "htmlview fetch failed", e)
            }

            fun parseHtmlTabs(content: String) {
                // Regex 0: Doc title <div id="doc-title"><span class="name">Doc : Sheet</span></div>
                val docTitlePattern = Pattern.compile("<div[^>]*id=[\"']doc-title[\"'][^>]*>.*?<span[^>]*class=[\"']name[\"'][^>]*>([^<]+)</span>", Pattern.CASE_INSENSITIVE or Pattern.DOTALL)
                val m0 = docTitlePattern.matcher(content)
                if (m0.find()) {
                    val fullTitle = m0.group(1) ?: ""
                    if (fullTitle.contains(":")) {
                        val subName = fullTitle.substringAfter(":").trim()
                        if (subName.isNotBlank()) {
                            addSheet("0", subName)
                        }
                    }
                }

                // Regex 1: <li id="sheet-button-xxx"><a href="#xxx">Sheet Name</a></li>
                val tabPattern1 = Pattern.compile("<li[^>]*id=[\"']sheet-button-([^\"']+)[\"'][^>]*>.*?<a[^>]*>([^<]+)</a>", Pattern.CASE_INSENSITIVE or Pattern.DOTALL)
                val m1 = tabPattern1.matcher(content)
                while (m1.find()) {
                    val gid = m1.group(1) ?: "0"
                    val name = m1.group(2) ?: ""
                    addSheet(gid, name)
                }

                // Regex 2: <a href="#gid">Tab Name</a>
                val tabPattern2 = Pattern.compile("<a[^>]*href=[\"']#([0-9]+)[\"'][^>]*>([^<]+)</a>", Pattern.CASE_INSENSITIVE)
                val m2 = tabPattern2.matcher(content)
                while (m2.find()) {
                    val gid = m2.group(1) ?: "0"
                    val name = m2.group(2) ?: ""
                    if (!name.contains("Google") && !name.contains("Sheet") && !name.contains("Report") && !name.contains("Terms")) {
                        addSheet(gid, name)
                    }
                }

                // Regex 3: Embedded JSON data {"name": "...", "gid": ...}
                val jsPattern1 = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"gid\"\\s*:\\s*\"?([0-9]+)\"?", Pattern.CASE_INSENSITIVE)
                val m3 = jsPattern1.matcher(content)
                while (m3.find()) {
                    val name = m3.group(1) ?: ""
                    val gid = m3.group(2) ?: "0"
                    addSheet(gid, name)
                }

                val jsPattern2 = Pattern.compile("\"title\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"sheetId\"\\s*:\\s*\"?([0-9]+)\"?", Pattern.CASE_INSENSITIVE)
                val m4 = jsPattern2.matcher(content)
                while (m4.find()) {
                    val name = m4.group(1) ?: ""
                    val gid = m4.group(2) ?: "0"
                    addSheet(gid, name)
                }

                // Regex 4: Bootstrap Data format in /edit: [\"gid\",0,...],null,[[{\"2\":3,\"3\":[2,\"SheetName\"]
                val jsPattern3 = Pattern.compile("\\[\\\\?\"([0-9]+)\\\\?\",\\s*0,[^\\]]*\\].*?\\[2,\\\\?\"([^\\\\\",\\]]+)\\\\?\"\\]", Pattern.CASE_INSENSITIVE)
                val m5 = jsPattern3.matcher(content)
                while (m5.find()) {
                    val gid = m5.group(1) ?: "0"
                    val name = m5.group(2) ?: ""
                    if (name.length in 1..80) {
                        addSheet(gid, name)
                    }
                }
            }

            if (html.isNotBlank()) {
                parseHtmlTabs(html)
            }

            // Step 2: If fewer than 2 sheets found, try /edit
            if (sheets.size < 2) {
                val editUrl = "https://docs.google.com/spreadsheets/d/$spreadsheetId/edit?usp=sharing"
                try {
                    val editReq = Request.Builder()
                        .url(editUrl)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .build()
                    val editResp = client.newCall(editReq).execute()
                    val editHtml = editResp.body?.string() ?: ""
                    if (editHtml.isNotBlank()) {
                        parseHtmlTabs(editHtml)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "edit fetch failed", e)
                }
            }

            // Step 3: If still fewer than 2 sheets found, try pubhtml
            if (sheets.size < 2) {
                val pubhtmlUrl = "https://docs.google.com/spreadsheets/d/$spreadsheetId/pubhtml"
                try {
                    val pubReq = Request.Builder()
                        .url(pubhtmlUrl)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .build()
                    val pubResp = client.newCall(pubReq).execute()
                    val pubHtml = pubResp.body?.string() ?: ""
                    if (pubHtml.isNotBlank()) {
                        parseHtmlTabs(pubHtml)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "pubhtml fetch failed", e)
                }
            }

            // Step 3: If an initial gid was in URL and hasn't been named yet, register it
            if (!initialGid.isNullOrBlank() && !seenGids.contains(initialGid)) {
                addSheet(initialGid, "الورقة الافتراضية ($initialGid)")
            }

            // Step 4: If still completely empty, add default sheet
            if (sheets.isEmpty()) {
                sheets.add(DiscoveredSheet(sheetId = "0", sheetName = "الطلبات (الورقة 1)"))
            }

            Result.success(sheets)
        } catch (e: Exception) {
            Log.e(TAG, "Sheet discovery error", e)
            Result.success(listOf(DiscoveredSheet(sheetId = "0", sheetName = "الطلبات (الورقة 1)")))
        }
    }

    /**
     * Reads all rows from a specific sheet using Google Visualization API.
     * Applies Smart Mapping and optional Custom Column Mapping.
     */
    suspend fun readSheetData(
        publicUrl: String,
        sheetName: String,
        sheetGid: String,
        columnMapping: Map<String, String> = emptyMap()
    ): Result<SheetReadResult> = withContext(Dispatchers.IO) {
        try {
            val spreadsheetId = extractSpreadsheetId(publicUrl)
            val encodedSheet = URLEncoder.encode(sheetName, "UTF-8")

            // Determine URLs to try: primary URL based on gid vs sheet name
            val urlByGid = if (sheetGid.isNotBlank() && sheetGid != "0") {
                "https://docs.google.com/spreadsheets/d/$spreadsheetId/gviz/tq?tqx=out:json&gid=$sheetGid"
            } else null
            val urlByName = "https://docs.google.com/spreadsheets/d/$spreadsheetId/gviz/tq?tqx=out:json&sheet=$encodedSheet"

            val urlsToTry = mutableListOf<String>()
            if (urlByGid != null) {
                urlsToTry.add(urlByGid)
                urlsToTry.add(urlByName)
            } else {
                urlsToTry.add(urlByName)
                urlsToTry.add("https://docs.google.com/spreadsheets/d/$spreadsheetId/gviz/tq?tqx=out:json&gid=0")
            }

            var cleanJson = ""
            var lastError: Exception? = null

            for (attemptUrl in urlsToTry) {
                try {
                    val request = Request.Builder()
                        .url(attemptUrl)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .build()

                    val response = client.newCall(request).execute()
                    val rawBody = response.body?.string() ?: ""

                    val jsonStart = rawBody.indexOf("{")
                    val jsonEnd = rawBody.lastIndexOf("}")
                    if (jsonStart != -1 && jsonEnd != -1) {
                        val candidate = rawBody.substring(jsonStart, jsonEnd + 1)
                        val candidateObj = JSONObject(candidate)
                        val status = candidateObj.optString("status")
                        if (status != "error" && candidateObj.has("table")) {
                            cleanJson = candidate
                            break
                        }
                    }
                } catch (e: Exception) {
                    lastError = e
                }
            }

            if (cleanJson.isBlank()) {
                // FALLBACK: Attempt fetching via /export?format=csv
                val csvGidPart = if (sheetGid.isNotBlank() && sheetGid != "0") "&gid=$sheetGid" else ""
                val csvUrl = "https://docs.google.com/spreadsheets/d/$spreadsheetId/export?format=csv$csvGidPart"
                try {
                    val csvRequest = Request.Builder()
                        .url(csvUrl)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .build()
                    val csvResponse = client.newCall(csvRequest).execute()
                    val csvBody = csvResponse.body?.string() ?: ""
                    if (csvResponse.isSuccessful && csvBody.isNotBlank() && (csvBody.contains(",") || csvBody.contains("\n"))) {
                        val csvResult = parseCsvStringToResult(csvBody, sheetName, columnMapping)
                        if (csvResult.isSuccess) {
                            return@withContext csvResult
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "CSV fallback attempt failed", e)
                }

                return@withContext Result.failure(lastError ?: Exception("لم يتم استلام بيانات صالحة من الورقة '$sheetName'"))
            }

            val root = JSONObject(cleanJson)
            val table = root.optJSONObject("table")
                ?: return@withContext Result.failure(Exception("جدول البيانات غير موجود في استجابة Google Sheets"))

            val cols = table.optJSONArray("cols") ?: JSONArray()
            val rows = table.optJSONArray("rows") ?: JSONArray()

            val headers = mutableListOf<String>()
            for (i in 0 until cols.length()) {
                val colObj = cols.optJSONObject(i)
                val label = colObj?.optString("label")?.trim() ?: ""
                headers.add(label)
            }

            // Check if headers were returned in cols, or if row 0 has headers
            var startIndex = 0
            if (headers.all { it.isBlank() } && rows.length() > 0) {
                val firstRowCells = rows.optJSONObject(0)?.optJSONArray("c")
                if (firstRowCells != null) {
                    headers.clear()
                    for (c in 0 until firstRowCells.length()) {
                        val cellObj = firstRowCells.optJSONObject(c)
                        val valStr = cellObj?.optString("v")?.trim() ?: "عمود_${c + 1}"
                        headers.add(valStr)
                    }
                    startIndex = 1
                }
            }

            // Ensure headers list is not empty
            if (headers.isEmpty()) {
                headers.addAll(listOf("الاسم", "الهاتف", "البريد", "الطلب", "التاريخ", "الملاحظات"))
            }

            val parsedRows = mutableListOf<ParsedRequestRow>()
            val rawTableList = mutableListOf<Map<String, String>>()

            // Prepare custom mappings (lowercased for matching)
            val mappedNameCol = columnMapping["clientName"]?.trim()?.lowercase()
            val mappedPhoneCol = columnMapping["clientPhone"]?.trim()?.lowercase()
            val mappedEmailCol = columnMapping["clientEmail"]?.trim()?.lowercase()
            val mappedDescCol = columnMapping["description"]?.trim()?.lowercase()
            val mappedUrgencyCol = columnMapping["urgency"]?.trim()?.lowercase()
            val mappedDateCol = columnMapping["receivedAt"]?.trim()?.lowercase()
            val mappedNotesCol = columnMapping["internalNotes"]?.trim()?.lowercase()

            for (r in startIndex until rows.length()) {
                val rowObj = rows.optJSONObject(r) ?: continue
                val cArray = rowObj.optJSONArray("c") ?: continue

                val rowMap = mutableMapOf<String, String>()
                var isRowBlank = true

                for (colIdx in 0 until headers.size) {
                    val headerName = headers.getOrElse(colIdx) { "عمود_${colIdx + 1}" }
                    val cellObj = cArray.optJSONObject(colIdx)
                    val cellVal = if (cellObj != null) {
                        val fVal = cellObj.optString("f")
                        val vVal = cellObj.optString("v")
                        when {
                            fVal.isNotBlank() && fVal != "null" -> fVal
                            vVal.isNotBlank() && vVal != "null" -> vVal
                            else -> ""
                        }
                    } else ""

                    if (cellVal.isNotBlank()) isRowBlank = false
                    rowMap[headerName] = cellVal.trim()
                }

                // Skip completely empty rows
                if (isRowBlank) continue

                rawTableList.add(rowMap)

                var clientName = ""
                var clientPhone = ""
                var clientEmail = ""
                var description = ""
                var urgency = "متوسطة"
                var receivedAt = ""
                var internalNotes = ""

                val additionalFields = mutableMapOf<String, String>()

                for ((header, value) in rowMap) {
                    val norm = header.trim().lowercase()

                    // Check custom mapping first
                    if (!mappedNameCol.isNullOrBlank() && norm == mappedNameCol && clientName.isBlank()) {
                        clientName = value
                    } else if (!mappedPhoneCol.isNullOrBlank() && norm == mappedPhoneCol && clientPhone.isBlank()) {
                        clientPhone = value
                    } else if (!mappedEmailCol.isNullOrBlank() && norm == mappedEmailCol && clientEmail.isBlank()) {
                        clientEmail = value
                    } else if (!mappedDescCol.isNullOrBlank() && norm == mappedDescCol && description.isBlank()) {
                        description = value
                    } else if (!mappedUrgencyCol.isNullOrBlank() && norm == mappedUrgencyCol) {
                        urgency = normalizeUrgency(value)
                    } else if (!mappedDateCol.isNullOrBlank() && norm == mappedDateCol && receivedAt.isBlank()) {
                        receivedAt = value
                    } else if (!mappedNotesCol.isNullOrBlank() && norm == mappedNotesCol && internalNotes.isBlank()) {
                        internalNotes = value
                    }
                    // Otherwise check smart auto-detection
                    else if (isNameHeader(norm) && clientName.isBlank()) {
                        clientName = value
                    } else if (isPhoneHeader(norm) && clientPhone.isBlank()) {
                        clientPhone = value
                    } else if (isEmailHeader(norm) && clientEmail.isBlank()) {
                        clientEmail = value
                    } else if (isDescriptionHeader(norm) && description.isBlank()) {
                        description = value
                    } else if (isDateHeader(norm) && receivedAt.isBlank()) {
                        receivedAt = value
                    } else if (isUrgencyHeader(norm)) {
                        urgency = normalizeUrgency(value)
                    } else if (isNotesHeader(norm) && internalNotes.isBlank()) {
                        internalNotes = value
                    } else {
                        if (value.isNotBlank()) {
                            additionalFields[header] = value
                        }
                    }
                }

                // Fallback defaults if blank
                if (clientName.isBlank()) {
                    clientName = "طلب وارد (صف ${r + 1})"
                }
                if (description.isBlank()) {
                    description = additionalFields.values.firstOrNull() ?: "طلب وارد من Google Sheet"
                }
                if (receivedAt.isBlank()) {
                    receivedAt = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US).format(java.util.Date())
                }

                val rawDataJson = JSONObject(rowMap as Map<*, *>).toString()
                val additionalFieldsJson = JSONObject(additionalFields as Map<*, *>).toString()

                parsedRows.add(
                    ParsedRequestRow(
                        rowId = "row_${r + 1}",
                        clientName = clientName,
                        clientPhone = clientPhone,
                        clientEmail = clientEmail,
                        description = description,
                        urgency = urgency,
                        receivedAt = receivedAt,
                        internalNotes = internalNotes,
                        rawDataJson = rawDataJson,
                        additionalFieldsJson = additionalFieldsJson
                    )
                )
            }

            Result.success(
                SheetReadResult(
                    sheetName = sheetName,
                    rowCount = parsedRows.size,
                    columnCount = headers.size,
                    rows = parsedRows,
                    detectedHeaders = headers,
                    rawTable = rawTableList
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error reading sheet data: $sheetName", e)
            Result.failure(Exception("خطأ في قراءة بيانات الورقة '$sheetName': ${e.localizedMessage}"))
        }
    }

    private fun isNameHeader(h: String): Boolean {
        return h in listOf("الاسم", "اسم العميل", "اسم الشخص", "العميل", "صاحب الطلب", "الموكل", "name", "client name", "client", "full name", "fullname", "person", "requester")
    }

    private fun isPhoneHeader(h: String): Boolean {
        return h in listOf("الهاتف", "رقم الهاتف", "الجوال", "رقم الجوال", "الموبايل", "phone", "mobile", "tel", "cell", "whatsapp", "phone number", "mobile number", "contact")
    }

    private fun isEmailHeader(h: String): Boolean {
        return h in listOf("البريد", "البريد الإلكتروني", "الايميل", "الإيميل", "email", "mail", "e-mail", "email address")
    }

    private fun isDescriptionHeader(h: String): Boolean {
        return h in listOf("الطلب", "تفاصيل الطلب", "المشكلة", "الوصف", "تفاصيل المشكلة", "محتوى الطلب", "بيان الحالة", "request", "description", "problem", "details", "issue", "case details", "summary")
    }

    private fun isDateHeader(h: String): Boolean {
        return h in listOf("التاريخ", "تاريخ الطلب", "تاريخ الاستلام", "الوقت", "date", "received at", "timestamp", "created at", "submission date", "time", "submitted at")
    }

    private fun isUrgencyHeader(h: String): Boolean {
        return h in listOf("الأولوية", "درجة الأهمية", "الاستعجال", "مستوى الخطر", "الخطر", "urgency", "priority", "importance", "risk level", "level")
    }

    private fun isNotesHeader(h: String): Boolean {
        return h in listOf("الملاحظات", "ملاحظات", "ملاحظة", "تعليق", "notes", "internal notes", "comments", "remarks", "extra")
    }

    private fun normalizeUrgency(u: String): String {
        val lower = u.trim().lowercase()
        return when {
            lower.contains("حرج") || lower.contains("critical") || lower.contains("طارئ") || lower.contains("urgent") -> "حرجة"
            lower.contains("عال") || lower.contains("high") -> "عالية"
            lower.contains("منخفض") || lower.contains("low") -> "منخفضة"
            else -> "متوسطة"
        }
    }

    /**
     * Parses raw CSV string into a structured SheetReadResult adhering to RFC-4180 rules.
     */
    fun parseCsvStringToResult(
        csvContent: String,
        sheetName: String = "بيانات CSV",
        columnMapping: Map<String, String> = emptyMap()
    ): Result<SheetReadResult> {
        return try {
            val lines = parseCsvLines(csvContent)
            if (lines.isEmpty()) {
                return Result.failure(Exception("محتوى CSV فارغ"))
            }

            val headers = lines[0].map { it.trim() }
            val parsedRows = mutableListOf<ParsedRequestRow>()
            val rawTableList = mutableListOf<Map<String, String>>()

            val mappedNameCol = columnMapping["clientName"]?.trim()?.lowercase()
            val mappedPhoneCol = columnMapping["clientPhone"]?.trim()?.lowercase()
            val mappedEmailCol = columnMapping["clientEmail"]?.trim()?.lowercase()
            val mappedDescCol = columnMapping["description"]?.trim()?.lowercase()
            val mappedUrgencyCol = columnMapping["urgency"]?.trim()?.lowercase()
            val mappedDateCol = columnMapping["receivedAt"]?.trim()?.lowercase()
            val mappedNotesCol = columnMapping["internalNotes"]?.trim()?.lowercase()

            for (r in 1 until lines.size) {
                val rowCells = lines[r]
                if (rowCells.all { it.isBlank() }) continue

                val rowMap = mutableMapOf<String, String>()
                for (c in headers.indices) {
                    val header = headers[c]
                    val cellVal = rowCells.getOrNull(c)?.trim() ?: ""
                    rowMap[header] = cellVal
                }

                rawTableList.add(rowMap)

                var clientName = ""
                var clientPhone = ""
                var clientEmail = ""
                var description = ""
                var urgency = "متوسطة"
                var receivedAt = ""
                var internalNotes = ""
                val additionalFields = mutableMapOf<String, String>()

                for ((header, value) in rowMap) {
                    val norm = header.lowercase()
                    if (!mappedNameCol.isNullOrBlank() && norm == mappedNameCol && clientName.isBlank()) clientName = value
                    else if (!mappedPhoneCol.isNullOrBlank() && norm == mappedPhoneCol && clientPhone.isBlank()) clientPhone = value
                    else if (!mappedEmailCol.isNullOrBlank() && norm == mappedEmailCol && clientEmail.isBlank()) clientEmail = value
                    else if (!mappedDescCol.isNullOrBlank() && norm == mappedDescCol && description.isBlank()) description = value
                    else if (!mappedUrgencyCol.isNullOrBlank() && norm == mappedUrgencyCol) urgency = normalizeUrgency(value)
                    else if (!mappedDateCol.isNullOrBlank() && norm == mappedDateCol && receivedAt.isBlank()) receivedAt = value
                    else if (!mappedNotesCol.isNullOrBlank() && norm == mappedNotesCol && internalNotes.isBlank()) internalNotes = value
                    else if (isNameHeader(norm) && clientName.isBlank()) clientName = value
                    else if (isPhoneHeader(norm) && clientPhone.isBlank()) clientPhone = value
                    else if (isEmailHeader(norm) && clientEmail.isBlank()) clientEmail = value
                    else if (isDescriptionHeader(norm) && description.isBlank()) description = value
                    else if (isDateHeader(norm) && receivedAt.isBlank()) receivedAt = value
                    else if (isUrgencyHeader(norm)) urgency = normalizeUrgency(value)
                    else if (isNotesHeader(norm) && internalNotes.isBlank()) internalNotes = value
                    else if (value.isNotBlank()) additionalFields[header] = value
                }

                if (clientName.isBlank()) clientName = "طلب وارد (صف $r)"
                if (description.isBlank()) description = additionalFields.values.firstOrNull() ?: "طلب مستورد من CSV"
                if (receivedAt.isBlank()) {
                    receivedAt = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US).format(java.util.Date())
                }

                parsedRows.add(
                    ParsedRequestRow(
                        rowId = "row_$r",
                        clientName = clientName,
                        clientPhone = clientPhone,
                        clientEmail = clientEmail,
                        description = description,
                        urgency = urgency,
                        receivedAt = receivedAt,
                        internalNotes = internalNotes,
                        rawDataJson = JSONObject(rowMap as Map<*, *>).toString(),
                        additionalFieldsJson = JSONObject(additionalFields as Map<*, *>).toString()
                    )
                )
            }

            Result.success(
                SheetReadResult(
                    sheetName = sheetName,
                    rowCount = parsedRows.size,
                    columnCount = headers.size,
                    rows = parsedRows,
                    detectedHeaders = headers,
                    rawTable = rawTableList
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Splits CSV lines while respecting quotes and escaped commas.
     */
    private fun parseCsvLines(csv: String): List<List<String>> {
        val result = mutableListOf<List<String>>()
        val currentLine = mutableListOf<String>()
        val currentCell = StringBuilder()
        var insideQuotes = false

        var i = 0
        while (i < csv.length) {
            val c = csv[i]
            when {
                c == '\"' -> {
                    if (insideQuotes && i + 1 < csv.length && csv[i + 1] == '\"') {
                        currentCell.append('\"')
                        i++ // skip escaped quote
                    } else {
                        insideQuotes = !insideQuotes
                    }
                }
                c == ',' && !insideQuotes -> {
                    currentLine.add(currentCell.toString())
                    currentCell.clear()
                }
                (c == '\n' || c == '\r') && !insideQuotes -> {
                    if (c == '\r' && i + 1 < csv.length && csv[i + 1] == '\n') {
                        i++
                    }
                    currentLine.add(currentCell.toString())
                    currentCell.clear()
                    if (currentLine.any { it.isNotBlank() }) {
                        result.add(ArrayList(currentLine))
                    }
                    currentLine.clear()
                }
                else -> {
                    currentCell.append(c)
                }
            }
            i++
        }

        if (currentCell.isNotEmpty() || currentLine.isNotEmpty()) {
            currentLine.add(currentCell.toString())
            if (currentLine.any { it.isNotBlank() }) {
                result.add(currentLine)
            }
        }
        return result
    }

    /**
     * Full diagnostic run for a Google Sheet URL.
     * Extracts Spreadsheet ID, checks accessibility, discovers all sheets,
     * reads headers, row counts, and verifies that it is strictly READ-ONLY.
     */
    suspend fun runFullSpreadsheetDiagnostic(publicUrl: String): SpreadsheetDiagnosticReport = withContext(Dispatchers.IO) {
        val spreadsheetId = extractSpreadsheetId(publicUrl)
        if (spreadsheetId.isBlank()) {
            return@withContext SpreadsheetDiagnosticReport(
                spreadsheetId = "",
                isAccessible = false,
                statusMessage = "رابط Google Sheet غير صالح، تعذر استخراج Spreadsheet ID"
            )
        }

        // Test connectivity
        val connResult = testConnection(publicUrl)
        val isAccessible = connResult.isSuccess
        val statusMsg = connResult.getOrElse { it.message ?: "فشل الاتصال" }

        // Discover sheets
        val sheetsResult = discoverSheets(publicUrl)
        val sheets = sheetsResult.getOrDefault(emptyList())

        var sampleHeaders = emptyList<String>()
        var sampleRowCount = 0

        if (isAccessible && sheets.isNotEmpty()) {
            val firstSheet = sheets.first()
            val readResult = readSheetData(publicUrl, firstSheet.sheetName, firstSheet.sheetId)
            readResult.onSuccess {
                sampleHeaders = it.detectedHeaders
                sampleRowCount = it.rowCount
            }
        }

        SpreadsheetDiagnosticReport(
            spreadsheetId = spreadsheetId,
            isAccessible = isAccessible,
            isReadOnly = true,
            accessMode = "المصدر للقراءة فقط (READ-ONLY) - التعديل المباشر يتطلب صلاحيات API خاصة",
            statusMessage = statusMsg,
            discoveredSheets = sheets,
            sampleHeaders = sampleHeaders,
            sampleRowCount = sampleRowCount
        )
    }
}
