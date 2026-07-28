package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.local.AppDatabase
import com.example.data.repository.TantsahaRepository
import com.example.ui.components.TantsahaBottomBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = TantsahaRepository(
            vaccineDao = database.vaccineDao(),
            bookmarkDao = database.bookmarkDao(),
            farmerNoteDao = database.farmerNoteDao()
        )

        val homeViewModel = HomeViewModel(repository)
        val marketplaceViewModel = MarketplaceViewModel(repository)
        val livestockViewModel = LivestockViewModel(repository)
        val cropsViewModel = CropsViewModel(repository)
        val calculatorViewModel = CalculatorViewModel(repository)
        val vaccineViewModel = VaccineViewModel(repository)
        val ebookViewModel = EbookViewModel(repository)
        val aiAssistantViewModel = AiAssistantViewModel()
        val weatherViewModel = WeatherViewModel(repository)

        setContent {
            MyApplicationTheme {
                TantsahaMainApp(
                    homeViewModel = homeViewModel,
                    marketplaceViewModel = marketplaceViewModel,
                    livestockViewModel = livestockViewModel,
                    cropsViewModel = cropsViewModel,
                    calculatorViewModel = calculatorViewModel,
                    vaccineViewModel = vaccineViewModel,
                    ebookViewModel = ebookViewModel,
                    aiAssistantViewModel = aiAssistantViewModel,
                    weatherViewModel = weatherViewModel
                )
            }
        }
    }
}

@Composable
fun TantsahaMainApp(
    homeViewModel: HomeViewModel,
    marketplaceViewModel: MarketplaceViewModel,
    livestockViewModel: LivestockViewModel,
    cropsViewModel: CropsViewModel,
    calculatorViewModel: CalculatorViewModel,
    vaccineViewModel: VaccineViewModel,
    ebookViewModel: EbookViewModel,
    aiAssistantViewModel: AiAssistantViewModel,
    weatherViewModel: WeatherViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            TantsahaBottomBar(
                currentRoute = currentRoute,
                onNavigate = { screen ->
                    navController.navigate(screen.route) {
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigate = { screen -> navController.navigate(screen.route) }
                )
            }
            composable(Screen.Marketplace.route) {
                MarketplaceScreen(
                    viewModel = marketplaceViewModel
                )
            }
            composable(Screen.Livestock.route) {
                LivestockScreen(
                    viewModel = livestockViewModel,
                    onNavigateToCalculator = { navController.navigate(Screen.Calculator.route) },
                    onNavigateToVaccines = { navController.navigate(Screen.Vaccines.route) }
                )
            }
            composable(Screen.Crops.route) {
                CropsScreen(
                    viewModel = cropsViewModel,
                    onNavigateToAiChat = { navController.navigate(Screen.AiAssistant.route) }
                )
            }
            composable(Screen.Calculator.route) {
                CalculatorScreen(
                    viewModel = calculatorViewModel
                )
            }
            composable(Screen.Vaccines.route) {
                VaccineScreen(
                    viewModel = vaccineViewModel
                )
            }
            composable(Screen.Ebooks.route) {
                EbookScreen(
                    viewModel = ebookViewModel
                )
            }
            composable(Screen.AiAssistant.route) {
                AiAssistantScreen(
                    viewModel = aiAssistantViewModel
                )
            }
            composable(Screen.Weather.route) {
                WeatherScreen(
                    viewModel = weatherViewModel
                )
            }
        }
    }
}
