package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Entity representing flexible profit split rules (percentage or fixed amount).
 */
@Entity(tableName = "profit_share_rules")
data class ProfitShareRuleEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String, // e.g. "حصة الإدارة (جعفر بدران)", "فريق التحليل الفني"
    val type: String, // "PERCENTAGE" or "FIXED_AMOUNT"
    val value: Double, // e.g. 50.0 for 50%, or 500.0 for 500 SAR
    val description: String = "",
    val isActive: Boolean = true,
    val sortOrder: Int = 0
)

/**
 * Entity representing full financial revenues and earnings.
 */
@Entity(tableName = "financial_revenues")
data class FinancialRevenueEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val caseId: String? = null,
    val caseNumber: String? = null,
    val clientName: String = "",
    val totalAmount: Double = 0.0,
    val paidAmount: Double = 0.0,
    val remainingAmount: Double = 0.0,
    val currency: String = "SAR",
    val incomeType: String = "أتعاب فحص جنائي", // "أتعاب فحص جنائي", "استرجاع حسابات وأصول", "استشارات أمنية", "تأمين وحماية", "أخرى"
    val date: String,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val isDeleted: Boolean = false
)
