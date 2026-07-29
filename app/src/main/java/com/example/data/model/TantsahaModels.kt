package com.example.data.model

data class LivestockCategory(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val topics: List<LivestockTopic>
)

data class LivestockTopic(
    val id: String,
    val title: String,
    val subtitle: String,
    val details: String,
    val keyTips: List<String>
)

data class CropCategory(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val methods: String,
    val soilPrep: String,
    val fertilizing: String,
    val careAndHarvest: String,
    val keyAdvice: List<String>
)

data class VaccineInfo(
    val id: String,
    val animalType: String,
    val vaccineName: String,
    val targetDisease: String,
    val agePeriod: String,
    val method: String,
    val frequency: String,
    val notes: String
)

data class EbookItem(
    val id: String,
    val title: String,
    val category: String,
    val author: String,
    val pagesCount: Int,
    val rating: Float,
    val summary: String,
    val tableOfContents: List<String>,
    val fullContent: String
)

data class NewsItem(
    val id: String,
    val title: String,
    val category: String,
    val date: String,
    val summary: String,
    val fullText: String,
    val isFeatured: Boolean = false
)

data class WeatherCity(
    val name: String,
    val region: String,
    val tempC: Int,
    val condition: String,
    val humidity: Int,
    val rainProb: Int,
    val windKmh: Int,
    val farmingAdvice: String
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageSender {
    USER,
    AI
}

data class MarketplaceItem(
    val id: String,
    val title: String,
    val category: String,
    val priceAr: Long,
    val commissionPercent: Double = 5.0,
    val sellerName: String,
    val sellerPhone: String,
    val location: String,
    val quantityAvailable: String,
    val description: String,
    val datePosted: String = "28 Jolay 2026",
    val isVerifiedSeller: Boolean = true
) {
    val commissionAmountAr: Long get() = (priceAr * (commissionPercent / 100.0)).toLong()
    val sellerPayoutAr: Long get() = priceAr - commissionAmountAr
}

data class CommissionSummary(
    val totalSalesAr: Long,
    val commissionRatePercent: Double,
    val totalCommissionEarnedAr: Long,
    val pendingPayoutsAr: Long,
    val completedTransactionsCount: Int
)

data class AdMobConfig(
    val publisherId: String = "pub-3904073341234567",
    val bannerAdUnitId: String = "ca-app-pub-3904073341234567/6300978111",
    val interstitialAdUnitId: String = "ca-app-pub-3904073341234567/1033173712",
    val isTestMode: Boolean = true,
    val isAdMobEnabled: Boolean = true
)

enum class UserRole(val displayName: String, val badgeColor: Long) {
    TANTSAHA("Tantsaha (Mpiompy / Mamboly)", 0xFF2E7D32),
    GROSSISTE("Grossiste (Mpambongadiny)", 0xFFE65100),
    MPIVAROTRA_MADINIKA("Mpivarotra Madinika (Détaillant)", 0xFF1565C0)
}

data class UserProfile(
    val id: String = "user_1",
    val name: String = "Rakoto Jean",
    val role: UserRole = UserRole.TANTSAHA,
    val phone: String = "034 12 345 67",
    val location: String = "Antananarivo (Ivato)",
    val bio: String = "Mpiompy akoho gasy nohatsaraina sy kisoa miteraka. Mpamatsy akoho sy atody madio.",
    val isVerified: Boolean = true,
    val avatarInitials: String = "RJ",
    val avatarColorHex: Long = 0xFF2E7D32,
    val coverColorHex: Long = 0xFF1B5E20,
    val facebookPage: String = "facebook.com/tantsaha.rakotojean",
    val whatsappNumber: String = "+261 34 12 345 67",
    val followersCount: Int = 342,
    val rating: Float = 4.9f
)

data class VideoPost(
    val id: String,
    val authorName: String,
    val authorRole: UserRole,
    val authorPhone: String,
    val location: String,
    val title: String,
    val description: String,
    val mediaType: String = "VIDEO", // "VIDEO", "PHOTO", "TEXT"
    val photoTag: String = "📷 SARY VOKATRA",
    val videoDurationText: String = "0:45",
    val priceAr: Long,
    val likesCount: Int,
    val commentsCount: Int,
    val sharesCount: Int,
    val datePosted: String = "Vao haingana",
    val category: String = "Akoho & Vorona"
)

data class CommunityGroup(
    val id: String,
    val name: String,
    val roleFocus: String,
    val membersCount: Int,
    val description: String,
    val recentPost: String
)

data class CommunityMessage(
    val id: String,
    val senderName: String,
    val senderRole: UserRole,
    val text: String,
    val timeAgo: String,
    val isMe: Boolean = false
)

