package com.example.data.local

import com.example.data.local.entities.OfficialSourceEntity

object PrepopulatedOfficialSourcesExpanded6 {
    val ADDITIONAL_SOURCES_PART6 = listOf(
        // ==========================================
        // TELECOM, SATELLITE & MARITIME SAFETY
        // ==========================================
        OfficialSourceEntity(
            id = "src_tel_itu_cyber",
            name = "الاتحاد الدولي للاتصالات - قطاع التنمية السيبرانية (ITU)",
            companyOrEntity = "International Telecommunication Union (United Nations)",
            sectionType = PrepopulatedOfficialSources.SEC_TELECOM_DEVICES,
            category = "الاتصالات والهواتف والأجهزة",
            portalType = "وكالة الأمم المتحدة المتخصصة في تكنولوجيا المعلومات والاتصالات",
            officialUrl = "https://www.itu.int/en/ITU-D/Cybersecurity/",
            description = "المؤشر العالمي للأمن السيبراني (GCI) ووضع المعايير التقنية الدولية لتأمين خطوط الاتصالات وشبكات الجيل الخامس 5G.",
            region = "دولي (193 دولة عضواً)",
            requirements = "مستندات التقييم والمعايير الفنية الدولية",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 200
        ),
        OfficialSourceEntity(
            id = "src_tel_fcc_csrc",
            name = "لجنة الاتصالات الفيدرالية الأمريكية - أمان الاتصالات (FCC CSRIC)",
            companyOrEntity = "Federal Communications Commission (FCC USA)",
            sectionType = PrepopulatedOfficialSources.SEC_TELECOM_DEVICES,
            category = "الاتصالات والهواتف والأجهزة",
            portalType = "مجلس أمان وموثوقية الاتصالات الفيدرالي",
            officialUrl = "https://www.fcc.gov",
            description = "تنظيم الاتصالات ومكافحة المكالمات الآلية الاحتيالية (Robocalls) وتطبيق بروتوكول STIR/SHAKEN للتحقق من هوية المتصل.",
            region = "الولايات المتحدة ودولياً",
            requirements = "بوابة تقديم شكاوى انتحال أرقام الهواتف (Caller ID Spoofing)",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 201
        ),
        OfficialSourceEntity(
            id = "src_mar_imo_cyber",
            name = "المنظمة البحرية الدولية - أمن السفن والموانئ (IMO Cyber Risk Management)",
            companyOrEntity = "International Maritime Organization (IMO)",
            sectionType = PrepopulatedOfficialSources.SEC_LEGAL_REGULATORY,
            category = "المصادر القانونية والتنظيمية",
            portalType = "إدارة المخاطر السيبرانية للسفن والملاحة البحرية الدولية",
            officialUrl = "https://www.imo.org",
            description = "اللوائح الإلزامية لحماية أنظمة الملاحة البحرية (ECDIS) وأنظمة التعريف الآلي (AIS) من الاختراق والتشويش والتضليل.",
            region = "دولي",
            requirements = "إرشادات السلامة البحرية وتدقيق السفن المعتمدة",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 202
        ),

        // ==========================================
        // CLOUD & HOSTING INFRASTRUCTURE COMPLIANCE
        // ==========================================
        OfficialSourceEntity(
            id = "src_cloud_aws_compliance",
            name = "AWS Trust & Safety (Amazon Web Services)",
            companyOrEntity = "Amazon Web Services Inc.",
            sectionType = PrepopulatedOfficialSources.SEC_HOSTING_CLOUD,
            category = "الاستضافة والنطاقات والخدمات السحابية",
            portalType = "بوابة الثقة والسلامة والامتثال السحابي",
            officialUrl = "https://aws.amazon.com/compliance/",
            description = "البوابة الرسمية للإبلاغ عن استغلال خوادم AWS في هجمات DDoS أو استضافة صفحات التصيد وتوزيع البرمجيات الضارة.",
            region = "عالمي",
            requirements = "تقديم بلاغ انتهاك فني مع سجلات الروابط والـ IP المشبوهة",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 210
        ),
        OfficialSourceEntity(
            id = "src_cloud_gcp_abuse",
            name = "Google Cloud Platform (GCP) Trust & Safety",
            companyOrEntity = "Google Cloud (Alphabet Inc.)",
            sectionType = PrepopulatedOfficialSources.SEC_HOSTING_CLOUD,
            category = "الاستضافة والنطاقات والخدمات السحابية",
            portalType = "بوابة التحقيق في إساءة استخدام السحابة وتعدين العملات غير المصرح به",
            officialUrl = "https://support.google.com/code/contact/cloud_platform_report",
            description = "إسقاط الخوادم السحابية والمشاريع المخترقة على منصة Google Cloud المستخدمة في منصات التجسس وسرقة البيانات.",
            region = "عالمي",
            requirements = "نموذج تقديم بلاغات إساءة الاستخدام المباشر",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 211
        ),
        OfficialSourceEntity(
            id = "src_cloud_azure_security",
            name = "Microsoft Azure Trust Center & Abuse",
            companyOrEntity = "Microsoft Azure",
            sectionType = PrepopulatedOfficialSources.SEC_HOSTING_CLOUD,
            category = "الاستضافة والنطاقات والخدمات السحابية",
            portalType = "مركز الثقة والأمان وإسقاط المواقع الخبيثة على آزور",
            officialUrl = "https://msrc.microsoft.com/report/abuse",
            description = "التحقيق في استخدام خوادم Azure الافتراضية في توجيه هجمات الفدية وتجميد اشتراكات الحسابات المخالفة لشروط الخدمة.",
            region = "عالمي",
            requirements = "إرفاق الأدلة الرقمية وسجلات الترافيك وعناوين URL",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 212
        ),

        // ==========================================
        // CRYPTOCURRENCY EXCHANGES & FINANCIAL FRAUD
        // ==========================================
        OfficialSourceEntity(
            id = "src_crypto_tether_compliance",
            name = "Tether Compliance & Freeze Team (USDT)",
            companyOrEntity = "Tether Operations Limited",
            sectionType = PrepopulatedOfficialSources.SEC_COMMERCE_ADS,
            category = "الإعلانات والمدفوعات والحسابات التجارية",
            portalType = "إدارة الامتثال وتجميد عناوين محافظ USDT المتورطة في الجرائم",
            officialUrl = "https://tether.to/en/compliance/",
            description = "الجهة المسؤولة عن تنفيذ أوامر جهات إنفاذ القانون الدولية لإدراج محافظ الهاكرز في القائمة السوداء وتجميد ملايين الدولارات من عملة USDT.",
            region = "عالمي",
            requirements = "طلب رسمي من جهة أمنية حكومية معتمدة أو مذكرة قضائية",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 220
        ),
        OfficialSourceEntity(
            id = "src_crypto_circle_compliance",
            name = "Circle Compliance & Legal (USDC)",
            companyOrEntity = "Circle Internet Financial LLC",
            sectionType = PrepopulatedOfficialSources.SEC_COMMERCE_ADS,
            category = "الإعلانات والمدفوعات والحسابات التجارية",
            portalType = "فريق الامتثال المالي وتجميد أصول USDC الخاضعة للعقوبات",
            officialUrl = "https://www.circle.com/en/legal/compliance",
            description = "الامتثال لأوامر مكتب مراقبة الأصول الأجنبية الأمريكي (OFAC) وأجهزة القضاء لتجميد العملات الرقمية المستولى عليها بالاحتيال.",
            region = "عالمي / الولايات المتحدة",
            requirements = "أمر تجميد قانوني أو إدراج في لوائح العقوبات الدولية",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 221
        ),
        OfficialSourceEntity(
            id = "src_crypto_chainalysis_trust",
            name = "Chainalysis Investigations & Public Sector",
            companyOrEntity = "Chainalysis Inc.",
            sectionType = PrepopulatedOfficialSources.SEC_OSINT_INVESTIGATION,
            category = "مركز التحقق والتقصي",
            portalType = "المنصة الرائدة لدعم التحقيقات الجنائية للقطاع العام والشرطة",
            officialUrl = "https://www.chainalysis.com",
            description = "تزويد وكالات الاستخبارات وإنفاذ القانون ببرمجيات Chainalysis Reactor لرسم خرائط تفكيك شبكات تبييض العملات المشفرة.",
            region = "عالمي",
            requirements = "عقود وتراخيص حكومية وتدريب جنائي معتمد",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 222
        ),

        // ==========================================
        // REGIONAL ANTI-FRAUD & CONSUMER PROTECTION
        // ==========================================
        OfficialSourceEntity(
            id = "src_reg_sama_care",
            name = "البنك المركزي السعودي (ساما) - حماية العملاء ومكافحة الاحتيال المالي",
            companyOrEntity = "Saudi Central Bank (SAMA)",
            sectionType = PrepopulatedOfficialSources.SEC_REGIONAL_LOCAL,
            category = "مصادر الدعم الإقليمية والمحلية",
            portalType = "المنظومة الرقابية للقطاع المصرفي ومكافحة الحسابات الوهمية",
            officialUrl = "https://www.sama.gov.sa",
            description = "التعليمات الصارمة للبنوك السعودية بإيقاف الحسابات المشبوهة، حماية المودعين، وتطبيق آليات استرداد الحوالات الاحتيالية.",
            region = "المملكة العربية السعودية",
            requirements = "بوابة ساما تهتم لتقديم الشكاوى المصرفية ضد البنوك المخالفة",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 230
        ),
        OfficialSourceEntity(
            id = "src_reg_cbuae_fraud",
            name = "مصرف الإمارات العربية المتحدة المركزي - حماية المستهلك المالي",
            companyOrEntity = "Central Bank of the UAE (CBUAE)",
            sectionType = PrepopulatedOfficialSources.SEC_REGIONAL_LOCAL,
            category = "مصادر الدعم الإقليمية والمحلية",
            portalType = "وحدة حماية المستهلك المصرفي ومكافحة الاحتيال",
            officialUrl = "https://www.centralbank.ae",
            description = "وضع ضوابط الحماية من الاحتيال المصرفي الإلكتروني وإلزام البنوك بالتعويض عند ثبوت القصور الأمني في الحسابات.",
            region = "دولة الإمارات العربية المتحدة",
            requirements = "بوابة تقديم الشكاوى للمستهلك المالي (سندك / Sanadak)",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 231
        ),
        OfficialSourceEntity(
            id = "src_reg_cbe_fraud",
            name = "البنك المركزي المصري - قطاع الأمن السيبراني المصرفي",
            companyOrEntity = "Central Bank of Egypt (CBE)",
            sectionType = PrepopulatedOfficialSources.SEC_REGIONAL_LOCAL,
            category = "مصادر الدعم الإقليمية والمحلية",
            portalType = "المركز القطاعي للاستجابة لطوارئ الحاسب بالبنوك (EG-FinCIRT)",
            officialUrl = "https://www.cbe.org.eg",
            description = "حماية الجهاز المصرفي المصري من الهجمات الإلكترونية ومتابعة عمليات سرقة البطاقات المصرفية وتوعية المواطنين.",
            region = "جمهورية مصر العربية",
            requirements = "بوابة شكاوى العملاء والاتصال المصرفي",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 232
        )
    )
}
