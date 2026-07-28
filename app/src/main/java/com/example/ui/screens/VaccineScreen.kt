package com.example.ui.screens

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
import com.example.viewmodel.VaccineViewModel

@Composable
fun VaccineScreen(
    viewModel: VaccineViewModel
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Reference List, 1: My Calendar & Schedules

    val savedSchedules by viewModel.savedSchedules.collectAsState()

    // Form inputs for new schedule
    var animalTypeInput by remember { mutableStateOf("Akoho") }
    var batchNameInput by remember { mutableStateOf("Bande 1") }
    var vaccineNameInput by remember { mutableStateOf("HB1 (Newcastle)") }
    var scheduledDateInput by remember { mutableStateOf("15/08/2026") }
    var animalCountInput by remember { mutableStateOf("100") }
    var notesInput by remember { mutableStateOf("Atsaharo ny rano misy chlore 24 ora mialoha.") }

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TantsahaAppHeader(
                title = "Kalandrie & Vaksiny",
                subtitle = "Fisorohana Aretina sy Reminders",
                badgeText = "+ Ampidiro Vaksiny",
                onBadgeClick = { showAddDialog = true }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = DarkGreenPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Vaccine Schedule")
            }
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
                    text = { Text("Lisitry ny Vaksiny Fototra", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Kalandrieko (${savedSchedules.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (selectedTab == 0) {
                    items(viewModel.vaccineList) { vac ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                    Surface(
                                        color = DarkGreenPrimary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = vac.animalType,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkGreenPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    Text(
                                        text = vac.agePeriod,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = vac.vaccineName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "Aretina hofoanana: ${vac.targetDisease}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "• Fomba fanaovana: ${vac.method}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "• Naoty: ${vac.notes}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedButton(
                                    onClick = {
                                        animalTypeInput = vac.animalType
                                        vaccineNameInput = vac.vaccineName
                                        showAddDialog = true
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Ampidiro amin me Kalandrieko", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else {
                    if (savedSchedules.isEmpty()) {
                        item {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.MedicalServices, contentDescription = null, tint = DarkGreenPrimary, modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Mbola tsy misy vaksiny nodaharahina", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Kitiho ny bouton '+' mba hampidirana vaksiny vaovao.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    } else {
                        items(savedSchedules) { sched ->
                            val isDone = sched.status == "Vita"

                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDone) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
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
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                color = if (isDone) Color.Gray else DarkGreenPrimary,
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = sched.animalType,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = sched.batchName,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        Text(
                                            text = "Daty: ${sched.scheduledDate}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkGreenPrimary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = sched.vaccineName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Text(
                                        text = "Isan'ny biby: ${sched.animalCount} heads | ${sched.notes}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        TextButton(onClick = { viewModel.deleteSchedule(sched) }) {
                                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Fafana", fontSize = 12.sp, color = Color.Red)
                                        }

                                        Button(
                                            onClick = { viewModel.markDone(sched.id) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isDone) Color.Gray else DarkGreenPrimary
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(if (isDone) Icons.Default.CheckCircle else Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (isDone) "VITA (DONE)" else "Ataovy hoe VITA", fontSize = 12.sp)
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

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Ampidiro Vaksiny Vaovao", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = animalTypeInput,
                        onValueChange = { animalTypeInput = it },
                        label = { Text("Karazana Biby (Akoho, Kisoa, etc.)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = batchNameInput,
                        onValueChange = { batchNameInput = it },
                        label = { Text("Anaran'ny Banda / Lot (Anarana)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = vaccineNameInput,
                        onValueChange = { vaccineNameInput = it },
                        label = { Text("Anaran'ny Vaksiny") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = scheduledDateInput,
                        onValueChange = { scheduledDateInput = it },
                        label = { Text("Daty fanaovana (dd/mm/yyyy)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = animalCountInput,
                        onValueChange = { animalCountInput = it },
                        label = { Text("Isan'ny biby") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        label = { Text("Naoty fanampiny") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val count = animalCountInput.toIntOrNull() ?: 0
                        viewModel.addSchedule(animalTypeInput, batchNameInput, vaccineNameInput, scheduledDateInput, count, notesInput)
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary)
                ) {
                    Text("Tehirizo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Ajanony")
                }
            }
        )
    }
}
