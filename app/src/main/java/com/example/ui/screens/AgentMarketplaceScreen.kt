package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentRegistry
import com.example.data.model.SpecialistAgent
import com.example.ui.components.AnimatedCyberBackground
import com.example.ui.components.GlassCard
import com.example.ui.components.ShareAppDialog
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.NovaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentMarketplaceScreen(
    onSelectAgent: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showShareDialog by remember { mutableStateOf(false) }
    val themeVars = NovaTheme.vars

    val categories = listOf(
        "✨ All",
        "🤖 Core",
        "💻 Engineering",
        "🔬 Research",
        "🎨 Creative",
        "👁️ Vision",
        "✍️ Writing",
        "📋 Productivity"
    )

    val filteredAgents = remember(searchQuery, selectedCategory) {
        AgentRegistry.agents.filter { agent ->
            val matchesSearch = agent.name.contains(searchQuery, ignoreCase = true) ||
                    agent.title.contains(searchQuery, ignoreCase = true) ||
                    agent.roleDescription.contains(searchQuery, ignoreCase = true)

            val catKey = selectedCategory.substringAfter(" ").trim()
            val matchesCategory = when (catKey) {
                "Core" -> agent.id in listOf("general", "voice")
                "Engineering" -> agent.id in listOf("coding")
                "Research" -> agent.id in listOf("research", "study")
                "Creative" -> agent.id in listOf("creative")
                "Vision" -> agent.id in listOf("vision")
                "Writing" -> agent.id in listOf("writing", "translation")
                "Productivity" -> agent.id in listOf("productivity")
                else -> true
            }

            matchesSearch && matchesCategory
        }
    }

    AnimatedCyberBackground(
        modifier = modifier
            .fillMaxSize()
            .testTag("agent_marketplace_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🤖 AGENT MARKETPLACE",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeVars.textPrimary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (themeVars.isDark) Color(0x3300F5FF) else themeVars.readyBadgeBg
                        ) {
                            Text(
                                text = "10 ACTIVE",
                                color = if (themeVars.isDark) CyanGlow else themeVars.readyBadgeText,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("marketplace_back_button")
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
                        modifier = Modifier.testTag("marketplace_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share App",
                            tint = if (themeVars.isDark) CyanGlow else themeVars.primary
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
                    .padding(horizontal = 16.dp)
            ) {
                // Search Bar with Light/Dark contrast
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "🔍 Search 10 specialist agents or capabilities...",
                            color = themeVars.textSecondary.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = if (themeVars.isDark) CyanGlow else themeVars.primary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("marketplace_search_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = themeVars.textPrimary,
                        unfocusedTextColor = themeVars.textPrimary,
                        focusedBorderColor = themeVars.primary,
                        unfocusedBorderColor = themeVars.surfaceBorder,
                        focusedContainerColor = themeVars.surfaceCard,
                        unfocusedContainerColor = themeVars.surfaceCard
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Categories Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = cat == selectedCategory
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) themeVars.primary else themeVars.surfaceCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) themeVars.primaryGlow else themeVars.surfaceBorder
                            ),
                            modifier = Modifier.clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color.White else themeVars.textSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Agents List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredAgents, key = { it.id }) { agent ->
                        AgentMarketplaceCard(
                            agent = agent,
                            onActivate = { onSelectAgent(agent.id) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    if (showShareDialog) {
        ShareAppDialog(onDismiss = { showShareDialog = false })
    }
}

@Composable
private fun AgentMarketplaceCard(
    agent: SpecialistAgent,
    onActivate: () -> Unit
) {
    val themeVars = NovaTheme.vars

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("marketplace_agent_card_${agent.id}"),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = themeVars.surfaceCard,
        contentPadding = 16.dp
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(agent.avatarColor).copy(alpha = 0.22f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = agent.emoji,
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = agent.name,
                            color = themeVars.textPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = agent.title,
                        color = if (themeVars.isDark) Color(agent.avatarColor) else themeVars.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = themeVars.readyBadgeBg
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = themeVars.readyBadgeText,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "READY",
                            color = themeVars.readyBadgeText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = agent.roleDescription,
                color = themeVars.textSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tool tags
            Text(
                text = "⚡ CAPABILITIES & TOOLS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = themeVars.sectionHeader,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                agent.tools.forEach { tool ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = themeVars.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, themeVars.surfaceBorder)
                    ) {
                        Text(
                            text = "✨ $tool",
                            color = themeVars.textSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Launch Button
            Button(
                onClick = onActivate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("activate_agent_button_${agent.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(agent.avatarColor),
                    contentColor = DeepNavy
                )
            ) {
                Text(
                    text = "Launch ${agent.name} ${agent.emoji}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
