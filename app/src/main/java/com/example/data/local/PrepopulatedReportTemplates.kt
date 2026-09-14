package com.example.data.local

import com.example.data.local.entities.ReportTemplateEntity

object PrepopulatedReportTemplates {
    val DEFAULT_TEMPLATES = listOf(
        ReportTemplateEntity(
            id = "tpl_case_official",
            templateName = "تقرير قضية رسمي معتمد",
            title = "تقرير فحص ومتابعة قضية رقمية",
            subtitle = "توثيق جنائي رقمي معتمد ومؤرخ",
            organizationName = "منظومة جعفر بدران للأدلة الرقمية والاستشارات السيبرانية",
            primaryColorHex = "#00E5FF",
            accentColorHex = "#7C4DFF",
            introText = "تفيد هذه الوثيقة الرسمية بنتائج الفحص الفني والتقصي الرقمي والتحقق من الأدلة المرتبطة بالقضية المعنية وفق المعايير المهنية المتبعة.",
            outroText = "تم إعداد هذا التقرير وتوثيق مخرجاته بناءً على الفحص الفني المباشر والبيانات المسجلة رقمياً.",
            signatureTitle = "الخبير الجنائي الرقمي والمستشار المعتمد",
            signatureName = "جعفر بدران",
            footerText = "وثيقة عمل رسمية صادرة ومعتمدة - منظومة جعفر بدران للأدلة الرقمية",
            visibleSectionsJson = """["COVER","SUMMARY","DETAILS","LINKS","ACTIONS","EVIDENCE","PAYMENTS","COMPLETION","SIGNATURE"]""",
            sectionsOrderJson = """["COVER","SUMMARY","DETAILS","LINKS","ACTIONS","EVIDENCE","PAYMENTS","COMPLETION","SIGNATURE"]""",
            isDefault = true
        ),
        ReportTemplateEntity(
            id = "tpl_case_completion",
            templateName = "تقرير إنجاز مهمة وتأمين",
            title = "تقرير إنجاز عمل وتأمين حسابات",
            subtitle = "إشعار إتمام المعالجة التقنية والتسليم",
            organizationName = "منظومة جعفر بدران للأدلة الرقمية والاستشارات السيبرانية",
            primaryColorHex = "#00E676",
            accentColorHex = "#00B0FF",
            introText = "نحيطكم علماً بأنه قد تم إنجاز الإجراءات الفنية والأمنية المطلوبة بنجاح وتأمين الحسابات أو استردادها حسب المطلوب.",
            outroText = "نوصي بالاستمرار في تطبيق إرشادات الأمان الرقمي وتفعيل المصادقة الثنائية وتغيير كلمات المرور بشكل دوري.",
            signatureTitle = "مدير العمليات الفنية والأمنية",
            signatureName = "جعفر بدران",
            footerText = "منظومة جعفر بدران - إنجاز وتأمين معتمد",
            visibleSectionsJson = """["COVER","SUMMARY","DETAILS","ACTIONS","COMPLETION","SIGNATURE"]""",
            sectionsOrderJson = """["COVER","SUMMARY","DETAILS","ACTIONS","COMPLETION","SIGNATURE"]""",
            isDefault = false
        ),
        ReportTemplateEntity(
            id = "tpl_case_financial",
            templateName = "تقرير مالي ومطالبة أتعاب",
            title = "كشف حساب وتسوية أتعاب مهنية",
            subtitle = "بيان المستحقات والدفعات المعتمدة",
            organizationName = "منظومة جعفر بدران للأدلة الرقمية والاستشارات السيبرانية",
            primaryColorHex = "#FFD600",
            accentColorHex = "#FF6D00",
            introText = "يوضح هذا التقرير السجل المالي الدقيق والمدفوعات والمتبقي للقضية المحددة.",
            outroText = "تعتبر هذه الوثيقة إشعاراً مالياً رسمياً بالمدفوعات المسجلة في المنظومة.",
            signatureTitle = "الإدارة المالية والتدقيق",
            signatureName = "جعفر بدران",
            footerText = "منظومة جعفر بدران - السجل المالي الرسمي",
            visibleSectionsJson = """["COVER","DETAILS","PAYMENTS","SIGNATURE"]""",
            sectionsOrderJson = """["COVER","DETAILS","PAYMENTS","SIGNATURE"]""",
            isDefault = false
        ),
        ReportTemplateEntity(
            id = "tpl_case_brief",
            templateName = "تقرير موجز تنفيذي",
            title = "ملخص تنفيذي موجز للقضية",
            subtitle = "نظرة سريعة على الوقائع والنتائج",
            organizationName = "منظومة جعفر بدران للأدلة الرقمية",
            primaryColorHex = "#7C4DFF",
            accentColorHex = "#00E5FF",
            introText = "ملخص موجز موجه للإدارة أو العميل يوضح الحالة الراهنة للقضية وأبرز التوصيات.",
            outroText = "لمزيد من التفاصيل الفنية، يرجى مراجعة التقرير الفني الشامل المرفق.",
            signatureTitle = "المستشار التقني والجنائي",
            signatureName = "جعفر بدران",
            footerText = "منظومة جعفر بدران - تقرير موجز",
            visibleSectionsJson = """["SUMMARY","DETAILS","COMPLETION","SIGNATURE"]""",
            sectionsOrderJson = """["SUMMARY","DETAILS","COMPLETION","SIGNATURE"]""",
            isDefault = false
        ),
        ReportTemplateEntity(
            id = "tpl_case_detailed",
            templateName = "تقرير فني تفصيلي شامل",
            title = "تقرير فحص وتحقيق جنائي رقمي تفصيلي",
            subtitle = "سجل متكامل للأدلة والفحص والروابط والإجراءات",
            organizationName = "منظومة جعفر بدران للأدلة الرقمية والاستشارات السيبرانية",
            primaryColorHex = "#00B0FF",
            accentColorHex = "#D500F9",
            introText = "تقرير فني مفصل يوثق كافة المراحل التحقيقية والأدلة المضبوطة والبصمات الرقمية وتتبع الحيازة وسجل الإجراءات المتخذة.",
            outroText = "تم تحريز الأدلة واستخراج البصمات وفق أفضل الممارسات المتبعة في التحقيقات الجنائية الرقمية.",
            signatureTitle = "الخبير الجنائي المعتمد",
            signatureName = "جعفر بدران",
            footerText = "منظومة جعفر بدران - تقرير تحقيق جنائي رقمي شامل",
            visibleSectionsJson = """["COVER","SUMMARY","DETAILS","LINKS","ACTIONS","EVIDENCE","PAYMENTS","COMPLETION","SIGNATURE"]""",
            sectionsOrderJson = """["COVER","SUMMARY","DETAILS","LINKS","ACTIONS","EVIDENCE","PAYMENTS","COMPLETION","SIGNATURE"]""",
            isDefault = false
        )
    )
}
