package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdMobConfig

@Composable
fun AdMobBannerCard(
    modifier: Modifier = Modifier,
    adMobConfig: AdMobConfig = AdMobConfig(),
    onOpenSettings: () -> Unit = {}
) {
    var isAdVisible by remember { mutableStateOf(true) }

    if (!isAdVisible || !adMobConfig.isAdMobEnabled) return

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF1F8E9),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784)),
        shadowElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFFB8C00),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "AdMob",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Google AdMob Banner",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2E7D32)
                    )

                    if (adMobConfig.isTestMode) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(Test Mode)",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "AdMob Settings",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = { isAdVisible = false },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Hide Ad",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Ad Contents (Sponsor Message / Agricultural equipment & fertilizers ad)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE8F5E9))
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Ad Icon",
                        tint = Color(0xFF1B5E20),
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Vokatra Zezika Organika & Provende",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = "Sponsorisé: Mividiana fitaovana fiompiana am-mora. Vidin'akoho sy kisoa nohatsaraina.",
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { /* Simulated Ad Click */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Jereo", fontSize = 11.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AdMobSettingsDialog(
    adMobConfig: AdMobConfig,
    onDismiss: () -> Unit,
    onSaveConfig: (AdMobConfig) -> Unit
) {
    var pubId by remember { mutableStateOf(adMobConfig.publisherId) }
    var bannerUnitId by remember { mutableStateOf(adMobConfig.bannerAdUnitId) }
    var interstitialUnitId by remember { mutableStateOf(adMobConfig.interstitialAdUnitId) }
    var isTestMode by remember { mutableStateOf(adMobConfig.isTestMode) }
    var isEnabled by remember { mutableStateOf(adMobConfig.isAdMobEnabled) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Google AdMob Configuration", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Ampidiro eto ny Publisher ID sy Ad Unit IDs avy amin'ny kontinao Google AdMob mba hahazoana vola amin'ny publicite.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ampandehano ny AdMob (Enable):", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Switch(checked = isEnabled, onCheckedChange = { isEnabled = it })
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Mode Test AdMob:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Switch(checked = isTestMode, onCheckedChange = { isTestMode = it })
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = pubId,
                    onValueChange = { pubId = it },
                    label = { Text("AdMob Publisher ID (pub-xxx)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = bannerUnitId,
                    onValueChange = { bannerUnitId = it },
                    label = { Text("Banner Ad Unit ID") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = interstitialUnitId,
                    onValueChange = { interstitialUnitId = it },
                    label = { Text("Interstitial Ad Unit ID") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "📊 Estimated Ad Income (Statistique)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF1B5E20)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Impressions: 1,420 | eCPM: $1.85 | Vola azo: ~ 12,500 Ar / andro",
                            fontSize = 11.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveConfig(
                        AdMobConfig(
                            publisherId = pubId,
                            bannerAdUnitId = bannerUnitId,
                            interstitialAdUnitId = interstitialUnitId,
                            isTestMode = isTestMode,
                            isAdMobEnabled = isEnabled
                        )
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("Tehirizo (Save)")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Akanjo (Cancel)")
            }
        }
    )
}

@Composable
fun AdMobInterstitialModal(
    onCloseAd: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCloseAd,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFFB8C00),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Ad",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Google AdMob Interstitial", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(onClick = onCloseAd) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close Ad")
                }
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = Color(0xFF2E7D32),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Fitaovana & Sakafo Fiompiana",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Midira ao amin'ny Tsena Online Tantsaha hahazoana tolotra manokana!",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Google AdMob Interstitial Fullscreen Demonstration",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onCloseAd,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("Akatona ny Publicité (Skip)")
            }
        }
    )
}
