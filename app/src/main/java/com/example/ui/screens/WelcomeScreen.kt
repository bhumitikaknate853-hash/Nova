package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AnimatedCyberBackground
import com.example.ui.components.GlassCard
import com.example.ui.components.RobotAvatar
import com.example.ui.components.ShareAppDialog
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.NovaTheme

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    val themeVars = NovaTheme.vars

    LaunchedEffect(Unit) {
        isVisible = true
    }

    AnimatedCyberBackground(isDarkTheme = themeVars.isDark) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .testTag("welcome_screen")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Pulsing Robot Avatar with cyber rings and mood emoji
                RobotAvatar(
                    size = 180.dp,
                    isThinking = true,
                    moodEmoji = "✨"
                )

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn() + slideInVertically { it / 2 }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // System online badge
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (themeVars.isDark) Color(0x2200F5FF) else themeVars.readyBadgeBg,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (themeVars.isDark) CyanGlow.copy(alpha = 0.5f) else themeVars.primary.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "⚡", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "NOVA CORE v4.0 ONLINE",
                                    color = if (themeVars.isDark) CyanGlow else themeVars.readyBadgeText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "NOVA AI BRAIN 🤖",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 32.sp,
                                letterSpacing = 1.5.sp
                            ),
                            color = themeVars.textPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "The Autonomous Multi-Agent AI Platform with Neural Voice, Vision OCR, & Creative Studio ✨",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            ),
                            color = themeVars.textSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Capabilities Grid with rich emojis
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureEmojiChipRow(
                        emoji1 = "🧠",
                        label1 = "10 Specialist Agents",
                        emoji2 = "🎙️",
                        label2 = "Conversational Voice"
                    )
                    FeatureEmojiChipRow(
                        emoji1 = "🎨",
                        label1 = "Creative Studio (AI)",
                        emoji2 = "👁️",
                        label2 = "Vision & OCR Scanner"
                    )
                    FeatureEmojiChipRow(
                        emoji1 = "💻",
                        label1 = "Code & Engineering",
                        emoji2 = "🔮",
                        label2 = "Persistent Memory Vault"
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Primary Action Button
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("get_started_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeVars.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Launch NOVA Assistant 🚀",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Share App Link Button
                OutlinedButton(
                    onClick = { showShareDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("welcome_share_button"),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        themeVars.primary.copy(alpha = 0.5f)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = themeVars.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Download App / Share Link 📲",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Direct Google Sign In Link
                Row(
                    modifier = Modifier.clickable { onSignIn() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have saved conversations? ",
                        color = themeVars.textSecondary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Sign In with Google 🔑",
                        color = themeVars.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showShareDialog) {
        ShareAppDialog(onDismiss = { showShareDialog = false })
    }
}

@Composable
private fun FeatureEmojiChipRow(
    emoji1: String,
    label1: String,
    emoji2: String,
    label2: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FeatureEmojiChip(emoji = emoji1, label = label1, modifier = Modifier.weight(1f))
        FeatureEmojiChip(emoji = emoji2, label = label2, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun FeatureEmojiChip(
    emoji: String,
    label: String,
    modifier: Modifier = Modifier
) {
    val themeVars = NovaTheme.vars

    GlassCard(
        modifier = modifier.height(52.dp),
        contentPadding = 8.dp,
        backgroundColor = themeVars.surfaceCard
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = themeVars.textPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}
