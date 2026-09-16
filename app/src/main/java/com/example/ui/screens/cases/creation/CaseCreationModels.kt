package com.example.ui.screens.cases.creation

import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

data class DraftCustomLink(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val url: String,
    val groupName: String = "",
    val linkType: String = "رابط"
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("title", title)
        put("url", url)
        put("groupName", groupName)
        put("linkType", linkType)
    }

    companion object {
        fun fromJson(json: JSONObject): DraftCustomLink = DraftCustomLink(
            id = json.optString("id", UUID.randomUUID().toString()),
            title = json.optString("title", ""),
            url = json.optString("url", ""),
            groupName = json.optString("groupName", ""),
            linkType = json.optString("linkType", "رابط")
        )

        fun listToJsonString(list: List<DraftCustomLink>): String {
            val array = JSONArray()
            list.forEach { array.put(it.toJson()) }
            return array.toString()
        }

        fun listFromJsonString(jsonString: String?): List<DraftCustomLink> {
            if (jsonString.isNullOrBlank()) return emptyList()
            return try {
                val array = JSONArray(jsonString)
                val list = mutableListOf<DraftCustomLink>()
                for (i in 0 until array.length()) {
                    list.add(fromJson(array.getJSONObject(i)))
                }
                list
            } catch (_: Exception) {
                emptyList()
            }
        }
    }
}

data class DraftLinkGroup(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val links: List<DraftCustomLink> = emptyList()
)

data class DraftIdentifier(
    val id: String = UUID.randomUUID().toString(),
    val type: String,
    val value: String,
    val isCustomType: Boolean = false
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("type", type)
        put("value", value)
        put("isCustomType", isCustomType)
    }

    companion object {
        fun fromJson(json: JSONObject): DraftIdentifier = DraftIdentifier(
            id = json.optString("id", UUID.randomUUID().toString()),
            type = json.optString("type", "معرف"),
            value = json.optString("value", ""),
            isCustomType = json.optBoolean("isCustomType", false)
        )

        fun listToJsonString(list: List<DraftIdentifier>): String {
            val array = JSONArray()
            list.forEach { array.put(it.toJson()) }
            return array.toString()
        }

        fun listFromJsonString(jsonString: String?): List<DraftIdentifier> {
            if (jsonString.isNullOrBlank()) return emptyList()
            return try {
                val array = JSONArray(jsonString)
                val list = mutableListOf<DraftIdentifier>()
                for (i in 0 until array.length()) {
                    list.add(fromJson(array.getJSONObject(i)))
                }
                list
            } catch (_: Exception) {
                emptyList()
            }
        }
    }
}

data class DraftCaseImage(
    val id: String = UUID.randomUUID().toString(),
    val uri: Uri,
    val localFile: File,
    val originalFileName: String,
    val displayName: String,
    val fileSizeFormatted: String,
    val fileSizeBytes: Long,
    val mimeType: String = "image/jpeg",
    val width: Int = 0,
    val height: Int = 0,
    val isEdited: Boolean = false
)

object IdentifierTypePresets {
    val PRESETS = listOf(
        "Facebook ID",
        "Instagram ID",
        "Page ID",
        "Post ID",
        "Group ID",
        "WhatsApp Number",
        "Ticket ID",
        "Reference Number",
        "External Case ID",
        "نوع معرف مخصص"
    )
}
