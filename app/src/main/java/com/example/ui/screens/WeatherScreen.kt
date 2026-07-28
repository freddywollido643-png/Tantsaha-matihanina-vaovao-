package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TantsahaAppHeader
import com.example.ui.theme.DarkGreenPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.GoldSecondaryBright
import com.example.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel
) {
    val selectedCity by viewModel.selectedCity.collectAsState()

    Scaffold(
        topBar = {
            TantsahaAppHeader(
                title = "Meteo Tantsaha",
                subtitle = "Toetrandro & Torohevitra Fambolena",
                badgeText = selectedCity.name
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // City / Location Selector Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(viewModel.cities) { city ->
                    val isSelected = city.name == selectedCity.name
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectCity(city) },
                        label = { Text(city.name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DarkGreenPrimary,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = GoldSecondaryBright
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Main Weather Display Card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            DarkGreenPrimary,
                                            Color(0xFF0D3B10)
                                        )
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        Text(
                                            text = selectedCity.name,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Faritra ${selectedCity.region}",
                                            fontSize = 13.sp,
                                            color = GoldSecondaryBright
                                        )
                                    }

                                    Icon(
                                        imageVector = when {
                                            selectedCity.condition.contains("orana", ignoreCase = true) -> Icons.Default.Thunderstorm
                                            selectedCity.condition.contains("rahona", ignoreCase = true) -> Icons.Default.Cloud
                                            else -> Icons.Default.WbSunny
                                        },
                                        contentDescription = null,
                                        tint = GoldSecondaryBright,
                                        modifier = Modifier.size(54.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "${selectedCity.tempC}°",
                                        fontSize = 54.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = selectedCity.condition,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White.copy(alpha = 0.9f),
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Divider(color = Color.White.copy(alpha = 0.2f))

                                Spacer(modifier = Modifier.height(14.dp))

                                // Weather Stats Grid
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    WeatherStatItem(
                                        title = "Orana (Rain)",
                                        value = "${selectedCity.rainProb}%",
                                        icon = Icons.Default.WaterDrop
                                    )
                                    WeatherStatItem(
                                        title = "Hamandoana",
                                        value = "${selectedCity.humidity}%",
                                        icon = Icons.Default.Air
                                    )
                                    WeatherStatItem(
                                        title = "Rivotra",
                                        value = "${selectedCity.windKmh} km/h",
                                        icon = Icons.Default.Air
                                    )
                                }
                            }
                        }
                    }
                }

                // Agricultural Weather Advisory Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(DarkGreenPrimary.copy(alpha = 0.15f))
                                ) {
                                    Icon(Icons.Default.Agriculture, contentDescription = null, tint = DarkGreenPrimary)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = "Torohevitra Fambolena & Fiompiana",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = selectedCity.farmingAdvice,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherStatItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = GoldSecondaryBright, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = title, fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
    }
}
