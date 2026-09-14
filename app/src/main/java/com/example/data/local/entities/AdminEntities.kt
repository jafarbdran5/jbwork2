package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_section_configs")
data class AppSectionConfigEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val description: String,
    val iconName: String,
    val sortOrder: Int,
    val isVisible: Boolean = true,
    val isCustom: Boolean = false,
    val category: String = "MAIN",
    val showInBottomNav: Boolean = false,
    val bottomNavOrder: Int = 0,
    val isDefaultStartScreen: Boolean = false
)

@Entity(tableName = "custom_field_definitions")
data class CustomFieldDefinitionEntity(
    @PrimaryKey val id: String,
    val targetEntity: String = "CASE", // "CASE", "REQUEST", "CLIENT"
    val fieldName: String,
    val fieldType: String = "TEXT", // "TEXT", "NUMBER", "DATE", "SELECT"
    val optionsJson: String = "[]",
    val isRequired: Boolean = false,
    val showInList: Boolean = false,
    val showInDetails: Boolean = true,
    val sortOrder: Int = 0
)

@Entity(tableName = "system_expenses")
data class SystemExpenseEntity(
    @PrimaryKey val id: String,
    val title: String,
    val amount: Double,
    val category: String, // "أدوات تقنية", "اشتراكات وسيرفرات", "مصاريف إدارية", "استشارات وفريق", "أخرى"
    val date: String,
    val notes: String = "",
    val relatedCaseId: String? = null,
    val createdDate: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val syncStatus: String = "SYNCED"
)

@Entity(tableName = "system_categories")
data class SystemCategoryEntity(
    @PrimaryKey val id: String,
    val scope: String, // "CASES", "TOOLS", "FORMS", "STUDIO", "EXPENSES"
    val name: String,
    val color: String = "",
    val sortOrder: Int = 0
)

@Entity(tableName = "admin_audit_logs")
data class AdminAuditLogEntity(
    @PrimaryKey val id: String,
    val adminName: String = "جعفر بدران (المدير الرئيسي)",
    val action: String,
    val target: String,
    val oldValue: String = "",
    val newValue: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val dateFormatted: String
)
