package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.navigation.ScreenDestination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

val Context.bottomBarDataStore: DataStore<Preferences> by preferencesDataStore(name = "bottom_bar_customization")

/**
 * تكوين عنصر فردي في الشريط السفلي أو القائمة الشاملة للأقسام
 */
data class BottomBarItemConfig(
    val destinationId: Int,
    val customTitle: String = "",
    val isVisibleInBar: Boolean = false,
    val order: Int = 0
) {
    val destination: ScreenDestination? get() = ScreenDestination.fromId(destinationId)
    val displayTitle: String get() = customTitle.ifBlank { destination?.title ?: "قسم غير معروف" }
}

/**
 * إعدادات الشريط السفلي المحفوظة في DataStore
 */
data class BottomBarSettings(
    val isEnabled: Boolean = true,
    val maxItems: Int = 5,
    val defaultDestinationId: Int = ScreenDestination.DASHBOARD.id,
    val items: List<BottomBarItemConfig> = emptyList()
) {
    /**
     * العناصر النشطة والمعروضة فعلياً في الشريط السفلي،
     * مرتبة حسب ترتيب المستخدم ومحددة بالحد الأقصى للعناصر.
     */
    val activeVisibleItems: List<BottomBarItemConfig>
        get() = items
            .filter { it.isVisibleInBar && it.destination != null }
            .sortedBy { it.order }
            .take(maxItems)
}

class BottomBarDataStore(private val context: Context) {

    private object PreferencesKeys {
        val KEY_IS_ENABLED = booleanPreferencesKey("is_bottom_bar_enabled")
        val KEY_MAX_ITEMS = intPreferencesKey("bottom_bar_max_items")
        val KEY_DEFAULT_DESTINATION = intPreferencesKey("bottom_bar_default_destination")
        val KEY_ITEMS_JSON = stringPreferencesKey("bottom_bar_items_json")
    }

    val settingsFlow: Flow<BottomBarSettings> = context.bottomBarDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val isEnabled = preferences[PreferencesKeys.KEY_IS_ENABLED] ?: true
            val maxItems = preferences[PreferencesKeys.KEY_MAX_ITEMS] ?: 5
            val defaultDestinationId = preferences[PreferencesKeys.KEY_DEFAULT_DESTINATION] ?: ScreenDestination.DASHBOARD.id
            val itemsJson = preferences[PreferencesKeys.KEY_ITEMS_JSON]

            val items = if (itemsJson.isNullOrBlank()) {
                getDefaultItems()
            } else {
                parseItemsJson(itemsJson)
            }

            BottomBarSettings(
                isEnabled = isEnabled,
                maxItems = maxItems.coerceIn(3, 7),
                defaultDestinationId = defaultDestinationId,
                items = items
            )
        }

    suspend fun saveSettings(settings: BottomBarSettings) {
        val itemsJson = serializeItemsJson(settings.items)
        context.bottomBarDataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_IS_ENABLED] = settings.isEnabled
            preferences[PreferencesKeys.KEY_MAX_ITEMS] = settings.maxItems.coerceIn(3, 7)
            preferences[PreferencesKeys.KEY_DEFAULT_DESTINATION] = settings.defaultDestinationId
            preferences[PreferencesKeys.KEY_ITEMS_JSON] = itemsJson
        }
    }

    suspend fun resetToDefault(): BottomBarSettings {
        val defaultSettings = getDefaultSettings()
        saveSettings(defaultSettings)
        return defaultSettings
    }

    fun getDefaultSettings(): BottomBarSettings {
        return BottomBarSettings(
            isEnabled = true,
            maxItems = 5,
            defaultDestinationId = ScreenDestination.DASHBOARD.id,
            items = getDefaultItems()
        )
    }

    private fun getDefaultItems(): List<BottomBarItemConfig> {
        return ScreenDestination.entries.map { dest ->
            BottomBarItemConfig(
                destinationId = dest.id,
                customTitle = "",
                isVisibleInBar = dest.defaultInBottomBar,
                order = dest.defaultOrder
            )
        }.sortedBy { it.order }
    }

    private fun serializeItemsJson(items: List<BottomBarItemConfig>): String {
        val array = JSONArray()
        items.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.destinationId)
                put("title", item.customTitle)
                put("visible", item.isVisibleInBar)
                put("order", item.order)
            }
            array.put(obj)
        }
        return array.toString()
    }

    private fun parseItemsJson(jsonStr: String): List<BottomBarItemConfig> {
        return try {
            val array = JSONArray(jsonStr)
            val parsedList = mutableListOf<BottomBarItemConfig>()
            val parsedIds = mutableSetOf<Int>()

            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.getInt("id")
                val customTitle = obj.optString("title", "")
                val isVisible = obj.optBoolean("visible", false)
                val order = obj.optInt("order", i)
                parsedList.add(BottomBarItemConfig(id, customTitle, isVisible, order))
                parsedIds.add(id)
            }

            // ضمان وجود أي قسم جديد تمت إضافته للتطبيق ولم يكن موجوداً في الإعدادات القديمة
            ScreenDestination.entries.forEach { dest ->
                if (!parsedIds.contains(dest.id)) {
                    parsedList.add(
                        BottomBarItemConfig(
                            destinationId = dest.id,
                            customTitle = "",
                            isVisibleInBar = dest.defaultInBottomBar,
                            order = parsedList.size
                        )
                    )
                }
            }

            parsedList.sortedBy { it.order }
        } catch (_: Exception) {
            getDefaultItems()
        }
    }
}
