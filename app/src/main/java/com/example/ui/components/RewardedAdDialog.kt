package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AdConfig
import com.example.model.AppLanguage
import kotlinx.coroutines.delay

@Composable
fun RewardedAdDialog(
    adConfig: AdConfig,
    language: AppLanguage = AppLanguage.URDU,
    showDevInfo: Boolean = false,
    onRewardEarned: () -> Unit,
    onDismiss: () -> Unit
) {
    var countdown by remember { mutableIntStateOf(5) }
    var rewardEarned by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000)
            countdown -= 1
        }
        rewardEarned = true
        onRewardEarned()
    }

    Dialog(
        onDismissRequest = {
            if (rewardEarned) onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = rewardEarned,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE6050814))
                .padding(16.dp)
                .testTag("rewarded_ad_dialog"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF10B981), Color(0xFF06B6D4))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111827))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF10B981), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "REWARDED AD",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.URDU) "ریوارڈڈ ویڈیو اشتہار" else "Sponsored Video",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (rewardEarned) {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color(0xFF10B981),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${countdown}s",
                                    color = Color(0xFF10B981),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Video Simulation Area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF0F172A), Color(0xFF022C22))
                                )
                            )
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(
                                        if (rewardEarned) Color(0xFF10B981) else Color(0xFF10B981).copy(alpha = 0.2f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (rewardEarned) Icons.Default.Check else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = if (rewardEarned) Color.Black else Color(0xFF10B981),
                                    modifier = Modifier.size(30.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = if (rewardEarned)
                                    (if (language == AppLanguage.URDU) "🎉 انعام حاصل ہو گیا! تصویر ڈاؤنلوڈ ہو رہی ہے" else "🎉 Reward Earned! Downloading image...")
                                else
                                    (if (language == AppLanguage.URDU) "اشتہار دیکھا جا رہا ہے... تصویر ان لاک ہو رہی ہے" else "Watching sponsored video to unlock full download..."),
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reward description card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color(0xFF10B981).copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CardGiftcard,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = if (language == AppLanguage.URDU) "مفت ڈاؤنلوڈ ریوارڈ" else "Free Image Download",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (language == AppLanguage.URDU)
                                        "اس اشتہار کے اختتام پر آپ کی کمپریسڈ تصویر گیلری میں محفوظ ہو جائے گی۔"
                                    else
                                        "Your compressed photo will be saved to your device gallery when video finishes.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    if (showDevInfo) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Unit ID: ${adConfig.rewardedAdId}\nStatus: Live Google AdMob Unit",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onDismiss,
                        enabled = rewardEarned,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("rewarded_ad_claim_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981),
                            disabledContainerColor = Color(0xFF1E293B)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = if (rewardEarned) Icons.Default.Download else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (rewardEarned) Color.Black else Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (rewardEarned)
                                (if (language == AppLanguage.URDU) "تصویر حاصل کریں اور بند کریں" else "Claim Image & Continue")
                            else
                                (if (language == AppLanguage.URDU) "اشتہار جاری ہے (${countdown}s)" else "Please wait (${countdown}s)"),
                            color = if (rewardEarned) Color.Black else Color(0xFF64748B),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
