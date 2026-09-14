package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.FinancialRevenueEntity
import com.example.data.local.entities.ProfitShareRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfitShareRuleDao {

    @Query("SELECT * FROM profit_share_rules ORDER BY sortOrder ASC")
    fun getAllRules(): Flow<List<ProfitShareRuleEntity>>

    @Query("SELECT * FROM profit_share_rules WHERE isActive = 1 ORDER BY sortOrder ASC")
    fun getActiveRules(): Flow<List<ProfitShareRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(rule: ProfitShareRuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rules: List<ProfitShareRuleEntity>)

    @Query("DELETE FROM profit_share_rules WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM profit_share_rules")
    suspend fun getCount(): Int

    @Query("SELECT * FROM profit_share_rules")
    suspend fun getAllRulesList(): List<ProfitShareRuleEntity>
}

@Dao
interface FinancialRevenueDao {

    @Query("SELECT * FROM financial_revenues WHERE isDeleted = 0 ORDER BY timestamp DESC")
    fun getAllRevenues(): Flow<List<FinancialRevenueEntity>>

    @Query("SELECT * FROM financial_revenues WHERE isDeleted = 0 AND id = :id LIMIT 1")
    suspend fun getRevenueById(id: String): FinancialRevenueEntity?

    @Query("SELECT * FROM financial_revenues WHERE isDeleted = 0 AND caseId = :caseId ORDER BY timestamp DESC")
    fun getRevenuesByCaseId(caseId: String): Flow<List<FinancialRevenueEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(revenue: FinancialRevenueEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(revenues: List<FinancialRevenueEntity>)

    @Query("UPDATE financial_revenues SET isDeleted = 1 WHERE id = :id")
    suspend fun softDelete(id: String)

    @Query("DELETE FROM financial_revenues WHERE id = :id")
    suspend fun permanentDelete(id: String)

    @Query("SELECT COUNT(*) FROM financial_revenues WHERE isDeleted = 0")
    suspend fun getCount(): Int

    @Query("SELECT * FROM financial_revenues WHERE isDeleted = 0")
    suspend fun getAllList(): List<FinancialRevenueEntity>

    @Query("SELECT SUM(paidAmount) FROM financial_revenues WHERE isDeleted = 0")
    fun getTotalPaidAmount(): Flow<Double?>

    @Query("SELECT SUM(remainingAmount) FROM financial_revenues WHERE isDeleted = 0")
    fun getTotalRemainingAmount(): Flow<Double?>
}
