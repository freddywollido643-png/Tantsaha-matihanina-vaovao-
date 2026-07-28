package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : Screen("home", "Accueil", Icons.Filled.Home, Icons.Outlined.Home)
    object Marketplace : Screen("marketplace", "Tsena", Icons.Filled.Storefront, Icons.Outlined.Storefront)
    object Livestock : Screen("livestock", "Fiompiana", Icons.Filled.Pets, Icons.Outlined.Pets)
    object Crops : Screen("crops", "Fambolena", Icons.Filled.Grass, Icons.Outlined.Grass)
    object Calculator : Screen("calculator", "Kajy", Icons.Filled.Calculate, Icons.Outlined.Calculate)
    object Vaccines : Screen("vaccines", "Vaksiny", Icons.Filled.MedicalServices, Icons.Outlined.MedicalServices)
    object Ebooks : Screen("ebooks", "Boky", Icons.Filled.Book, Icons.Outlined.Book)
    object AiAssistant : Screen("ai_assistant", "AI Chat", Icons.Filled.SmartToy, Icons.Outlined.SmartToy)
    object Weather : Screen("weather", "Meteo", Icons.Filled.WbSunny, Icons.Outlined.WbSunny)

    companion object {
        val bottomNavScreens = listOf(
            Home,
            Marketplace,
            Livestock,
            Crops,
            Calculator,
            Vaccines,
            Ebooks,
            AiAssistant,
            Weather
        )
    }
}
