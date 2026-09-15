package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AuditLogDao
import com.example.data.local.dao.CaseAuditLogDao
import com.example.data.local.dao.CaseDao
import com.example.data.local.dao.CaseFinancialLogDao
import com.example.data.local.dao.CaseLinkedItemDao
import com.example.data.local.dao.CasePaymentDao
import com.example.data.local.dao.ClientDao
import com.example.data.local.dao.ContentDao
import com.example.data.local.dao.EvidenceDao
import com.example.data.local.dao.ExternalRequestDao
import com.example.data.local.dao.ExternalRequestSourceDao
import com.example.data.local.dao.ExternalSheetDao
import com.example.data.local.dao.InvestigationToolDao
import com.example.data.local.dao.KnowledgeDao
import com.example.data.local.dao.SettingsDao
import com.example.data.local.dao.SupportFormDao
import com.example.data.local.dao.SyncOperationDao
import com.example.data.local.dao.TaskDao
import com.example.data.local.dao.VideoIdeaDao
import com.example.data.local.dao.VideoScriptDao
import com.example.data.local.dao.AppSectionConfigDao
import com.example.data.local.dao.CustomFieldDefinitionDao
import com.example.data.local.dao.SystemExpenseDao
import com.example.data.local.dao.SystemCategoryDao
import com.example.data.local.dao.AdminAuditLogDao
import com.example.data.local.dao.OfficialSourceDao
import com.example.data.local.dao.ProfitShareRuleDao
import com.example.data.local.dao.FinancialRevenueDao
import com.example.data.local.dao.CaseCustomLinkDao
import com.example.data.local.dao.GeneratedReportDao
import com.example.data.local.dao.ReportTemplateDao
import com.example.data.local.entities.AdminAuditLogEntity
import com.example.data.local.entities.OfficialSourceEntity
import com.example.data.local.entities.ProfitShareRuleEntity
import com.example.data.local.entities.FinancialRevenueEntity
import com.example.data.local.entities.AppSectionConfigEntity
import com.example.data.local.entities.AppSettingsEntity
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.CaseAuditLogEntity
import com.example.data.local.entities.CaseEntity
import com.example.data.local.entities.CaseFinancialLogEntity
import com.example.data.local.entities.CaseLinkedItemEntity
import com.example.data.local.entities.CasePaymentEntity
import com.example.data.local.entities.ClientEntity
import com.example.data.local.entities.ContentEntity
import com.example.data.local.entities.CustomFieldDefinitionEntity
import com.example.data.local.entities.EvidenceEntity
import com.example.data.local.entities.ExternalRequestEntity
import com.example.data.local.entities.ExternalRequestSourceEntity
import com.example.data.local.entities.ExternalSheetEntity
import com.example.data.local.entities.InvestigationToolEntity
import com.example.data.local.entities.KnowledgeEntity
import com.example.data.local.entities.SupportFormEntity
import com.example.data.local.entities.SyncOperationEntity
import com.example.data.local.entities.SystemCategoryEntity
import com.example.data.local.entities.SystemExpenseEntity
import com.example.data.local.entities.TaskEntity
import com.example.data.local.entities.VideoIdeaEntity
import com.example.data.local.entities.VideoScriptEntity
import com.example.data.local.entities.CaseCustomLinkEntity
import com.example.data.local.entities.GeneratedReportEntity
import com.example.data.local.entities.ReportTemplateEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CaseEntity::class,
        ClientEntity::class,
        EvidenceEntity::class,
        ContentEntity::class,
        KnowledgeEntity::class,
        AuditLogEntity::class,
        TaskEntity::class,
        SyncOperationEntity::class,
        AppSettingsEntity::class,
        SupportFormEntity::class,
        InvestigationToolEntity::class,
        CaseLinkedItemEntity::class,
        ExternalRequestSourceEntity::class,
        ExternalSheetEntity::class,
        ExternalRequestEntity::class,
        CasePaymentEntity::class,
        CaseFinancialLogEntity::class,
        CaseAuditLogEntity::class,
        VideoIdeaEntity::class,
        VideoScriptEntity::class,
        AppSectionConfigEntity::class,
        CustomFieldDefinitionEntity::class,
        SystemExpenseEntity::class,
        SystemCategoryEntity::class,
        AdminAuditLogEntity::class,
        OfficialSourceEntity::class,
        ProfitShareRuleEntity::class,
        FinancialRevenueEntity::class,
        CaseCustomLinkEntity::class,
        GeneratedReportEntity::class,
        ReportTemplateEntity::class
    ],
    version = 13,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun caseDao(): CaseDao
    abstract fun clientDao(): ClientDao
    abstract fun evidenceDao(): EvidenceDao
    abstract fun contentDao(): ContentDao
    abstract fun knowledgeDao(): KnowledgeDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun taskDao(): TaskDao
    abstract fun syncOperationDao(): SyncOperationDao
    abstract fun settingsDao(): SettingsDao
    abstract fun supportFormDao(): SupportFormDao
    abstract fun investigationToolDao(): InvestigationToolDao
    abstract fun caseLinkedItemDao(): CaseLinkedItemDao
    abstract fun externalRequestSourceDao(): ExternalRequestSourceDao
    abstract fun externalSheetDao(): ExternalSheetDao
    abstract fun externalRequestDao(): ExternalRequestDao
    abstract fun casePaymentDao(): CasePaymentDao
    abstract fun caseFinancialLogDao(): CaseFinancialLogDao
    abstract fun caseAuditLogDao(): CaseAuditLogDao
    abstract fun videoIdeaDao(): VideoIdeaDao
    abstract fun videoScriptDao(): VideoScriptDao
    abstract fun appSectionConfigDao(): AppSectionConfigDao
    abstract fun customFieldDefinitionDao(): CustomFieldDefinitionDao
    abstract fun systemExpenseDao(): SystemExpenseDao
    abstract fun systemCategoryDao(): SystemCategoryDao
    abstract fun adminAuditLogDao(): AdminAuditLogDao
    abstract fun officialSourceDao(): OfficialSourceDao
    abstract fun profitShareRuleDao(): ProfitShareRuleDao
    abstract fun financialRevenueDao(): FinancialRevenueDao
    abstract fun caseCustomLinkDao(): CaseCustomLinkDao
    abstract fun generatedReportDao(): GeneratedReportDao
    abstract fun reportTemplateDao(): ReportTemplateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("ALTER TABLE external_sheets ADD COLUMN customDisplayName TEXT DEFAULT NULL")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE external_sheets ADD COLUMN columnMappingJson TEXT NOT NULL DEFAULT '{}'")
                } catch (_: Exception) {}
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `app_section_configs` (
                        `id` TEXT NOT NULL,
                        `displayName` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `iconName` TEXT NOT NULL,
                        `sortOrder` INTEGER NOT NULL,
                        `isVisible` INTEGER NOT NULL DEFAULT 1,
                        `isCustom` INTEGER NOT NULL DEFAULT 0,
                        `category` TEXT NOT NULL DEFAULT 'MAIN',
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `custom_field_definitions` (
                        `id` TEXT NOT NULL,
                        `targetEntity` TEXT NOT NULL,
                        `fieldName` TEXT NOT NULL,
                        `fieldType` TEXT NOT NULL,
                        `optionsJson` TEXT NOT NULL DEFAULT '[]',
                        `isRequired` INTEGER NOT NULL DEFAULT 0,
                        `showInList` INTEGER NOT NULL DEFAULT 0,
                        `showInDetails` INTEGER NOT NULL DEFAULT 1,
                        `sortOrder` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `system_expenses` (
                        `id` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `amount` REAL NOT NULL,
                        `category` TEXT NOT NULL,
                        `date` TEXT NOT NULL,
                        `notes` TEXT NOT NULL DEFAULT '',
                        `relatedCaseId` TEXT DEFAULT NULL,
                        `createdDate` INTEGER NOT NULL,
                        `isDeleted` INTEGER NOT NULL DEFAULT 0,
                        `syncStatus` TEXT NOT NULL DEFAULT 'SYNCED',
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `system_categories` (
                        `id` TEXT NOT NULL,
                        `scope` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `color` TEXT NOT NULL DEFAULT '',
                        `sortOrder` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `admin_audit_logs` (
                        `id` TEXT NOT NULL,
                        `adminName` TEXT NOT NULL,
                        `action` TEXT NOT NULL,
                        `target` TEXT NOT NULL,
                        `oldValue` TEXT NOT NULL,
                        `newValue` TEXT NOT NULL,
                        `timestamp` INTEGER NOT NULL,
                        `dateFormatted` TEXT NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `cases` ADD COLUMN `externalPlatformCaseId` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `cases` ADD COLUMN `supportTicketId` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `cases` ADD COLUMN `targetIdentifier` TEXT NOT NULL DEFAULT ''")

                db.execSQL("ALTER TABLE `evidence` ADD COLUMN `localFilePath` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `evidence` ADD COLUMN `fileSizeBytes` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `evidence` ADD COLUMN `fileSizeFormatted` TEXT NOT NULL DEFAULT '0 KB'")
                db.execSQL("ALTER TABLE `evidence` ADD COLUMN `mimeType` TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("ALTER TABLE `app_section_configs` ADD COLUMN `showInBottomNav` INTEGER NOT NULL DEFAULT 0")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE `app_section_configs` ADD COLUMN `bottomNavOrder` INTEGER NOT NULL DEFAULT 0")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE `app_section_configs` ADD COLUMN `isDefaultStartScreen` INTEGER NOT NULL DEFAULT 0")
                } catch (_: Exception) {}

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `case_custom_links` (
                        `id` TEXT NOT NULL,
                        `caseId` TEXT NOT NULL,
                        `caseNumber` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `url` TEXT NOT NULL,
                        `linkType` TEXT NOT NULL DEFAULT 'رابط خارجي',
                        `notes` TEXT NOT NULL DEFAULT '',
                        `sortOrder` INTEGER NOT NULL DEFAULT 0,
                        `createdAt` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `generated_reports` (
                        `id` TEXT NOT NULL,
                        `caseId` TEXT NOT NULL DEFAULT '',
                        `caseNumber` TEXT NOT NULL DEFAULT '',
                        `clientName` TEXT NOT NULL DEFAULT '',
                        `templateId` TEXT NOT NULL DEFAULT '',
                        `title` TEXT NOT NULL,
                        `subtitle` TEXT NOT NULL DEFAULT '',
                        `sectionsJson` TEXT NOT NULL DEFAULT '[]',
                        `executiveSummary` TEXT NOT NULL DEFAULT '',
                        `completionNotes` TEXT NOT NULL DEFAULT '',
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        `format` TEXT NOT NULL DEFAULT 'PDF',
                        `filePath` TEXT NOT NULL DEFAULT '',
                        `isDeleted` INTEGER NOT NULL DEFAULT 0,
                        `deletedAt` INTEGER,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `report_templates` (
                        `id` TEXT NOT NULL,
                        `templateName` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `subtitle` TEXT NOT NULL,
                        `organizationName` TEXT NOT NULL DEFAULT 'منظومة جعفر بدران للأدلة الرقمية والاستشارات السيبرانية',
                        `primaryColorHex` TEXT NOT NULL DEFAULT '#00E5FF',
                        `accentColorHex` TEXT NOT NULL DEFAULT '#7C4DFF',
                        `introText` TEXT NOT NULL DEFAULT '',
                        `outroText` TEXT NOT NULL DEFAULT '',
                        `signatureTitle` TEXT NOT NULL DEFAULT 'المسؤول والخبير الجنائي',
                        `signatureName` TEXT NOT NULL DEFAULT 'جعفر بدران',
                        `footerText` TEXT NOT NULL DEFAULT 'وثيقة عمل رسمية صادرة ومعتمدة - منظومة جعفر بدران للأدلة الرقمية',
                        `visibleSectionsJson` TEXT NOT NULL DEFAULT '[]',
                        `sectionsOrderJson` TEXT NOT NULL DEFAULT '[]',
                        `isDefault` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `evidence` ADD COLUMN `category` TEXT NOT NULL DEFAULT 'مستندات'")
                db.execSQL("ALTER TABLE `evidence` ADD COLUMN `description` TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("ALTER TABLE `external_sheets` ADD COLUMN `sheetType` TEXT NOT NULL DEFAULT 'GRID'")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE `external_sheets` ADD COLUMN `hidden` INTEGER NOT NULL DEFAULT 0")
                } catch (_: Exception) {}
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jaffar_forensics.db"
                )
                .addMigrations(MIGRATION_4_5, MIGRATION_6_7, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11, MIGRATION_12_13)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance

                scope.launch(Dispatchers.IO) {
                    seedInitialData(instance)
                }

                instance
            }
        }

        suspend fun seedInitialData(db: AppDatabase) {
            try {
                // Seed 42 Certified Forensic Guides if empty
                if (db.knowledgeDao().getOfficialGuideCount() == 0) {
                    db.knowledgeDao().insertAll(PrepopulatedKnowledge.OFFICIAL_GUIDES)
                }

                // Seed Sample Cases if empty
                if (db.caseDao().getCount() == 0) {
                    val sampleCases = listOf(
                CaseEntity(
                    id = "case_2026_01",
                    caseNumber = "JB-2026-0814",
                    title = "ابتزاز رقمي وانتحال صفة مصرفية عبر واتساب",
                    clientName = "سارة ناصر الأحمد",
                    clientPhone = "+966501234567",
                    threatType = "ابتزاز",
                    priority = "حرجة",
                    status = "قيد المتابعة",
                    assignedInvestigator = "جعفر بدران (المسؤول الرئيسي)",
                    timelineEventsJson = "[{\"time\":\"2026-09-08 14:30\",\"event\":\"استلام الطلب وتوثيق المرفقات وتأمين الحسابات\"},{\"time\":\"2026-09-09 10:15\",\"event\":\"استخراج البصمة الرقمية والتواصل مع مزود الخدمة\"}]",
                    notes = "المبتز استخدم رقم VoIP دولي +1 وحساب تيليجرام مجهول. تم جمع البصمات وتأمين حسابات العميل."
                ),
                CaseEntity(
                    id = "case_2026_02",
                    caseNumber = "JB-2026-0815",
                    title = "تسريب بيانات حساسة ومحاولة اختراق بريد تنفيذي (CEO Fraud)",
                    clientName = "شركة أفق للاستشارات المالية",
                    clientPhone = "+966559876543",
                    threatType = "تسريب بيانات",
                    priority = "عالية",
                    status = "إحالة للجهات المختصة",
                    assignedInvestigator = "م. فهد القحطاني",
                    timelineEventsJson = "[{\"time\":\"2026-09-07 09:00\",\"event\":\"رصد نشاط غير مصرح به على الخادم الرئيسي\"},{\"time\":\"2026-09-08 17:00\",\"event\":\"إعداد ملف التوثيق الرسمي وإرساله للجهات المختصة\"}]",
                    notes = "محاولة تحويل مالي بقيمة 120 ألف دولار. تم اعتراض الحوالة وتجميد المعاملة بالتنسيق مع البنك."
                ),
                CaseEntity(
                    id = "case_2026_03",
                    caseNumber = "JB-2026-0816",
                    title = "انتحال شخصية صانع محتوى على إنستغرام للاحتيال",
                    clientName = "خالد بن صالح",
                    clientPhone = "+966543210987",
                    threatType = "انتحال صفة",
                    priority = "متوسطة",
                    status = "تم الحل بنجاح",
                    assignedInvestigator = "أ. ريم الشمري",
                    timelineEventsJson = "[{\"time\":\"2026-09-05 12:00\",\"event\":\"تقديم بلاغ انتحال معتمد عبر بوابة Meta LERS\"},{\"time\":\"2026-09-06 18:30\",\"event\":\"حظر الحساب المزيف نهائياً وتوثيق الحساب الأصلي\"}]",
                    notes = "تم حذف الحساب المنتحل واستعادة التفاعل الآمن للمتابعين."
                )
            )
            sampleCases.forEach { db.caseDao().insertOrUpdate(it) }
            }

            // Seed Sample Clients
            val sampleClients = listOf(
                ClientEntity(
                    id = "cli_01",
                    fullName = "سارة ناصر الأحمد",
                    phoneNumber = "+966 50 *** 4567",
                    encryptedEmail = "s***a@gmail.com",
                    riskLevel = "ابتزاز نشط",
                    notes = "حالة نفسية قلقة. تم تطبيق بروتوكول الإسعاف النفسي وتأمين الهاتف بحقيبة فاراداي.",
                    activeCaseId = "case_2026_01"
                ),
                ClientEntity(
                    id = "cli_02",
                    fullName = "شركة أفق للاستشارات المالية",
                    phoneNumber = "+966 11 *** 8899",
                    encryptedEmail = "c***o@ofoq.sa",
                    riskLevel = "عالي الخطورة",
                    notes = "تدقيق تقني وفحص شامل لخوادم البريد وتفعيل مفاتيح المرور FIDO2.",
                    activeCaseId = "case_2026_02"
                ),
                ClientEntity(
                    id = "cli_03",
                    fullName = "خالد بن صالح",
                    phoneNumber = "+966 54 *** 0987",
                    encryptedEmail = "k***d@outlook.com",
                    riskLevel = "متابعة دورية",
                    notes = "تم حل القضية ويخضع للمتابعة الأمنية الشهرية.",
                    activeCaseId = "case_2026_03"
                )
            )
            sampleClients.forEach { db.clientDao().insertOrUpdate(it) }

            // Seed Sample Evidence
            val sampleEvidence = listOf(
                EvidenceEntity(
                    id = "evi_01",
                    caseId = "case_2026_01",
                    caseNumber = "JB-2026-0814",
                    evidenceName = "لقطة شاشة محادثة التهديد المالي",
                    fileType = "لقطة شاشة",
                    originalFilename = "screenshot_threat_whatsapp_01.png",
                    md5Hash = "e4d909c290d0fb1ca068ffaddf22cbd0",
                    sha256Hash = "8f434346648f6b96df89dda901c5176b10a6d83961dd3c1ac88b59b2dc327aa4",
                    exifDeviceModel = "Apple iPhone 15 Pro",
                    exifSoftware = "iOS 18.1 / WhatsApp Messenger 24.18.78",
                    exifGpsCoords = "24.7136° N, 46.6753° E (الرياض)",
                    exifTimestamp = "2026-09-08 14:12:05 UTC+3",
                    chainOfCustodyLog = "تم استخراج الصورة وفحص سلامتها التشفيرية بواسطة جعفر بدران - معمل الأدلة الرقمية",
                    notes = "الصورة لم تخضع لأي تلاعب أو تعديل ببرامج تحرير. خريطة ELA أظهرت ضغطاً متجانساً."
                ),
                EvidenceEntity(
                    id = "evi_02",
                    caseId = "case_2026_02",
                    caseNumber = "JB-2026-0815",
                    evidenceName = "تفريغ سجلات هجوم البريد (Server Access Dump)",
                    fileType = "تفريغ شبكي",
                    originalFilename = "mail_auth_logs_audit.log",
                    md5Hash = "3b712de48137572f3849aafd5666cae3",
                    sha256Hash = "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3",
                    exifDeviceModel = "Linux Ubuntu 24.04 LTS Server",
                    exifSoftware = "Postfix / OpenSSH 9.6p1",
                    exifGpsCoords = "غير محدد (خادم محمي)",
                    exifTimestamp = "2026-09-07 08:44:21 UTC",
                    chainOfCustodyLog = "تم استخراج السجل وتشفيره وحفظ البصمة بواسطة م. فهد القحطاني",
                    notes = "تأكيد محاولات Brute Force قادمة من 3 عناوين IP ممررة عبر شبكة Tor."
                )
            )
            sampleEvidence.forEach { db.evidenceDao().insertOrUpdate(it) }

            // Seed Initial Content Studio Items
            val sampleContent = listOf(
                ContentEntity(
                    id = "cnt_1725968400000_init01",
                    title = "ماذا تفعل إذا تعرضت للابتزاز الإلكتروني الآن؟ (خطة الطوارئ 5 خطوات)",
                    body = """🚨 تعرضت أو تعرض شخص تعرفه للابتزاز بصور أو محادثات خاصة؟ 

إليك الخطوات الخمس الفورية التي تحميك:
1️⃣ لا تدفع فلساً واحداً: الدفع يثبت قابليتك للرضوخ ويضاعف طلبات المبتز 10 أضعاف.
2️⃣ لا تحذف المحادثات: الأدلة والرسائل والبصمات هي سلاحنا القانوني للوصول للفاعل.
3️⃣ نفّذ تمرين التنفس 4-7-8 لتهدئة ضربات قلبك واستعادة التفكير المنطقي.
4️⃣ تواصل فوراً مع جهة مختصة أو خبير أمني موثوق.
5️⃣ قُم بتسجيل بصمة صورك عبر منصة StopNCII.org لحظر نشرها عالمياً فوراً.

أنت لست وحدك، والحل متاح قانونياً وتقنياً.""",
                    platform = "Instagram",
                    status = "منشور",
                    tagsJson = "#أمن_سيبراني, #مكافحة_الابتزاز, #جعفر_بدران, #توثيق_رقمي, #حماية_البيانات",
                    scheduledTimestamp = null
                ),
                ContentEntity(
                    id = "cnt_1725968410000_init02",
                    title = "احذر فخ رسائل الواتساب 'أرسل لي الكود بالغلط'",
                    body = """⚠️ تنبيه أمني عاجل لمستخدمي واتساب:
حملة اختراق نشطة تعتمد على انتحال صفة صديق مقرب أو قريب، يطلب منك إرسال رمز التحقق (SMS) الذي وصلك بحجة أنه أرسله بالخطأ.

تذكر دائماً:
❌ رمز التحقق لا يُشارك مع أي كائن حي، حتى لو كان أخاك أو والدك!
✅ قم فوراً بتفعيل التحقق بخطوتين (Two-Step Verification) مع وضع بريد إلكتروني حقيقي للاسترداد.""",
                    platform = "X",
                    status = "مجدول",
                    tagsJson = "#واتساب, #أمن_المعلومات, #اختراق, #توعية_سيبرانية",
                    scheduledTimestamp = System.currentTimeMillis() + 86400000
                ),
                ContentEntity(
                    id = "cnt_1725968420000_init03",
                    title = "كيف تتحقق من الصور المفبركة عبر الـ ELA والبصمات الرقمية؟",
                    body = """هل تشك في صحة صورة أو مستند رقمي؟
في مختبر الفحص الفني، نعتمد على تحليل مستويات الخطأ (Error Level Analysis - ELA) لرصد التفاوت في معدل ضغط البكسلات، مما يكشف الأجزاء المركبة والمعدلة في ثوانٍ معدودة.

سنشرح ذلك بالتفصيل في الثريد القادم!""",
                    platform = "Telegram",
                    status = "مسودة",
                    tagsJson = "#ملفات_رقمية, #تزييف_عميق, #فحص_الصور, #فحص_فني",
                    scheduledTimestamp = null
                )
            )
            sampleContent.forEach { db.contentDao().insertOrUpdate(it) }

            // Seed Initial Tasks
            val sampleTasks = listOf(
                TaskEntity(
                    id = "tsk_01",
                    title = "حفظ وتوثيق النسخة الاحتياطية لسجلات خادم البريد",
                    description = "استخراج ملفات auth.log وحساب بصمة SHA-256 قبل انتهاء مدة التدوير",
                    priority = "حرجة",
                    dueDate = "اليوم، 04:00 م",
                    status = "قيد التنفيذ",
                    relatedCaseId = "case_2026_02",
                    relatedCaseNumber = "JB-2026-0815"
                ),
                TaskEntity(
                    id = "tsk_02",
                    title = "متابعة طلب الحظر العاجل عبر Meta LERS",
                    description = "التحقق من استجابة الدعم الفني لحظر حساب انتحال الصفة وتثبيت الهوية الرسمية",
                    priority = "عالية",
                    dueDate = "غداً، 11:00 ص",
                    status = "جديدة",
                    relatedCaseId = "case_2026_03",
                    relatedCaseNumber = "JB-2026-0816"
                ),
                TaskEntity(
                    id = "tsk_03",
                    title = "جلسة الدعم الاستشاري والتهدئة للعميل",
                    description = "متابعة تفعيل التحقق بخطوتين والاطمئنان على استقرار الحالة بعد إحباط الابتزاز",
                    priority = "متوسطة",
                    dueDate = "12 سبتمبر 2026",
                    status = "مكتملة",
                    relatedCaseId = "case_2026_01",
                    relatedCaseNumber = "JB-2026-0814",
                    completedAt = System.currentTimeMillis()
                )
            )
            sampleTasks.forEach { db.taskDao().insertOrUpdate(it) }

            // Initial App Settings
            val initialSettings = listOf(
                AppSettingsEntity("sync_provider", "GOOGLE_SHEETS"),
                AppSettingsEntity("sheet_id", "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms"),
                AppSettingsEntity("web_app_url", "https://script.google.com/macros/s/AKfycbz_SAMPLE_APPSSCRIPT_ENDPOINT/exec"),
                AppSettingsEntity("auto_sync_enabled", "true"),
                AppSettingsEntity("app_theme", "DARK"),
                AppSettingsEntity("screenshot_protection", "false"),
                AppSettingsEntity("conflict_resolution", "LOCAL_WINS")
            )
            initialSettings.forEach { db.settingsDao().saveSetting(it) }

            // Initial Audit Log
            db.auditLogDao().insertLog(
                AuditLogEntity(
                    id = "log_system_init",
                    actionType = "LOGIN",
                    module = "AUTH",
                    entityId = "user_jaffar",
                    performedBy = "جعفر بدران",
                    userRole = "Super Admin",
                    details = "تسجيل دخول وبدء تشغيل منظومة إدارة القضايا محلياً وتأمين الحاويات المشفرة."
                )
            )

            // Seed Direct Support Forms
            if (db.supportFormDao().getCount() < PrepopulatedSupportForms.OFFICIAL_FORMS.size) {
                db.supportFormDao().insertAll(PrepopulatedSupportForms.OFFICIAL_FORMS)
            }

            // Seed Digital Investigation Tools
            if (db.investigationToolDao().getCount() < PrepopulatedInvestigationTools.OFFICIAL_TOOLS.size) {
                db.investigationToolDao().insertAll(PrepopulatedInvestigationTools.OFFICIAL_TOOLS)
            }

            // Seed Initial External Requests Source & Sheets only if no sources exist
            if (db.externalRequestSourceDao().getAllSourcesList().isEmpty()) {
                val defaultSource = ExternalRequestSourceEntity(
                    id = "src_demo_01",
                    name = "طلبات العملاء - البلاغات العامة",
                    publicUrl = "https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit?usp=sharing",
                    spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms",
                    sourceType = "GOOGLE_SHEETS",
                    enabled = true,
                    status = "متصل",
                    lastSync = System.currentTimeMillis()
                )
                db.externalRequestSourceDao().insertOrUpdate(defaultSource)

                val defaultSheets = listOf(
                    ExternalSheetEntity(
                        id = "src_demo_01_0",
                        sourceId = "src_demo_01",
                        sheetId = "0",
                        sheetName = "الطلبات الجديدة",
                        enabled = true,
                        ignored = false,
                        rowCount = 3,
                        columnCount = 7,
                        lastSync = System.currentTimeMillis()
                    ),
                    ExternalSheetEntity(
                        id = "src_demo_01_1",
                        sourceId = "src_demo_01",
                        sheetId = "1",
                        sheetName = "طلبات قيد المراجعة",
                        enabled = true,
                        ignored = false,
                        rowCount = 1,
                        columnCount = 7,
                        lastSync = System.currentTimeMillis()
                    ),
                    ExternalSheetEntity(
                        id = "src_demo_01_2",
                        sourceId = "src_demo_01",
                        sheetId = "2",
                        sheetName = "أرشيف الطلبات",
                        enabled = false,
                        ignored = true,
                        rowCount = 8,
                        columnCount = 6,
                        lastSync = System.currentTimeMillis()
                    )
                )
                db.externalSheetDao().insertAll(defaultSheets)

                val defaultRequests = listOf(
                    ExternalRequestEntity(
                        id = "src_demo_01_1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms_0_row_1",
                        sourceId = "src_demo_01",
                        spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms",
                        sheetId = "0",
                        sheetName = "الطلبات الجديدة",
                        rowId = "row_1",
                        requestNumber = "EXT-2026-001",
                        clientName = "عبدالله بن فهد التميمي",
                        clientPhone = "+966551122334",
                        clientEmail = "abdullah.tamimi@example.com",
                        description = "تعرض حساب بنكي وتطبيق تجاري لمحاولة اختراق بروابط تصيد تطلب OTP وتحويلات مشبوهة.",
                        urgency = "حرجة",
                        status = "جديد",
                        receivedAt = "2026-09-10 10:15",
                        internalNotes = "حالة عاجلة - تم التواصل المبدئي لتأمين الحسابات البنكية فوراً",
                        rawData = "{\"الاسم\":\"عبدالله بن فهد التميمي\",\"الهاتف\":\"+966551122334\",\"الطلب\":\"تعرض حساب بنكي لمحاولة اختراق\"}",
                        additionalFields = "{\"المدينة\":\"الرياض\",\"الجهة المصرفية المستهدفة\":\"مصرف الراجحي\",\"نوع الرابط المشبوه\":\"sms-phishing\"}"
                    ),
                    ExternalRequestEntity(
                        id = "src_demo_01_1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms_0_row_2",
                        sourceId = "src_demo_01",
                        spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms",
                        sheetId = "0",
                        sheetName = "الطلبات الجديدة",
                        rowId = "row_2",
                        requestNumber = "EXT-2026-002",
                        clientName = "منى خالد السبيعي",
                        clientPhone = "+966549876543",
                        clientEmail = "muna.subaie@example.com",
                        description = "ابتزاز رقمي وتهديد بنشر صور عائلية تم سحبها بعد تثبيت تطبيق ملغوم بصيغة APK.",
                        urgency = "حرجة",
                        status = "جديد",
                        receivedAt = "2026-09-10 09:30",
                        internalNotes = "مطلوب فحص حزمة الـ APK وحفظ وتوثيق المرفقات التقنية",
                        rawData = "{\"الاسم\":\"منى خالد السبيعي\",\"الهاتف\":\"+966549876543\",\"الطلب\":\"ابتزاز رقمي عبر APK ملغوم\"}",
                        additionalFields = "{\"المدينة\":\"جدة\",\"الملف المشبوه\":\"update_play_services.apk\",\"رقم المبتز\":\"+9647700000000\"}"
                    ),
                    ExternalRequestEntity(
                        id = "src_demo_01_1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms_0_row_3",
                        sourceId = "src_demo_01",
                        spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms",
                        sheetId = "0",
                        sheetName = "الطلبات الجديدة",
                        rowId = "row_3",
                        requestNumber = "EXT-2026-003",
                        clientName = "سلطان عبد العزيز الشهري",
                        clientPhone = "+966567788990",
                        clientEmail = "sultan.sh@example.com",
                        description = "انتحال اسم وعلامة تجارية لمؤسستنا وتصميم موقع توظيف وهمي لجمع السير الذاتية وتمرير احتيال مالي.",
                        urgency = "عالية",
                        status = "قيد المراجعة",
                        receivedAt = "2026-09-09 16:45",
                        internalNotes = "تم تنفيذ فحص Whois على النطاق المشبوه وتحديد شركة الاستضافة",
                        rawData = "{\"الاسم\":\"سلطان عبد العزيز الشهري\",\"الهاتف\":\"+966567788990\",\"الطلب\":\"انتحال علامة تجارية\"}",
                        additionalFields = "{\"النطاق المزيف\":\"careers-al-shehri-portal.com\",\"الدولة\":\"السعودية\"}"
                    )
                )
                db.externalRequestDao().insertAll(defaultRequests)
            }

            // Seed App Sections if empty
            if (db.appSectionConfigDao().getCount() == 0) {
                val defaultSections = listOf(
                    AppSectionConfigEntity("DASHBOARD", "الرئيسية", "لوحة المعلومات والملخص السريع", "Home", 0, isVisible = true, isCustom = false, category = "الرئيسية", showInBottomNav = true, bottomNavOrder = 0, isDefaultStartScreen = true),
                    AppSectionConfigEntity("CASES", "القضايا", "إدارة وتتبع القضايا الجنائية والسيبرانية", "Folder", 1, isVisible = true, isCustom = false, category = "العمليات", showInBottomNav = true, bottomNavOrder = 1),
                    AppSectionConfigEntity("EXTERNAL_REQUESTS", "الطلبات الخارجية", "استقبال وإدارة الطلبات الواردة وجداول العمل", "CloudDownload", 2, isVisible = true, isCustom = false, category = "العمليات"),
                    AppSectionConfigEntity("EVIDENCE", "المرفقات والملفات", "توثيق الأدلة والبصمات الرقمية وتتبع الحيازة", "AttachFile", 3, isVisible = true, isCustom = false, category = "الأدلة والتحقيق", showInBottomNav = true, bottomNavOrder = 2),
                    AppSectionConfigEntity("TASKS", "المهام والتقويم", "توزيع ومتابعة المهام ومواعيد المتابعة", "Assignment", 4, isVisible = true, isCustom = false, category = "العمليات", showInBottomNav = true, bottomNavOrder = 3),
                    AppSectionConfigEntity("SUPPORT_FORMS", "نماذج الدعم المباشرة", "بوابات الدعم الفني الرسمية ومنصات التواصل", "ContactSupport", 5, isVisible = true, isCustom = false, category = "المصادر والأدوات"),
                    AppSectionConfigEntity("INVESTIGATION_HUB", "أدوات ومصادر العمل", "أدوات التحقق والتقصي وOSINT وتحليل الشبكات", "TravelExplore", 6, isVisible = true, isCustom = false, category = "المصادر والأدوات"),
                    AppSectionConfigEntity("STUDIO", "استوديو المحتوى", "إدارة أفكار وسكربتات ومسودات التوعية الأمنية", "AutoAwesome", 7, isVisible = true, isCustom = false, category = "الإعلام والتوعية", showInBottomNav = true, bottomNavOrder = 4),
                    AppSectionConfigEntity("CLIENTS", "العملاء", "سجل العملاء وإدارة العلاقات ومستوى الخطورة", "People", 8, isVisible = true, isCustom = false, category = "العمليات"),
                    AppSectionConfigEntity("KNOWLEDGE", "الموسوعة المعرفية", "الأدلة الإجرائية والأنظمة والسياسات الرسمية", "MenuBook", 9, isVisible = true, isCustom = false, category = "المعرفة"),
                    AppSectionConfigEntity("REPORTS", "تقارير العمل والمتابعة", "لوحة الأرباح والتحصيلات وإصدار تقارير العمل", "Assessment", 10, isVisible = true, isCustom = false, category = "المالية والتقارير"),
                    AppSectionConfigEntity("SEARCH", "البحث الشامل", "محرك بحث عميق عبر جميع أقسام المنظومة", "Search", 11, isVisible = true, isCustom = false, category = "أدوات عامة"),
                    AppSectionConfigEntity("TRASH", "سلة المحذوفات", "استعادة أو التطهير النهائي للعناصر المحذوفة", "Delete", 12, isVisible = true, isCustom = false, category = "الصيانة"),
                    AppSectionConfigEntity("SETTINGS", "الإعدادات والمزامنة", "التحكم بالنظام وإدارة المنظومة والربط السحابي", "Settings", 13, isVisible = true, isCustom = false, category = "النظام"),
                    AppSectionConfigEntity("SECURITY", "سجل الأمان والتدقيق", "مراقبة العمليات والتحقق البيومتري والتأمين", "Security", 14, isVisible = true, isCustom = false, category = "النظام")
                )
                db.appSectionConfigDao().insertAll(defaultSections)
            } else {
                // Ensure default bottom nav items are activated if not already configured
                val defaultBottomIds = listOf("DASHBOARD", "CASES", "EVIDENCE", "TASKS", "STUDIO")
                defaultBottomIds.forEachIndexed { index, secId ->
                    val sec = db.appSectionConfigDao().getSectionById(secId)
                    if (sec != null && !sec.showInBottomNav) {
                        db.appSectionConfigDao().updateBottomNavConfig(secId, true, index)
                    }
                }
            }

            // Seed Report Templates if empty
            if (db.reportTemplateDao().getCount() == 0) {
                db.reportTemplateDao().insertAll(PrepopulatedReportTemplates.DEFAULT_TEMPLATES)
            }

            // Seed System Categories
            val defaultCategories = listOf(
                SystemCategoryEntity("cat_case_01", "CASES", "ابتزاز رقمي", "#E53935", 0),
                SystemCategoryEntity("cat_case_02", "CASES", "احتيال مالي", "#FB8C00", 1),
                SystemCategoryEntity("cat_case_03", "CASES", "انتحال شخصية", "#8E24AA", 2),
                SystemCategoryEntity("cat_case_04", "CASES", "اختراق وتجسس", "#D81B60", 3),
                SystemCategoryEntity("cat_case_05", "CASES", "فحص جنائي", "#1E88E5", 4),
                SystemCategoryEntity("cat_tool_01", "TOOLS", "استخبارات المصادر المفتوحة (OSINT)", "#00ACC1", 0),
                SystemCategoryEntity("cat_tool_02", "TOOLS", "فحص الروابط والملفات المشبوهة", "#43A047", 1),
                SystemCategoryEntity("cat_tool_03", "TOOLS", "التحقق من الهوية والصور", "#3949AB", 2),
                SystemCategoryEntity("cat_tool_04", "TOOLS", "تحليل التشفير والبصمات", "#5E35B1", 3),
                SystemCategoryEntity("cat_form_01", "FORMS", "بوابات الإنفاذ والجرائم الإلكترونية", "#D32F2F", 0),
                SystemCategoryEntity("cat_form_02", "FORMS", "منصات التواصل الاجتماعي الرسمية", "#1976D2", 1),
                SystemCategoryEntity("cat_form_03", "FORMS", "مزودو خدمات الاستضافة والبريد", "#388E3C", 2),
                SystemCategoryEntity("cat_exp_01", "EXPENSES", "أدوات وبرامج تقنية", "#0288D1", 0),
                SystemCategoryEntity("cat_exp_02", "EXPENSES", "سيرفرات واستضافة وسحابة", "#7B1FA2", 1),
                SystemCategoryEntity("cat_exp_03", "EXPENSES", "مصاريف إدارية ومكتبية", "#689F38", 2),
                SystemCategoryEntity("cat_exp_04", "EXPENSES", "استشارات وفريق عمل", "#F57C00", 3),
                // Case Files Categories (ملفات القضية)
                SystemCategoryEntity("cat_file_01", "CASE_FILES", "صور", "#00E5FF", 0),
                SystemCategoryEntity("cat_file_02", "CASE_FILES", "مستندات", "#2979FF", 1),
                SystemCategoryEntity("cat_file_03", "CASE_FILES", "مراسلات", "#7C4DFF", 2),
                SystemCategoryEntity("cat_file_04", "CASE_FILES", "تقارير", "#00B0FF", 3),
                SystemCategoryEntity("cat_file_05", "CASE_FILES", "مرفقات العميل", "#00E676", 4),
                SystemCategoryEntity("cat_file_06", "CASE_FILES", "مرفقات المنصة", "#FF9100", 5),
                SystemCategoryEntity("cat_file_07", "CASE_FILES", "فواتير", "#FFD600", 6),
                SystemCategoryEntity("cat_file_08", "CASE_FILES", "أخرى", "#78909C", 7)
            )
            db.systemCategoryDao().insertAll(defaultCategories)

            // Seed Comprehensive Official Sources & Portals (Ensures all official platforms are present)
            db.officialSourceDao().insertAll(PrepopulatedOfficialSources.SOURCES)

            // Seed Profit Share Rules
            if (db.profitShareRuleDao().getCount() == 0) {
                db.profitShareRuleDao().insertAll(PrepopulatedOfficialSources.DEFAULT_PROFIT_RULES)
            }

            // Seed Financial Revenues
            if (db.financialRevenueDao().getCount() == 0) {
                db.financialRevenueDao().insertAll(PrepopulatedOfficialSources.SAMPLE_FINANCIAL_REVENUES)
            }
        } catch (e: Exception) {
            android.util.Log.e("AppDatabase", "Error during seedInitialData", e)
        }
    }
}
}

