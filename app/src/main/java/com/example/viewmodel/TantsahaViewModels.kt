package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
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
                text = "Manao ahoana Tantsaha! Izaho no Tantsaha AI, mpanolotsaina anao momba ny fiompiana sy fambolena eto Madagasikara. Inona no zava-misy na fanontaniana azoko ampiana anao androany?"
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(userText: String) {
        if (userText.isBlank() || _isLoading.value) return

        val userMsg = ChatMessage(sender = MessageSender.USER, text = userText)
        _messages.value = _messages.value + userMsg
        _isLoading.value = true

        viewModelScope.launch {
            val reply = GeminiClient.askTantsahaAi(userText)
            val aiMsg = ChatMessage(sender = MessageSender.AI, text = reply)
            _messages.value = _messages.value + aiMsg
            _isLoading.value = false
        }
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
