package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.LivestockTopic
import com.example.ui.components.TantsahaAppHeader
import com.example.ui.navigation.Screen
import com.example.ui.theme.DarkGreenPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.GoldSecondaryBright
import com.example.viewmodel.LivestockViewModel

@Composable
fun LivestockScreen(
    viewModel: LivestockViewModel,
    onNavigateToCalculator: () -> Unit,
    onNavigateToVaccines: () -> Unit
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    var expandedTopicId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TantsahaAppHeader(
                title = "Fiompiana Matihanina",
                subtitle = "Fomba, Trano, Sakafo, Vaksiny & Kajy",
                badgeText = "Kajy Tombony",
                onBadgeClick = onNavigateToCalculator
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Horizontal Category Selector Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(viewModel.categories) { cat ->
                    val isSelected = cat.id == selectedCategory.id
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.selectCategory(cat)
                            expandedTopicId = null
                        },
                        label = {
                            Text(
                                text = cat.name,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = when (cat.id) {
                                    "akoho_gasy" -> Icons.Default.Pets
                                    "poulet_de_chair" -> Icons.Default.Restaurant
                                    "kisoa" -> Icons.Default.Pets
                                    "bitro" -> Icons.Default.CrueltyFree
                                    "tantely" -> Icons.Default.Hive
                                    else -> Icons.Default.SetMeal
                                },
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

            // Main Category Content Area
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Header Banner Image for Selected Category
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_fiompiana_banner_1785239701941),
                                contentDescription = selectedCategory.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.5f))
                            )

                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = selectedCategory.name,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = selectedCategory.description,
                                    fontSize = 12.sp,
                                    color = GoldSecondaryBright
                                )
                            }
                        }
                    }
                }

                // Quick Navigation Shortcuts (Vaksiny / Kajy)
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                    ) {
                        OutlinedButton(
                            onClick = onNavigateToVaccines,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Vaksiny ${selectedCategory.name}", fontSize = 12.sp)
                        }

                        Button(
                            onClick = onNavigateToCalculator,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Kajy Tombony", fontSize = 12.sp)
                        }
                    }
                }

                // List of Topics (Fomba, Trano, Sakafo, Fiterahana, Fisorohana, Vaksiny, Kajy)
                items(selectedCategory.topics) { topic ->
                    val isExpanded = expandedTopicId == topic.id

                    Card(
                        onClick = {
                            expandedTopicId = if (isExpanded) null else topic.id
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isExpanded) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(DarkGreenPrimary.copy(alpha = 0.15f))
                                    ) {
                                        Icon(
                                            imageVector = when {
                                                topic.title.contains("Sakafo") -> Icons.Default.Restaurant
                                                topic.title.contains("Trano") -> Icons.Default.Home
                                                topic.title.contains("Vaksiny") -> Icons.Default.MedicalServices
                                                topic.title.contains("Aretina") -> Icons.Default.Shield
                                                topic.title.contains("Kajy") -> Icons.Default.Calculate
                                                else -> Icons.Default.MenuBook
                                            },
                                            contentDescription = null,
                                            tint = DarkGreenPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = topic.title,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = topic.subtitle,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = DarkGreenPrimary
                                )
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Column(modifier = Modifier.padding(top = 12.dp)) {
                                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                                    Text(
                                        text = topic.details,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 18.sp
                                    )

                                    if (topic.keyTips.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Torohevitra sy TANDREMO:",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkGreenPrimary
                                        )

                                        topic.keyTips.forEach { tip ->
                                            Row(
                                                verticalAlignment = Alignment.Top,
                                                modifier = Modifier.padding(top = 4.dp)
                                            ) {
                                                Text(
                                                    text = "• ",
                                                    fontWeight = FontWeight.Bold,
                                                    color = GoldSecondary
                                                )
                                                Text(
                                                    text = tip,
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
