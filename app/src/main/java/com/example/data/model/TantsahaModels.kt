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
