package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
import com.example.data.api.GeminiContent
import com.example.data.api.GeminiPart
import com.example.data.local.BookmarkEntity
import com.example.data.local.FarmerNoteEntity
import com.example.data.local.VaccineScheduleEntity
import com.example.data.model.*
import com.example.data.repository.TantsahaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: TantsahaRepository) : ViewModel() {
    val newsList: StateFlow<List<NewsItem>> = MutableStateFlow(repository.getNewsAndTips()).asStateFlow()
    val weatherOverview: StateFlow<WeatherCity> = MutableStateFlow(repository.getWeatherCities().first()).asStateFlow()
}

class LivestockViewModel(private val repository: TantsahaRepository) : ViewModel() {
    val categories: List<LivestockCategory> = repository.getLivestockCategories()
    
    private val _selectedCategory = MutableStateFlow(categories.first())
    val selectedCategory: StateFlow<LivestockCategory> = _selectedCategory.asStateFlow()

    private val _selectedTopic = MutableStateFlow<LivestockTopic?>(null)
    val selectedTopic: StateFlow<LivestockTopic?> = _selectedTopic.asStateFlow()

    fun selectCategory(category: LivestockCategory) {
        _selectedCategory.value = category
        _selectedTopic.value = null
    }

    fun selectTopic(topic: LivestockTopic?) {
        _selectedTopic.value = topic
    }
}

class CropsViewModel(private val repository: TantsahaRepository) : ViewModel() {
    val categories: List<CropCategory> = repository.getCropsCategories()

    private val _selectedCategory = MutableStateFlow(categories.first())
    val selectedCategory: StateFlow<CropCategory> = _selectedCategory.asStateFlow()

    fun selectCategory(category: CropCategory) {
        _selectedCategory.value = category
    }
}

class CalculatorViewModel(private val repository: TantsahaRepository) : ViewModel() {
    // State for Feed Calculator
    var animalTypeFeed = MutableStateFlow("Akoho Gasy")
    var countFeed = MutableStateFlow("100")
    var ageDaysFeed = MutableStateFlow("30")

    // State for Cost & Profit Calculator
    var investmentCost = MutableStateFlow("500000")
    var feedCost = MutableStateFlow("1200000")
    var healthCost = MutableStateFlow("80000")
    var expectedSalePriceUnit = MutableStateFlow("25000")
    var expectedQuantitySold = MutableStateFlow("90")

    // State for Density & Yield
    var areaSqMeters = MutableStateFlow("100")
    var animalOrCropType = MutableStateFlow("Akoho")

    val savedNotes: StateFlow<List<FarmerNoteEntity>> = repository.savedNotes.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun saveCalculationNote(title: String, category: String, content: String) {
        viewModelScope.launch {
            repository.saveFarmerNote(title, category, content)
        }
    }

    fun deleteNote(note: FarmerNoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
}

class VaccineViewModel(private val repository: TantsahaRepository) : ViewModel() {
    val vaccineList: List<VaccineInfo> = repository.getVaccinesByAnimal()

    val savedSchedules: StateFlow<List<VaccineScheduleEntity>> = repository.savedSchedules.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun addSchedule(animalType: String, batchName: String, vaccineName: String, date: String, count: Int, notes: String) {
        viewModelScope.launch {
            repository.saveVaccineSchedule(animalType, batchName, vaccineName, date, count, notes)
        }
    }

    fun markDone(id: Long) {
        viewModelScope.launch {
            repository.markScheduleDone(id)
        }
    }

    fun deleteSchedule(schedule: VaccineScheduleEntity) {
        viewModelScope.launch {
            repository.deleteSchedule(schedule)
        }
    }
}

class EbookViewModel(private val repository: TantsahaRepository) : ViewModel() {
    val ebooksList: List<EbookItem> = repository.getEbooks()

    private val _selectedCategory = MutableStateFlow("Rehetra")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _readingEbook = MutableStateFlow<EbookItem?>(null)
    val readingEbook: StateFlow<EbookItem?> = _readingEbook.asStateFlow()

    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.savedBookmarks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun openEbook(ebook: EbookItem?) {
        _readingEbook.value = ebook
    }

    fun toggleBookmark(bookId: String, title: String, category: String, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.toggleBookmark(bookId, title, category, isBookmarked)
        }
    }
}

class AiAssistantViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = MessageSender.AI,
                text = "Manao ahoana Tantsaha! Izaho no Tantsaha AI 🟢. Mikaroka sy manome torohevitra momba ny fiompiana, fambolena, vaksiny, sakafo sy vidin-tsena eto Madagasikara. Inona no fanontaniana azoko ampiana anao androany?"
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _apiKeyInput = MutableStateFlow(GeminiClient.userCustomApiKey)
    val apiKeyInput: StateFlow<String> = _apiKeyInput.asStateFlow()

    fun updateApiKey(newKey: String) {
        _apiKeyInput.value = newKey
        GeminiClient.userCustomApiKey = newKey
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank() || _isLoading.value) return

        val userMsg = ChatMessage(sender = MessageSender.USER, text = userText)
        val currentMsgs = _messages.value
        _messages.value = currentMsgs + userMsg
        _isLoading.value = true

        viewModelScope.launch {
            // Build conversation history for Gemini API
            val history = currentMsgs
                .filter { it.text.isNotBlank() }
                .takeLast(6)
                .map {
                    GeminiContent(
                        role = if (it.sender == MessageSender.USER) "user" else "model",
                        parts = listOf(GeminiPart(text = it.text))
                    )
                }

            val reply = GeminiClient.askTantsahaAi(userText, history)
            val aiMsg = ChatMessage(sender = MessageSender.AI, text = reply)
            _messages.value = _messages.value + aiMsg
            _isLoading.value = false
        }
    }

    fun clearChatHistory() {
        _messages.value = listOf(
            ChatMessage(
                sender = MessageSender.AI,
                text = "Karakarao tsara ny fiompiana sy fambolena! Nadio ny tantaram-pikarohana. Ampidiro indray ny fanontanianao."
            )
        )
    }
}

class WeatherViewModel(private val repository: TantsahaRepository) : ViewModel() {
    val cities: List<WeatherCity> = repository.getWeatherCities()

    private val _selectedCity = MutableStateFlow(cities.first())
    val selectedCity: StateFlow<WeatherCity> = _selectedCity.asStateFlow()

    fun selectCity(city: WeatherCity) {
        _selectedCity.value = city
    }
}

class MarketplaceViewModel(private val repository: TantsahaRepository) : ViewModel() {
    private val _items = MutableStateFlow(repository.getMarketplaceItems())
    val items: StateFlow<List<MarketplaceItem>> = _items.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Rehetra")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _adMobConfig = MutableStateFlow(AdMobConfig())
    val adMobConfig: StateFlow<AdMobConfig> = _adMobConfig.asStateFlow()

    private val _showInterstitial = MutableStateFlow(false)
    val showInterstitial: StateFlow<Boolean> = _showInterstitial.asStateFlow()

    private val _totalSalesAr = MutableStateFlow(4850000L)
    val totalSalesAr: StateFlow<Long> = _totalSalesAr.asStateFlow()

    private val _totalCommissionEarnedAr = MutableStateFlow(242500L) // 5% of total sales
    val totalCommissionEarnedAr: StateFlow<Long> = _totalCommissionEarnedAr.asStateFlow()

    private val _completedOrdersCount = MutableStateFlow(18)
    val completedOrdersCount: StateFlow<Int> = _completedOrdersCount.asStateFlow()

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateAdMobConfig(newConfig: AdMobConfig) {
        _adMobConfig.value = newConfig
    }

    fun triggerInterstitialAd() {
        if (_adMobConfig.value.isAdMobEnabled) {
            _showInterstitial.value = true
        }
    }

    fun dismissInterstitialAd() {
        _showInterstitial.value = false
    }

    fun postNewItem(
        title: String,
        category: String,
        priceAr: Long,
        sellerName: String,
        sellerPhone: String,
        location: String,
        quantity: String,
        description: String
    ) {
        val newItem = MarketplaceItem(
            id = "m_${System.currentTimeMillis()}",
            title = title,
            category = category,
            priceAr = priceAr,
            commissionPercent = 5.0,
            sellerName = sellerName,
            sellerPhone = sellerPhone,
            location = location,
            quantityAvailable = quantity,
            description = description,
            isVerifiedSeller = true
        )
        _items.value = listOf(newItem) + _items.value
        triggerInterstitialAd()
    }

    fun placeOrder(item: MarketplaceItem) {
        _totalSalesAr.value += item.priceAr
        _totalCommissionEarnedAr.value += item.commissionAmountAr
        _completedOrdersCount.value += 1
        triggerInterstitialAd()
    }
}

class CommunityViewModel(private val repository: TantsahaRepository) : ViewModel() {
    private val _currentUser = MutableStateFlow(UserProfile())
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _videoPosts = MutableStateFlow(repository.getVideoPosts())
    val videoPosts: StateFlow<List<VideoPost>> = _videoPosts.asStateFlow()

    private val _groups = MutableStateFlow(repository.getCommunityGroups())
    val groups: StateFlow<List<CommunityGroup>> = _groups.asStateFlow()

    private val _chatMessages = MutableStateFlow(repository.getCommunityMessages())
    val chatMessages: StateFlow<List<CommunityMessage>> = _chatMessages.asStateFlow()

    fun setUserRole(role: UserRole) {
        _currentUser.value = _currentUser.value.copy(role = role)
    }

    fun updateUserProfile(
        name: String,
        phone: String,
        location: String,
        bio: String,
        facebookPage: String = "",
        whatsappNumber: String = "",
        avatarColorHex: Long = 0xFF2E7D32,
        coverColorHex: Long = 0xFF1B5E20
    ) {
        val initials = name.split(" ")
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .take(2)
            .joinToString("")
            .ifEmpty { "RJ" }

        _currentUser.value = _currentUser.value.copy(
            name = name,
            phone = phone,
            location = location,
            bio = bio,
            facebookPage = facebookPage.ifBlank { _currentUser.value.facebookPage },
            whatsappNumber = whatsappNumber.ifBlank { _currentUser.value.whatsappNumber },
            avatarInitials = initials,
            avatarColorHex = avatarColorHex,
            coverColorHex = coverColorHex
        )
    }

    fun publishMediaPost(
        title: String,
        description: String,
        priceAr: Long,
        category: String,
        mediaType: String, // "PHOTO" or "VIDEO"
        photoTag: String = "📷 SARY VOKATRA"
    ) {
        val prefix = if (mediaType == "PHOTO") "📸" else "🎥"
        val newPost = VideoPost(
            id = "post_${System.currentTimeMillis()}",
            authorName = "${_currentUser.value.name} (${_currentUser.value.role.displayName.take(12)})",
            authorRole = _currentUser.value.role,
            authorPhone = _currentUser.value.phone,
            location = _currentUser.value.location,
            title = "$prefix $title",
            description = description,
            mediaType = mediaType,
            photoTag = photoTag,
            videoDurationText = if (mediaType == "VIDEO") "1:15" else "0:00",
            priceAr = priceAr,
            likesCount = 1,
            commentsCount = 0,
            sharesCount = 0,
            datePosted = "Vao haingana",
            category = category
        )
        _videoPosts.value = listOf(newPost) + _videoPosts.value
    }

    fun likeVideoPost(postId: String) {
        _videoPosts.value = _videoPosts.value.map { post ->
            if (post.id == postId) post.copy(likesCount = post.likesCount + 1) else post
        }
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val msg = CommunityMessage(
            id = "cm_${System.currentTimeMillis()}",
            senderName = "Moi (${_currentUser.value.name})",
            senderRole = _currentUser.value.role,
            text = text,
            timeAgo = "Vao haingana",
            isMe = true
        )
        _chatMessages.value = _chatMessages.value + msg
    }
}

