package com.efecandonmez.subtracker.app.ui.badges

data class BadgeInfo(val title: String, val description: String, val emoji: String)

val BADGE_INFO_MAP = mapOf(
    "FIRST_SUBSCRIPTION" to BadgeInfo("İlk Adım", "İlk aboneliğini ekledin", "🎉"),
    "FIVE_SUBSCRIPTIONS" to BadgeInfo("Koleksiyoncu", "5 abonelik ekledin", "📦"),
    "TEN_SUBSCRIPTIONS" to BadgeInfo("Uzman Takipçi", "10 abonelik ekledin", "🏆"),
    "FIRST_DELETE" to BadgeInfo("Temizlik Zamanı", "Kullanmadığın bir aboneliği sildin", "🧹"),
    "BUDGET_AWARE" to BadgeInfo("Bilinçli Kullanıcı", "Bütçe özetini ilk kez görüntüledin", "📊")
)

val ALL_BADGE_TYPES = BADGE_INFO_MAP.keys.toList()