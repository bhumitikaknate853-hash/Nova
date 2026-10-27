package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentRegistry
import com.example.data.model.ChatSession
import com.example.data.model.SpecialistAgent
import com.example.ui.components.AnimatedCyberBackground
import com.example.ui.components.GlassCard
import com.example.ui.components.RobotAvatar
import com.example.ui.components.ShareAppDialog
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.NovaTheme
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userName: String,
    recentSessions: List<ChatSession>,
    isDarkTheme: Boolean = NovaTheme.isDark,
    onStartChat: (agentId: String, prompt: String?) -> Unit,
    onOpenSession: (sessionId: String) -> Unit,
    onDeleteSession: (sessionId: String) -> Unit,
    onOpenVoice: () -> Unit,
    onOpenCreativeStudio: () -> Unit,
    onOpenVision: () -> Unit,
    onOpenMarketplace: () -> Unit,
    onOpenMemory: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val greeting = rememberGreeting()
    var showShareDialog by remember { mutableStateOf(false) }
    val themeVars = NovaTheme.vars

    AnimatedCyberBackground(isDarkTheme = isDarkTheme) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .testTag("dashboard_screen")
        ) {
            // Futuristic Top App Bar with high contrast title & action icons
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RobotAvatar(size = 40.dp, showMoodBadge = false)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "NOVA AI",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = themeVars.textPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "✨", fontSize = 14.sp)
                            }
                            Text(
                                text = "⚡ Core Online • 99.8% Sync",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = themeVars.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showShareDialog = true },
                        modifier = Modifier.testTag("dashboard_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share App Link",
                            tint = if (isDarkTheme) CyanGlow else themeVars.primary
                        )
                    }
                    IconButton(
                        onClick = onOpenMemory,
                        modifier = Modifier.testTag("dashboard_memory_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Memory & History",
                            tint = themeVars.textPrimary
                        )
                    }
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.testTag("dashboard_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = themeVars.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Hero Greeting Card with Animated Cute Robot
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dashboard_hero_card"),
                    shape = RoundedCornerShape(20.dp),
                    backgroundColor = themeVars.surfaceCard,
                    borderColor = if (isDarkTheme) CyanGlow.copy(alpha = 0.35f) else themeVars.surfaceBorder
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "$greeting ☀️",
                                color = if (isDarkTheme) CyanGlow else themeVars.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (userName.isNotBlank()) "Hello, $userName 👋" else "Hello, Explorer 👋",
                                color = themeVars.textPrimary,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "10 specialist neural agents are primed for code, voice, vision, & creative generation 🧠",
                                color = themeVars.textSecondary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        RobotAvatar(size = 96.dp, isThinking = false, moodEmoji = "💖")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Quick Action Grid (4 primary buttons)
                Text(
                    text = "NEURAL CAPABILITIES ⚡",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeVars.sectionHeader,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionEmojiCard(
                        title = "AI Chat",
                        subtitle = "Smart Assistant",
                        emoji = "💬",
                        tint = if (isDarkTheme) CyanGlow else Color(0xFF007A87),
                        modifier = Modifier.weight(1f),
                        testTag = "action_chat",
                        onClick = { onStartChat("general", null) }
                    )
                    QuickActionEmojiCard(
                        title = "Voice Mode",
                        subtitle = "Real-time Vocal",
                        emoji = "🎙️",
                        tint = if (isDarkTheme) CyanPrimary else Color(0xFF00897B),
                        modifier = Modifier.weight(1f),
                        testTag = "action_voice",
                        onClick = onOpenVoice
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionEmojiCard(
                        title = "Creative Studio",
                        subtitle = "Generate Images",
                        emoji = "🎨",
                        tint = Color(0xFF9C27B0),
                        modifier = Modifier.weight(1f),
                        testTag = "action_studio",
                        onClick = onOpenCreativeStudio
                    )
                    QuickActionEmojiCard(
                        title = "Vision & OCR",
                        subtitle = "Image Analysis",
                        emoji = "👁️",
                        tint = Color(0xFFE65100),
                        modifier = Modifier.weight(1f),
                        testTag = "action_vision",
                        onClick = onOpenVision
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Direct Root Path Download Banner Card
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showShareDialog = true }
                        .testTag("dashboard_download_app_card"),
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = themeVars.surfaceCard,
                    borderColor = if (isDarkTheme) CyanGlow.copy(alpha = 0.5f) else themeVars.primary.copy(alpha = 0.4f),
                    contentPadding = 12.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RobotAvatar(size = 46.dp, moodEmoji = "💖", showMoodBadge = true)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Download App to Phone",
                                    color = themeVars.textPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "📲", fontSize = 13.sp)
                            }
                            Text(
                                text = "Direct root path install for Android & Desktop",
                                color = if (isDarkTheme) CyanGlow else themeVars.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = themeVars.primary
                        ) {
                            Text(
                                text = "INSTALL",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Specialist Agents Section Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SPECIALIST AGENTS (10) 🤖",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeVars.sectionHeader,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Marketplace →",
                        color = themeVars.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { onOpenMarketplace() }
                            .padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                // Specialist Agents Carousel
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(AgentRegistry.agents) { agent ->
                        AgentCarouselCard(
                            agent = agent,
                            onClick = { onStartChat(agent.id, null) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Prompts Inspiration with emojis
                Text(
                    text = "INSTANT INSPIRATION ✨",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeVars.sectionHeader,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    InspirationPromptRow(
                        emoji = "💻",
                        prompt = "Debug Kotlin Coroutines deadlock in Compose",
                        onClick = { onStartChat("coding", "Can you help me diagnose a potential deadlock in Kotlin Coroutines inside Jetpack Compose?") }
                    )
                    InspirationPromptRow(
                        emoji = "🔬",
                        prompt = "Synthesize recent advances in quantum computing",
                        onClick = { onStartChat("research", "Synthesize a concise research report on quantum computing error-correction breakthroughs.") }
                    )
                    InspirationPromptRow(
                        emoji = "🎨",
                        prompt = "Generate a neon cyberpunk rainy metropolis at dusk",
                        onClick = { onStartChat("creative", "Create an ultra-detailed cinematic prompt for a rainy cyberpunk street with neon reflections.") }
                    )
                    InspirationPromptRow(
                        emoji = "🌐",
                        prompt = "Translate business email to polite Hindi & Marathi",
                        onClick = { onStartChat("translation", "Translate this business partnership offer to polite Hindi and Marathi with cultural nuance.") }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Recent Sessions Section
                if (recentSessions.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RECENT CONVERSATIONS 💬",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeVars.sectionHeader,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "History →",
                            color = themeVars.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable { onOpenMemory() }
                                .padding(4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        recentSessions.take(4).forEach { session ->
                            RecentSessionRow(
                                session = session,
                                onClick = { onOpenSession(session.id) },
                                onDelete = { onDeleteSession(session.id) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    if (showShareDialog) {
        ShareAppDialog(onDismiss = { showShareDialog = false })
    }
}

@Composable
private fun QuickActionEmojiCard(
    title: String,
    subtitle: String,
    emoji: String,
    tint: Color,
    modifier: Modifier = Modifier,
    testTag: String = "",
    onClick: () -> Unit
) {
    val themeVars = NovaTheme.vars

    GlassCard(
        modifier = modifier
            .height(96.dp)
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = themeVars.surfaceCard,
        contentPadding = 12.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(tint.copy(alpha = if (themeVars.isDark) 0.18f else 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 20.sp)
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = themeVars.textSecondary.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    color = themeVars.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = themeVars.textSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun AgentCarouselCard(
    agent: SpecialistAgent,
    onClick: () -> Unit
) {
    val themeVars = NovaTheme.vars

    GlassCard(
        modifier = Modifier
            .width(160.dp)
            .height(140.dp)
            .clickable { onClick() }
            .testTag("agent_card_${agent.id}"),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = themeVars.surfaceCard,
        contentPadding = 12.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(agent.avatarColor).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = agent.emoji, fontSize = 20.sp)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = themeVars.readyBadgeBg
                ) {
                    Text(
                        text = "READY",
                        color = themeVars.readyBadgeText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column {
                Text(
                    text = "${agent.emoji} ${agent.name}",
                    color = themeVars.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = agent.title,
                    color = themeVars.textSecondary,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun InspirationPromptRow(
    emoji: String,
    prompt: String,
    onClick: () -> Unit
) {
    val themeVars = NovaTheme.vars

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        contentPadding = 10.dp,
        backgroundColor = themeVars.surfaceCard
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = prompt,
                color = themeVars.textPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = themeVars.textSecondary.copy(alpha = 0.6f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun RecentSessionRow(
    session: ChatSession,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val agent = AgentRegistry.getAgent(session.agentId)
    val themeVars = NovaTheme.vars

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        contentPadding = 12.dp,
        backgroundColor = themeVars.surfaceCard
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = agent.emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.title.ifBlank { "Conversation" },
                    color = themeVars.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = session.lastMessage.ifBlank { "Agent: ${agent.name}" },
                    color = themeVars.textSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete conversation",
                    tint = themeVars.textSecondary.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun rememberGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "GOOD MORNING"
        hour < 17 -> "GOOD AFTERNOON"
        else -> "GOOD EVENING"
    }
}
