package com.example.data.local

import com.example.data.local.entities.KnowledgeEntity

object PrepopulatedKnowledge {

    val OFFICIAL_GUIDES: List<KnowledgeEntity> = listOf(
        // ==========================================
        // 1. PLATFORM POLICIES & CORPORATE LEGAL INQUIRIES (سياسات الشركات ومخاطبات إنفاذ القانون)
        // ==========================================
        KnowledgeEntity(
            id = "kb_meta_lers_01",
            title = "نظام طلبات إنفاذ القانون لدى ميتا (Meta LERS)",
            category = "سياسات الشركات",
            summary = "إجراءات تقديم طلبات الحفظ الطارئة (Preservation Requests) وطلبات الكشف القضائية عبر بوابة Meta LERS الرسمية.",
            content = """1. التعريف بالنظام:
بوابة Meta Law Enforcement Online Request System (LERS) هي القناة الرسمية الوحيدة المعترف بها قانونياً لطلب بيانات المستخدمين وسجلات الاتصال (IP, Timestamps, Linked Accounts) من Facebook وInstagram وWhatsApp.

2. طلبات الحفظ المؤقت (Preservation Requests):
- بموجب المادة 18 U.S.C. § 2703(f)، تلتزم ميتا بحفظ السجلات لمدة 90 يوماً قابلة للتمديد لمرة واحدة (إجمالي 180 يوماً).
- متطلبات الطلب: المعرّف الرقمي الحصري (Vanity URL / Instagram ID)، عنوان البريد الإلكتروني المرتبط، وأرقام الهواتف إن وجدت.

3. طلبات الطوارئ الإنسانية (Emergency Disclosure Requests - EDR):
- تُقبل فقط في حالات التهديد الوشيك بالحياة أو الأذى الجسدي الجسيم أو الابتزاز الجنسي الحرج للقُصّر (CSAM).
- يجب إرفاق مذكرة موثقة رسمياً تشرح طبيعة الخطر الفوري وتبرر تجاوز إجراءات الإنابة القضائية الدولية (MLAT).""",
            officialUrl = "https://www.facebook.com/records",
            tags = "Meta, Instagram, WhatsApp, LERS, EDR, Legal"
        ),
        KnowledgeEntity(
            id = "kb_meta_preservation_02",
            title = "بروتوكول تجميد وحفظ أدلة إنستغرام وفيسبوك (90-Day Hold)",
            category = "سياسات الشركات",
            summary = "خطوات إلزام المنصة بعدم إتلاف الحسابات الممسوحة أو المحذوفة من قبل المبتز قبل انتهاء المتابعة والمعالجة.",
            content = """1. الأهمية الفنية والتوثيقية:
عندما يقوم الطرف الآخر بحذف الحساب أو سحب الرسائل في Direct Message، تظل البيانات مؤرشفة في خوادم النسخ الاحتياطي لفترة تتراوح بين 30 إلى 90 يوماً قبل الكتابة فوقها نهائياً.

2. إجراءات الإرسال:
- تجهيز خطاب رسمي صادر من جهة استشارية مرخصة بالتعاون مع العميل.
- توثيق التاريخ الدقيق ووحدة التوقيت الزمني (UTC/GMT) لإرسال الرسائل ومشاركتها.
- إرسال طلب Preservation رسمي عبر القنوات المخصصة.""",
            officialUrl = "https://help.instagram.com",
            tags = "Instagram, Preservation, Retention, Evidence"
        ),
        KnowledgeEntity(
            id = "kb_google_ncii_03",
            title = "إزالة المحتوى الصريح غير الرضائي وقضايا الدوكسينغ من جوجل",
            category = "سياسات الشركات",
            summary = "المسار السريع لحذف الروابط ومحركات البحث للصور الحساسة وعمليات التشهير (NCII Removal).",
            content = """1. إطار السياسة:
تتيح Google نموذجاً خاصاً لحذف الصور الحميمية غير الرضائية (Non-Consensual Explicit Imagery) والبيانات الشخصية الحساسة المسربة (Doxxing) كأرقام الهوية وعناوين الإقامة وسجلات البنوك.

2. خطوات التقديم السريع:
- جمع روابط النتائج المباشرة (Direct URLs) من صفحة نتائج البحث (SERP).
- التقاط لقطات شاشة واضحة توثق ظهور الاسم أو الصور.
- استخدام نموذج 'Remove select personally identifiable info or explicit content from Google Search'.
- في حال القُصّر: يتم استخدام مسار Child Sexual Abuse Material الفوري الذي يُزال خلال ساعات مع إشعار NCMEC.""",
            officialUrl = "https://support.google.com/websearch/troubleshooter/3111061",
            tags = "Google, NCII, Doxxing, Takedown, SERP"
        ),
        KnowledgeEntity(
            id = "kb_telegram_abuse_04",
            title = "مكافحة الابتزاز وقنوات التسريب على تيليجرام (Telegram Abuse)",
            category = "سياسات الشركات",
            summary = "آليات تقديم بلاغات التشهير وحظر القنوات والمجموعات جغرافياً ومخاطبة فريق الدعم الفني.",
            content = """1. طبيعة منصة تيليجرام:
تعتمد تيليجرام على سياسة صارمة ضد التشهير والمواد الإباحية الانتقامية، وتتيح الحظر الجغرافي (Geo-blocking) على أجهزة iOS وأندرويد للقنوات المخالفة.

2. عناوين الاتصال الفنية:
- بريد البلاغات العامة: abuse@telegram.org
- بريد الابتزاز ومواد القاصرين: stopCA@telegram.org
- حساب البلاغات الداخلي: @notoscam

3. شروط قبول البلاغ:
- رابط الرسالة المحدد (t.me/channel/1234) وليس فقط اسم المعرف العام.
- إيضاح أن العميل لم يمنح إذناً بنشر الصور أو البيانات الشخصية.
- إرفاق معرف المستخدم الأصلي المبتز (User ID العددي المكتشف عبر أدوات التحقق الرقمي).""",
            officialUrl = "https://telegram.org/faq#q-there-is-illegal-content-on-telegram-how-do-i-take-it-down",
            tags = "Telegram, Abuse, Geo-blocking, Harassment"
        ),
        KnowledgeEntity(
            id = "kb_apple_id_05",
            title = "سياسة إفصاح بيانات Apple iCloud واستعادة الحسابات المخترقة",
            category = "سياسات الشركات",
            summary = "محددات التشفير الكامل (End-to-End Encryption) ومدة حظر الاسترداد (Account Recovery Delay).",
            content = """1. البيانات المشفرة القابلة للإفصاح:
- النسخ الاحتياطية لجهاز آيفون (Device Backups) في حال عدم تفعيل الحماية المتقدمة للبيانات (ADP).
- سجل المعاملات على App Store، تفاصيل اشتراك iCloud، وعناوين IP المسجلة للدخول.

2. البيانات المشفرة غير القابلة للقراءة من Apple:
- كلمات المرور في Keychain، البيانات الصحية Health Data، الرسائل المحمية بـ Advanced Data Protection.

3. معالجة اختراق Apple ID:
- في حال تغيير البريد أو تفعيل المفتاح الأمني، يخضع الحساب لفترة انتظار إلزامية (Security Delay) تمتد من 24 ساعة إلى 14 يوماً للتحقق من هوية المالك الأصلي عبر iforgot.apple.com.""",
            officialUrl = "https://support.apple.com/legal/privacy/law-enforcement",
            tags = "Apple, iCloud, Forensics, ADP, Account Recovery"
        ),
        KnowledgeEntity(
            id = "kb_x_twitter_06",
            title = "إجراءات انتهاكات الحسابات والتشهير على منصة X (Twitter سابقا)",
            category = "سياسات الشركات",
            summary = "التعامل مع حسابات انتحال الشخصية، تسريب الوثائق السرية، والوقف الفوري للحسابات المسيئة.",
            content = """1. بلاغات انتحال الهوية (Impersonation):
- تتطلب إثبات الهوية الرسمية (جواز سفر أو رخصة قيادة للمتضرر).
- إبراز الفارق بين الحساب الحقيقي والحساب المزيف (Bio، تاريخ الإنشاء، المنشورات).

2. تسريب الصور الخاصة والتهديد بالتشهير:
- تصنف X التهديد بنشر صور خاصة كانتهاك جسيم لقواعد الخصوصية، ويؤدي إلى تعليق الحساب نهائياً وإلغاء المعرف المرتبط.""",
            officialUrl = "https://help.twitter.com/forms",
            tags = "X, Twitter, Impersonation, Privacy, Suspension"
        ),
        KnowledgeEntity(
            id = "kb_tiktok_legal_07",
            title = "قواعد إزالة التهديد والابتزاز على تيك توك (TikTok Safety)",
            category = "سياسات الشركات",
            summary = "مسار الطوارئ لحذف المقاطع المركبة والبثوث المباشرة التي تستهدف العملاء.",
            content = """1. مركز الأمان والبلاغات الرسمية:
- يوفر تيك توك نظاماً لمراجعة المحتوى المسيء في البث المباشر (Live) وحذفه في غضون دقائق عند ورود بلاغات انتهاك السلامة الجسدية.
- بريد فريق الاستجابة القانونية: lawenforcement@tiktok.com.""",
            officialUrl = "https://www.tiktok.com/safety",
            tags = "TikTok, Live, Safety, Harassment, Video Takedown"
        ),
        KnowledgeEntity(
            id = "kb_snapchat_law_08",
            title = "سياسات سناب شات وحفظ أدلة الرسائل المختفية (Snap Inc LER)",
            category = "سياسات الشركات",
            summary = "استرجاع بيانات السنابات وسجلات الدردشة قبل زوالها نهائياً من خوادم Snap.",
            content = """1. طبيعة حفظ الوسائط:
- يتم حذف السنابات غير المفتوحة بعد 30 يوماً، والرسائل المفتوحة تُحذف فوراً ما لم يقم أحد الأطراف بحفظها في المحادثة.
- سجلات الاتصال الفنية (Metadata): تشمل تواريخ الإرسال، عناوين IP، اسم المستخدم، والبريد المسجل وتبقى مؤرشفة لفترة محددة ويتاح طلب تجميدها عبر preservation@snapchat.com.""",
            officialUrl = "https://support.snapchat.com/a/law-enforcement-guidelines",
            tags = "Snapchat, Ephemeral, Metadata, LER"
        ),

        // ==========================================
        // 2. OSINT METHODOLOGIES & TOOLING (استخبارات المصادر المفتوحة)
        // ==========================================
        KnowledgeEntity(
            id = "kb_osint_lifecycle_09",
            title = "دورة حياة استخبارات المصادر المفتوحة (OSINT Life Cycle)",
            category = "استخبارات المصادر المفتوحة",
            summary = "المراحل الخمس الأساسية في الفحص والتحليل الرقمي: التخطيط، الجمع، المعالجة، التحليل، وإعداد التقرير.",
            content = """1. التخطيط والتوجيه (Planning & Direction):
تحديد السؤال التقني الدقيق: من يبتز العميل؟ ما البصمة الرقمية للطرف المعني؟ ما الأهداف القابلة للقياس؟

2. الجمع المباشر (Collection):
جمع البيانات من وسائل التواصل، السجلات المفتوحة، محركات البحث المتخصصة، وقواعد البيانات العامة.

3. المعالجة والفلترة (Processing):
تنظيم المعلومات، استخراج النصوص من الصور (OCR)، وتوحيد التنسيقات الزمنية.

4. التحليل والربط (Analysis & Production):
بناء شبكة العلاقات (Link Analysis)، مطابقة الأسماء المستعارة (Usernames Pivot)، وتقييم مصداقية المرفقات والبيانات.

5. إعداد التقرير الفني المعتمد (Dissemination):
صياغة التقرير بلغة مهنية وفنية محكمة خالية من التخمينات والتأويلات غير المدعومة بالبيانات الموثقة.""",
            officialUrl = "https://www.sans.org/blog/osint-investigation-lifecycle/",
            tags = "OSINT, Methodology, Life Cycle, Intelligence, Reporting"
        ),
        KnowledgeEntity(
            id = "kb_sock_puppets_10",
            title = "صناعة الشخصيات الوهمية وأمان العمليات (Sock Puppets & OPSEC)",
            category = "استخبارات المصادر المفتوحة",
            summary = "قواعد إنشاء وإدارة حسابات البحث دون كشف هوية المسؤول أو موقع الفريق.",
            content = """1. قواعد العزل الصارم (Operational Security - OPSEC):
- عدم فتح الحساب الاستقصائي من الهاتف الشخصي أو المتصفح اليومي إطلاقاً.
- استخدام بيئة افتراضية معزولة (Virtual Machine) أو جهاز مخصص (Burner Device).

2. الاتصال بالشبكة:
- الاتصال حصراً عبر شبكات VPN موثوقة متعددة الطبقات أو شبكة تور (Tor) مع تفادي تسريب DNS (DNS Leak).

3. بناء الهوية المزيفة:
- توليد صور شخصية باستخدام ذكاء اصطناعي (ThisPersonDoesNotExist) مع معالجة العيوب البصرية المعتادة.
- بناء سجل رقمي وتفاعل تدريجي للحساب قبل الشروع في التتبع الميداني.""",
            officialUrl = "https://inteltechniques.com",
            tags = "Sock Puppets, OPSEC, Burner, VM, Privacy, Anonymity"
        ),
        KnowledgeEntity(
            id = "kb_phone_email_intel_11",
            title = "استخبارات أرقام الهواتف والبريد الإلكتروني (Phone & Email Pivoting)",
            category = "استخبارات المصادر المفتوحة",
            summary = "استخراج المعرفات والبيانات المسربة وفحص استعادة كلمات المرور لكشف الجاني.",
            content = """1. فحص التسريبات الكبرى (Breach Data):
- استخدام خدمات مثل HaveIBeenPwned للتحقق من وجود البريد في تسريبات سابقة.
- البحث عن كلمات المرور المجتزأة في قواعد البيانات الأرشيفية للربط بين حسابات مختلفة يستخدمها نفس الشخص.

2. استغلال واجهات استعادة الحسابات (Password Reset Leakage):
- فحص شاشات استرداد الحساب في منصات التواصل لمشاهدة المقاطع المكشوفة من الهاتف (e.g. +966 50 *** **12) أو أجزاء من البريد المساعد.

3. فحص مزود الخدمة وHLR Lookup:
- التحقق من شبكة الاتصال، الدولة، وما إذا كان الرقم افتراضياً (VoIP مثل TextNow / Google Voice) أم شريحة حقيقية.""",
            officialUrl = "https://haveibeenpwned.com",
            tags = "Phone, Email, OSINT, Breach, Password Reset, VoIP"
        ),
        KnowledgeEntity(
            id = "kb_geoint_suncalc_12",
            title = "التحليل الجغرافي وحساب الظلال (GEOINT & SunCalc)",
            category = "استخبارات المصادر المفتوحة",
            summary = "تحديد الموقع الجغرافي الدقيق وتوقيت التقاط الصور بالاعتماد على زوايا الشمس والظلال والتضاريس.",
            content = """1. فحص التضاريس والمعالم:
- مطابقة خطوط الأفق، قمم الجبال، وأعمدة الإنارة مع صور الأقمار الصناعية (Google Earth Pro).
- تحليل لوحات السيارات، اللافتات التجارية، وأشكال منافذ الكهرباء المنزلية لمعرفة الدولة والمدينة.

2. استخدام SunCalc لتحديد التوقيت:
- إيجاد زاوية ارتفاع الشمس (Solar Elevation) واتجاه الظل (Azimuth) في الموقع المعني لمقارنتها مع صورة الملف الموثق لمعرفة الساعة الدقيقة للواقعة.""",
            officialUrl = "https://www.suncalc.org",
            tags = "GEOINT, SunCalc, Shadows, Geolocation, Satellite"
        ),
        KnowledgeEntity(
            id = "kb_telegram_osint_13",
            title = "التتبع التقني لمجموعات وتيليجرام (Telegram Scraping & Bots)",
            category = "استخبارات المصادر المفتوحة",
            summary = "استخراج المعرف الرقمي الثابت (Telegram User ID) ومراقبة القنوات المغلقة.",
            content = """1. الفرق بين Username وUser ID:
- يمكن تغيير اسم المستخدم (@username) في أي ثانية، بينما المعرف الرقمي (User ID e.g. 109847289) يظل ثابتاً للأبد ومرتبطاً بالحساب.
- استخدام أدوات موثوقة لاستخراج الـ ID فور التقاط المحادثة.

2. أرشفة الرسائل وسجل التعديلات:
- تسجيل الرسائل قبل قيام الجاني بالحذف المتبادل (Delete for both parties).
- فحص سجل الرسائل المتبادلة في المجموعات المشتركة.""",
            officialUrl = "https://core.telegram.org",
            tags = "Telegram, Scraping, UserID, Forensics, Archiving"
        ),
        KnowledgeEntity(
            id = "kb_domain_whois_14",
            title = "تحليل نطاقات التصيد والاحتيال (Domain & DNS Pivoting)",
            category = "استخبارات المصادر المفتوحة",
            summary = "كشف خوادم الصفحات المزورة وسجلات WHOIS وتاريخ تغييرات أسماء النطاقات.",
            content = """1. أدوات الاستعلام الأساسية:
- فحص سجلات DNS (A, MX, TXT, NS).
- استخدام خدمات مثل SecurityTrails وViewDNS لاسترجاع السجلات التاريخية قبل تطبيق إخفاء الهوية.

2. تتبع شهادات الأمان (SSL Certificates & Censys):
- مطابقة معرّف شهادة SSL (Certificate Fingerprint) لاكتشاف خوادم أخرى يديرها نفس المحتال.""",
            officialUrl = "https://securitytrails.com",
            tags = "DNS, WHOIS, Phishing, SSL, Censys"
        ),
        KnowledgeEntity(
            id = "kb_darkweb_monitoring_15",
            title = "رصد منتديات الاختراق والويب المظلم (Breach Forums & Dark Web)",
            category = "استخبارات المصادر المفتوحة",
            summary = "البحث الآمن في قواعد البيانات المعروضة للبيع دون تعريض بنية الفريق للاختراق.",
            content = """1. مسارات الدخول الآمن:
- الولوج عبر عقد Tor خاصة ومحركات بحث مظلمة مخصصة (Ahmia, Onion Search).
- البحث عن اسم المستخدم الخاص بالعميل أو الشركة المستهدفة للتأكد من عدم طرح بياناتها للبيع.

2. تتبع محافظ العملات المشفرة:
- تتبع عناوين محافظ Bitcoin وUSDT (TRC-20) باستخدام مستكشفات البلوكشين (Blockchain Explorers) لمعرفة وجهة الأموال.""",
            officialUrl = "https://ahmia.fi",
            tags = "Dark Web, Tor, Breach Forums, Crypto, Blockchain"
        ),
        KnowledgeEntity(
            id = "kb_social_graph_16",
            title = "رسم وتتبع الشبكات الاجتماعية (Social Graph Analysis)",
            category = "استخبارات المصادر المفتوحة",
            summary = "كشف دوائر المعارف المقربة وتحديد الحسابات البديلة لنفس الجاني عبر التفاعل المشترك.",
            content = """1. تحليل التعليقات والتفاعلات الأولى:
- الحسابات المنشأة حديثاً غالباً ما تترك أثراً في حسابات أصحابها الحقيقيين عبر الإعجابات أو المتابعات المبكرة.
- رسم جدول بالمعرفات الأكثر تفاعلاً لاستنتاج الشخصية الواقعية.""",
            officialUrl = "https://maltego.com",
            tags = "Graph, Social Network, Maltego, Correlation"
        ),

        // ==========================================
        // 3. PSYCHOLOGICAL CASEWORK & VICTIM DE-ESCALATION (الإسعاف النفسي وإدارة القضايا)
        // ==========================================
        KnowledgeEntity(
            id = "kb_pfa_protocol_17",
            title = "بروتوكول الدعم النفسي الأولي لعملاء قضايا الابتزاز (PFA Protocol)",
            category = "الدعم النفسي وإدارة القضايا",
            summary = "الخطوات الأربع لاحتواء الهلع وتخفيف لوم الذات واستعادة التفكير المنطقي خلال أول 30 دقيقة.",
            content = """1. مرحلة تفريغ الصدمة والأمان الفوري:
- طمأنة العميل: 'أنت في بيئة آمنة وسرية تامة ولست أول ولا آخر من يتعرض لهذا الموقف'.
- تطبيق تمرين التنفس التهدئي 4-7-8 لتخفيض إفراز الأدرينالين والكورتيزول وإيقاف نوبة الهلع (Panic Attack).

2. نزع الشعور بالذنب (Guilt Alleviation):
- التأكيد أن الواقعة يتحمل مسؤوليتها بالكامل الطرف المبتز، وأن الوقوع في الفخ نتيجة تلاعب نفسي مدروس وليست دليلاً على انعدام الوعي.

3. منع القرارات الاندفاعية:
- تحذير العميل قطعياً من: الاستجابة لطلبات الدفع المالي، إرسال مزيد من الوسائط، أو حذف المحادثات والملفات الموثقة.""",
            officialUrl = "https://www.who.int/publications/i/item/9789241548205",
            tags = "PFA, Psychological, Panic, Extortion, Support"
        ),
        KnowledgeEntity(
            id = "kb_blackmail_negotiation_18",
            title = "تكتيكات كسب الوقت وإدارة التواصل مع المبتز (Cooling-off Tactics)",
            category = "الدعم النفسي وإدارة القضايا",
            summary = "كيفية تبريد الأزمة وإبطاء تصعيد الجاني دون دفع فدية ودون إثارة غضبه.",
            content = """1. المبدأ الذهبي للتفاوض:
هدف مسؤول المتابعة ليس مجادلة المبتز، بل شراء الوقت (Buying Time) لتمكين الفريق الفني من تحديد هويته وتجميد حساباته.

2. افتعال المعوقات المالية والتقنية المنطقية:
- التذرع بمشاكل في الحسابات المصرفية، تعليق التحويل، أو الحاجة لاستلام دفعة الراتب.
- التحدث بنبرة هادئة غير مستفزة تجعل الجاني يعتقد أنه مسيطر على الموقف وقريب من تحقيق هدفه المالي.

3. إيقاف الوعود الزائفة:
- عدم تقديم وعود قاطعة بالدفع في ساعة محددة تتسبب في انفجار الغضب عند حلولها، بل ربط الأمر بإجراءات خارجة عن الإرادة.""",
            officialUrl = "https://www.ic3.gov",
            tags = "Negotiation, Blackmail, Crisis, De-escalation"
        ),
        KnowledgeEntity(
            id = "kb_proof_of_destruction_19",
            title = "حقيقة وعود الحذف لدى المبتزين وتفكيك أوهام الدفع",
            category = "الدعم النفسي وإدارة القضايا",
            summary = "إقناع العميل علمياً ومنطقياً بأن الدفع لا ينهي الابتزاز بل يضاعفه 10 أضعاف.",
            content = """1. الإحصاءات والدراسات المعتمدة:
أكثر من 87% من المبتزين الذين استلموا مبالغ مالية أعادوا طلب مبالغ أكبر خلال أسبوع واحد، لأن استجابة العميل تثبت قدرته المالية وقابليته للرضوخ.

2. استحالة إثبات الحذف:
لا يمكن تقنياً إثبات قيام الجاني بحذف النسخ الاحتياطية أو صور السحاب أو محركات الأقراص الخارجية؛ وبالتالي لا قيمة لأي فيديو يرسله المبتز يزعم فيه إفراغ سلة المهملات.""",
            officialUrl = "https://cybercrime.gov",
            tags = "Ransom, Evidence, Blackmail Psychology, Awareness"
        ),
        KnowledgeEntity(
            id = "kb_post_incident_hygiene_20",
            title = "إعادة التأهيل الرقمي وتطهير الحسابات بعد انتهاء الأزمة",
            category = "الدعم النفسي وإدارة القضايا",
            summary = "خطة العمل الوقائية للعميل بعد إغلاق القضية لتجنب التعرض لهجمات مستقبلية.",
            content = """1. تغيير مفاتيح الأمان وكلمات المرور:
- تغيير كلمات المرور لكافة المنصات وتفعيل مفاتيح المرور Passkeys والمصادقة الثنائية عبر تطبيقات مستقلة (Google Authenticator).

2. مراجعة إعدادات الخصوصية:
- تحويل الحسابات العامة إلى خاصة، إخفاء قوائم المتابعين والأصدقاء، ومراجعة الأجهزة المسجلة للدخول وتسجيل الخروج منها جميعاً.

3. الدعم النفسي طويل الأمد:
- توجيه العميل لجلسات تفريغ نفسي مع مختصين مرخصين لعلاج أعراض الصدمة وما بعد الصدمة (PTSD).""",
            officialUrl = "https://staysafeonline.org",
            tags = "Post Incident, 2FA, Passkeys, Digital Hygiene, PTSD"
        ),
        KnowledgeEntity(
            id = "kb_family_crisis_containment_21",
            title = "إدارة الأزمات الأسرية والمجتمعية لقضايا التشهير",
            category = "الدعم النفسي وإدارة القضايا",
            summary = "كيفية مصارحة العائلة عند وصول التهديد للأقارب وبناء درع حماية نفسي للعميل.",
            content = """1. تقييم المخاطر الأسرية:
تحديد ما إذا كان إشراك العائلة يحمي العميل من التورط في قروض أو انتحار، أم قد يضاعف الخطر الجسدي.

2. صياغة الرواية المتماسكة:
مساعدة العميل في عرض الموقف كجريمة قرصنة واحتيال خارجي مدبر وليس كخطأ شخصي، والتركيز على مواجهة الموقف نظامياً.""",
            officialUrl = "https://www.fbi.gov/how-we-can-help-you/safety-resources",
            tags = "Family, Crisis, Support, Protection, Privacy"
        ),
        KnowledgeEntity(
            id = "kb_financial_mitigation_22",
            title = "معالجة التحويلات المالية المغصوبة وتجميد الحسابات البنكية",
            category = "الإسعاف النفسي وإدارة القضايا",
            summary = "التواصل مع البنوك لملاحقة الحوالات السريعة (Western Union, CashU, STC Pay).",
            content = """1. البلاغ المصرفي العاجل لمكافحة الاحتيال:
الاتصال المباشر بوحدة مكافحة الاحتيال في البنك لإيقاف الحوالة قبل تسليمها للمستفيد أو وضع إشارة حجز على رقم المعاملة (MTCN).""",
            officialUrl = "https://www.westernunion.com/fraudawareness",
            tags = "Banking, Fraud, Wire Transfer, Western Union, Freezing"
        ),

        // ==========================================
        // 4. MENTAL HEALTH & RESILIENCE (الصلابة المهنية لمسؤول المتابعة)
        // ==========================================
        KnowledgeEntity(
            id = "kb_surgeon_mindset_23",
            title = "عقلية الجرّاح المهنية أثناء فحص الأدلة الصادمة (Surgeon Mindset)",
            category = "الصلابة المهنية لمسؤول المتابعة",
            summary = "استراتيجيات الفصل المعرفي والحفاظ على الحياد العاطفي عند معاينة المواد الحساسة والمقلقة.",
            content = """1. مفهوم عقلية الجراح:
كما يدخل الطبيب الجراح غرفة العمليات بتركيز تقني بحت على إيقاف النزيف دون التماهي مع ألم المريض، يجب على الفاحص التقني النظر إلى الملفات والبيانات كثنائيات برمجية (0s and 1s) وعناصر استدلالية.

2. تقنيات العرض الآمن للملفات:
- تحويل الصور الملونة الصادمة إلى أبيض وأسود (Grayscale) لتقليل التأثير البصري الحسي.
- تقليل حجم المعاينة وتشويش الأوجه عند فحص التلاعب، والتركيز على البصمة التشفيرية (Hash, Hex, Metadata).""",
            officialUrl = "https://www.interpol.int/How-we-work/Policing-capabilities",
            tags = "Mental Health, Resilience, Surgeon Mindset, Coping, Wellness"
        ),
        KnowledgeEntity(
            id = "kb_vicarious_trauma_24",
            title = "الوقاية من الصدمات الثانوية والاحتراق الوظيفي (Vicarious Trauma)",
            category = "الصلابة النفسية لمسؤول المتابعة",
            summary = "مؤشرات الإنهاك النفسي لدى فاحصي الملفات وبرامج التدوير وإزالة السموم الرقمية.",
            content = """1. الأعراض التحذيرية المبكرة:
- استرجاع مشاهد القضايا في الكوابيس وأثناء النوم.
- الشك المفرط في المحيط الاجتماعي والأجهزة الذكية المنزلية (Paranoia).
- تبلد المشاعر أو الانفعال المفاجئ غير المبرر.

2. بروتوكول الوقاية المؤسسي:
- وضع سقف زمني لا يتجاوز ساعتين متواصلتين في فحص المواد شديدة الحساسية.
- إقرار جلسات تفريغ جماعية دورية (Peer Debriefing) لمناقشة المشاعر دون خرق سرية القضايا.""",
            officialUrl = "https://www.apa.org/topics/trauma",
            tags = "Burnout, Trauma, Mental Health, Investigator, Fatigue"
        ),
        KnowledgeEntity(
            id = "kb_strict_boundaries_25",
            title = "قواعد الحدود المهنية الصارمة في التعامل مع العملاء (Boundaries)",
            category = "الصلابة النفسية لمسؤول المتابعة",
            summary = "فصل الحياة الشخصية وتجنب التقمص العاطفي المفرط أو تقديم وعود خلاص مطلقة.",
            content = """1. قنوات الاتصال الرسمية الحصرية:
- منع التواصل مع العملاء عبر الحسابات الشخصية أو في أوقات الراحة إلا في حالات الطوارئ القصوى المصرح بها.
- استخدام قنوات المنصة الموحدة وأرقام العمل الرسمية فقط.

2. الواقعية ومنع الوعود غير القابلة للتحقق:
- عدم إعطاء العميل وعوداً حتمية بنسبة 100% بحذف الصور أو إنهاء المشكلة خلال ساعات، بل شرح الإجراءات الموضوعية ونسب النجاح المتوقعة.""",
            officialUrl = "https://www.counseling.org",
            tags = "Ethics, Boundaries, Investigator, Professionalism"
        ),
        KnowledgeEntity(
            id = "kb_digital_detox_26",
            title = "بروتوكول الديتوكس الرقمي وفصل الشاشات (Digital Detox)",
            category = "الصلابة النفسية لمسؤول المتابعة",
            summary = "استعادة التوازن الذهني للمسؤول بعد إدارة القضايا المعقدة والمتشابكة.",
            content = """1. إيقاف الإشعارات القهرية:
تفعيل وضع عدم الإزعاج وتخصيص هواتف العمل في غرفة مكتب مغلقة خلال العطلات الأسبوعية.""",
            officialUrl = "https://mindful.org",
            tags = "Detox, Digital Wellness, Work Life Balance"
        ),

        // ==========================================
        // 5. OPERATIONAL SOPS (إجراءات التعامل الميدانية وتوثيق الملفات)
        // ==========================================
        KnowledgeEntity(
            id = "kb_stopncii_27",
            title = "تسجيل البصمات الرقمية عبر StopNCII.org لحظر الصور الحميمية",
            category = "إجراءات التعامل الميدانية",
            summary = "كيفية توليد وتشفير بصمات الصور الحساسة على جهاز العميل محلياً دون رفعها لمنع تداولها على المنصات العالمية.",
            content = """1. فلسفة عمل المنصة:
منصة StopNCII.org المعتمدة من تحالف شركات التقنية العالمية (Meta, TikTok, OnlyFans, Bumble) لا تستلم الصورة إطلاقاً، بل تقوم بتوليد بصمة رياضية مجتزأة (Hash) محلياً داخل متصفح العميل.

2. شروط التسجيل:
- أن يكون العميل بالغاً (سن 18 فما فوق).
- أن تكون الصور الأصلية بحوزة المتقدم لتمكين المتصفح من استخراج الـ Hash.

3. النتيجة المترتبة:
يتم تعميم البصمة التشفيرية على خوارزميات المنصات الشريكة، وعندما يحاول أي شخص رفع الصورة أو إرسالها في الدردشات يتم حظرها تلقائياً.""",
            officialUrl = "https://stopncii.org",
            tags = "StopNCII, Hashing, Privacy, Revenge Porn, Takedown"
        ),
        KnowledgeEntity(
            id = "kb_takeitdown_28",
            title = "حماية القُصّر وإزالة الصور المسربة عبر TakeItDown من NCMEC",
            category = "إجراءات التعامل الميدانية",
            summary = "الإجراء المخصص لإنقاذ الأطفال والمراهقين تحت سن 18 عاماً من استغلال وتداول صورهم.",
            content = """1. الجهة المسؤولة:
المركز الوطني للأطفال المفقودين والمستغلين (NCMEC) في الولايات المتحدة بالتعاون مع جهات إنفاذ القانون الدولية.

2. آلية التفعيل:
- الدخول إلى TakeItDown.ncmec.org بواسطة القاصر أو ولي أمره.
- اختيار الصورة محلياً ليقوم الموقع بإنشاء بصمة Hash سرية وربطها بقاعدة البيانات العالمية لمكافحة استغلال الأطفال.""",
            officialUrl = "https://takeitdown.ncmec.org",
            tags = "TakeItDown, NCMEC, Minors, Child Protection, CSAM"
        ),
        KnowledgeEntity(
            id = "kb_instagram_selfie_29",
            title = "استرداد حسابات إنستغرام عبر فحص فيديو السيلفي (Video Selfie Verification)",
            category = "إجراءات التعامل الميدانية",
            summary = "المسار التقني للالتفاف على تغيير البريد الإلكتروني ورقم الهاتف وتجاوز اختراق 2FA.",
            content = """1. الحالات المؤهلة:
- الحسابات التي تحتوي صوراً واضحة لوجه صاحب الحساب الأصلي في المنشورات (Posts).

2. طريقة الدخول للمسار:
- اختيار 'Forgot password' -> 'Need more help?' -> 'My account was hacked'.
- اختيار 'Yes, I have a photo of myself in my account'.
- تسجيل مقطع سيلفي للوجه يدور يميناً ويساراً وأعلى للتحقق من الأبعاد الحيوية (Biometric Match).""",
            officialUrl = "https://help.instagram.com/contact/hack",
            tags = "Instagram, Recovery, Video Selfie, Hacked, Account"
        ),
        KnowledgeEntity(
            id = "kb_whatsapp_simswap_30",
            title = "إنقاذ حسابات واتساب المستهدفة بالاحتيال أو مبادلة الشريحة (SIM-Swap)",
            category = "إجراءات التعامل الميدانية",
            summary = "بروتوكول التواصل مع دعم واتساب لتعليق الحساب المسروق فوراً ومنع مراسلة جهات اتصال العميل.",
            content = """1. الخطوة الفورية:
إرسال بريد إلكتروني عاجل إلى: support@whatsapp.com
- موضوع الرسالة: Lost/Stolen: Please deactivate my account
- محتوى الرسالة: رقم الهاتف بصيغته الدولية الكاملة (مثال: +96650XXXXXXX).

2. النتيجة التقنية:
يتم تسجيل خروج الحساب من جميع الأجهزة فوراً وإدخاله في وضع الخمول لمدة 30 يوماً قبل الحذف، مع إمكانية استعادته بمجرد إصدار شريحة جديدة وتأكيد كود التحقق.""",
            officialUrl = "https://www.whatsapp.com/contact",
            tags = "WhatsApp, SIM-Swap, Deactivation, Hijacking, Support"
        ),
        KnowledgeEntity(
            id = "kb_cease_and_desist_31",
            title = "صياغة إنذار التوقف والامتناع القانوني (Cease & Desist Notice)",
            category = "إجراءات التعامل الميدانية",
            summary = "النموذج الموجه للمبتز للتحذير من الإجراءات النظامية وحفظ سجل المحادثات.",
            content = """1. عناصر الإشعار الإلزامي:
- تنبيه صريح بأن كافة الرسائل والمكالمات مسجلة وموثقة ومودعة لدى مسؤول التوثيق الرقمي.
- الاستشهاد بنصوص أنظمة وقوانين مكافحة جرائم تقنية المعلومات المحلية والعقوبات المقررة (السجن والغرامات ومصادرة الأجهزة).
- طلب الكف الفوري عن التواصل وحذف أي مواد غير قانونية دون الدخول في سجال.""",
            officialUrl = "https://www.justice.gov",
            tags = "Legal, Cease and Desist, Notice, Cybercrime, Police"
        ),
        KnowledgeEntity(
            id = "kb_police_referral_32",
            title = "تجهيز ملف الإحالة والتوثيق الرسمي للجهات المختصة (Referral Package)",
            category = "إجراءات التعامل الميدانية",
            summary = "المعايير الفنية الواجب توفرها في تقرير الفحص الفني لتقديمه للجهات المختصة والجهات النظامية.",
            content = """1. حزمة المرفقات المتكاملة:
- تسلسل زمني دقيق للوقائع بالأيام والساعات.
- جدول المعرفات الرقمية (IP, URLs, User IDs, Phone Numbers, Wallet Addresses).
- شهادة إثبات البصمات التشفيرية (MD5 & SHA-256) للملفات لإثبات عدم التلاعب (Integrity Assurance).
- سجل التوثيق وحماية الملفات يوضح هوية المسؤول وتاريخ الاستخراج.""",
            officialUrl = "https://www.interpol.int",
            tags = "Police, Referral, Evidence, Prosecution, Case File"
        ),
        KnowledgeEntity(
            id = "kb_evidence_packaging_33",
            title = "حفظ الملفات والمستندات وسجل التوثيق (Documentation & Integrity)",
            category = "إجراءات التعامل الميدانية",
            summary = "كيفية حماية الأجهزة والملفات الموثقة من التلف الكهرومغناطيسي أو التعديل غير المقصود.",
            content = """1. أكياس فاراداي (Faraday Bags):
- وضع الهواتف فوراً في حقيبة عازلة للإشارات لمنع إرسال أمر المسح عن بُعد (Remote Wipe via Find My).

2. كتابة وثيقة التوثيق والاستلام:
- تسجيل الرقم التسلسلي للجهاز (IMEI / Serial Number).
- توقيع المستلم والمُسلّم مع تدوين التاريخ والساعة والحالة التشغيلية.""",
            officialUrl = "https://www.nist.gov",
            tags = "Chain of Custody, Faraday, Forensics, Hardware, Integrity"
        ),
        KnowledgeEntity(
            id = "kb_memory_acquisition_34",
            title = "أساسيات أخذ النسخ الحية للذاكرة المؤقتة (Live RAM Acquisition)",
            category = "إجراءات التعامل الميدانية",
            summary = "أهمية استخراج الذاكرة العشوائية قبل إطفاء الجهاز لحفظ مفاتيح التشفير والمحادثات الجارية.",
            content = """1. قاعدة التقلب والزوال (Order of Volatility):
الذاكرة العشوائية RAM هي الأكثر زوالاً يليها ذاكرة التخزين المؤقت، ثم الأقراص الصلبة. إيقاف التشغيل يؤدي إلى فقدان مفاتيح التشفير المؤقتة ووصلات الشبكة النشطة.""",
            officialUrl = "https://www.sans.org",
            tags = "RAM, Acquisition, Volatility, Live Forensics, Encryption"
        ),
        KnowledgeEntity(
            id = "kb_exif_metadata_35",
            title = "فحص البيانات الوصفية للصور ومستندات PDF (EXIF & Metadata Forensics)",
            category = "إجراءات التعامل الميدانية",
            summary = "استخراج إحداثيات GPS ونوع العدسة والبرامج المستخدمة في التعديل (Photoshop / GIMP).",
            content = """1. وسوم EXIF الحيوية:
- Make & Model: نوع الهاتف أو الكاميرا.
- Software: إذا ظهر برنامج تعديل، فهذا مؤشر قوي على التلاعب.
- GPS Latitude / Longitude: الإحداثيات الجغرافية لمكان الالتقاط.""",
            officialUrl = "https://exiftool.org",
            tags = "EXIF, Metadata, ExifTool, GPS, Photos"
        ),
        KnowledgeEntity(
            id = "kb_ela_analysis_36",
            title = "تحليل مستويات الخطأ في الصور الرقمية (Error Level Analysis - ELA)",
            category = "إجراءات التعامل الميدانية",
            summary = "كشف التعديلات والتراكيب المفبركة على الصور عبر قياس تفاوت معدل ضغط JPEG.",
            content = """1. المبدأ العلمي:
عند حفظ صورة بصيغة JPEG، تتعرض البكسلات لمعدل ضغط متجانس. عند لصق جزء من صورة أخرى أو التعديل بالفرشاة، يختلف معدل خطأ الضغط في المنطقة المعدلة ويظهر بلون متوهج في خريطة الـ ELA.""",
            officialUrl = "https://fotoforensics.com",
            tags = "ELA, Image Forensics, Compression, Manipulation, Forgery"
        ),
        KnowledgeEntity(
            id = "kb_hashing_integrity_37",
            title = "حساب ومطابقة البصمات التشفيرية للأدلة (MD5 & SHA-256)",
            category = "إجراءات التعامل الميدانية",
            summary = "القاعدة القانونية لإثبات سلامة الدليل الرقمي في المحاكم الدولية والمحلية.",
            content = """1. معنى البصمة التشفيرية:
دالة رياضية أحادية الاتجاه (Cryptographic Hash) تُنتج سلسلة نصية فريدة للملف. أي تغيير في حرف أو بكسل واحد يغير البصمة بالكامل (Avalanche Effect).

2. متطلبات المحكمة:
توثيق البصمة فور استلام الملف، وإعادة حسابها أثناء الفحص لإثبات مطابقتها الأصلية وعدم العبث بها.""",
            officialUrl = "https://csrc.nist.gov",
            tags = "Hash, MD5, SHA-256, Integrity, Forensic Law"
        ),
        KnowledgeEntity(
            id = "kb_reverse_image_38",
            title = "محركات البحث العكسي المتقدمة ومطابقة الوجوه (Reverse Image & Face OSINT)",
            category = "إجراءات التعامل الميدانية",
            summary = "استخدام Yandex, Google Lens, PimEyes لكشف أصل الصور المجهولة وحسابات أصحابها.",
            content = """1. الفروقات بين المحركات:
- Yandex: الأكثر دقة وتفوقاً في مطابقة ملامح الوجه والصور الملتقطة في بيئات غير مثالية.
- Google Lens: متفوق في مطابقة المنتجات والملابس والأماكن والمعالم السياحية.
- Bing Visual: ممتاز في رصد الصور داخل المواقع الإخبارية والمدونات المفتوحة.""",
            officialUrl = "https://yandex.com/images",
            tags = "Reverse Image, Yandex, Google Lens, PimEyes, Visual Search"
        ),
        KnowledgeEntity(
            id = "kb_passkey_mfa_39",
            title = "تطبيق معايير FIDO2 ومفاتيح المرور لمكافحة التصيد (Passkeys & Security Keys)",
            category = "إجراءات التعامل الميدانية",
            summary = "الحماية المطلقة للحسابات ضد روابط الاختراق والصفحات المزورة.",
            content = """1. كيف تهزم الـ Passkeys هجمات التصيد؟
تعتمد مفاتيح المرور ومفاتيح YubiKey المادية على مطابقة اسم النطاق المشفر، فلا ترسل الرمز السري أبداً إلى صفحة مزيفة مهما كانت مطابقة في الشكل للموقع الأصلي.""",
            officialUrl = "https://fidoalliance.org",
            tags = "Passkeys, FIDO2, Security Keys, Phishing Defense"
        ),
        KnowledgeEntity(
            id = "kb_doxxing_mitigation_40",
            title = "مواجهة ونزع تسريبات البيانات الشخصية (Anti-Doxxing Protocols)",
            category = "إجراءات التعامل الميدانية",
            summary = "حذف سجلات أرقام الهواتف والعناوين من محركات بحث الأشخاص (Data Brokers).",
            content = """1. إزالة البيانات من وسطاء البيانات (Data Brokers):
طلب حذف السجلات الشخصية من أدلة الهواتف الشائعة ومواقع الأرقام (Truecaller, NumBuster) لمنع الوصول إلى عائلة العميل.""",
            officialUrl = "https://privacyrights.org",
            tags = "Doxxing, Data Brokers, Privacy, Truecaller, Security"
        ),
        KnowledgeEntity(
            id = "kb_ransomware_containment_41",
            title = "بروتوكول احتواء برمجيات الفدية وتشفير البيانات (Ransomware Response)",
            category = "إجراءات التعامل الميدانية",
            summary = "عزل الأجهزة المصابة عن الشبكة فوراً وفحص إمكانية فك التشفير عبر NoMoreRansom.",
            content = """1. العزل المادي الفوري:
فصل كابل الشبكة فوراً أو إيقاف بطاقة الواي فاي قبل انتشار التشفير إلى الخوادم المشتركة.

2. فحص مبادرة No More Ransom:
رفع عينة من الملف المشفر ومذكرة الفدية للتحقق من توفر أداة فك تشفير مجانية صادرة من تحالف يوروبول وشركات الأمن السيبراني.""",
            officialUrl = "https://www.nomoreransom.org",
            tags = "Ransomware, NoMoreRansom, Malware, Incident Response"
        ),
        KnowledgeEntity(
            id = "kb_incident_report_writing_42",
            title = "كتابة التقارير الفنية الاحترافية والاعتماد الرسمي",
            category = "إجراءات التعامل الميدانية",
            summary = "هيكلية تقرير الخبير والمستشار الفني ليكون معتمداً ومقنعاً في مراجعة القضايا.",
            content = """1. هيكل التقرير المعتمد:
- الملخص التنفيذي (Executive Summary) للإدارة والمستشارين.
- الوصف التقني المفصل للمرفقات والبيانات والبصمات التشفيرية.
- المنهجية المتبعة والأدوات المستخدمة وأرقام إصداراتها.
- الاستنتاج الفني النهائي المسبب والموثق.""",
            officialUrl = "https://www.nist.gov/itl",
            tags = "Report, Professional Report, Technical Report, Expert Consulting"
        )
    )
}
