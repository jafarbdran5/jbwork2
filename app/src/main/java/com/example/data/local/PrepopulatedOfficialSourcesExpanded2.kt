package com.example.data.local

import com.example.data.local.entities.OfficialSourceEntity

object PrepopulatedOfficialSourcesExpanded2 {
    val ADDITIONAL_SOURCES_PART2 = listOf(
        // ==========================================
        // INTELLECTUAL PROPERTY & BRAND PROTECTION
        // ==========================================
        OfficialSourceEntity(
            id = "src_ip_amazon_brand",
            name = "Amazon Brand Registry",
            companyOrEntity = "Amazon.com Inc.",
            sectionType = PrepopulatedOfficialSources.SEC_COPYRIGHT_IP,
            category = "حقوق النشر والملكية الفكرية",
            portalType = "نظام حماية العلامات التجارية ومكافحة التزييف",
            officialUrl = "https://brandservices.amazon.com",
            description = "البوابة الرسمية لأصحاب العلامات التجارية المسجلة للبحث عن البضائع المقلدة وإسقاط قوائم البيع المزيفة وحظر حسابات البائعين المخالفين.",
            region = "عالمي",
            requirements = "شهادة تسجيل علامة تجارية حكومية نشطة",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 50
        ),
        OfficialSourceEntity(
            id = "src_ip_meta_rights_manager",
            name = "Meta Rights Manager",
            companyOrEntity = "Meta Platforms Inc.",
            sectionType = PrepopulatedOfficialSources.SEC_COPYRIGHT_IP,
            category = "حقوق النشر والملكية الفكرية",
            portalType = "منظومة المطابقة الآلية للمحتوى البصري والصوتي",
            officialUrl = "https://rightsmanager.fb.com",
            description = "أداة متطورة من ميتا لفحص مقاطع الفيديو والصوت المرفوعة على Facebook وInstagram وإصدار أوامر حظر أو حذف تلقائي عند انتهاك الحقوق.",
            region = "عالمي",
            requirements = "إثبات ملكية محتوى حصري وتوثيق صفحة ناشر معتمدة",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 51
        ),
        OfficialSourceEntity(
            id = "src_ip_ebay_vero",
            name = "eBay Verified Rights Owner Program (VeRO)",
            companyOrEntity = "eBay Inc.",
            sectionType = PrepopulatedOfficialSources.SEC_COPYRIGHT_IP,
            category = "حقوق النشر والملكية الفكرية",
            portalType = "برنامج حماية الحقوق الموثقة",
            officialUrl = "https://pages.ebay.com/seller-center/listing-and-marketing/verified-rights-owner-program.html",
            description = "بوابة تقديم إشعارات الانتهاك لحذف السلع المزورة والمنتجات المقلدة المعروضة للبيع على منصة إيباي فورياً.",
            region = "عالمي",
            requirements = "تعبئة نموذج إشعار انتهاك VeRO موقع من الممثل القانوني",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 52
        ),
        OfficialSourceEntity(
            id = "src_ip_lumen",
            name = "Lumen Database (Berkman Klein Center)",
            companyOrEntity = "جامعة هارفارد (Harvard University)",
            sectionType = PrepopulatedOfficialSources.SEC_COPYRIGHT_IP,
            category = "حقوق النشر والملكية الفكرية",
            portalType = "الأرشيف العالمي لطلبات الحذف والشكاوى القانونية",
            officialUrl = "https://lumendatabase.org",
            description = "أرشيف شفاف يجمع إشعارات الشكاوى القانونية وإخطارات DMCA المرسلة إلى Google وTwitter لحذف نتائج البحث والمواقع.",
            region = "عالمي",
            requirements = "بحث عام ومجاني متاح للمحققين والصحفيين",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 53
        ),

        // ==========================================
        // FACT CHECKING & TRUSTED CREDIBILITY INSTITUTIONS
        // ==========================================
        OfficialSourceEntity(
            id = "src_fact_ifcn",
            name = "International Fact-Checking Network (IFCN)",
            companyOrEntity = "معهد بوينتر (Poynter Institute)",
            sectionType = PrepopulatedOfficialSources.SEC_FACT_CHECKING,
            category = "منصات تقصي الحقائق",
            portalType = "المظلة الدولية لمدققي الحقائق المعتمدين",
            officialUrl = "https://www.poynter.org/ifcn/",
            description = "الهيئة العالمية التي تمنح الاعتماد المهني لمنصات تدقيق الحقائق وتطبق ميثاق الشفافية ومكافحة التضليل بالتعاون مع المنصات الرقمية.",
            region = "عالمي",
            requirements = "الاطلاع على قائمة المنصات المعتمدة ووثائق الاعتماد",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 60
        ),
        OfficialSourceEntity(
            id = "src_fact_afcn",
            name = "شبكة مصدقي الحقائق العربية (AFCN)",
            companyOrEntity = "إعلاميون من أجل صحافة استقصائية عربية (أريج - ARIJ)",
            sectionType = PrepopulatedOfficialSources.SEC_FACT_CHECKING,
            category = "منصات تقصي الحقائق",
            portalType = "الشبكة الإقليمية لمدققي الحقائق بالعالم العربي",
            officialUrl = "https://afcn.arij.net",
            description = "شبكة تجمع منظمات وفرق التحقق من الأخبار الزائفة والتزييف الرقمي في الدول العربية وتوفر دليلاً للتحري الجنائي مفتوح المصدر.",
            region = "الشرق الأوسط وشمال أفريقيا",
            requirements = "مكتبة أدوات وأدلة تدريبية متخصصة ومحدثة",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 61
        ),

        // ==========================================
        // TELECOM & DEVICE REGISTRIES
        // ==========================================
        OfficialSourceEntity(
            id = "src_tel_gsma",
            name = "GSMA Device Check / IMEI Registry",
            companyOrEntity = "الاتحاد العالمي للاتصالات المتنقلة (GSMA)",
            sectionType = PrepopulatedOfficialSources.SEC_TELECOM_DEVICES,
            category = "الاتصالات والهواتف والأجهزة",
            portalType = "القاعدة العالمية للأجهزة والهواتف المسروقة",
            officialUrl = "https://www.gsma.com/solutions-and-impact/technologies/device-security/device-check/",
            description = "السجل الدولي الشامل للتحقق من أرقام IMEI للأجهزة والهواتف والتأكد مما إذا كان الهاتف مبلغاً عن سرقته أو مسجلاً في القائمة السوداء.",
            region = "عالمي (أكثر من 800 مشغل شبكة)",
            requirements = "الاستعلام برقم الـ IMEI المكون من 15 رقماً",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 70
        ),
        OfficialSourceEntity(
            id = "src_tel_cst_saudi",
            name = "هيئة الاتصالات والفضاء والتقنية - منظومة أرقامي وبلاغات الاحتيال",
            companyOrEntity = "هيئة الاتصالات والفضاء والتقنية (CST KSA)",
            sectionType = PrepopulatedOfficialSources.SEC_TELECOM_DEVICES,
            category = "الاتصالات والهواتف والأجهزة",
            portalType = "البوابة التنظيمية لشرائح الاتصال ومكافحة السبام",
            officialUrl = "https://www.cst.gov.sa",
            description = "خدمة 'أرقامي' للاستعلام عن كافة الأرقام المسجلة بهوية العميل، وخدمة الإبلاغ عن الرسائل الاحتيالية عبر إرسالها للرقم 330330.",
            region = "المملكة العربية السعودية",
            requirements = "تسجيل الدخول عبر النفاذ الوطني الموحد (أبشر)",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 71
        ),

        // ==========================================
        // FINANCIAL INTELLIGENCE & ANTI-FRAUD UNITS
        // ==========================================
        OfficialSourceEntity(
            id = "src_fin_egmont",
            name = "مجموعة إيغمونت لوحدات التحريات المالية (Egmont Group)",
            companyOrEntity = "The Egmont Group of Financial Intelligence Units",
            sectionType = PrepopulatedOfficialSources.SEC_LEGAL_REGULATORY,
            category = "المصادر القانونية والتنظيمية",
            portalType = "شبكة استخبارات مالية دولية (170 دولة)",
            officialUrl = "https://egmontgroup.org",
            description = "المظلة الدولية لتبادل المعلومات الاستخباراتية المالية لمكافحة غسل الأموال وتتبع الأصول الناتجة عن الابتزاز والاحتيال الإلكتروني.",
            region = "دولي",
            requirements = "قنوات مشفرة ومخصصة لوحدات التحريات المالية الوطنية (FIUs)",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 80
        ),
        OfficialSourceEntity(
            id = "src_fin_fincen",
            name = "شبكة مكافحة الجرائم المالية الأمريكية (FinCEN)",
            companyOrEntity = "U.S. Financial Crimes Enforcement Network (Department of the Treasury)",
            sectionType = PrepopulatedOfficialSources.SEC_LEGAL_REGULATORY,
            category = "المصادر القانونية والتنظيمية",
            portalType = "بوابة استخبارات مالية وتنظيمية",
            officialUrl = "https://www.fincen.gov",
            description = "إدارة الخزانة الأمريكية لتتبع التحويلات المصرفية المشبوهة، التحقيق في منصات العملات المشفرة المخالفة، وتنفيذ العقوبات.",
            region = "الولايات المتحدة / دولي",
            requirements = "إرسال تقارير الأنشطة المشبوهة (SARs) والشكاوى المالية",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 81
        ),
        OfficialSourceEntity(
            id = "src_fin_saudi_fiu",
            name = "الإدارة العامة للتحريات المالية السعودية (SAFIU)",
            companyOrEntity = "رئاسة أمن الدولة - المملكة العربية السعودية",
            sectionType = PrepopulatedOfficialSources.SEC_LEGAL_REGULATORY,
            category = "المصادر القانونية والتنظيمية",
            portalType = "وحدة التحريات المالية الوطنية",
            officialUrl = "https://www.safiu.gov.sa",
            description = "الجهاز الوطني المركزي المختص بتلقي وتحليل البلاغات عن العمليات والمعاملات المالية المشبوهة وإحالتها للجهات المختصة.",
            region = "المملكة العربية السعودية",
            requirements = "البوابة الآمنة المعتمدة للبنوك والجهات المبلغة",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 82
        ),

        // ==========================================
        // REGIONAL & LOCAL EMERGENCY AND COMBAT HOTLINES
        // ==========================================
        OfficialSourceEntity(
            id = "src_reg_kollon_amna",
            name = "تطبيق كلنا أمن - الأمن العام السعودي",
            companyOrEntity = "وزارة الداخلية - الأمن العام",
            sectionType = PrepopulatedOfficialSources.SEC_REGIONAL_LOCAL,
            category = "مصادر الدعم الإقليمية والمحلية",
            portalType = "المنظومة الأمنية للبلاغات الجنائية والجرائم الإلكترونية",
            officialUrl = "https://www.psd.gov.sa",
            description = "المنصة الرسمية المعتمدة لتقديم بلاغات الجرائم الإلكترونية، الاختراق، الابتزاز، وانتحال الشخصية في السعودية مع إرفاق الأدلة.",
            region = "المملكة العربية السعودية",
            requirements = "رقم الهوية الوطنية أو الإقامة وإرفاق لقطات الشاشة وروابط الحسابات",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 90
        ),
        OfficialSourceEntity(
            id = "src_reg_dubai_ecrime",
            name = "منصة الجرائم الإلكترونية (eCrime) - شرطة دبي",
            companyOrEntity = "القيادة العامة لشرطة دبي",
            sectionType = PrepopulatedOfficialSources.SEC_REGIONAL_LOCAL,
            category = "مصادر الدعم الإقليمية والمحلية",
            portalType = "بوابة مكافحة الجرائم الإلكترونية",
            officialUrl = "https://www.dubaipolice.gov.ae/wps/portal/home/services/individualservices/cybercrime",
            description = "خدمة رسمية لتلقي شكاوى الابتزاز الإلكتروني، القرصنة، الاحتيال المالي، واختراق الحسابات مع متابعة سريعة من المباحث الإلكترونية.",
            region = "دولة الإمارات العربية المتحدة (إمارة دبي وعالمياً)",
            requirements = "الهوية الإماراتية أو جواز السفر مع الأدلة الرقمية",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 91
        ),
        OfficialSourceEntity(
            id = "src_reg_aman_abudhabi",
            name = "خدمة أمان - القيادة العامة لشرطة أبوظبي",
            companyOrEntity = "شرطة أبوظبي",
            sectionType = PrepopulatedOfficialSources.SEC_REGIONAL_LOCAL,
            category = "مصادر الدعم الإقليمية والمحلية",
            portalType = "قناة أمنية سرية للبلاغات والابتزاز (هاتف 8002626)",
            officialUrl = "https://www.adpolice.gov.ae",
            description = "قناة مجتمعية أمنية عالية السرية لاستقبال بلاغات قضايا الابتزاز وسرقة الحسابات وتقديم الحماية والدعم التام للضحية.",
            region = "دولة الإمارات العربية المتحدة (إمارة أبوظبي)",
            requirements = "تقديم البلاغ هاتفياً أو عبر الرسائل أو التطبيق بسرية مطلقة",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 92
        ),
        OfficialSourceEntity(
            id = "src_reg_jordan_cyber",
            name = "وحدة مكافحة الجرائم الإلكترونية - مديرية الأمن العام الأردنية",
            companyOrEntity = "مديرية الأمن العام - إدارة البحث الجنائي",
            sectionType = PrepopulatedOfficialSources.SEC_REGIONAL_LOCAL,
            category = "مصادر الدعم الإقليمية والمحلية",
            portalType = "وحدة تحقيق جنائي سيبراني رسمية",
            officialUrl = "https://psd.gov.jo",
            description = "تلقي البلاغات والتحقيق الفني في قضايا الابتزاز والتهديد الإلكتروني وسرقة الحسابات عبر الواتساب والموقع الرسمي للمديرية.",
            region = "المملكة الأردنية الهاشمية",
            requirements = "مراجعة الوحدة أو التواصل عبر رقم الواتساب الرسمي (0797911911)",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 93
        ),
        OfficialSourceEntity(
            id = "src_reg_kuwait_cyber",
            name = "إدارة مكافحة الجرائم الإلكترونية - وزارة الداخلية الكويتية",
            companyOrEntity = "الإدارة العامة للمباحث الجنائية - الكويت",
            sectionType = PrepopulatedOfficialSources.SEC_REGIONAL_LOCAL,
            category = "مصادر الدعم الإقليمية والمحلية",
            portalType = "إدارة تحقيق جنائي سيبراني",
            officialUrl = "https://www.moi.gov.kw",
            description = "متابعة قضايا الابتزاز الإلكتروني، النصب والاحتيال المالي، وانتحال صفة الشخصيات والشركات في دولة الكويت (هاتف 97288884).",
            region = "دولة الكويت",
            requirements = "البطاقة المدنية مع الاحتفاظ بالمحادثات والصور كأدلة جنائية",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 94
        )
    )
}
