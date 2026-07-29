package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.ActionQuickCard
import com.example.ui.components.AdMobBannerCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.TantsahaAppHeader
import com.example.ui.navigation.Screen
import com.example.ui.theme.DarkGreenPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.GoldSecondaryBright
import com.example.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigate: (Screen) -> Unit
) {
    val newsList by viewModel.newsList.collectAsState()
    val weatherOverview by viewModel.weatherOverview.collectAsState()

    Scaffold(
        topBar = {
            TantsahaAppHeader(
                title = "Tantsaha Matihanina",
                subtitle = "Fiompiana & Fambolena Mahomby",
                badgeText = "Meteo: ${weatherOverview.tempC}°C",
                onBadgeClick = { onNavigate(Screen.Weather) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // AdMob Banner Header
            item {
                AdMobBannerCard()
            }

            // Hero Banner Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_banner_1785239691291),
                            contentDescription = "Fambolena sy Fiompiana Malagasy",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.8f)
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Surface(
                                color = GoldSecondaryBright,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "TOROHEVITRA ARTIKA",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3E2723),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Fambolena sy Fiompiana Nohatsaraina",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Omeo lanja ny vokatra lokalina amin'ny fahaizana maoderina",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            // Tsena Online E-Commerce Highlight Card
            item {
                Card(
                    onClick = { onNavigate(Screen.Marketplace) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GoldSecondary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(DarkGreenPrimary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = "Tsena Online",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "🛒 TSENA ONLINE (E-COMMERCE)",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF281800)
                                )
                                Text(
                                    text = "Amidio & Vidio ny akoho, kisoa, vary sy zezika! (Commission 5%)",
                                    fontSize = 11.sp,
                                    color = Color(0xFF3E2723)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFF281800)
                        )
                    }
                }
            }

            // Réseau Video Tantsaha Highlight Card (Tsy miankina amin'ny FB)
            item {
                Card(
                    onClick = { onNavigate(Screen.Community) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkGreenPrimary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(GoldSecondary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OndemandVideo,
                                    contentDescription = "Réseau Video",
                                    tint = Color(0xFF281800),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "🎬 RÉSEAU VIDEO & CHAT (TSY FB)",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Compte Tantsaha, Grossiste sy Mpivarotra • Video clips • Chat direct",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }

            // Quick Access Navigation Grid
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionHeader(title = "Fizarana Rehetra (Menu)")

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        QuickSquareButton(
                            title = "Tsena Online",
                            subtitle = "E-Commerce & Commission",
                            icon = Icons.Filled.Storefront,
                            color = Color(0xFFE65100),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Screen.Marketplace) }
                        )
                        QuickSquareButton(
                            title = "Fiompiana",
                            subtitle = "Akoho, Kisoa, Bitro...",
                            icon = Icons.Filled.Pets,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Screen.Livestock) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        QuickSquareButton(
                            title = "Fambolena",
                            subtitle = "Vary, Legioma, Zezika...",
                            icon = Icons.Filled.Grass,
                            color = Color(0xFF388E3C),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Screen.Crops) }
                        )
                        QuickSquareButton(
                            title = "Kajy Tantsaha",
                            subtitle = "Sakafo, Tombony...",
                            icon = Icons.Filled.Calculate,
                            color = Color(0xFF1565C0),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Screen.Calculator) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        QuickSquareButton(
                            title = "AI Assistant",
                            subtitle = "Chat amin'ny AI",
                            icon = Icons.Filled.SmartToy,
                            color = Color(0xFF6A1B9A),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Screen.AiAssistant) }
                        )
                        QuickSquareButton(
                            title = "Vaksiny",
                            subtitle = "Kalandrie & Reminders",
                            icon = Icons.Filled.MedicalServices,
                            color = Color(0xFFC62828),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Screen.Vaccines) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        QuickSquareButton(
                            title = "Boky Premium",
                            subtitle = "Ebooks & PDF Offline",
                            icon = Icons.Filled.Book,
                            color = Color(0xFFD84315),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Screen.Ebooks) }
                        )
                        QuickSquareButton(
                            title = "Meteo",
                            subtitle = "Toetrandro & Faritra",
                            icon = Icons.Filled.WbSunny,
                            color = Color(0xFFF57F17),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Screen.Weather) }
                        )
                    }
                }
            }

            // Weather Snapshot
            item {
                Card(
                    onClick = { onNavigate(Screen.Weather) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = GoldSecondary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Toetrandro: ${weatherOverview.name}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${weatherOverview.tempC}°C - ${weatherOverview.condition}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Daily News & Wisdom Section
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionHeader(title = "Vaovao & Torohevitra Isan'andro")

                    newsList.forEach { news ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Surface(
                                        color = if (news.category == "Torohevitra") DarkGreenPrimary.copy(alpha = 0.15f) else GoldSecondary.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = news.category,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkGreenPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }

                                    Text(
                                        text = news.date,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = news.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = news.summary,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickSquareButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
