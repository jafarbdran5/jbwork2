package com.example.data.local

import com.example.data.local.entities.OfficialSourceEntity

object PrepopulatedOfficialSourcesExpanded5 {
    val ADDITIONAL_SOURCES_PART5 = listOf(
        // ==========================================
        // VULNERABILITY DISCLOSURE & SECURITY BOUNTIES
        // ==========================================
        OfficialSourceEntity(
            id = "src_vuln_msrc",
            name = "Microsoft Security Response Center (MSRC)",
            companyOrEntity = "Microsoft Corporation",
            sectionType = PrepopulatedOfficialSources.SEC_CYBER_SECURITY,
            category = "الأمن السيبراني والإبلاغ الأمني",
            portalType = "بوابة الإبلاغ عن الثغرات والتحقيقات الأمنية في أنظمة ويندوز",
            officialUrl = "https://msrc.microsoft.com",
            description = "البوابة الرسمية لتلقي بلاغات الثغرات الأمنية في أنظمة Windows وسحابة Azure وإصدار تصحيحات 'Patch Tuesday'.",
            region = "عالمي",
            requirements = "تقرير فني مفصل ومفهوم إثبات الثغرة (PoC)",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 170
        ),
        OfficialSourceEntity(
            id = "src_vuln_google_vrp",
            name = "Google Bug Hunters (Vulnerability Reward Program)",
            companyOrEntity = "Google LLC",
            sectionType = PrepopulatedOfficialSources.SEC_CYBER_SECURITY,
            category = "الأمن السيبراني والإبلاغ الأمني",
            portalType = "منظومة مكافآت الثغرات الأمنية في خدمات غوغل وأندرويد",
            officialUrl = "https://bughunters.google.com",
            description = "المنصة الرسمية لباحثي الأمن السيبراني للإبلاغ عن ثغرات أندرويد وكروم وخدمات غوغل السحابية ومكافأتهم مالياً.",
            region = "عالمي",
            requirements = "حساب Google وتقرير أمني مطابق لإرشادات الإفصاح المسؤول",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 171
        ),
        OfficialSourceEntity(
            id = "src_vuln_apple_bounty",
            name = "Apple Security Research & Bounty Portal",
            companyOrEntity = "Apple Inc.",
            sectionType = PrepopulatedOfficialSources.SEC_CYBER_SECURITY,
            category = "الأمن السيبراني والإبلاغ الأمني",
            portalType = "بوابة أبحاث أمن أجهزة آبل وأنظمة iOS وmacOS",
            officialUrl = "https://security.apple.com",
            description = "البوابة المعتمدة للإبلاغ عن ثغرات كسر الحماية وتجاوز شاشات القفل واستغلال برمجيات التجسس في أجهزة آبل.",
            region = "عالمي",
            requirements = "حساب Apple ID وتقرير تقني لاختبار الاختراق",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 172
        ),
        OfficialSourceEntity(
            id = "src_vuln_meta_whitehat",
            name = "Meta Whitehat Bug Bounty",
            companyOrEntity = "Meta Platforms Inc.",
            sectionType = PrepopulatedOfficialSources.SEC_CYBER_SECURITY,
            category = "الأمن السيبراني والإبلاغ الأمني",
            portalType = "بوابة الإبلاغ عن ثغرات فيسبوك وإنستغرام وواتساب",
            officialUrl = "https://www.facebook.com/whitehat",
            description = "بوابة مخصصة للإبلاغ عن ثغرات تسريب البيانات وتجاوز الصلاحيات وسرقة الحسابات على منصات ميتا ومكافأة المكتشفين.",
            region = "عالمي",
            requirements = "حساب فيسبوك والالتزام بسياسة عدم الإضرار ببيانات المستخدمين",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 173
        ),

        // ==========================================
        // THREAT FRAMEWORKS & STANDARDS INSTITUTIONS
        // ==========================================
        OfficialSourceEntity(
            id = "src_frame_mitre_attck",
            name = "MITRE ATT&CK Enterprise Framework",
            companyOrEntity = "The MITRE Corporation",
            sectionType = PrepopulatedOfficialSources.SEC_OSINT_INVESTIGATION,
            category = "مركز التحقق والتقصي",
            portalType = "المصفوفة المعيارية الدولية لتكتيكات وتقنيات القراصنة",
            officialUrl = "https://attack.mitre.org",
            description = "المرجع العالمي لتصنيف سلوكيات المخترقين من الاستطلاع الأولي حتى سرقة وتشفير البيانات وربطها بمجموعات APT.",
            region = "عالمي",
            requirements = "قاعدة معرفية مفتوحة لجميع المحللين والخبراء الجنائيين",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 180
        ),
        OfficialSourceEntity(
            id = "src_frame_nist_nvd",
            name = "NIST National Vulnerability Database (NVD)",
            companyOrEntity = "المعهد الوطني الأمريكي للمعايير والتكنولوجيا (NIST)",
            sectionType = PrepopulatedOfficialSources.SEC_OSINT_INVESTIGATION,
            category = "مركز التحقق والتقصي",
            portalType = "قاعدة البيانات الرسمية للثغرات البرمجية والدرجات القياسية (CVSS)",
            officialUrl = "https://nvd.nist.gov",
            description = "المستودع الحكومي الفيدرالي لتقييم درجات خطورة الثغرات الأمنية (CVSS) وتقديم الإرشادات الفنية لإغلاقها.",
            region = "عالمي",
            requirements = "محرك بحث ومعلومات مجانية مفتوحة المصدر",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 181
        ),
        OfficialSourceEntity(
            id = "src_frame_owasp",
            name = "Open Web Application Security Project (OWASP)",
            companyOrEntity = "مؤسسة OWASP العالمية",
            sectionType = PrepopulatedOfficialSources.SEC_OSINT_INVESTIGATION,
            category = "مركز التحقق والتقصي",
            portalType = "المعايير الدولية لأمن تطبيقات الويب والهواتف (OWASP Top 10)",
            officialUrl = "https://owasp.org",
            description = "المرجع القياسي المعترف به في المحاكم والجهات التنظيمية لتحديد الإهمال الأمني في برمجة المواقع والتطبيقات.",
            region = "عالمي",
            requirements = "أدلة تدقيق واختبارات مفتوحة المصدر بالكامل",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 182
        ),
        OfficialSourceEntity(
            id = "src_frame_cve",
            name = "CVE Program (Common Vulnerabilities and Exposures)",
            companyOrEntity = "CVE Program Secretariat",
            sectionType = PrepopulatedOfficialSources.SEC_OSINT_INVESTIGATION,
            category = "مركز التحقق والتقصي",
            portalType = "السجل الدولي لترقيم وتوثيق الثغرات الأمنية",
            officialUrl = "https://www.cve.org",
            description = "المنظومة الدولية المعتمدة لمنح أرقام المعرفات القياسية (CVE-YYYY-XXXXX) لكل ثغرة أمنية تكتشف في العالم.",
            region = "عالمي",
            requirements = "محرك بحث مفتوح ومحدث على مدار الساعة",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 183
        ),

        // ==========================================
        // GLOBAL THREAT TELEMETRY & INCIDENT RESPONSE
        // ==========================================
        OfficialSourceEntity(
            id = "src_tele_shadowserver",
            name = "The Shadowserver Foundation",
            companyOrEntity = "The Shadowserver Foundation",
            sectionType = PrepopulatedOfficialSources.SEC_CYBER_SECURITY,
            category = "الأمن السيبراني والإبلاغ الأمني",
            portalType = "مؤسسة مراقبة أمان الإنترنت وإنذار الضحايا والمؤسسات مجاناً",
            officialUrl = "https://www.shadowserver.org",
            description = "مؤسسة أمنية غير ربحية تمسح الإنترنت يومياً لرصد الأجهزة المخترقة وإبلاغ الحكومات والشركات فور إصابتها ببرمجيات التجسس.",
            region = "عالمي (أكثر من 130 دولة)",
            requirements = "اشتراك الجهات الوطنية والشركات لتلقي تقارير الإصابة اليومية",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 190
        ),
        OfficialSourceEntity(
            id = "src_tele_spamhaus",
            name = "The Spamhaus Project",
            companyOrEntity = "The Spamhaus Project (Geneva / London)",
            sectionType = PrepopulatedOfficialSources.SEC_CYBER_SECURITY,
            category = "الأمن السيبراني والإبلاغ الأمني",
            portalType = "أكبر قائمة سوداء عالمية لمكافحة شبكات السبام وسرقة البيانات",
            officialUrl = "https://www.spamhaus.org",
            description = "القائمة السوداء الأكثر تأثيراً في العالم، تحمي 3 مليارات صندوق بريد من رسائل التصيد والروابط الاحتيالية.",
            region = "عالمي",
            requirements = "بوابة استعلام عن العناوين المحظورة وتقديم طلبات الشطب عند التنظيف",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 191
        ),
        OfficialSourceEntity(
            id = "src_tele_first_org",
            name = "Forum of Incident Response and Security Teams (FIRST)",
            companyOrEntity = "FIRST.Org Inc.",
            sectionType = PrepopulatedOfficialSources.SEC_CYBER_SECURITY,
            category = "الأمن السيبراني والإبلاغ الأمني",
            portalType = "المنتدى العالمي لفرق الاستجابة للحوادث والأمن السيبراني",
            officialUrl = "https://www.first.org",
            description = "المنظمة الدولية المعترف بها التي تجمع أكثر من 700 فريق استجابة للطوارئ (CSIRT) من القطاعات الحكومية والمصرفية.",
            region = "عالمي (أكثر من 100 دولة)",
            requirements = "دليل الاتصال المعتمد لفرق الطوارئ السيبرانية المعتمدة",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 192
        ),
        OfficialSourceEntity(
            id = "src_tele_cyberpeace",
            name = "CyberPeace Institute (Geneva)",
            companyOrEntity = "CyberPeace Institute",
            sectionType = PrepopulatedOfficialSources.SEC_GLOBAL_EMERGENCY,
            category = "الطوارئ الرقمية العالمية",
            portalType = "معهد السلام السيبراني لدعم ضحايا الهجمات الإلكترونية الإنسانية",
            officialUrl = "https://cyberpeaceinstitute.org",
            description = "منظمة سويسرية مستقلة تقدم العون الفني الجنائي للقطاعات الإنسانية والضحايا الأكثر ضعفاً المعرضين لهجمات الفدية المنظمة.",
            region = "عالمي",
            requirements = "بوابة المساعدة الفنية للجهات المتضررة",
            verificationStatus = "معتمد ورسمي 100%",
            lastVerifiedDate = "سبتمبر 2026",
            sortOrder = 193
        )
    )
}
