package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TantsahaAppHeader
import com.example.ui.theme.DarkGreenPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.GoldSecondaryBright
import com.example.viewmodel.CalculatorViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Feed, 1: Profit & Cost, 2: Saved Notes

    val savedNotes by viewModel.savedNotes.collectAsState()

    // State for Feed Calc
    val animalType by viewModel.animalTypeFeed.collectAsState()
    val countFeed by viewModel.countFeed.collectAsState()
    val ageDaysFeed by viewModel.ageDaysFeed.collectAsState()

    // State for Profit Calc
    val investmentCost by viewModel.investmentCost.collectAsState()
    val feedCost by viewModel.feedCost.collectAsState()
    val healthCost by viewModel.healthCost.collectAsState()
    val unitPrice by viewModel.expectedSalePriceUnit.collectAsState()
    val quantitySold by viewModel.expectedQuantitySold.collectAsState()

    var noteTitleSave by remember { mutableStateOf("") }
    var showSaveSuccessSnack by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TantsahaAppHeader(
                title = "Kajy Tantsaha",
                subtitle = "Sakafo, Vidin'ny Famokarana & Tombony",
                badgeText = "${savedNotes.size} Kajy voatahiry"
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = DarkGreenPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("1. Kajy Sakafo", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("2. Kajy Tombony", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("3. Voatahiry (${savedNotes.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (selectedTab == 0) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "🧮 Kajy Sakafo Biby Isan'andro",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkGreenPrimary
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Safidio ny biby fiompy:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                ) {
                                    listOf("Akoho Gasy", "Poulet de Chair", "Kisoa").forEach { type ->
                                        FilterChip(
                                            selected = animalType == type,
                                            onClick = { viewModel.animalTypeFeed.value = type },
                                            label = { Text(type, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = DarkGreenPrimary,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = countFeed,
                                    onValueChange = { viewModel.countFeed.value = it },
                                    label = { Text("Isan'ny biby (Head count)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = ageDaysFeed,
                                    onValueChange = { viewModel.ageDaysFeed.value = it },
                                    label = { Text("Taona am-bolana/andro (Age in days)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Calculate Feed Results
                                val headcount = countFeed.toIntOrNull() ?: 0
                                val ageDays = ageDaysFeed.toIntOrNull() ?: 0

                                val (dailyPerHeadGrams, totalDailyKg, totalMonthlyKg) = calculateFeed(animalType, headcount, ageDays)

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "VOKATRY NY KAJY SAKAFO:",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("• Isan-dohany isan'andro: $dailyPerHeadGrams gramme/head", fontSize = 13.sp)
                                        Text("• Fitambarany isan'andro ($headcount $animalType): ${formatNumber(totalDailyKg)} kg / andro", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkGreenPrimary)
                                        Text("• Fitambarany isam-bolana (30 andro): ${formatNumber(totalMonthlyKg)} kg / mois", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkGreenPrimary)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = noteTitleSave,
                                    onValueChange = { noteTitleSave = it },
                                    label = { Text("Anaran'ny fiompiana hitehirizana ity kajy ity") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        if (noteTitleSave.isNotBlank()) {
                                            viewModel.saveCalculationNote(
                                                title = noteTitleSave,
                                                category = "Kajy Sakafo",
                                                content = "Biby: $animalType ($headcount heads), Taona: $ageDays days. Sakafo/andro: ${formatNumber(totalDailyKg)} kg. Sakafo/volana: ${formatNumber(totalMonthlyKg)} kg."
                                            )
                                            noteTitleSave = ""
                                            showSaveSuccessSnack = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Tehirizo ao amin'ny Bokin'ny Tantsaha")
                                }
                            }
                        }
                    }
                } else if (selectedTab == 1) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "💰 Kajy Vidin'ny Famokarana & Tombony",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkGreenPrimary
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = investmentCost,
                                    onValueChange = { viewModel.investmentCost.value = it },
                                    label = { Text("Vidin'ny zanak'akohokely / kisoa / ketsa (Ar)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = feedCost,
                                    onValueChange = { viewModel.feedCost.value = it },
                                    label = { Text("Total Vidin'ny Sakafo / Zezika (Ar)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = healthCost,
                                    onValueChange = { viewModel.healthCost.value = it },
                                    label = { Text("Vaksiny & Fanafody / Labor (Ar)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = unitPrice,
                                    onValueChange = { viewModel.expectedSalePriceUnit.value = it },
                                    label = { Text("Vidin'ny varotra isan'ny vokatra (Ar/unit)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = quantitySold,
                                    onValueChange = { viewModel.expectedQuantitySold.value = it },
                                    label = { Text("Isan'ny vokatra lafo (Quantity sold)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Calculate Profit
                                val inv = investmentCost.toLongOrNull() ?: 0L
                                val feed = feedCost.toLongOrNull() ?: 0L
                                val health = healthCost.toLongOrNull() ?: 0L
                                val totalCost = inv + feed + health

                                val priceUnit = unitPrice.toLongOrNull() ?: 0L
                                val qty = quantitySold.toLongOrNull() ?: 0L
                                val totalRevenue = priceUnit * qty

                                val netProfit = totalRevenue - totalCost
                                val roiPercent = if (totalCost > 0) ((netProfit.toDouble() / totalCost) * 100).toInt() else 0

                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (netProfit >= 0) DarkGreenPrimary.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "VOKATRY NY KAJY TOMBONY:",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkGreenPrimary
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("• Total Vidin'ny Famokarana (Cost): ${formatMoney(totalCost)} Ar", fontSize = 13.sp)
                                        Text("• Fitambarany Varotra (Revenue): ${formatMoney(totalRevenue)} Ar", fontSize = 13.sp)
                                        Text("• TOMBONY MADIO (Net Profit): ${formatMoney(netProfit)} Ar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (netProfit >= 0) DarkGreenPrimary else Color.Red)
                                        Text("• Taham-pivoarana (ROI): $roiPercent%", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = noteTitleSave,
                                    onValueChange = { noteTitleSave = it },
                                    label = { Text("Anaran'ny tetikasa / kajy tombony") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        if (noteTitleSave.isNotBlank()) {
                                            viewModel.saveCalculationNote(
                                                title = noteTitleSave,
                                                category = "Kajy Tombony",
                                                content = "Cost: ${formatMoney(totalCost)} Ar. Revenue: ${formatMoney(totalRevenue)} Ar. Tombony Madio: ${formatMoney(netProfit)} Ar (ROI: $roiPercent%)."
                                            )
                                            noteTitleSave = ""
                                            showSaveSuccessSnack = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Tehirizo amin'ny Bokin'ny Tantsaha")
                                }
                            }
                        }
                    }
                } else {
                    // Saved Notes tab
                    if (savedNotes.isEmpty()) {
                        item {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Calculate, contentDescription = null, tint = DarkGreenPrimary, modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Mbola tsy misy kajy voatahiry", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Manao kajy ao amin me fizarana ambony mba hitehirizana izany.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    } else {
                        items(savedNotes) { note ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = note.title,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkGreenPrimary
                                        )

                                        IconButton(onClick = { viewModel.deleteNote(note) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f))
                                        }
                                    }

                                    Text(text = note.content, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(text = "Noforonina: ${note.dateCreated}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun calculateFeed(type: String, count: Int, ageDays: Int): Triple<Double, Double, Double> {
    val dailyPerHeadGrams = when (type) {
        "Poulet de Chair" -> when {
            ageDays <= 14 -> 25.0
            ageDays <= 28 -> 70.0
            else -> 120.0
        }
        "Kisoa" -> when {
            ageDays <= 60 -> 400.0
            ageDays <= 120 -> 1200.0
            else -> 2500.0
        }
        else -> when { // Akoho Gasy
            ageDays <= 30 -> 20.0
            ageDays <= 90 -> 50.0
            else -> 100.0
        }
    }

    val totalDailyKg = (dailyPerHeadGrams * count) / 1000.0
    val totalMonthlyKg = totalDailyKg * 30.0

    return Triple(dailyPerHeadGrams, totalDailyKg, totalMonthlyKg)
}

private fun formatNumber(valDouble: Double): String {
    return String.format(Locale.getDefault(), "%.1f", valDouble)
}

private fun formatMoney(valLong: Long): String {
    return NumberFormat.getNumberInstance(Locale.FRANCE).format(valLong)
}
