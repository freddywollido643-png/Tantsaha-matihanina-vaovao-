package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import com.example.data.model.MarketplaceItem
import com.example.ui.components.AdMobBannerCard
import com.example.ui.components.AdMobInterstitialModal
import com.example.ui.components.AdMobSettingsDialog
import com.example.ui.components.TantsahaAppHeader
import com.example.ui.theme.DarkGreenPrimary
import com.example.ui.theme.GoldSecondary
import com.example.viewmodel.MarketplaceViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    viewModel: MarketplaceViewModel
) {
    val items by viewModel.items.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val adMobConfig by viewModel.adMobConfig.collectAsState()
    val showInterstitial by viewModel.showInterstitial.collectAsState()
    val totalSalesAr by viewModel.totalSalesAr.collectAsState()
    val totalCommissionEarnedAr by viewModel.totalCommissionEarnedAr.collectAsState()
    val completedOrdersCount by viewModel.completedOrdersCount.collectAsState()

    var showPostDialog by remember { mutableStateOf(false) }
    var showCommissionDashboard by remember { mutableStateOf(false) }
    var showVipSubscriptionDialog by remember { mutableStateOf(false) }
    var showSponsoredAdDialog by remember { mutableStateOf(false) }
    var showAdSettings by remember { mutableStateOf(false) }
    var selectedItemForOrder by remember { mutableStateOf<MarketplaceItem?>(null) }
    var orderSuccessMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        "Rehetra",
        "Akoho & Vorona",
        "Kisoa & Omby",
        "Vary & Legioma",
        "Zezika & Fitaovana",
        "Fanafody"
    )

    val filteredItems = items.filter { item ->
        val matchesCategory = selectedCategory == "Rehetra" || item.category == selectedCategory
        val matchesSearch = searchQuery.isEmpty() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.location.contains(searchQuery, ignoreCase = true) ||
                item.sellerName.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Scaffold(
        topBar = {
            TantsahaAppHeader(
                title = "Tsena Online Tantsaha",
                subtitle = "E-Commerce & Commission (5%)",
                badgeText = "Commission: ${formatAriary(totalCommissionEarnedAr)}",
                onBadgeClick = { showCommissionDashboard = true }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // AdMob Banner Card
                item {
                    AdMobBannerCard(
                        adMobConfig = adMobConfig,
                        onOpenSettings = { showAdSettings = true }
                    )
                }

                // Header Action Banner: Post Item & Commission Stats Buttons
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkGreenPrimary),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Varoty am-mora ny vokatrao!",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Akoho, Kisoa, Vary, Zezika... Mampiasa Mobile Money (MVola, Orange, Airtel)",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }

                                Surface(
                                    color = GoldSecondary,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Commission 5%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF281800),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = { showPostDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldSecondary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1.1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color(0xFF281800),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "Hamidy",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF281800),
                                        fontSize = 12.sp
                                    )
                                }

                                OutlinedButton(
                                    onClick = { showCommissionDashboard = true },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Analytics,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "Commission",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }

                                OutlinedButton(
                                    onClick = { showVipSubscriptionDialog = true },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldSecondary),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldSecondary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = GoldSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "👑 VIP",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Search Field & Category Selector
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Hikaroka vokatra, tanàna na mpivarotra...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = DarkGreenPrimary
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Fafao")
                                    }
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(categories) { cat ->
                                val isSelected = cat == selectedCategory
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setCategory(cat) },
                                    label = { Text(cat, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = DarkGreenPrimary,
                                        selectedLabelColor = Color.White,
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }
                }

                // Items Count Header
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Vokatra Amidy (${filteredItems.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Text(
                            text = "Standard Commission 5%",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Marketplace Product Items
                items(filteredItems, key = { it.id }) { item ->
                    MarketplaceItemCard(
                        item = item,
                        onOrderClick = { selectedItemForOrder = item }
                    )
                }
            }

            // Order Success Notification Toast
            orderSuccessMessage?.let { msg ->
                Snackbar(
                    action = {
                        TextButton(onClick = { orderSuccessMessage = null }) {
                            Text("OK", color = GoldSecondary)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(msg, fontSize = 12.sp)
                }
            }
        }
    }

    // Dialog 1: Post Item Dialog
    if (showPostDialog) {
        PostItemDialog(
            onDismiss = { showPostDialog = false },
            onPost = { title, cat, price, name, phone, loc, qty, desc ->
                viewModel.postNewItem(title, cat, price, name, phone, loc, qty, desc)
                showPostDialog = false
                orderSuccessMessage = "Tafiditra soa aman-tsara ny vokatrao! Efa hitan'ny mpividy am-tsena."
            }
        )
    }

    // Dialog 2: Order & Commission Payment Dialog
    selectedItemForOrder?.let { item ->
        OrderPaymentDialog(
            item = item,
            onDismiss = { selectedItemForOrder = null },
            onConfirmOrder = {
                viewModel.placeOrder(item)
                selectedItemForOrder = null
                orderSuccessMessage = "Tafiditra ny kaomandy! Nalefa tamin'ny mpivarotra ${item.sellerName} (${item.sellerPhone}) ny hafatra."
            }
        )
    }

    // Dialog 3: Commission Dashboard Dialog
    if (showCommissionDashboard) {
        CommissionDashboardModal(
            totalSalesAr = totalSalesAr,
            totalCommissionAr = totalCommissionEarnedAr,
            ordersCount = completedOrdersCount,
            onDismiss = { showCommissionDashboard = false }
        )
    }

    // Dialog 4: AdMob Settings Dialog
    if (showAdSettings) {
        AdMobSettingsDialog(
            adMobConfig = adMobConfig,
            onDismiss = { showAdSettings = false },
            onSaveConfig = { viewModel.updateAdMobConfig(it) }
        )
    }

    // Dialog 5: VIP Subscription Modal
    if (showVipSubscriptionDialog) {
        VipSubscriptionModal(
            onDismiss = { showVipSubscriptionDialog = false },
            onSubscribeSuccess = { planName ->
                showVipSubscriptionDialog = false
                orderSuccessMessage = "Misaotra betsaka! Lasa Mpikambana VIP ($planName) ianao izao."
            }
        )
    }
}

@Composable
fun MarketplaceItemCard(
    item: MarketplaceItem,
    onOrderClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Category & Location Chips
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = DarkGreenPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = item.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreenPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = item.location,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                if (item.isVerifiedSeller) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Mpivarotra Salama",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title & Description
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = item.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Price & Commission Breakdown Surface
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Vidin'ny Vokatra",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = formatAriary(item.priceAr),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DarkGreenPrimary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Commission App (5%)",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "${formatAriary(item.commissionAmountAr)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD84315)
                        )
                        Text(
                            text = "Sora-panahy mpivarotra: ${formatAriary(item.sellerPayoutAr)}",
                            fontSize = 10.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Seller Contact Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.sellerName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Misy: ${item.quantityAvailable}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Hividy (Order) & Antsoy (Call)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onOrderClick,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.5f)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hividy (Kaomandy)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${item.sellerPhone.replace(" ", "")}")
                        }
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = DarkGreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Antsoy", fontSize = 12.sp, color = DarkGreenPrimary)
                }
            }
        }
    }
}

@Composable
fun PostItemDialog(
    onDismiss: () -> Unit,
    onPost: (
        title: String,
        category: String,
        priceAr: Long,
        sellerName: String,
        sellerPhone: String,
        location: String,
        quantity: String,
        description: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Akoho & Vorona") }
    var priceText by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") }
    var sellerName by remember { mutableStateOf("") }
    var sellerPhone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Antananarivo") }
    var description by remember { mutableStateOf("") }

    val categories = listOf("Akoho & Vorona", "Kisoa & Omby", "Vary & Legioma", "Zezika & Fitaovana", "Fanafody")
    val priceVal = priceText.toLongOrNull() ?: 0L
    val commissionVal = (priceVal * 0.05).toLong()
    val sellerPayoutVal = priceVal - commissionVal

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = DarkGreenPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hamidy Vokatra (Post Item)", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Anaran'ny Vokatra (ex: Akoho gasy 6 volana)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Text("Sokajy (Category):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DarkGreenPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Vidy (Ariary)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                if (priceVal > 0) {
                    item {
                        Surface(
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "💡 Kajy Commission 5%:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color(0xFFE65100)
                                )
                                Text(
                                    text = "Platform Fee: ${formatAriary(commissionVal)} | Azo'ny Mpivarotra: ${formatAriary(sellerPayoutVal)}",
                                    fontSize = 11.sp,
                                    color = Color(0xFFBF360C)
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Isany / Fatra (ex: 20 akoho, 5 gony)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = sellerName,
                        onValueChange = { sellerName = it },
                        label = { Text("Anaranao (Mpivarotra)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = sellerPhone,
                        onValueChange = { sellerPhone = it },
                        label = { Text("Laharana Téléphone (MVola, Orange, Airtel)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Toerana (Tanàna/Faritra)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Mombamomba ny vokatra (Description)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotEmpty() && priceVal > 0 && sellerName.isNotEmpty() && sellerPhone.isNotEmpty()) {
                        onPost(
                            title, category, priceVal, sellerName, sellerPhone,
                            if (location.isEmpty()) "Antananarivo" else location,
                            if (quantityText.isEmpty()) "1" else quantityText,
                            description
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary)
            ) {
                Text("Amboary & Amidio")
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
fun OrderPaymentDialog(
    item: MarketplaceItem,
    onDismiss: () -> Unit,
    onConfirmOrder: () -> Unit
) {
    var selectedPayment by remember { mutableStateOf("MVola") }
    val commissionAr = item.commissionAmountAr
    val mobileMoneyFee = 500L
    val totalToPay = item.priceAr + mobileMoneyFee

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = DarkGreenPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Kaomandy & Fandoavam-bola", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Mpivarotra: ${item.sellerName} (${item.location})",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Breakdown Box
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Vidin'ny Vokatra:", fontSize = 12.sp)
                            Text(formatAriary(item.priceAr), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Commission Platform (5% included):", fontSize = 12.sp, color = Color.Gray)
                            Text(formatAriary(commissionAr), fontSize = 12.sp, color = DarkGreenPrimary)
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Frais Mobile Money / Transfert:", fontSize = 12.sp, color = Color.Gray)
                            Text(formatAriary(mobileMoneyFee), fontSize = 12.sp, color = Color.Gray)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("TOTAL ALOHA (Total to pay):", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(formatAriary(totalToPay), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = DarkGreenPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Safidio ny fomba fandoavam-bola:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(6.dp))

                val paymentMethods = listOf("MVola (Telma)", "Orange Money", "Airtel Money", "Payer Direct en Espèces")
                paymentMethods.forEach { method ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPayment = method }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedPayment == method,
                            onClick = { selectedPayment = method }
                        )
                        Text(text = method, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📲 Mandefa hafatra amin'ny ${item.sellerPhone} rehefa vita ny fandoavana amin'ny $selectedPayment.",
                        fontSize = 11.sp,
                        color = Color(0xFF1B5E20),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmOrder,
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary)
            ) {
                Text("Alefaso Kaomandy")
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
fun CommissionDashboardModal(
    totalSalesAr: Long,
    totalCommissionAr: Long,
    ordersCount: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = null,
                    tint = DarkGreenPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tableau de Bord Commission", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Torohay ny vola azo amin'ny Commission (5%) avy amin'ny varotra sy ny kaomandy ao amin'ny Tsena Online.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Stats cards
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkGreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("TOTAL COMMISSION AZO (Earnings):", fontSize = 11.sp, color = GoldSecondary)
                        Text(
                            text = formatAriary(totalCommissionAr),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Taham-boninahitra standard: 5.0% ny varotra rehetra",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Total Volume Varotra:", fontSize = 10.sp, color = Color.Gray)
                            Text(formatAriary(totalSalesAr), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Kaomandy Vita:", fontSize = 10.sp, color = Color.Gray)
                            Text("$ordersCount kaomandy", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Retrait Commission via Mobile Money:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Surface(
                    color = Color(0xFFFFF8E1),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Ny vola commission dia alefa amin'ny MVola / Orange Money / Airtel Money an'ny Tompon'ny APK isam-bolana.",
                            fontSize = 11.sp,
                            color = Color(0xFF8D6E63)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary)
            ) {
                Text("Ekena (OK)")
            }
        }
    )
}

fun formatAriary(amount: Long): String {
    val formatter = NumberFormat.getNumberInstance(Locale.FRANCE)
    return "${formatter.format(amount)} Ar"
}

@Composable
fun VipSubscriptionModal(
    onDismiss: () -> Unit,
    onSubscribeSuccess: (String) -> Unit
) {
    var selectedPlan by remember { mutableStateOf("1 Volana (5 000 Ar)") }
    var selectedPayment by remember { mutableStateOf("MVola") }
    var phoneInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = GoldSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("👑 Abonnement VIP Tantsaha", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Lasa Mpikambana VIP hampiakatra ny tombony amin'ny fiompiana sy fambolena:",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))

                // VIP Perks List
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("✨ TOMBONY AZO (VIP Perks):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFE65100))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• 0% Commission amin'ny varotra ao amin'ny Tsena Online", fontSize = 11.sp, color = Color(0xFF3E2723))
                        Text("• Badge VIP Mpivarotra Vedette (Ahitana anao voalohany)", fontSize = 11.sp, color = Color(0xFF3E2723))
                        Text("• Miditra maimaimpoana amin'ny Boky PDF & Ebooks rehetra", fontSize = 11.sp, color = Color(0xFF3E2723))
                        Text("• AI Assistant miteny Malagasy tsy misy fetra 24/7", fontSize = 11.sp, color = Color(0xFF3E2723))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Safidio ny Faharetany (Plan):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                val plans = listOf(
                    "1 Volana (5 000 Ar)" to "5 000 Ar/m",
                    "3 Volana (12 000 Ar)" to "4 000 Ar/m (-20%)",
                    "1 Taona (40 000 Ar)" to "3 333 Ar/m (-33%)"
                )

                plans.forEach { (planTitle, discount) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPlan = planTitle }
                            .padding(vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedPlan == planTitle,
                                onClick = { selectedPlan = planTitle }
                            )
                            Text(text = planTitle, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Text(text = discount, fontSize = 10.sp, color = DarkGreenPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Fandoavam-bola via Mobile Money:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                val paymentMethods = listOf("MVola (Telma)", "Orange Money", "Airtel Money")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    paymentMethods.forEach { method ->
                        FilterChip(
                            selected = selectedPayment == method,
                            onClick = { selectedPayment = method },
                            label = { Text(method, fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text("Laharana Mobile Money (ex: 034 xx xxx xx)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (phoneInput.length >= 8) {
                        onSubscribeSuccess(selectedPlan)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreenPrimary)
            ) {
                Text("Mandoa Vola & Lasa VIP")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Akanjo")
            }
        }
    )
}
