package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EbookItem
import com.example.ui.components.TantsahaAppHeader
import com.example.ui.theme.DarkGreenPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.GoldSecondaryBright
import com.example.viewmodel.EbookViewModel

@Composable
fun EbookScreen(
    viewModel: EbookViewModel
) {
    val ebooks = viewModel.ebooksList
    val readingEbook by viewModel.readingEbook.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()

    if (readingEbook != null) {
        // Fullscreen Offline Reader View
        EbookReaderScreen(
            ebook = readingEbook!!,
            onClose = { viewModel.openEbook(null) }
        )
    } else {
        Scaffold(
            topBar = {
                TantsahaAppHeader(
                    title = "Boky Premium & Ebooks",
                    subtitle = "Vakio Offline noho ny Fahaizana",
                    badgeText = "${bookmarks.size} Bookmarks"
                )
            }
        ) { padding ->
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                item {
                    Text(
                        text = "📚 Bokin'ny Tantsaha Matihanina (Offline Guide)",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreenPrimary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                items(ebooks) { ebook ->
                    val isBookmarked = bookmarks.any { it.bookId == ebook.id }

                    Card(
                        onClick = { viewModel.openEbook(ebook) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
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
                                        text = ebook.category,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkGreenPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                IconButton(onClick = {
                                    viewModel.toggleBookmark(
                                        bookId = ebook.id,
                                        title = ebook.title,
                                        category = ebook.category,
                                        isBookmarked = isBookmarked
                                    )
                                }) {
                                    Icon(
                                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Bookmark",
                                        tint = if (isBookmarked) GoldSecondaryBright else Color.Gray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = ebook.title,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "Mpanoratra: ${ebook.author} | ${ebook.pagesCount} Pejy",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = ebook.summary,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { viewModel.openEbook(ebook) },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Vakio Ny Boky (Offline Reader)")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EbookReaderScreen(
    ebook: EbookItem,
    onClose: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ebook.title, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Close Reader")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkGreenPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Surface(
                color = GoldSecondary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("📖 FITANISANA NY TOKO (TABLE OF CONTENTS):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkGreenPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    ebook.tableOfContents.forEach { ch ->
                        Text("• $ch", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Text(
                text = ebook.fullContent,
                fontSize = 15.sp,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Efa voavaky - Hiverina")
            }
        }
    }
}
