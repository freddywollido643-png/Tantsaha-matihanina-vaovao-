package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.data.api.GeminiClient
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.ui.components.TantsahaAppHeader
import com.example.ui.theme.DarkGreenPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.GoldSecondaryBright
import com.example.viewmodel.AiAssistantViewModel

@Composable
fun AiAssistantScreen(
    viewModel: AiAssistantViewModel
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val apiKeyInput by viewModel.apiKeyInput.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var showApiKeyModal by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val promptChips = listOf(
        "🐓 Akoho gasy & pondeuse",
        "🐖 Kisoa & PPA",
        "🌾 Vary SRI / SRA",
        "🍅 Voatabia & Legioma",
        "💩 Compost organika",
        "💉 Vaksiny akoho sy kisoa",
        "🐰 Bitro & Trondro",
        "💰 Vidin-tsena malagasy"
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TantsahaAppHeader(
                title = "Tantsaha AI & Fikarohana",
                subtitle = "Mpanolotsaina & Fikarohana Miaraka Aminao 24/7",
                badgeText = if (apiKeyInput.isNotBlank()) "🟢 Gemini Cloud" else "⚡ Search Engine",
                onBadgeClick = { showApiKeyModal = true }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Status and Settings Header Card (Compact & responsive layout)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(GoldSecondaryBright)
                                .padding(2.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_ai_tantsaha_1785239725790),
                                contentDescription = "Tantsaha AI Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Tantsaha AI 🟢 Mpanolotsaina",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            Text(
                                text = "Mikaroka sy manome torohevitra momba ny fambolena & fiompiana",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                maxLines = 1
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.clearChatHistory() },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Fafao Chat",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = { showApiKeyModal = true },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Paramètres API",
                                tint = DarkGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Quick Category Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(promptChips) { chipText ->
                    SuggestionChip(
                        onClick = {
                            val cleanQuery = chipText.replace(Regex("[^a-zA-Z ]"), "").trim()
                            viewModel.sendMessage("Torohevitra momba ny $cleanQuery eto Madagasikara")
                        },
                        label = { Text(chipText, fontSize = 11.sp, maxLines = 1) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            // Chat Messages List
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(messages) { msg ->
                    ChatBubbleItem(message = msg)
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (isLoading) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = DarkGreenPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "🔎 Mikaroka ao amin'ny banky angona sy mandika valiny feno...",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Message Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Mikaroka: Akoho, Kisoa, Vary, Voatabia...", fontSize = 12.sp) },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.weight(1f),
                        maxLines = 3,
                        trailingIcon = {
                            if (inputText.isNotEmpty()) {
                                IconButton(onClick = { inputText = "" }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(DarkGreenPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send Message",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }

    // Modal: Settings & Gemini API Key Modal
    if (showApiKeyModal) {
        var keyState by remember { mutableStateOf(apiKeyInput) }

        AlertDialog(
            onDismissRequest = { showApiKeyModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.VpnKey, contentDescription = null, tint = DarkGreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Configuration Tantsaha AI", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Ny Tantsaha AI dia ampiasaina amin'ny fomba 2:\n" +
                                "1. ⚡ **Smart Agriculture Search Engine**: Efa vonona sy mandeha avy hatrany amin'ny banky angona feno (Akoho, Kisoa, Vary, Legioma, Vaksiny).\n" +
                                "2. 🌐 **Google Gemini API Key**: Azonao ampidirina eto ny Gemini API Key-nao avy amin me AI Studio mba hahazoana valiny miaro amin me Cloud.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = keyState,
                        onValueChange = { keyState = it },
                        label = { Text("Gemini API Key (Optionnel)") },
                        placeholder = { Text("AIzaSy...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateApiKey(keyState)
                        showApiKeyModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary)
                ) {
                    Text("Tahirizo Key")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApiKeyModal = false }) {
                    Text("Akatona")
                }
            }
        )
    }
}

@Composable
fun ChatBubbleItem(message: ChatMessage) {
    val isUser = message.sender == MessageSender.USER

    Row(
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(GoldSecondaryBright)
                    .padding(2.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_ai_tantsaha_1785239725790),
                    contentDescription = "Tantsaha AI Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            color = if (isUser) DarkGreenPrimary else MaterialTheme.colorScheme.surface,
            contentColor = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            shadowElevation = 2.dp,
            modifier = Modifier
                .weight(1f, fill = false)
                .widthIn(max = 320.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!isUser) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Tantsaha AI 🟢",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreenPrimary
                        )
                        Surface(
                            color = DarkGreenPrimary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Mpanolotsaina",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkGreenPrimary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = message.text,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(DarkGreenPrimary.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    tint = DarkGreenPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
