package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import com.example.ui.components.TantsahaAppHeader
import com.example.ui.theme.DarkGreenPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.GoldSecondaryBright
import com.example.viewmodel.CropsViewModel

@Composable
fun CropsScreen(
    viewModel: CropsViewModel,
    onNavigateToAiChat: () -> Unit
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: Fomba & Teknika, 1: Tany & Zezika, 2: Fikarakarana & Jinja

    Scaffold(
        topBar = {
            TantsahaAppHeader(
                title = "Fambolena Matihanina",
                subtitle = "Vary, Legioma, Voankazo, Zezika Organika",
                badgeText = "Fanontaniana AI",
                onBadgeClick = onNavigateToAiChat
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Category Chips
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
                            selectedTab = 0
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
                                    "vary" -> Icons.Default.Grass
                                    "legioma" -> Icons.Default.ShoppingBag
                                    "voankazo" -> Icons.Default.Spa
                                    "fambolena_maharitra" -> Icons.Default.Eco
                                    "zezika_organika" -> Icons.Default.Compost
                                    else -> Icons.Default.Landscape
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

            // Category Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_fambolena_banner_1785239713335),
                        contentDescription = selectedCategory.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.55f))
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
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

            // TabRow for Category Stages
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = DarkGreenPrimary,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("1. Fomba & Teknika", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("2. Tany & Zezika", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("3. Fikarakarana & Jinja", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
            }

            // Tab Content
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    when (selectedTab) {
                        0 -> DetailSectionCard(
                            title = "Fomba Fambolena & Teknika",
                            icon = Icons.Default.Psychology,
                            content = selectedCategory.methods,
                            adviceList = selectedCategory.keyAdvice
                        )
                        1 -> Column {
                            DetailSectionCard(
                                title = "Fikarakarana Tany",
                                icon = Icons.Default.Landscape,
                                content = selectedCategory.soilPrep,
                                adviceList = emptyList()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            DetailSectionCard(
                                title = "Fahaiza-Mampiasa Zezika Organika & NPK",
                                icon = Icons.Default.Compost,
                                content = selectedCategory.fertilizing,
                                adviceList = emptyList()
                            )
                        }
                        2 -> DetailSectionCard(
                            title = "Fikarakarana, Fiarovana & Jinja",
                            icon = Icons.Default.WaterDrop,
                            content = selectedCategory.careAndHarvest,
                            adviceList = selectedCategory.keyAdvice
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: String,
    adviceList: List<String>
) {
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
                    Icon(imageVector = icon, contentDescription = null, tint = DarkGreenPrimary)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = content,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            if (adviceList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Lalàna volamena amin me fambolena:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreenPrimary
                )

                adviceList.forEach { advice ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Text("• ", fontWeight = FontWeight.Bold, color = GoldSecondary)
                        Text(
                            text = advice,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
