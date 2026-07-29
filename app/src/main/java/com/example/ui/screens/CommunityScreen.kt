package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CommunityGroup
import com.example.data.model.CommunityMessage
import com.example.data.model.UserRole
import com.example.data.model.VideoPost
import com.example.ui.components.AdMobBannerCard
import com.example.ui.components.TantsahaAppHeader
import com.example.ui.theme.DarkGreenPrimary
import com.example.ui.theme.GoldSecondary
import com.example.viewmodel.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val videoPosts by viewModel.videoPosts.collectAsState()
    val groups by viewModel.groups.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Videos/Feed, 1: Groups, 2: Chat
    var showRoleSelector by remember { mutableStateOf(false) }
    var showNewVideoDialog by remember { mutableStateOf(false) }
    var chatInputText by remember { mutableStateOf("") }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TantsahaAppHeader(
                title = "Réseau Tantsaha & Video",
                subtitle = "Tsy miankina amin'ny Facebook (Tantsaha, Grossiste, Retailer)",
                badgeText = "Role: ${currentUser.role.displayName.take(16)}...",
                onBadgeClick = { showRoleSelector = true }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Role Indicator Banner Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkGreenPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(currentUser.role.badgeColor))
                        ) {
                            Icon(
                                imageVector = when (currentUser.role) {
                                    UserRole.TANTSAHA -> Icons.Default.Agriculture
                                    UserRole.GROSSISTE -> Icons.Default.LocalShipping
                                    UserRole.MPIVAROTRA_MADINIKA -> Icons.Default.Storefront
                                },
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = currentUser.name,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                            Text(
                                text = currentUser.role.displayName,
                                color = GoldSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Button(
                        onClick = { showRoleSelector = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldSecondary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Soloy Compte", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF281800))
                    }
                }
            }

            // Tab Navigation: 🎬 Videos, 👥 Groups, 💬 Chat
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = DarkGreenPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("🎬 Video & Feed", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("👥 Vondrona (Groups)", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("💬 Chat Direct", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> VideoFeedContent(
                        videoPosts = videoPosts,
                        onLike = { viewModel.likeVideoPost(it) },
                        onNewVideo = { showNewVideoDialog = true }
                    )
                    1 -> GroupsContent(
                        groups = groups,
                        onJoinGroup = { groupName ->
                            toastMessage = "Tafiditra tao amin'ny vondrona $groupName ianao!"
                        }
                    )
                    2 -> DirectChatContent(
                        messages = chatMessages,
                        inputText = chatInputText,
                        onInputChange = { chatInputText = it },
                        onSend = {
                            viewModel.sendChatMessage(chatInputText)
                            chatInputText = ""
                        }
                    )
                }

                toastMessage?.let { msg ->
                    Snackbar(
                        action = { TextButton(onClick = { toastMessage = null }) { Text("OK") } },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    ) {
                        Text(msg)
                    }
                }
            }
        }
    }

    // Modal 1: Role Switcher Dialog
    if (showRoleSelector) {
        RoleSelectorModal(
            currentRole = currentUser.role,
            onDismiss = { showRoleSelector = false },
            onSelectRole = { newRole ->
                viewModel.setUserRole(newRole)
                showRoleSelector = false
                toastMessage = "Fiovan'ny profil: ${newRole.displayName}"
            }
        )
    }

    // Modal 2: Publish New Video Post Dialog
    if (showNewVideoDialog) {
        PublishVideoDialog(
            userRole = currentUser.role,
            onDismiss = { showNewVideoDialog = false },
            onPublish = { title, desc, price, cat ->
                viewModel.publishVideoPost(title, desc, price, cat)
                showNewVideoDialog = false
                toastMessage = "Nalefa soa aman-tsara ny Video-nao!"
            }
        )
    }
}

@Composable
fun VideoFeedContent(
    videoPosts: List<VideoPost>,
    onLike: (String) -> Unit,
    onNewVideo: () -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            AdMobBannerCard()
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📹 Alefaso ny Video-nao (Tsy miankina amin'ny FB)!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = "Asehoy ny akoho, kisoa na ny vokatrao amin'ny mpambongadiny sy ny mpividy.",
                            fontSize = 11.sp,
                            color = Color(0xFF3E2723)
                        )
                    }

                    Button(
                        onClick = onNewVideo,
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Post Video", fontSize = 11.sp)
                    }
                }
            }
        }

        items(videoPosts, key = { it.id }) { post ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Author Header & Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(post.authorRole.badgeColor))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = post.authorName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${post.location} • ${post.datePosted}",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Surface(
                            color = Color(post.authorRole.badgeColor).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = post.authorRole.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(post.authorRole.badgeColor),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = post.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = post.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Simulated Video Player Canvas Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B1B)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    color = DarkGreenPrimary,
                                    shape = CircleShape,
                                    modifier = Modifier.size(54.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Mamely Video",
                                            tint = Color.White,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "🎥 VIDEO PRESENTATION (${post.videoDurationText})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Vidy: ${formatAriary(post.priceAr)}",
                                    fontSize = 12.sp,
                                    color = GoldSecondary,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action buttons: Like, Comment, Phone call
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(onClick = { onLike(post.id) }) {
                                Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${post.likesCount}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            }

                            TextButton(onClick = { }) {
                                Icon(imageVector = Icons.Default.Comment, contentDescription = null, tint = DarkGreenPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${post.commentsCount}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            }

                            TextButton(onClick = { }) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${post.sharesCount}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${post.authorPhone.replace(" ", "")}")
                                }
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Antsoy", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupsContent(
    groups: List<CommunityGroup>,
    onJoinGroup: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "👥 Vondrona sy Groupe Resaka Tantsaha (Forums)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Midira ao amin'ny vondron'ny mpambongadiny sy ny mpiompy hitady tsena matanjaka.",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        items(groups) { group ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = group.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DarkGreenPrimary,
                            modifier = Modifier.weight(1f)
                        )

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${group.membersCount} Mpikambana",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = group.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "💬 " + group.recentPost,
                            fontSize = 11.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { onJoinGroup(group.name) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Midira amin'ny Groupe", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DirectChatContent(
    messages: List<CommunityMessage>,
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Chat list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val alignment = if (msg.isMe) Alignment.CenterEnd else Alignment.CenterStart
                val bubbleColor = if (msg.isMe) DarkGreenPrimary else MaterialTheme.colorScheme.surfaceVariant
                val textColor = if (msg.isMe) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = bubbleColor),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = msg.senderName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (msg.isMe) GoldSecondary else DarkGreenPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = msg.text,
                                fontSize = 13.sp,
                                color = textColor
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = msg.timeAgo,
                                fontSize = 9.sp,
                                color = textColor.copy(alpha = 0.7f),
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }

        // Input Box
        Surface(
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(12.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    placeholder = { Text("Manehoa hafatra ho an'ny mpivarotra...") },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onSend,
                    modifier = Modifier
                        .background(DarkGreenPrimary, CircleShape)
                        .size(44.dp)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Alefaso", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun RoleSelectorModal(
    currentRole: UserRole,
    onDismiss: () -> Unit,
    onSelectRole: (UserRole) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Badge, contentDescription = null, tint = DarkGreenPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Safidio ny Karazan'ny Profil-nao", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Ny karazan'ny profil no mamaritra ny toerana misy anao eo amin'ny tsena tantsaha:",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                UserRole.values().forEach { role ->
                    Card(
                        onClick = { onSelectRole(role) },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (role == currentRole) DarkGreenPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(role.badgeColor))
                            ) {
                                Icon(
                                    imageVector = when (role) {
                                        UserRole.TANTSAHA -> Icons.Default.Agriculture
                                        UserRole.GROSSISTE -> Icons.Default.LocalShipping
                                        UserRole.MPIVAROTRA_MADINIKA -> Icons.Default.Storefront
                                    },
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(role.displayName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    text = when (role) {
                                        UserRole.TANTSAHA -> "Mpiompy sy Mamboly vokatra"
                                        UserRole.GROSSISTE -> "Mpanangona & Mpambongadiny am-bata"
                                        UserRole.MPIVAROTRA_MADINIKA -> "Mpivarotra amin'ny bazary & déteil"
                                    },
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Akanjo")
            }
        }
    )
}

@Composable
fun PublishVideoDialog(
    userRole: UserRole,
    onDismiss: () -> Unit,
    onPublish: (title: String, desc: String, priceAr: Long, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Akoho & Vorona") }

    val priceVal = priceText.toLongOrNull() ?: 0L

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Videocam, contentDescription = null, tint = DarkGreenPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("📹 Handefa Video Vokatra", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Profil: ${userRole.displayName}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreenPrimary
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Lohatenin'ny Video (ex: Jereo ny kisoa miteraka)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Vidy Tohanana (Ariary)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Fanazavana fohy ny vokatra...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotEmpty() && priceVal > 0) {
                        onPublish(title, desc, priceVal, category)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary)
            ) {
                Text("Mandefa Video")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Akanjo")
            }
        }
    )
}
