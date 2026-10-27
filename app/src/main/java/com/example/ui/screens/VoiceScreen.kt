package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.repository.VoiceState
import com.example.ui.components.AnimatedCyberBackground
import com.example.ui.components.GlassCard
import com.example.ui.components.RobotAvatar
import com.example.ui.components.ShareAppDialog
import com.example.ui.components.WaveformVisualizer
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.NovaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceScreen(
    voiceState: VoiceState,
    soundLevel: Float,
    transcription: String,
    lastAiSpokenResponse: String,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onStopSpeaking: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    var showShareDialog by remember { mutableStateOf(false) }
    val themeVars = NovaTheme.vars

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onStartListening()
        } else {
            Toast.makeText(context, "Microphone permission is required for voice assistant.", Toast.LENGTH_SHORT).show()
        }
    }

    val isListening = voiceState is VoiceState.Listening
    val isSpeaking = voiceState is VoiceState.Speaking

    AnimatedCyberBackground(isDarkTheme = themeVars.isDark) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .testTag("voice_screen")
        ) {
            // Voice Top Bar
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎙️", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AURA VOCAL TERMINAL",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeVars.textPrimary,
                            letterSpacing = 1.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("voice_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = themeVars.textPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showShareDialog = true },
                        modifier = Modifier.testTag("voice_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = if (themeVars.isDark) CyanGlow else themeVars.primary
                        )
                    }
                    if (isSpeaking) {
                        IconButton(
                            onClick = onStopSpeaking,
                            modifier = Modifier.testTag("voice_mute_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeOff,
                                contentDescription = "Interrupt Spoken Output",
                                tint = themeVars.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Language Selection Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    LanguageChip(
                        code = "en-US",
                        label = "🇺🇸 English",
                        selected = selectedLanguage == "en-US",
                        onSelect = onLanguageSelected
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    LanguageChip(
                        code = "hi-IN",
                        label = "🇮🇳 Hindi (हिंदी)",
                        selected = selectedLanguage == "hi-IN",
                        onSelect = onLanguageSelected
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    LanguageChip(
                        code = "mr-IN",
                        label = "🚩 Marathi (मराठी)",
                        selected = selectedLanguage == "mr-IN",
                        onSelect = onLanguageSelected
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Animated Cute Robot Avatar in Vocal Mode
                RobotAvatar(
                    size = 130.dp,
                    isSpeaking = isSpeaking,
                    isThinking = isListening,
                    moodEmoji = if (isListening) "🎙️" else if (isSpeaking) "🔊" else "✨"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when {
                        isListening -> if (themeVars.isDark) Color(0x3300FF88) else Color(0xFFD1FAE5)
                        isSpeaking -> if (themeVars.isDark) Color(0x3300F5FF) else Color(0xFFE0F2FE)
                        else -> themeVars.surface
                    },
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when {
                            isListening -> if (themeVars.isDark) Color(0xFF00FF88) else Color(0xFF059669)
                            isSpeaking -> themeVars.primary
                            else -> themeVars.surfaceBorder
                        }
                    )
                ) {
                    Text(
                        text = when (voiceState) {
                            is VoiceState.Listening -> "🔴 LISTENING... SPEAK NOW"
                            is VoiceState.Speaking -> "🔊 SYNTHESIZING VOICE..."
                            is VoiceState.Error -> "⚠️ MIC PAUSED"
                            else -> "⚡ TAP MIC TO CONVERSE"
                        },
                        color = when {
                            isListening -> if (themeVars.isDark) Color(0xFF00FF88) else Color(0xFF065F46)
                            isSpeaking -> if (themeVars.isDark) CyanGlow else themeVars.primary
                            else -> themeVars.textPrimary
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Live Animated Waveform
                WaveformVisualizer(
                    soundLevel = soundLevel,
                    isActive = isListening || isSpeaking,
                    height = 54.dp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Transcription & AI Response Card
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("voice_transcript_card"),
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = themeVars.surfaceCard,
                    contentPadding = 16.dp
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🎙️", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE TRANSCRIPTION",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeVars.sectionHeader,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (transcription.isNotBlank()) "\"$transcription\"" else "Speak clearly into the microphone...",
                            color = if (transcription.isNotBlank()) themeVars.textPrimary else themeVars.textSecondary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )

                        if (lastAiSpokenResponse.isNotBlank()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🤖", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "NOVA'S SPOKEN RESPONSE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = themeVars.sectionHeader,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = lastAiSpokenResponse,
                                color = themeVars.textPrimary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Big Central Mic Button with radial glow
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    if (isListening) Color(0xFFFF5252) else if (themeVars.isDark) CyanGlow else themeVars.primaryGlow,
                                    if (isListening) Color(0xFFD32F2F) else if (themeVars.isDark) CyanPrimary else themeVars.primary
                                )
                            )
                        )
                        .border(
                            3.dp,
                            if (isListening) Color(0xFFFF8A80) else Color.White.copy(alpha = 0.85f),
                            CircleShape
                        )
                        .clickable {
                            if (isListening) {
                                onStopListening()
                            } else {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasPermission) {
                                    onStartListening()
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        }
                        .testTag("voice_mic_toggle_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (isListening) "Stop Listening" else "Start Listening",
                        tint = if (isListening) Color.White else if (themeVars.isDark) DeepNavy else Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isListening) "Tap to Finish Speaking ⏹️" else "Tap to Speak to NOVA 🎙️",
                    color = themeVars.textSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showShareDialog) {
        ShareAppDialog(onDismiss = { showShareDialog = false })
    }
}

@Composable
private fun LanguageChip(
    code: String,
    label: String,
    selected: Boolean,
    onSelect: (String) -> Unit
) {
    val themeVars = NovaTheme.vars

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (selected) themeVars.primary else themeVars.surfaceCard,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) themeVars.primaryGlow else themeVars.surfaceBorder
        ),
        modifier = Modifier.clickable { onSelect(code) }
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else themeVars.textSecondary,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
