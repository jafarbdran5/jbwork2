package com.example.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URLEncoder
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import java.util.zip.ZipInputStream

data class DiscoveredSheet(
    val sheetId: String,
    val sheetName: String,
    val index: Int = 0,
    val sheetType: String = "GRID",
    val hidden: Boolean = false,
    val rowCount: Int = 0,
    val columnCount: Int = 0
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
     * Automatically discovers all sheet tabs inside the public Google Sheet metadata.
     * Extracts all sheets without requiring Google login, manual GID, or fixed sheet names.
     * Uses streaming XLSX workbook metadata analysis, correlated with HTML/JSON bootstrap extraction.
     */
    suspend fun discoverSheets(publicUrl: String): Result<List<DiscoveredSheet>> = withContext(Dispatchers.IO) {
        try {
            val spreadsheetId = extractSpreadsheetId(publicUrl)
            if (spreadsheetId.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("الرابط غير متاح - رابط Google Sheet غير صالح"))
            }

            val discoveredList = mutableListOf<DiscoveredSheet>()
            val nameToGidMap = mutableMapOf<String, String>()
            val htmlDiscoveredSheets = mutableListOf<DiscoveredSheet>()
            var connectionError: Exception? = null

            // Helper to clean HTML/XML encoded text
            fun cleanSheetName(raw: String): String {
                val decoded = android.text.Html.fromHtml(raw.trim(), android.text.Html.FROM_HTML_MODE_LEGACY).toString().trim()
                return decoded.replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&apos;", "'")
            }

            // Step 1: Query htmlview / pubhtml / edit pages to extract all sheet GIDs and tab names
            val urlsToQuery = mutableListOf<String>()
            if (publicUrl.contains("/d/e/")) {
                urlsToQuery.add("https://docs.google.com/spreadsheets/d/e/$spreadsheetId/pubhtml")
                urlsToQuery.add("https://docs.google.com/spreadsheets/d/e/$spreadsheetId/htmlview")
            } else {
                urlsToQuery.add("https://docs.google.com/spreadsheets/d/$spreadsheetId/htmlview")
                urlsToQuery.add("https://docs.google.com/spreadsheets/d/$spreadsheetId/edit?usp=sharing")
            }

            for (pageUrl in urlsToQuery) {
                try {
                    val req = Request.Builder()
                        .url(pageUrl)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                        .build()
                    val resp = client.newCall(req).execute()
                    val html = resp.body?.string() ?: ""

                    // Check access restrictions in HTML
                    if (resp.code == 404) {
                        return@withContext Result.failure(Exception("الرابط غير متاح - ملف Google Sheet غير موجود (404)"))
                    }
                    if (resp.code == 401 || resp.code == 403 || html.contains("accounts.google.com/ServiceLogin") || html.contains("Sign in - Google Accounts")) {
                        return@withContext Result.failure(Exception("المصدر غير عام - يرجى ضبط مشاركة الملف على «أي شخص لديه الرابط يمكنه العرض»"))
                    }

                    // Pattern 0: items.push({name: "SheetName", ... gid: "0"}) standard in htmlview
                    val itemsPushPattern = Pattern.compile("name:\\s*[\"']([^\"']+)[\"'][^}]*gid:\\s*[\"']([0-9]+)[\"']", Pattern.CASE_INSENSITIVE)
                    val mPush = itemsPushPattern.matcher(html)
                    var pushIdx = 0
                    while (mPush.find()) {
                        val name = cleanSheetName(mPush.group(1) ?: "")
                        val gid = mPush.group(2) ?: "$pushIdx"
                        if (name.isNotBlank()) {
                            nameToGidMap[name] = gid
                            if (htmlDiscoveredSheets.none { it.sheetName == name }) {
                                htmlDiscoveredSheets.add(
                                    DiscoveredSheet(
                                        sheetId = gid,
                                        sheetName = name,
                                        index = pushIdx,
                                        sheetType = "GRID",
                                        hidden = false
                                    )
                                )
                            }
                            pushIdx++
                        }
                    }

                    // Pattern 1: <li id="sheet-button-xxx">...<a ...>Sheet Name</a></li> (standard in htmlview / pubhtml)
                    val tabPatternLi = Pattern.compile("<li[^>]*id=[\"']sheet-button-([a-zA-Z0-9_-]+)[\"'][^>]*>(.*?)</li>", Pattern.CASE_INSENSITIVE or Pattern.DOTALL)
                    val mLi = tabPatternLi.matcher(html)
                    var liIdx = 0
                    while (mLi.find()) {
                        val gid = mLi.group(1) ?: "$liIdx"
                        val liContent = mLi.group(2) ?: ""
                        val rawName = liContent.replace(Regex("<[^>]+>"), "").trim()
                        val name = cleanSheetName(rawName)
                        if (name.isNotBlank()) {
                            nameToGidMap[name] = gid
                            if (htmlDiscoveredSheets.none { it.sheetName == name }) {
                                htmlDiscoveredSheets.add(
                                    DiscoveredSheet(
                                        sheetId = gid,
                                        sheetName = name,
                                        index = htmlDiscoveredSheets.size,
                                        sheetType = "GRID",
                                        hidden = false
                                    )
                                )
                            }
                            liIdx++
                        }
                    }

                    // Pattern 2: Bootstrap Chunk in edit page: [0, index, "gid", [{"1": [[0, 0, "SheetName"]
                    val chunkPattern = Pattern.compile("\\[0\\s*,\\s*(\\d+)\\s*,\\s*\\\\?\"([0-9]+)\\\\?\"\\s*,\\s*\\[\\{\\\\?\"1\\\\?\":\\s*\\[\\[\\s*0\\s*,\\s*0\\s*,\\s*\\\\?\"([^\\\\\"]+)\\\\?\"", Pattern.CASE_INSENSITIVE)
                    val mChunk = chunkPattern.matcher(html)
                    while (mChunk.find()) {
                        val gid = mChunk.group(2) ?: "0"
                        val name = cleanSheetName(mChunk.group(3) ?: "")
                        if (name.isNotBlank()) {
                            nameToGidMap[name] = gid
                            if (htmlDiscoveredSheets.none { it.sheetName == name }) {
                                htmlDiscoveredSheets.add(
                                    DiscoveredSheet(
                                        sheetId = gid,
                                        sheetName = name,
                                        index = htmlDiscoveredSheets.size,
                                        sheetType = "GRID",
                                        hidden = false
                                    )
                                )
                            }
                        }
                    }

                    // Pattern 3: JSON embedded sheet attributes: "name":"SheetName","sheetId":12345
                    val jsonSheetPattern = Pattern.compile("[\"']name[\"']\\s*:\\s*[\"']([^\"']+)[\"']\\s*,\\s*[\"']sheetId[\"']\\s*:\\s*([0-9]+)", Pattern.CASE_INSENSITIVE)
                    val mJson = jsonSheetPattern.matcher(html)
                    while (mJson.find()) {
                        val sName = cleanSheetName(mJson.group(1) ?: "")
                        val sGid = mJson.group(2) ?: "0"
                        if (sName.isNotBlank()) {
                            nameToGidMap[sName] = sGid
                            if (htmlDiscoveredSheets.none { it.sheetName == sName }) {
                                htmlDiscoveredSheets.add(
                                    DiscoveredSheet(
                                        sheetId = sGid,
                                        sheetName = sName,
                                        index = htmlDiscoveredSheets.size,
                                        sheetType = "GRID",
                                        hidden = false
                                    )
                                )
                            }
                        }
                    }

                    if (nameToGidMap.isNotEmpty()) {
                        break
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "HTML discovery on $pageUrl encountered: ${e.message}")
                    if (e is UnknownHostException || e is SocketTimeoutException) {
                        connectionError = e
                    }
                }
            }

            // Step 2: Download XLSX export stream to parse OpenXML workbook structure (xl/workbook.xml & rels & worksheets)
            // This discovers ALL sheets regardless of how many exist (1, 5, 25, 50, 100+) with their exact titles, row counts, and column counts
            try {
                val xlsxUrls = if (publicUrl.contains("/d/e/")) {
                    listOf(
                        "https://docs.google.com/spreadsheets/d/e/$spreadsheetId/pub?output=xlsx",
                        "https://docs.google.com/spreadsheets/d/e/$spreadsheetId/pub?format=xlsx"
                    )
                } else {
                    listOf(
                        "https://docs.google.com/spreadsheets/d/$spreadsheetId/export?format=xlsx",
                        "https://docs.google.com/spreadsheets/d/$spreadsheetId/pub?output=xlsx"
                    )
                }

                for (xlsxUrl in xlsxUrls) {
                    val xlsxReq = Request.Builder()
                        .url(xlsxUrl)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                        .build()
                    val xlsxResp = client.newCall(xlsxReq).execute()

                    val requestFinalUrl = xlsxResp.request.url.toString()
                    if (requestFinalUrl.contains("accounts.google.com") || requestFinalUrl.contains("ServiceLogin")) {
                        xlsxResp.close()
                        continue
                    }
                    if (xlsxResp.code == 404 || xlsxResp.code == 401 || xlsxResp.code == 403) {
                        xlsxResp.close()
                        continue
                    }

                    if (xlsxResp.isSuccessful && xlsxResp.body != null) {
                        val xlsxBytes = xlsxResp.body!!.bytes()
                        xlsxResp.close()

                        val zipEntries = mutableMapOf<String, String>()
                        val buffer = ByteArray(8192)
                        ZipInputStream(ByteArrayInputStream(xlsxBytes)).use { zis ->
                            var entry = zis.nextEntry
                            while (entry != null) {
                                val name = entry.name
                                if (name == "xl/workbook.xml" || name == "xl/_rels/workbook.xml.rels" || (name.startsWith("xl/worksheets/") && name.endsWith(".xml"))) {
                                    val baos = ByteArrayOutputStream()
                                    var len: Int
                                    while (zis.read(buffer).also { len = it } > 0) {
                                        baos.write(buffer, 0, len)
                                    }
                                    zipEntries[name] = baos.toString("UTF-8")
                                }
                                zis.closeEntry()
                                entry = zis.nextEntry
                            }
                        }

                        val wbXml = zipEntries["xl/workbook.xml"]
                        if (!wbXml.isNullOrBlank()) {
                            // Parse rels map
                            val relsXml = zipEntries["xl/_rels/workbook.xml.rels"] ?: ""
                            val relMap = mutableMapOf<String, String>()
                            val relPattern = Pattern.compile("<Relationship[^>]+Id=[\"']([^\"']+)[\"'][^>]+Target=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE)
                            val mRel = relPattern.matcher(relsXml)
                            while (mRel.find()) {
                                val relId = mRel.group(1) ?: ""
                                val target = mRel.group(2) ?: ""
                                if (relId.isNotBlank() && target.isNotBlank()) {
                                    relMap[relId] = if (target.startsWith("xl/")) target else "xl/$target"
                                }
                            }

                            val sheetTagPattern = Pattern.compile("<sheet\\s+([^>]+)/?>", Pattern.CASE_INSENSITIVE)
                            val nameAttrPattern = Pattern.compile("name=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE)
                            val idAttrPattern = Pattern.compile("sheetId=[\"']?([0-9]+)[\"']?", Pattern.CASE_INSENSITIVE)
                            val stateAttrPattern = Pattern.compile("state=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE)
                            val rIdPattern = Pattern.compile("r:id=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE)

                            val matcher = sheetTagPattern.matcher(wbXml)
                            var sheetIndex = 0
                            val seenNames = mutableSetOf<String>()

                            while (matcher.find()) {
                                val attrs = matcher.group(1) ?: ""
                                val nameM = nameAttrPattern.matcher(attrs)
                                val idM = idAttrPattern.matcher(attrs)
                                val stateM = stateAttrPattern.matcher(attrs)
                                val ridM = rIdPattern.matcher(attrs)

                                if (nameM.find()) {
                                    val sheetName = cleanSheetName(nameM.group(1) ?: "")
                                    val workbookSheetId = if (idM.find()) idM.group(1) ?: "$sheetIndex" else "$sheetIndex"
                                    val state = if (stateM.find()) stateM.group(1) ?: "visible" else "visible"
                                    val isHidden = state.equals("hidden", ignoreCase = true)
                                    val relId = if (ridM.find()) ridM.group(1) ?: "" else ""
                                    val targetPath = relMap[relId] ?: "xl/worksheets/sheet${sheetIndex + 1}.xml"

                                    // Extract row count and column count from worksheet xml
                                    var rowCount = 0
                                    var colCount = 0
                                    val wsXml = zipEntries[targetPath]
                                    if (!wsXml.isNullOrBlank()) {
                                        // Count <row tags
                                        val rowMatcher = Pattern.compile("<row[\\s>]", Pattern.CASE_INSENSITIVE).matcher(wsXml)
                                        while (rowMatcher.find()) {
                                            rowCount++
                                        }

                                        // Count unique columns
                                        val colMatcher = Pattern.compile("<c\\s+[^>]*r=[\"']([A-Za-z]+)[0-9]+[\"']", Pattern.CASE_INSENSITIVE).matcher(wsXml)
                                        val colSet = mutableSetOf<String>()
                                        while (colMatcher.find()) {
                                            val letters = colMatcher.group(1) ?: ""
                                            if (letters.isNotBlank()) colSet.add(letters.uppercase())
                                        }
                                        colCount = colSet.size
                                    }

                                    if (sheetName.isNotBlank() && !seenNames.contains(sheetName)) {
                                        seenNames.add(sheetName)
                                        // Prefer real Google GID if discovered from htmlview, else workbook sheet ID
                                        val finalGid = nameToGidMap[sheetName] ?: workbookSheetId
                                        discoveredList.add(
                                            DiscoveredSheet(
                                                sheetId = finalGid,
                                                sheetName = sheetName,
                                                index = sheetIndex,
                                                sheetType = "GRID",
                                                hidden = isHidden,
                                                rowCount = rowCount,
                                                columnCount = colCount
                                            )
                                        )
                                        sheetIndex++
                                    }
                                }
                            }

                            if (discoveredList.isNotEmpty()) {
                                break
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "XLSX export discovery encountered: ${e.message}")
                if (e is UnknownHostException || e is SocketTimeoutException) {
                    connectionError = e
                }
            }

            // Step 3: If XLSX export was restricted, fall back to HTML-discovered sheets
            if (discoveredList.isEmpty() && htmlDiscoveredSheets.isNotEmpty()) {
                discoveredList.addAll(htmlDiscoveredSheets)
            }

            // Step 4: If still empty, verify spreadsheet connectivity via gviz
            if (discoveredList.isEmpty()) {
                try {
                    val gvizUrl = "https://docs.google.com/spreadsheets/d/$spreadsheetId/gviz/tq?tqx=out:json&tq=limit%201"
                    val gvizReq = Request.Builder()
                        .url(gvizUrl)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .build()
                    val gvizResp = client.newCall(gvizReq).execute()
                    val gvizBody = gvizResp.body?.string() ?: ""

                    if (gvizResp.code == 404) {
                        return@withContext Result.failure(Exception("الرابط غير متاح - تأكد من صحة رابط Google Sheet"))
                    }
                    if (gvizResp.code == 401 || gvizResp.code == 403 || gvizBody.contains("accounts.google.com") || gvizBody.contains("Sign in")) {
                        return@withContext Result.failure(Exception("المصدر غير عام - تأكد من ضبط المشاركة على «أي شخص لديه الرابط يمكنه العرض»"))
                    }
                } catch (e: Exception) {
                    if (e is UnknownHostException || e is SocketTimeoutException) {
                        return@withContext Result.failure(Exception("مشكلة في الاتصال - تعذر الاتصال بـ Google Sheets، تحقق من اتصال الإنترنت"))
                    }
                }
            }

            // If completely empty, fail with clear error
            if (discoveredList.isEmpty()) {
                if (connectionError != null) {
                    return@withContext Result.failure(Exception("مشكلة في الاتصال - تعذر الاتصال بالخادم، يرجى التحقق من اتصال الإنترنت"))
                }
                return@withContext Result.failure(Exception("المصدر لا يحتوي على أوراق قابلة للقراءة أو تعذر قراءة Spreadsheet"))
            }

            Result.success(discoveredList)
        } catch (e: Exception) {
            Log.e(TAG, "Sheet discovery critical error", e)
            val msg = when {
                e is UnknownHostException || e is SocketTimeoutException -> "مشكلة في الاتصال - تحقق من اتصال الإنترنت"
                e.message?.contains("404") == true -> "الرابط غير متاح - لم يتم العثور على ملف Google Sheet"
                e.message?.contains("غير عام") == true -> e.message ?: "المصدر غير عام"
                else -> e.localizedMessage ?: "تعذر قراءة Spreadsheet"
            }
            Result.failure(Exception(msg))
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

            // Determine URLs to try: try by gid if numeric, and by sheet name
            val urlsToTry = mutableListOf<String>()
            if (sheetGid.isNotBlank() && sheetGid.all { it.isDigit() }) {
                urlsToTry.add("https://docs.google.com/spreadsheets/d/$spreadsheetId/gviz/tq?tqx=out:json&gid=$sheetGid")
            }
            if (sheetName.isNotBlank()) {
                urlsToTry.add("https://docs.google.com/spreadsheets/d/$spreadsheetId/gviz/tq?tqx=out:json&sheet=$encodedSheet")
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
                val csvGidPart = if (sheetGid.isNotBlank() && sheetGid.all { it.isDigit() }) "&gid=$sheetGid" else ""
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
