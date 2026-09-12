package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.CaseEntity
import com.example.data.local.entities.ClientEntity
import com.example.data.local.entities.ContentEntity
import com.example.data.local.entities.EvidenceEntity
import com.example.data.local.entities.KnowledgeEntity
import com.example.data.local.entities.TaskEntity
import com.example.data.local.entities.SyncOperationEntity
import com.example.data.local.entities.AppSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDao {
    @Query("SELECT * FROM cases WHERE isDeleted = 0 ORDER BY updatedDate DESC")
    fun getAllActiveCases(): Flow<List<CaseEntity>>

    @Query("SELECT * FROM cases WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedCases(): Flow<List<CaseEntity>>

    @Query("SELECT * FROM cases WHERE id = :id LIMIT 1")
    suspend fun getCaseById(id: String): CaseEntity?

    @Query("SELECT COUNT(*) FROM cases")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(caseEntity: CaseEntity)

    @Update
    suspend fun update(caseEntity: CaseEntity)

    @Query("UPDATE cases SET isDeleted = 1, deletedAt = :deletedAt, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun softDelete(id: String, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE cases SET isDeleted = 0, deletedAt = NULL, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun restoreCase(id: String)

    @Query("DELETE FROM cases WHERE id = :id")
    suspend fun permanentDelete(id: String)
}

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients WHERE isDeleted = 0 ORDER BY createdDate DESC")
    fun getAllActiveClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedClients(): Flow<List<ClientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(client: ClientEntity)

    @Query("UPDATE clients SET isDeleted = 1, deletedAt = :deletedAt, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun softDelete(id: String, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE clients SET isDeleted = 0, deletedAt = NULL, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun restoreClient(id: String)

    @Query("DELETE FROM clients WHERE id = :id")
    suspend fun permanentDelete(id: String)
}

@Dao
interface EvidenceDao {
    @Query("SELECT * FROM evidence WHERE isDeleted = 0 ORDER BY createdDate DESC")
    fun getAllActiveEvidence(): Flow<List<EvidenceEntity>>

    @Query("SELECT * FROM evidence WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedEvidence(): Flow<List<EvidenceEntity>>

    @Query("SELECT * FROM evidence WHERE caseId = :caseId AND isDeleted = 0 ORDER BY createdDate DESC")
    fun getEvidenceForCase(caseId: String): Flow<List<EvidenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(evidence: EvidenceEntity)

    @Query("UPDATE evidence SET isDeleted = 1, deletedAt = :deletedAt, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun softDelete(id: String, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE evidence SET isDeleted = 0, deletedAt = NULL, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun restoreEvidence(id: String)

    @Query("DELETE FROM evidence WHERE id = :id")
    suspend fun permanentDelete(id: String)
}

@Dao
interface ContentDao {
    @Query("SELECT * FROM content_studio WHERE isDeleted = 0 ORDER BY updatedDate DESC")
    fun getAllActiveContent(): Flow<List<ContentEntity>>

    @Query("SELECT * FROM content_studio WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedContent(): Flow<List<ContentEntity>>

    @Query("SELECT * FROM content_studio WHERE id = :id LIMIT 1")
    suspend fun getContentById(id: String): ContentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(content: ContentEntity)

    @Query("UPDATE content_studio SET isDeleted = 1, deletedAt = :deletedAt, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun softDelete(id: String, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE content_studio SET isDeleted = 0, deletedAt = NULL, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun restoreContent(id: String)

    @Query("DELETE FROM content_studio WHERE id = :id")
    suspend fun permanentDelete(id: String)
}

@Dao
interface KnowledgeDao {
    @Query("SELECT * FROM knowledge_base WHERE isDeleted = 0 ORDER BY category ASC, title ASC")
    fun getAllActiveKnowledge(): Flow<List<KnowledgeEntity>>

    @Query("SELECT * FROM knowledge_base WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedKnowledge(): Flow<List<KnowledgeEntity>>

    @Query("SELECT COUNT(*) FROM knowledge_base WHERE isOfficialGuide = 1 AND isDeleted = 0")
    suspend fun getOfficialGuideCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(guides: List<KnowledgeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(guide: KnowledgeEntity)

    @Query("UPDATE knowledge_base SET isDeleted = 1, deletedAt = :deletedAt, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun softDelete(id: String, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE knowledge_base SET isDeleted = 0, deletedAt = NULL, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun restoreKnowledge(id: String)

    @Query("DELETE FROM knowledge_base WHERE id = :id")
    suspend fun permanentDelete(id: String)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 200")
    fun getRecentAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 ORDER BY createdDate DESC")
    fun getAllActiveTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE relatedCaseId = :caseId AND isDeleted = 0")
    fun getTasksForCase(caseId: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(task: TaskEntity)

    @Query("UPDATE tasks SET status = :status, completedAt = :completedAt WHERE id = :id")
    suspend fun updateTaskStatus(id: String, status: String, completedAt: Long?)

    @Query("UPDATE tasks SET isDeleted = 1, deletedAt = :deletedAt, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun softDelete(id: String, deletedAt: Long)

    @Query("UPDATE tasks SET isDeleted = 0, deletedAt = NULL, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun restoreTask(id: String)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun permanentDelete(id: String)
}

@Dao
interface SyncOperationDao {
    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING' ORDER BY timestamp ASC")
    suspend fun getPendingOperations(): List<com.example.data.local.entities.SyncOperationEntity>

    @Query("SELECT COUNT(*) FROM sync_queue WHERE status = 'PENDING'")
    fun getPendingCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(op: com.example.data.local.entities.SyncOperationEntity)

    @Update
    suspend fun updateOperation(op: com.example.data.local.entities.SyncOperationEntity)

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun deleteOperation(id: String)

    @Query("DELETE FROM sync_queue WHERE status = 'SUCCESS'")
    suspend fun clearCompleted()
}

@Dao
interface SettingsDao {
    @Query("SELECT value FROM app_settings WHERE key = :key LIMIT 1")
    suspend fun getSetting(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSetting(setting: com.example.data.local.entities.AppSettingsEntity)

    @Query("SELECT * FROM app_settings")
    fun getAllSettings(): Flow<List<com.example.data.local.entities.AppSettingsEntity>>
}

@Dao
interface SupportFormDao {
    @Query("SELECT * FROM support_forms WHERE isDeleted = 0 ORDER BY verified DESC, company ASC")
    fun getAllActiveForms(): Flow<List<com.example.data.local.entities.SupportFormEntity>>

    @Query("SELECT * FROM support_forms WHERE id = :id")
    suspend fun getFormById(id: String): com.example.data.local.entities.SupportFormEntity?

    @Query("SELECT COUNT(*) FROM support_forms")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(form: com.example.data.local.entities.SupportFormEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(forms: List<com.example.data.local.entities.SupportFormEntity>)

    @Query("UPDATE support_forms SET isDeleted = 1, deletedAt = :deletedAt, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun softDelete(id: String, deletedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM support_forms WHERE id = :id")
    suspend fun permanentDelete(id: String)
}

@Dao
interface InvestigationToolDao {
    @Query("SELECT * FROM investigation_tools WHERE isDeleted = 0 ORDER BY isFavorite DESC, verified DESC, name ASC")
    fun getAllActiveTools(): Flow<List<com.example.data.local.entities.InvestigationToolEntity>>

    @Query("SELECT * FROM investigation_tools WHERE id = :id")
    suspend fun getToolById(id: String): com.example.data.local.entities.InvestigationToolEntity?

    @Query("SELECT COUNT(*) FROM investigation_tools")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(tool: com.example.data.local.entities.InvestigationToolEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tools: List<com.example.data.local.entities.InvestigationToolEntity>)

    @Query("UPDATE investigation_tools SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE investigation_tools SET lastUsedAt = :timestamp WHERE id = :id")
    suspend fun updateLastUsed(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE investigation_tools SET isDeleted = 1, deletedAt = :deletedAt, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun softDelete(id: String, deletedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM investigation_tools WHERE id = :id")
    suspend fun permanentDelete(id: String)
}

@Dao
interface CaseLinkedItemDao {
    @Query("SELECT * FROM case_linked_items WHERE caseId = :caseId ORDER BY linkedAt DESC")
    fun getLinkedItemsForCase(caseId: String): Flow<List<com.example.data.local.entities.CaseLinkedItemEntity>>

    @Query("SELECT * FROM case_linked_items WHERE caseId = :caseId AND itemType = :itemType ORDER BY linkedAt DESC")
    fun getLinkedItemsForCaseByType(caseId: String, itemType: String): Flow<List<com.example.data.local.entities.CaseLinkedItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: com.example.data.local.entities.CaseLinkedItemEntity)

    @Query("DELETE FROM case_linked_items WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM case_linked_items WHERE caseId = :caseId AND itemId = :itemId")
    suspend fun deleteByCaseAndItem(caseId: String, itemId: String)
}

@Dao
interface CasePaymentDao {
    @Query("SELECT * FROM case_payments WHERE caseId = :caseId ORDER BY createdDate DESC")
    fun getPaymentsForCase(caseId: String): Flow<List<com.example.data.local.entities.CasePaymentEntity>>

    @Query("SELECT * FROM case_payments ORDER BY createdDate DESC")
    fun getAllPayments(): Flow<List<com.example.data.local.entities.CasePaymentEntity>>

    @Query("SELECT * FROM case_payments ORDER BY createdDate DESC LIMIT :limit")
    fun getRecentPayments(limit: Int = 50): Flow<List<com.example.data.local.entities.CasePaymentEntity>>

    @Query("SELECT SUM(amount) FROM case_payments WHERE caseId = :caseId")
    suspend fun getTotalPaidForCase(caseId: String): Double?

    @Query("SELECT SUM(amount) FROM case_payments")
    fun getTotalPaidAcrossAllCases(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: com.example.data.local.entities.CasePaymentEntity)

    @Query("DELETE FROM case_payments WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface CaseFinancialLogDao {
    @Query("SELECT * FROM case_financial_logs WHERE caseId = :caseId ORDER BY timestamp DESC")
    fun getLogsForCase(caseId: String): Flow<List<com.example.data.local.entities.CaseFinancialLogEntity>>

    @Query("SELECT * FROM case_financial_logs ORDER BY timestamp DESC")
    fun getAllFinancialLogs(): Flow<List<com.example.data.local.entities.CaseFinancialLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: com.example.data.local.entities.CaseFinancialLogEntity)
}

@Dao
interface CaseAuditLogDao {
    @Query("SELECT * FROM case_audit_logs WHERE caseId = :caseId ORDER BY timestamp DESC")
    fun getLogsForCase(caseId: String): Flow<List<com.example.data.local.entities.CaseAuditLogEntity>>

    @Query("SELECT * FROM case_audit_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 100): Flow<List<com.example.data.local.entities.CaseAuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: com.example.data.local.entities.CaseAuditLogEntity)
}

@Dao
interface VideoIdeaDao {
    @Query("SELECT * FROM video_ideas WHERE isDeleted = 0 ORDER BY updatedDate DESC")
    fun getAllActiveIdeas(): Flow<List<com.example.data.local.entities.VideoIdeaEntity>>

    @Query("SELECT * FROM video_ideas WHERE isDeleted = 0 AND status = :status ORDER BY updatedDate DESC")
    fun getIdeasByStatus(status: String): Flow<List<com.example.data.local.entities.VideoIdeaEntity>>

    @Query("SELECT * FROM video_ideas WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedIdeas(): Flow<List<com.example.data.local.entities.VideoIdeaEntity>>

    @Query("SELECT * FROM video_ideas WHERE id = :id LIMIT 1")
    suspend fun getIdeaById(id: String): com.example.data.local.entities.VideoIdeaEntity?

    @Query("SELECT COUNT(*) FROM video_ideas WHERE isDeleted = 0")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(idea: com.example.data.local.entities.VideoIdeaEntity)

    @Query("UPDATE video_ideas SET isDeleted = 1, deletedAt = :deletedAt, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun softDelete(id: String, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE video_ideas SET isDeleted = 0, deletedAt = NULL, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun restore(id: String)

    @Query("DELETE FROM video_ideas WHERE id = :id")
    suspend fun permanentDelete(id: String)
}

@Dao
interface VideoScriptDao {
    @Query("SELECT * FROM video_scripts WHERE isDeleted = 0 ORDER BY updatedDate DESC")
    fun getAllActiveScripts(): Flow<List<com.example.data.local.entities.VideoScriptEntity>>

    @Query("SELECT * FROM video_scripts WHERE ideaId = :ideaId AND isDeleted = 0 ORDER BY updatedDate DESC")
    fun getScriptsForIdea(ideaId: String): Flow<List<com.example.data.local.entities.VideoScriptEntity>>

    @Query("SELECT * FROM video_scripts WHERE id = :id LIMIT 1")
    suspend fun getScriptById(id: String): com.example.data.local.entities.VideoScriptEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(script: com.example.data.local.entities.VideoScriptEntity)

    @Query("UPDATE video_scripts SET isDeleted = 1, deletedAt = :deletedAt, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun softDelete(id: String, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE video_scripts SET isDeleted = 0, deletedAt = NULL, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun restore(id: String)

    @Query("DELETE FROM video_scripts WHERE id = :id")
    suspend fun permanentDelete(id: String)
}


