package com.example.data.local

import com.example.data.local.entities.OfficialSourceEntity

object PrepopulatedOfficialSourcesExpanded3 {
    val ADDITIONAL_SOURCES_PART3 = listOf(
        // ==========================================
        // INTERNATIONAL LAW ENFORCEMENT CYBER DIVISIONS
        // ==========================================
        OfficialSourceEntity(
            id = "src_le_fbi_ic3",
            name = "FBI Internet Crime Complaint Center (IC3)",
            companyOrEntity = "مكتب التحقيقات الفيدرالي الأمريكي (FBI)",
            sectionType = PrepopulatedOfficialSources.SEC_LAW_ENFORCEMENT,
            category = "إنفاذ القانون والقضاء",
            portalType = "المركز الوطني لتلقي شكاوى جرائم الإنترنت عالمياً",
            officialUrl = "https://www.ic3.gov",
            description = "البوابة المركزية للحكومة الأمريكية لتلقي بلاغات الاحتيال المالي، هجمات الفدية، وسرقة العملات المشفرة وإحالتها للفرق الفيدرالية المختصة.",
            region = "الولايات المتحدة وعالمياً",
            requirements = "تفاصيل المعاملة المالية وأرقام المحافظ والحسابات والبريد الإلكتروني",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 100
        ),
        OfficialSourceEntity(
            id = "src_le_us_secretservice",
            name = "U.S. Secret Service Cyber Fraud Task Forces (CFTF)",
            companyOrEntity = "جهاز الخدمة السرية الأمريكي (USSS)",
            sectionType = PrepopulatedOfficialSources.SEC_LAW_ENFORCEMENT,
            category = "إنفاذ القانون والقضاء",
            portalType = "فرق مكافحة الاحتيال السيبراني وتتبع الأموال",
            officialUrl = "https://www.secretservice.gov/investigation/cyber",
            description = "مكافحة الهجمات المالية المتطورة، اختراق البريد الإلكتروني للشركات (BEC)، وتتبع ومصادرة أموال الفدية المشفرة.",
            region = "الولايات المتحدة ودولياً",
            requirements = "إبلاغ فوري عند سرقة الحوالات المصرفية الكبرى لتفعيل بروتوكول التجميد السريع (Kill Chain)",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 101
        ),
        OfficialSourceEntity(
            id = "src_le_uk_nca",
            name = "وكالة الجريمة الوطنية البريطانية (NCA National Cyber Crime Unit)",
            companyOrEntity = "National Crime Agency (NCA UK)",
            sectionType = PrepopulatedOfficialSources.SEC_LAW_ENFORCEMENT,
            category = "إنفاذ القانون والقضاء",
            portalType = "الوحدة الوطنية العليا لمكافحة الجريمة السيبرانية",
            officialUrl = "https://www.nationalcrimeagency.gov.uk/what-we-do/crime-threats/cyber-crime",
            description = "ملاحقة شبكات القرصنة الدولية المنظمة، استهداف منصات الدارك نت، وتعطيل البنية التحتية لبرمجيات الفدية.",
            region = "المملكة المتحدة ودولياً",
            requirements = "التنسيق عبر الشرطة البريطانية أو الشركاء الدوليين",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 102
        ),
        OfficialSourceEntity(
            id = "src_le_germany_bka",
            name = "المكتب الاتحادي للشرطة الجنائية الألمانية (BKA Cybercrime)",
            companyOrEntity = "Bundeskriminalamt (BKA Germany)",
            sectionType = PrepopulatedOfficialSources.SEC_LAW_ENFORCEMENT,
            category = "إنفاذ القانون والقضاء",
            portalType = "الإدارة المركزية لمكافحة الجرائم السيبرانية بألمانيا",
            officialUrl = "https://www.bka.de",
            description = "الجهة القيادية في أوروبا المسؤولة عن إسقاط خوادم البوت نت العالمية والتعاون مع اليوروبول في تجميد محافظ الجريمة المنظمة.",
            region = "ألمانيا والاتحاد الأوروبي",
            requirements = "قنوات الاتصال القضائية الرسمية والإنتربول",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 103
        ),
        OfficialSourceEntity(
            id = "src_le_afp_cyber",
            name = "الشرطة الاتحادية الأسترالية - قيادة الجرائم السيبرانية (AFP Cyber Command)",
            companyOrEntity = "Australian Federal Police (AFP)",
            sectionType = PrepopulatedOfficialSources.SEC_LAW_ENFORCEMENT,
            category = "إنفاذ القانون والقضاء",
            portalType = "مركز الاستجابة والتحقيق السيبراني المشترك (JCSC)",
            officialUrl = "https://www.afp.gov.au/crimes/cybercrime",
            description = "حماية المصالح الوطنية الأسترالية والتحقيق في قضايا الابتزاز والاختراق والتعاون الأمني بمنطقة آسيا والمحيط الهادئ.",
            region = "أستراليا ودولياً",
            requirements = "بلاغ عبر بوابة ReportCyber الأسترالية",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 104
        ),

        // ==========================================
        // EMERGENCY DIGITAL RIGHTS & HELPLINES
        // ==========================================
        OfficialSourceEntity(
            id = "src_help_accessnow",
            name = "خط المساعدة للأمان الرقمي (Access Now Digital Security Helpline)",
            companyOrEntity = "منظمة أكسس ناو (Access Now)",
            sectionType = PrepopulatedOfficialSources.SEC_GLOBAL_EMERGENCY,
            category = "الطوارئ الرقمية العالمية",
            portalType = "طوارئ أمن رقمي مجانية 24/7 (باللغة العربية)",
            officialUrl = "https://www.accessnow.org/help/",
            description = "فريق طوارئ تقني يقدم المساعدة الفورية على مدار الساعة لضحايا الاختراق، برمجيات التجسس (مثل Pegasus)، والابتزاز الرقمي.",
            region = "عالمي (يدعم اللغة العربية)",
            requirements = "تواصل عبر البريد المشفر PGP أو Signal أو النموذج الرسمي",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 110
        ),
        OfficialSourceEntity(
            id = "src_help_frontline",
            name = "خط الطوارئ لحماية المدافعين عن الحقوق (Front Line Defenders)",
            companyOrEntity = "فرونت لاين ديفندرز (Front Line Defenders)",
            sectionType = PrepopulatedOfficialSources.SEC_GLOBAL_EMERGENCY,
            category = "الطوارئ الرقمية العالمية",
            portalType = "خط استجابة طارئة للمخاطر الرقمية والجسدية (هاتف طوارئ 24/7)",
            officialUrl = "https://www.frontlinedefenders.org/ar/emergency-contact",
            description = "دعم أمني تقني متخصص لفحص أجهزة الضحايا المستهدفة ببرمجيات التجسس المتطورة وتأمين قنوات الاتصال والبيانات الحساسة.",
            region = "عالمي",
            requirements = "متاح على مدار 24 ساعة عبر خط الهاتف المشفر والبريد",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 111
        ),
        OfficialSourceEntity(
            id = "src_help_ccri",
            name = "مبادرة الحقوق المدنية السيبرانية (Cyber Civil Rights Initiative - CCRI)",
            companyOrEntity = "CCRI USA",
            sectionType = PrepopulatedOfficialSources.SEC_GLOBAL_EMERGENCY,
            category = "الطوارئ الرقمية العالمية",
            portalType = "خط مساعدة ضحايا الابتزاز بالصور الحساسة (Non-Consensual Pornography)",
            officialUrl = "https://cybercivilrights.org",
            description = "أبرز منظمة عالمية تقدم المشورة القانونية والتقنية والدعم النفسي لضحايا نشر الصور الخاصة والابتزاز العاطفي عبر الإنترنت.",
            region = "عالمي",
            requirements = "خط ساخن واستشارات سرية مجانية",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 112
        ),

        // ==========================================
        // TECH PLATFORMS LAW ENFORCEMENT PROCESSES
        // ==========================================
        OfficialSourceEntity(
            id = "src_le_telegram",
            name = "Telegram Law Enforcement Guidelines & Bot Abuse",
            companyOrEntity = "Telegram FZ-LLC",
            sectionType = PrepopulatedOfficialSources.SEC_LAW_ENFORCEMENT,
            category = "إنفاذ القانون والقضاء",
            portalType = "بوابة الإجراءات القانونية لتيليجرام",
            officialUrl = "https://telegram.org/privacy#8-3-law-enforcement-authorities",
            description = "الإجراءات المعتمدة لتقديم أوامر المحكمة الصالحة لكشف عناوين IP وأرقام الهواتف للمشتبه بهم في قضايا الإرهاب والجرائم الخطيرة.",
            region = "عالمي",
            requirements = "أمر قضائي معتمد متوافق مع قوانين حماية البيانات وبوابة التنسيق",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 120
        ),
        OfficialSourceEntity(
            id = "src_le_signal",
            name = "Signal Messenger Legal Process Guidelines",
            companyOrEntity = "Signal Technology Foundation",
            sectionType = PrepopulatedOfficialSources.SEC_LAW_ENFORCEMENT,
            category = "إنفاذ القانون والقضاء",
            portalType = "الإفصاح القانوني لمعلومات الحسابات",
            officialUrl = "https://signal.org/legal/",
            description = "المعايير الرسمية لرد تطبيق سيجنال على مذكرات التفتيش الحكومية، والتي تقتصر على تاريخ إنشاء الحساب ووقت آخر اتصال.",
            region = "عالمي",
            requirements = "أمر قضائي صادر بموجب القانون الأمريكي أو معاهدة المساعدة القانونية المتبادلة (MLAT)",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 121
        ),
        OfficialSourceEntity(
            id = "src_le_proton",
            name = "Proton AG Legal Process Portal (Switzerland)",
            companyOrEntity = "Proton AG (ProtonMail / ProtonVPN)",
            sectionType = PrepopulatedOfficialSources.SEC_LAW_ENFORCEMENT,
            category = "إنفاذ القانون والقضاء",
            portalType = "الإجراءات القضائية السويسرية",
            officialUrl = "https://proton.me/legal/law-enforcement",
            description = "إرشادات الاستجابة للأوامر القضائية الملزمة حصراً من السلطات القضائية السويسرية الفيدرالية أو عبر معاهدات MLAT.",
            region = "سويسرا / دولي",
            requirements = "أمر ملزم صادر ومصدق من محكمة سويسرية رسمية",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 122
        ),
        OfficialSourceEntity(
            id = "src_le_tiktok",
            name = "TikTok Law Enforcement Request Guidelines",
            companyOrEntity = "ByteDance / TikTok Inc.",
            sectionType = PrepopulatedOfficialSources.SEC_LAW_ENFORCEMENT,
            category = "إنفاذ القانون والقضاء",
            portalType = "نظام الاستجابة لطلبات جهات التحقيق الرسمية",
            officialUrl = "https://www.tiktok.com/legal/law-enforcement",
            description = "البوابة المخصصة لجهات إنفاذ القانون لطلب بيانات مستخدمي تيك توك، عناوين IP، سجلات البث المباشر، وبيانات الحفظ الطارئ.",
            region = "عالمي",
            requirements = "بريد رسمي صادر من نطاق جهة التحقيق مع أمر الاستدعاء الرسمي",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 123
        ),
        OfficialSourceEntity(
            id = "src_le_reddit",
            name = "Reddit Law Enforcement Guidelines & Emergency Requests",
            companyOrEntity = "Reddit Inc.",
            sectionType = PrepopulatedOfficialSources.SEC_LAW_ENFORCEMENT,
            category = "إنفاذ القانون والقضاء",
            portalType = "بوابة الشؤون القانونية لريديت",
            officialUrl = "https://www.redditinc.com/policies/guidelines-for-law-enforcement",
            description = "مركز استلام مذكرات التحقيق الجنائي لبيانات حسابات ريديت، الرسائل الخاصة، وسجلات الاتصال والـ IP في القضايا الجنائية.",
            region = "عالمي",
            requirements = "أمر تفتيش فيدرالي أو محلي ساري المفعول بأمريكا أو معاهدة مساعدة دولية",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 124
        ),
        OfficialSourceEntity(
            id = "src_le_uber",
            name = "Uber Public Safety Law Enforcement Portal",
            companyOrEntity = "Uber Technologies Inc.",
            sectionType = PrepopulatedOfficialSources.SEC_LAW_ENFORCEMENT,
            category = "إنفاذ القانون والقضاء",
            portalType = "بوابة السلامة العامة للمحققين الرسميين",
            officialUrl = "https://www.uber.com/legal/en/document/?name=guidelines-for-law-enforcement-authorities",
            description = "النظام الرسمي لتزويد جهات إنفاذ القانون ببيانات الرحلات، مسارات GPS للسيارات، وهوية السائق والراكب في الجرائم والحوادث العاجلة.",
            region = "عالمي (أكثر من 70 دولة)",
            requirements = "تسجيل محقق رسمي عبر البوابة المشفرة بأوراق الاعتماد الحكومية",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 125
        ),
        OfficialSourceEntity(
            id = "src_le_airbnb",
            name = "Airbnb Law Enforcement Portal",
            companyOrEntity = "Airbnb Inc.",
            sectionType = PrepopulatedOfficialSources.SEC_LAW_ENFORCEMENT,
            category = "إنفاذ القانون والقضاء",
            portalType = "بوابة الاستجابة لإنفاذ القانون وتتبع الحجوزات",
            officialUrl = "https://www.airbnb.com/help/article/960",
            description = "تزويد المحققين ببيانات حجز العقارات، الهويات الرسمية للنزلاء، وسجلات الدفع في القضايا الجنائية ومكافحة غسل الأموال.",
            region = "عالمي",
            requirements = "طلب قانوني ساري المفعول من سلطة قضائية مختصة",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 126
        )
    )
}
