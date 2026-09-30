package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AdConfig
import com.example.model.AppLanguage

@Composable
fun AdSettingsDialog(
    adConfig: AdConfig,
    language: AppLanguage,
    onSave: (AdConfig) -> Unit,
    onTestInterstitial: () -> Unit,
    onTestRewarded: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var bannerId by remember { mutableStateOf(adConfig.bannerAdId) }
    var interstitialId by remember { mutableStateOf(adConfig.interstitialAdId) }
    var rewardedId by remember { mutableStateOf(adConfig.rewardedAdId) }
    var appId by remember { mutableStateOf(adConfig.appId) }
    var adsEnabled by remember { mutableStateOf(adConfig.adsEnabled) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("ad_settings_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111827))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF06B6D4).copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = null,
                                tint = Color(0xFF06B6D4)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (language == AppLanguage.URDU) "گوگل ایڈز سیٹنگز" else "AdMob Configuration",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = if (language == AppLanguage.URDU) "آپ کی گوگل ایڈ آئی ڈیز" else "Your Google AdMob IDs",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // User Google ID active note
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF06B6D4).copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF06B6D4),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.URDU)
                            "ٹیسٹ ایڈ ختم کر دیا گیا ہے۔ آپ کی درج کردہ گوگل آئی ڈیز براہ راست استعمال ہوں گی۔"
                        else
                            "Test Ads permanently removed. Your custom Google Ad IDs are used directly.",
                        color = Color(0xFFE2E8F0),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Toggle Enable Ads
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == AppLanguage.URDU) "اشتہارات فعال کریں" else "Enable Advertisements",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (language == AppLanguage.URDU) "گوگل بینر اور انٹرسٹیشل ایڈز دکھائیں" else "Display Google banner and interstitial ads",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = adsEnabled,
                        onCheckedChange = { adsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF06B6D4),
                            checkedTrackColor = Color(0xFF0E7490)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // AdMob App ID
                Text(
                    text = "Google AdMob App ID (e.g. ca-app-pub-xxxxxxxx~xxxxxxxx)",
                    color = Color(0xFFE2E8F0),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = appId,
                    onValueChange = { appId = it },
                    placeholder = { Text("ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy", color = Color(0xFF64748B), fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("admob_app_id_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF06B6D4),
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Banner Unit ID
                Text(
                    text = if (language == AppLanguage.URDU) "بینر ایڈ یونٹ ID (Banner Ad Unit ID)" else "Banner Ad Unit ID (ca-app-pub-xxxxxxxx/xxxxxxxx)",
                    color = Color(0xFFE2E8F0),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = bannerId,
                    onValueChange = { bannerId = it },
                    placeholder = { Text("ca-app-pub-xxxxxxxxxxxxxxxx/yyyyyyyyyy", color = Color(0xFF64748B), fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("banner_ad_id_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF06B6D4),
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Interstitial Unit ID
                Text(
                    text = if (language == AppLanguage.URDU) "انٹرسٹیشل ایڈ یونٹ ID (Interstitial Unit ID)" else "Interstitial Ad Unit ID (ca-app-pub-xxxxxxxx/xxxxxxxx)",
                    color = Color(0xFFE2E8F0),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = interstitialId,
                    onValueChange = { interstitialId = it },
                    placeholder = { Text("ca-app-pub-xxxxxxxxxxxxxxxx/yyyyyyyyyy", color = Color(0xFF64748B), fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("interstitial_ad_id_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF06B6D4),
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Rewarded Unit ID
                Text(
                    text = if (language == AppLanguage.URDU) "ریوارڈڈ ایڈ یونٹ ID (Rewarded Unit ID)" else "Rewarded Ad Unit ID (ca-app-pub-xxxxxxxx/xxxxxxxx)",
                    color = Color(0xFFE2E8F0),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = rewardedId,
                    onValueChange = { rewardedId = it },
                    placeholder = { Text("ca-app-pub-xxxxxxxxxxxxxxxx/yyyyyyyyyy", color = Color(0xFF64748B), fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("rewarded_ad_id_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF10B981),
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Test Interstitial Button
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onTestInterstitial()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF06B6D4))
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = if (language == AppLanguage.URDU) "انٹرسٹیشل ایڈ چلائیں" else "Test Interstitial Ad Now")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Test Rewarded Ad Button
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onTestRewarded()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF10B981))
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = if (language == AppLanguage.URDU) "ریوارڈڈ ایڈ چلائیں" else "Test Rewarded Ad Now")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Save Button
                Button(
                    onClick = {
                        onSave(
                            adConfig.copy(
                                bannerAdId = bannerId.trim(),
                                interstitialAdId = interstitialId.trim(),
                                rewardedAdId = rewardedId.trim(),
                                appId = appId.trim(),
                                adsEnabled = adsEnabled
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("save_ad_settings_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == AppLanguage.URDU) "سیو کریں" else "Save Ad Settings",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
