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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CommunityGroup
import com.example.data.model.CommunityMessage
import com.example.data.model.UserProfile
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

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Feed (Sary/Video), 1: Profile, 2: Groups, 3: Chat
    var showRoleSelector by remember { mutableStateOf(false) }
    var showNewPostDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var selectedVideoForPlayer by remember { mutableStateOf<VideoPost?>(null) }
    var selectedPhotoForViewer by remember { mutableStateOf<VideoPost?>(null) }

    var chatInputText by remember { mutableStateOf("") }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TantsahaAppHeader(
                title = "Réseau Tantsaha & Profile",
                subtitle = "Tsy miankina amin'ny Facebook (Tantsaha, Grossiste, Retailer)",
                badgeText = "Compte: ${currentUser.role.displayName.take(14)}...",
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
            // Role & Quick Switcher Banner Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkGreenPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(currentUser.avatarColorHex))
                        ) {
                            Text(
                                text = currentUser.avatarInitials,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentUser.name,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                if (currentUser.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = GoldSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = currentUser.role.displayName,
                                color = GoldSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = { showEditProfileDialog = true },
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .size(34.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile", tint = Color.White, modifier = Modifier.size(18.dp))
                        }

                        Button(
                            onClick = { showRoleSelector = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldSecondary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Rôle", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF281800))
                        }
                    }
                }
            }

            // Tab Navigation: 📰 Sary & Video, 👤 Profil-ko, 👥 Vondrona, 💬 Chat
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = DarkGreenPrimary,
                edgePadding = 8.dp
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("📰 Feed Sary & Video", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("👤 Profil FB-ko", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("👥 Vondrona (Groups)", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("💬 Chat Direct", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> FeedMediaContent(
                        posts = videoPosts,
                        onLike = { viewModel.likeVideoPost(it) },
                        onNewPost = { showNewPostDialog = true },
                        onOpenVideo = { selectedVideoForPlayer = it },
                        onOpenPhoto = { selectedPhotoForViewer = it }
                    )
                    1 -> FacebookProfileContent(
                        profile = currentUser,
                        myPosts = videoPosts.filter { it.authorPhone == currentUser.phone || it.authorName.contains(currentUser.name) },
                        onEditProfile = { showEditProfileDialog = true },
                        onNewPost = { showNewPostDialog = true },
                        onOpenVideo = { selectedVideoForPlayer = it },
                        onOpenPhoto = { selectedPhotoForViewer = it }
                    )
                    2 -> GroupsContent(
                        groups = groups,
                        onJoinGroup = { groupName ->
                            toastMessage = "Tafiditra tao amin'ny vondrona $groupName ianao!"
                        }
                    )
                    3 -> DirectChatContent(
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

    // Modal 2: Publish New Photo / Video Post Dialog
    if (showNewPostDialog) {
        PublishMediaDialog(
            userRole = currentUser.role,
            onDismiss = { showNewPostDialog = false },
            onPublish = { title, desc, price, cat, mediaType, photoTag ->
                viewModel.publishMediaPost(title, desc, price, cat, mediaType, photoTag)
                showNewPostDialog = false
                toastMessage = if (mediaType == "PHOTO") "Nalefa soa aman-tsara ny Sary-nao!" else "Nalefa soa aman-tsara ny Video-nao!"
            }
        )
    }

    // Modal 3: Edit Facebook-style User Profile Dialog
    if (showEditProfileDialog) {
        EditProfileModal(
            profile = currentUser,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, phone, loc, bio, fbPage, wa, avatarColor, coverColor ->
                viewModel.updateUserProfile(name, phone, loc, bio, fbPage, wa, avatarColor, coverColor)
                showEditProfileDialog = false
                toastMessage = "Voatahiry ny fampahalalana ny Profil-nao!"
            }
        )
    }

    // Modal 4: Interactive Video Player Dialog
    selectedVideoForPlayer?.let { post ->
        VideoPlayerModal(
            post = post,
            onDismiss = { selectedVideoForPlayer = null },
            onLike = { viewModel.likeVideoPost(post.id) }
        )
    }

    // Modal 5: Interactive Photo Viewer Dialog
    selectedPhotoForViewer?.let { post ->
        PhotoViewerModal(
            post = post,
            onDismiss = { selectedPhotoForViewer = null },
            onLike = { viewModel.likeVideoPost(post.id) }
        )
    }
}

@Composable
fun FeedMediaContent(
    posts: List<VideoPost>,
    onLike: (String) -> Unit,
    onNewPost: () -> Unit,
    onOpenVideo: (VideoPost) -> Unit,
    onOpenPhoto: (VideoPost) -> Unit
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
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📸 Mandefa Sary na 📹 Video Vokatra!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = "Asehoy ny akoho, kisoa, vary na legiomanao mba hahazoana mpanjifa.",
                            fontSize = 11.sp,
                            color = Color(0xFF3E2723)
                        )
                    }

                    Button(
                        onClick = onNewPost,
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Post Sary / Video", fontSize = 11.sp)
                    }
                }
            }
        }

        items(posts, key = { it.id }) { post ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
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

                    // Photo vs Video Media Container (Clickable to open full player / viewer)
                    if (post.mediaType == "PHOTO") {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF263238)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(170.dp)
                                .clickable { onOpenPhoto(post) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF37474F), Color(0xFF1A237E))
                                        )
                                    )
                                    .padding(14.dp)
                            ) {
                                Surface(
                                    color = GoldSecondary,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.align(Alignment.TopStart)
                                ) {
                                    Text(
                                        text = post.photoTag,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF281800),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.align(Alignment.Center)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = "Sary Vokatra",
                                        tint = Color.White.copy(alpha = 0.9f),
                                        modifier = Modifier.size(44.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "📸 SARY HD (Kitiho hijerena sary feno)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Surface(
                                    color = Color.Black.copy(alpha = 0.75f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                ) {
                                    Text(
                                        text = "Vidy: ${formatAriaryComm(post.priceAr)}",
                                        fontSize = 12.sp,
                                        color = GoldSecondary,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        // Video Player Interactive Card Display
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B1B)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clickable { onOpenVideo(post) }
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
                                        text = "🎥 KITIHO MPO MAMAFA VIDEO (${post.videoDurationText})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Vidy: ${formatAriaryComm(post.priceAr)}",
                                        fontSize = 12.sp,
                                        color = GoldSecondary,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action buttons: Like, Comment, Call Phone
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

                            TextButton(onClick = {
                                if (post.mediaType == "VIDEO") onOpenVideo(post) else onOpenPhoto(post)
                            }) {
                                Icon(imageVector = Icons.Default.Comment, contentDescription = null, tint = DarkGreenPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${post.commentsCount}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
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
fun FacebookProfileContent(
    profile: UserProfile,
    myPosts: List<VideoPost>,
    onEditProfile: () -> Unit,
    onNewPost: () -> Unit,
    onOpenVideo: (VideoPost) -> Unit,
    onOpenPhoto: (VideoPost) -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(profile.coverColorHex), Color(profile.avatarColorHex))
                                )
                            )
                    ) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sary Cover", color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-40).dp)
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(84.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, Color.White, CircleShape)
                                    .background(Color(profile.avatarColorHex))
                            ) {
                                Text(
                                    text = profile.avatarInitials,
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Surface(
                                color = GoldSecondary,
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(26.dp)
                                    .clickable { onEditProfile() }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Hanova Sary",
                                    tint = Color(0xFF281800),
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = profile.name,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            )
                            if (profile.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Badge",
                                    tint = DarkGreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Surface(
                            color = Color(profile.role.badgeColor).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "👑 ${profile.role.displayName}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(profile.role.badgeColor),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "📍 ${profile.location} • ⭐ ${profile.rating} Rating • 👥 ${profile.followersCount} Followers",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .offset(y = (-30).dp)
                    ) {
                        Button(
                            onClick = onEditProfile,
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mamaritra Profile", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://${profile.facebookPage}"))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = Color(0xFF1877F2), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("FB Page", fontSize = 12.sp, color = Color(0xFF1877F2), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Bio & Contact Details Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("ℹ️ Fanazavana momba ny Profil (Bio)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(profile.bio, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = DarkGreenPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Laharana Telefona: ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(profile.phone, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("WhatsApp: ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(profile.whatsappNumber, fontSize = 12.sp)
                    }
                }
            }
        }

        // Post Status Launcher Bar
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(12.dp)
                        .clickable { onNewPost() }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(profile.avatarColorHex))
                    ) {
                        Text(profile.avatarInitials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Ahoana ny vaovao ampiasainao ${profile.name}? Mandefa Sary na Video...",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = onNewPost) {
                        Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = "Post Sary", tint = DarkGreenPrimary)
                    }
                }
            }
        }

        item {
            Text(
                text = "📸 Sary sy Post napetrakao (${myPosts.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }

        if (myPosts.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text("Tsy mbola nandefa Sary na Video ianao.", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onNewPost, colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary)) {
                            Text("Mandefa Sary Voalohany")
                        }
                    }
                }
            }
        } else {
            items(myPosts, key = { it.id }) { post ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                        .clickable {
                            if (post.mediaType == "VIDEO") onOpenVideo(post) else onOpenPhoto(post)
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(
                            imageVector = if (post.mediaType == "PHOTO") Icons.Default.Image else Icons.Default.Videocam,
                            contentDescription = null,
                            tint = DarkGreenPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(post.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${post.category} • ${formatAriaryComm(post.priceAr)}", fontSize = 11.sp, color = DarkGreenPrimary, fontWeight = FontWeight.SemiBold)
                            Text("${post.likesCount} Likes • ${post.commentsCount} Comments", fontSize = 10.sp, color = Color.Gray)
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
        contentPadding = PaddingValues(14.dp),
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
    val context = LocalContext.current

    val contactSellers = remember {
        listOf(
            Triple("Rakoto (Mpiompy Kisoa)", "+261 34 12 345 67", 0xFF2E7D32),
            Triple("Rabary (Akoho Gasy Mahitsy)", "+261 32 98 765 43", 0xFFE65100),
            Triple("Koloina (Mpivarotra Vary Marovoay)", "+261 33 45 678 90", 0xFF1565C0),
            Triple("Rasoa (Mamboly Legioma Antsirabe)", "+261 34 56 789 01", 0xFF6A1B9A)
        )
    }

    var selectedSellerIndex by remember { mutableIntStateOf(0) }
    val currentSeller = contactSellers[selectedSellerIndex]

    val quickReplyChips = listOf(
        "👋 Manao ahoana, mbola misy ve ny vokatra?",
        "💰 Firy ny vidiny farany ambany?",
        "📍 Aiza ny toerana handraisana azy?",
        "🚚 Manao livraison ve ianao?"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Seller Contacts Switcher Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = "👥 Mpivarotra & Tantsaha Azonao Resahina Direct:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(contactSellers.indices.toList()) { index ->
                        val seller = contactSellers[index]
                        val isSelected = index == selectedSellerIndex

                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedSellerIndex = index },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color(seller.third))
                                )
                            },
                            label = {
                                Text(
                                    text = seller.first.take(18),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DarkGreenPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Active Seller Banner Header
        Card(
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(currentSeller.third))
                    ) {
                        Text(
                            text = currentSeller.first.take(1),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(currentSeller.first, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(currentSeller.second, fontSize = 10.sp, color = Color.Gray)
                    }
                }

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${currentSeller.second.replace(" ", "")}")
                        }
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Antsoy Direct", fontSize = 10.sp)
                }
            }
        }

        // Chat Messages Thread
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val alignment = if (msg.isMe) Alignment.CenterEnd else Alignment.CenterStart
                val bubbleColor = if (msg.isMe) DarkGreenPrimary else MaterialTheme.colorScheme.surface
                val textColor = if (msg.isMe) Color.White else MaterialTheme.colorScheme.onSurface

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = bubbleColor),
                        modifier = Modifier.widthIn(max = 290.dp)
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

        // Quick Reply Suggestions Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(quickReplyChips) { chipText ->
                SuggestionChip(
                    onClick = {
                        onInputChange(chipText)
                    },
                    label = { Text(chipText, fontSize = 10.sp) }
                )
            }
        }

        // Bottom Input Row
        Surface(
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    placeholder = { Text("Soraty ny hafatra ho an'i ${currentSeller.first.take(10)}...", fontSize = 12.sp) },
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
fun VideoPlayerModal(
    post: VideoPost,
    onDismiss: () -> Unit,
    onLike: () -> Unit
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var currentProgressSeconds by remember { mutableIntStateOf(14) }
    var isMuted by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    val commentsList = remember { mutableStateListOf("Salama! Aiza ny toerana handraisana ireto kisoa/akoho ireto?", "Mbola misy livraison ve eto Antananarivo?") }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            kotlinx.coroutines.delay(1000L)
            currentProgressSeconds = (currentProgressSeconds + 1) % 120
        }
    }

    val formatTime = { secs: Int ->
        val mins = secs / 60
        val remaining = secs % 60
        String.format("%02d:%02d", mins, remaining)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(vertical = 16.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Videocam, contentDescription = null, tint = DarkGreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(post.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Akatona")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
            ) {
                // Interactive Video Viewport Frame
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Background gradient visual effect
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF1B5E20), Color(0xFF000000))
                                    )
                                )
                        )

                        // Top Badges Overlay
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .align(Alignment.TopCenter)
                        ) {
                            Surface(
                                color = Color.Red,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "🔴 LIVE VIDEO PLAYER (HD)",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Surface(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = formatTime(currentProgressSeconds) + " / 02:00",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Play/Pause Main Interactive Center Button
                        IconButton(
                            onClick = { isPlaying = !isPlaying },
                            modifier = Modifier
                                .size(60.dp)
                                .align(Alignment.Center)
                                .background(DarkGreenPrimary.copy(alpha = 0.85f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(38.dp)
                            )
                        }

                        // Bottom Player Controls Bar
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .background(Color.Black.copy(alpha = 0.7f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Slider(
                                value = currentProgressSeconds.toFloat(),
                                onValueChange = { currentProgressSeconds = it.toInt() },
                                valueRange = 0f..120f,
                                colors = SliderDefaults.colors(
                                    thumbColor = GoldSecondary,
                                    activeTrackColor = DarkGreenPrimary
                                ),
                                modifier = Modifier.height(20.dp)
                            )

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Vidy: ${formatAriaryComm(post.priceAr)}",
                                    color = GoldSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )

                                IconButton(
                                    onClick = { isMuted = !isMuted },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                        contentDescription = "Mute",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Post details & seller contact
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(post.authorName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("📍 ${post.location} • ${post.authorPhone}", fontSize = 11.sp, color = Color.Gray)
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

                Spacer(modifier = Modifier.height(8.dp))

                // Comment Section inside Modal
                Text("💬 Hevitry ny mpijery (${commentsList.size}):", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                LazyColumn(
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(commentsList) { comment ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(comment, fontSize = 10.sp, modifier = Modifier.padding(6.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Soraty ny hevitrao...", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                commentsList.add(commentText)
                                commentText = ""
                            }
                        },
                        modifier = Modifier.background(DarkGreenPrimary, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
fun PhotoViewerModal(
    post: VideoPost,
    onDismiss: () -> Unit,
    onLike: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(vertical = 16.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = DarkGreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(post.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Akatona")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
            ) {
                // HD Photo View Frame
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF263238)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF37474F), Color(0xFF1A237E))
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Surface(
                            color = GoldSecondary,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            Text(
                                text = post.photoTag,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF281800),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Sary HD",
                                tint = Color.White,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "🖼️ SARY HD TSARA KALITATY",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Surface(
                            color = Color.Black.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Text(
                                text = "Vidy: ${formatAriaryComm(post.priceAr)}",
                                fontSize = 12.sp,
                                color = GoldSecondary,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(post.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                Text("📍 Toerana: ${post.location} • Nampidirina: ${post.datePosted}", fontSize = 11.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${post.authorPhone.replace(" ", "")}")
                            }
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Antsoy Ny Mpivarotra", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onLike,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tiako (${post.likesCount})", fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {}
    )
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
fun PublishMediaDialog(
    userRole: UserRole,
    onDismiss: () -> Unit,
    onPublish: (title: String, desc: String, priceAr: Long, category: String, mediaType: String, photoTag: String) -> Unit
) {
    var mediaType by remember { mutableStateOf("PHOTO") } // "PHOTO" or "VIDEO"
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Akoho & Vorona") }
    var photoTag by remember { mutableStateOf("🐖 KISOA VOKATRA (SARY)") }

    val priceVal = priceText.toLongOrNull() ?: 0L

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (mediaType == "PHOTO") Icons.Default.AddAPhoto else Icons.Default.Videocam,
                    contentDescription = null,
                    tint = DarkGreenPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("📸 Mandefa Sary na Video", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Media Type Switcher: Photo vs Video
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = mediaType == "PHOTO",
                        onClick = { mediaType = "PHOTO" },
                        label = { Text("🖼️ Mandefa Sary (Photo)", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = mediaType == "VIDEO",
                        onClick = { mediaType = "VIDEO" },
                        label = { Text("📹 Mandefa Video", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (mediaType == "PHOTO") {
                    Text("Safidio ny Tag amin'ny Sary:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    val photoTags = listOf(
                        "🐖 KISOA VOKATRA (SARY)",
                        "🐔 AKOHO GASY (SARY)",
                        "🌾 VARY & KATSAKA (SARY)",
                        "🥕 LEGIOMA TSENA (SARY)"
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(photoTags) { tag ->
                            FilterChip(
                                selected = photoTag == tag,
                                onClick = { photoTag = tag },
                                label = { Text(tag, fontSize = 9.sp) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Lohateny (ex: Kisoa miteraka 12 salama tsara)") },
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
                    label = { Text("Fanazavana fohy momba ny Sary/Video...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotEmpty() && priceVal > 0) {
                        onPublish(title, desc, priceVal, category, mediaType, photoTag)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary)
            ) {
                Text(if (mediaType == "PHOTO") "Mandefa Sary" else "Mandefa Video")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Akanjo")
            }
        }
    )
}

@Composable
fun EditProfileModal(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, loc: String, bio: String, fbPage: String, wa: String, avatarColor: Long, coverColor: Long) -> Unit
) {
    var name by remember { mutableStateOf(profile.name) }
    var phone by remember { mutableStateOf(profile.phone) }
    var location by remember { mutableStateOf(profile.location) }
    var bio by remember { mutableStateOf(profile.bio) }
    var fbPage by remember { mutableStateOf(profile.facebookPage) }
    var waNumber by remember { mutableStateOf(profile.whatsappNumber) }

    var selectedColor by remember { mutableLongStateOf(profile.avatarColorHex) }

    val colorOptions = listOf(
        0xFF2E7D32 to "Maitso Tantsaha",
        0xFFE65100 to "Manjarano Grossiste",
        0xFF1565C0 to "Manga Mpivarotra",
        0xFF6A1B9A to "Violets VIP"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = DarkGreenPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("✏️ Hanova Fampahalalana Profil", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Anarana sy Fanampiny") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Laharana Telefona (Contact)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Toerana (ex: Antananarivo - Ivato)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio / Fanazavana momba ny asa") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                OutlinedTextField(
                    value = fbPage,
                    onValueChange = { fbPage = it },
                    label = { Text("Lien Pejy Facebook (ex: facebook.com/myfarm)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = waNumber,
                    onValueChange = { waNumber = it },
                    label = { Text("Laharana WhatsApp") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("Loko sy Sary Avatar:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    colorOptions.forEach { (hex, colorName) ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(hex))
                                .border(
                                    width = if (selectedColor == hex) 3.dp else 0.dp,
                                    color = if (selectedColor == hex) GoldSecondary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = hex }
                        ) {
                            if (selectedColor == hex) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(name, phone, location, bio, fbPage, waNumber, selectedColor, selectedColor)
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary)
            ) {
                Text("Tahirizo ny Profil")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Akanjo")
            }
        }
    )
}

private fun formatAriaryComm(amount: Long): String {
    return String.format("%,d Ar", amount).replace(',', ' ')
}
