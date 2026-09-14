package com.example.data.local

import com.example.data.local.entities.OfficialSourceEntity

object PrepopulatedOfficialSourcesExpanded4 {
    val ADDITIONAL_SOURCES_PART4 = listOf(
        // ==========================================
        // TECH GIANTS DIGITAL CRIMES & THREAT UNITS
        // ==========================================
        OfficialSourceEntity(
            id = "src_le_ms_dcu",
            name = "Microsoft Digital Crimes Unit (DCU)",
            companyOrEntity = "Microsoft Corporation",
            sectionType = PrepopulatedOfficialSources.SEC_CYBER_SECURITY,
            category = "الأمن السيبراني والإبلاغ الأمني",
            portalType = "وحدة مكافحة الجرائم الرقمية وإسقاط شبكات البوت نت",
            officialUrl = "https://www.microsoft.com/en-us/security/business/threat-intelligence",
            description = "فريق النخبة القانوني والتقني لدى مايكروسوفت للحصول على أوامر المحاكم الفيدرالية لمصادرة خوادم التحكم وإسقاط برمجيات الفدية.",
            region = "عالمي",
            requirements = "تنسيق أمني مشترك مع أجهزة إنفاذ القانون الدولية والنيابات العامة",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 130
        ),
        OfficialSourceEntity(
            id = "src_le_google_tag",
            name = "Google Threat Analysis Group (TAG)",
            companyOrEntity = "Google LLC",
            sectionType = PrepopulatedOfficialSources.SEC_CYBER_SECURITY,
            category = "الأمن السيبراني والإبلاغ الأمني",
            portalType = "استخبارات الهجمات الموجهة وبرمجيات التجسس الحكومية",
            officialUrl = "https://blog.google/threat-analysis-group/",
            description = "مجموعة بحثية رائدة ترصد وتفضح حملات التجسس المدعومة من الدول وبرمجيات الاستهداف التجاري (مثل Pegasus وPredator).",
            region = "عالمي",
            requirements = "تقارير وبلاغات استخبارات التهديدات العامة",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 131
        ),
        OfficialSourceEntity(
            id = "src_le_cloudflare",
            name = "Cloudflare Law Enforcement Guidelines & Subpoenas",
            companyOrEntity = "Cloudflare Inc.",
            sectionType = PrepopulatedOfficialSources.SEC_HOSTING_CLOUD,
            category = "الاستضافة والنطاقات والخدمات السحابية",
            portalType = "بوابة الاستجابة لمذكرات استدعاء الخوادم الأصلية",
            officialUrl = "https://www.cloudflare.com/trust-hub/law-enforcement/",
            description = "البوابة الرسمية لتقديم أوامر المحكمة لكشف عناوين IP الأصلية للمواقع المحمية بخدمة Cloudflare Proxy وبيانات أصحاب الحسابات.",
            region = "عالمي",
            requirements = "مذكرة استدعاء أو أمر محكمة رسمي ساري المفعول بأمريكا أو طلب MLAT",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 132
        ),
        OfficialSourceEntity(
            id = "src_le_godaddy",
            name = "GoDaddy Legal & Court Order Compliance",
            companyOrEntity = "GoDaddy Operating Company LLC",
            sectionType = PrepopulatedOfficialSources.SEC_HOSTING_CLOUD,
            category = "الاستضافة والنطاقات والخدمات السحابية",
            portalType = "بوابة الامتثال للأوامر القضائية وحجز النطاقات",
            officialUrl = "https://www.godaddy.com/legal/agreements/subpoena-policy",
            description = "الإجراءات الرسمية لتنفيذ أحكام المحاكم بتجميد ونقل ملكية النطاقات المسجلة واستضافة المواقع المتورطة في الاحتيال التجاري.",
            region = "عالمي",
            requirements = "أمر قضائي صادر من محكمة مختصة وموجه إلى الإدارة القانونية",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 133
        ),
        OfficialSourceEntity(
            id = "src_le_namecheap",
            name = "Namecheap Legal & Fraud Department",
            companyOrEntity = "Namecheap Inc.",
            sectionType = PrepopulatedOfficialSources.SEC_HOSTING_CLOUD,
            category = "الاستضافة والنطاقات والخدمات السحابية",
            portalType = "إدارة مكافحة الاحتيال والامتثال القانوني",
            officialUrl = "https://www.namecheap.com/legal/",
            description = "استقبال طلبات أوامر المحاكم، قضايا نزاعات العلامات التجارية، ومصادرة النطاقات المستغلة في صفحات التصيد وسرقة الهويات.",
            region = "عالمي",
            requirements = "مستندات الدعوى القضائية أو وثيقة UDRP المعتمدة من الويبو",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 134
        ),

        // ==========================================
        // TOP NATIONAL COMPUTER EMERGENCY TEAMS (GLOBAL)
        // ==========================================
        OfficialSourceEntity(
            id = "src_cert_eu",
            name = "فريق الاستجابة لطوارئ الحاسب لمؤسسات الاتحاد الأوروبي (CERT-EU)",
            companyOrEntity = "الاتحاد الأوروبي (European Union)",
            sectionType = PrepopulatedOfficialSources.SEC_CYBER_SECURITY,
            category = "الأمن السيبراني والإبلاغ الأمني",
            portalType = "المركز السيبراني الأوروبي الموحد",
            officialUrl = "https://cert.europa.eu",
            description = "تنسيق الاستجابة للحوادث وحماية مؤسسات وهيئات ومفوضيات الاتحاد الأوروبي من الهجمات السيبرانية المعقدة.",
            region = "الاتحاد الأوروبي",
            requirements = "التعاون المشترك وتبادل مؤشرات الاختراق (IOCs)",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 140
        ),
        OfficialSourceEntity(
            id = "src_cert_anssi_fr",
            name = "الوكالة الوطنية الفرنسية لأمن نظم المعلومات (ANSSI / CERT-FR)",
            companyOrEntity = "Agence nationale de la sécurité des systèmes d'information",
            sectionType = PrepopulatedOfficialSources.SEC_CYBER_SECURITY,
            category = "الأمن السيبراني والإبلاغ الأمني",
            portalType = "المركز الوطني الفرنسي للأمن السيبراني",
            officialUrl = "https://www.cert.ssi.gouv.fr",
            description = "أحد أقوى مراكز التحليل الجنائي السيبراني في أوروبا، يصدر نشرات أمنية تفصيلية لتحليل ثغرات اليوم الصفر (Zero-Day).",
            region = "فرنسا والاتحاد الأوروبي",
            requirements = "تقارير تقنية مفتوحة وإشعارات أمنية رسمية",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 141
        ),
        OfficialSourceEntity(
            id = "src_cert_bsi_de",
            name = "المكتب الاتحادي الألماني لأمن تكنولوجيا المعلومات (BSI / CERT-Bund)",
            companyOrEntity = "Bundesamt für Sicherheit in der Informationstechnik",
            sectionType = PrepopulatedOfficialSources.SEC_CYBER_SECURITY,
            category = "الأمن السيبراني والإبلاغ الأمني",
            portalType = "الهيئة الاتحادية الألمانية لأمن المعلومات",
            officialUrl = "https://www.bsi.bund.de",
            description = "وضع المعايير الأمنية وحماية الشبكات الحكومية والشركات الحيوية ونشر معايير التشفير الموصى بها دولياً.",
            region = "ألمانيا والاتحاد الأوروبي",
            requirements = "بوابات الاستجابة للطوارئ للحكومة والقطاع الخاص",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 142
        ),
        OfficialSourceEntity(
            id = "src_cert_jpcert",
            name = "مركز تنسيق طوارئ الحاسب الآلي الياباني (JPCERT/CC)",
            companyOrEntity = "Japan Computer Emergency Response Team Coordination Center",
            sectionType = PrepopulatedOfficialSources.SEC_CYBER_SECURITY,
            category = "الأمن السيبراني والإبلاغ الأمني",
            portalType = "مركز التنسيق السيبراني الياباني",
            officialUrl = "https://www.jpcert.or.jp/english/",
            description = "المركز الرائد في شرق آسيا للتعامل مع حوادث القرصنة الصناعية، برمجيات التجسس المالي، وفحص عينات الفيروسات.",
            region = "اليابان وآسيا والمحيط الهادئ",
            requirements = "قنوات التنسيق الدولية لفرق CSIRT/CERT المعتمدة",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 143
        ),
        OfficialSourceEntity(
            id = "src_cert_singcert",
            name = "فريق الاستجابة لطوارئ الحاسب السنغافوري (SingCERT)",
            companyOrEntity = "Cyber Security Agency of Singapore (CSA)",
            sectionType = PrepopulatedOfficialSources.SEC_CYBER_SECURITY,
            category = "الأمن السيبراني والإبلاغ الأمني",
            portalType = "المركز الوطني للأمن السيبراني بسنغافورة",
            officialUrl = "https://www.csa.gov.sg/singcert",
            description = "تأمين المركز المالي والتقني العالمي بسنغافورة وتقديم المساعدة في حوادث التصيد المالي وسرقة الحسابات المصرفية.",
            region = "سنغافورة ودولياً",
            requirements = "بوابة الإبلاغ عن الحوادث السيبرانية للشركات والأفراد",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 144
        ),

        // ==========================================
        // NATIONAL DATA PROTECTION AUTHORITIES (EUROPE & ASIA)
        // ==========================================
        OfficialSourceEntity(
            id = "src_priv_cnil",
            name = "اللجنة الوطنية للمعلوماتية والحريات الفرنسية (CNIL)",
            companyOrEntity = "Commission Nationale de l'Informatique et des Libertés",
            sectionType = PrepopulatedOfficialSources.SEC_PRIVACY_DATA,
            category = "الخصوصية وحماية البيانات",
            portalType = "الهيئة الوطنية الفرنسية لحماية البيانات",
            officialUrl = "https://www.cnil.fr",
            description = "أشهر هيئة رقابية لحماية الخصوصية في أوروبا، تفرض عقوبات وغرامات قياسية على شركات التكنولوجيا عند انتهاك بيانات المستخدمين.",
            region = "فرنسا والاتحاد الأوروبي",
            requirements = "بوابة الشكاوى الرسمية للمستخدمين من كافة الدول",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 150
        ),
        OfficialSourceEntity(
            id = "src_priv_aepd",
            name = "الوكالة الإسبانية لحماية البيانات (AEPD)",
            companyOrEntity = "Agencia Española de Protección de Datos",
            sectionType = PrepopulatedOfficialSources.SEC_PRIVACY_DATA,
            category = "الخصوصية وحماية البيانات",
            portalType = "الهيئة الرقابية الرائدة للحق في النسيان الرقمي (Right to be Forgotten)",
            officialUrl = "https://www.aepd.es",
            description = "صاحبة السابقة القضائية التاريخية التي فرضت على غوغل تطبيق 'حق النسيان' وإلزام المحركات بحذف الروابط التشهيرية.",
            region = "إسبانيا والاتحاد الأوروبي",
            requirements = "طلب إزالة روابط المحتوى التشهيري والشكاوى الرسمية",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 151
        ),
        OfficialSourceEntity(
            id = "src_priv_garante",
            name = "الهيئة الإيطالية لحماية البيانات الشخصية (Garante Privacy)",
            companyOrEntity = "Garante per la protezione dei dati personali",
            sectionType = PrepopulatedOfficialSources.SEC_PRIVACY_DATA,
            category = "الخصوصية وحماية البيانات",
            portalType = "هيئة تنظيم الخصوصية الإيطالية",
            officialUrl = "https://www.garanteprivacy.it",
            description = "الهيئة المشهورة بالقرارات السريعة لحماية القاصرين على منصات التواصل الاجتماعي وتطبيقات الذكاء الاصطناعي.",
            region = "إيطاليا والاتحاد الأوروبي",
            requirements = "نماذج تقديم الشكاوى وحظر معالجة البيانات غير القانونية",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 152
        ),

        // ==========================================
        // INTERNATIONAL CRIME & ASSET RECOVERY CONVENTIONS
        // ==========================================
        OfficialSourceEntity(
            id = "src_legal_fatf",
            name = "مجموعة العمل المالي الدولية (FATF / GAFI)",
            companyOrEntity = "Financial Action Task Force",
            sectionType = PrepopulatedOfficialSources.SEC_LEGAL_REGULATORY,
            category = "المصادر القانونية والتنظيمية",
            portalType = "المنظمة الدولية المعنية بمكافحة غسل الأموال والقوائم الرمادية والسوداء",
            officialUrl = "https://www.fatf-gafi.org",
            description = "المرجع العالمي الأعلى لوضع المعايير الدولية الملزمة لمكافحة غسل الأموال ومراقبة منصات الأصول الافتراضية (العملات الرقمية).",
            region = "عالمي (أكثر من 200 ولاية قضائية)",
            requirements = "إرشادات الامتثال والتحقق من القوائم الرمادية والسوداء للدول",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 160
        ),
        OfficialSourceEntity(
            id = "src_legal_unodc",
            name = "مكتب الأمم المتحدة المعني بالمخدرات والجريمة (UNODC Cybercrime)",
            companyOrEntity = "الأمم المتحدة (United Nations)",
            sectionType = PrepopulatedOfficialSources.SEC_LEGAL_REGULATORY,
            category = "المصادر القانونية والتنظيمية",
            portalType = "المستودع القانوني للتشريعات السيبرانية العالمية",
            officialUrl = "https://www.unodc.org/unodc/en/cybercrime/index.html",
            description = "قاعدة بيانات شاملة لجميع القوانين الوطنية للجرائم الإلكترونية والاتفاقيات الدولية للمساعدة القانونية المتبادلة.",
            region = "دولي (الأمم المتحدة)",
            requirements = "الاطلاع على مستودع القوانين والتشريعات المقارنة",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 161
        ),
        OfficialSourceEntity(
            id = "src_legal_budapest",
            name = "اتفاقية بودابست بشأن الجريمة السيبرانية (Council of Europe CETS 185)",
            companyOrEntity = "مجلس أوروبا (Council of Europe)",
            sectionType = PrepopulatedOfficialSources.SEC_LEGAL_REGULATORY,
            category = "المصادر القانونية والتنظيمية",
            portalType = "المعاهدة الدولية الأولى والملزمة لمكافحة جرائم الإنترنت",
            officialUrl = "https://www.coe.int/en/web/cybercrime/the-budapest-convention",
            description = "المعاهدة الدولية الأهم التي تحدد آليات التحقيق المشترك، جمع الأدلة الرقمية العابرة للحدود، وشبكة الاتصال للطوارئ 24/7.",
            region = "دولي (أكثر من 70 دولة موقعة)",
            requirements = "قنوات الاتصال المعتمدة لجهات التحقيق المعتمدة بالدول الأعضاء",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 162
        )
    )
}
