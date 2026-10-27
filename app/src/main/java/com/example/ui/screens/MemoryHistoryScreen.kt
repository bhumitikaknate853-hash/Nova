package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentRegistry
import com.example.data.model.ChatSession
import com.example.data.model.MemoryItem
import com.example.ui.components.AnimatedCyberBackground
import com.example.ui.components.GlassCard
import com.example.ui.components.ShareAppDialog
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.NovaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryHistoryScreen(
    sessions: List<ChatSession>,
    memories: List<MemoryItem>,
    onOpenSession: (String) -> Unit,
    onDeleteSession: (String) -> Unit,
    onAddMemory: (key: String, value: String, category: String) -> Unit,
    onDeleteMemory: (String) -> Unit,
    onClearAllMemories: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val themeVars = NovaTheme.vars

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Sessions, 1: Memory Vault
    var searchQuery by remember { mutableStateOf("") }
    var showAddMemoryDialog by remember { mutableStateOf(false) }
    var showClearConfirmation by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }

    // Dialog form state
    var newMemoryKey by remember { mutableStateOf("") }
    var newMemoryValue by remember { mutableStateOf("") }
    var newMemoryCategory by remember { mutableStateOf("preference") }

    val filteredSessions = remember(sessions, searchQuery) {
        if (searchQuery.isBlank()) sessions
        else sessions.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.lastMessage.contains(searchQuery, ignoreCase = true)
        }
    }

    val filteredMemories = remember(memories, searchQuery) {
        if (searchQuery.isBlank()) memories
        else memories.filter {
            it.key.contains(searchQuery, ignoreCase = true) ||
                    it.value.contains(searchQuery, ignoreCase = true) ||
                    it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    AnimatedCyberBackground(
        modifier = modifier
            .fillMaxSize()
            .testTag("memory_history_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "🧠 MEMORY & VAULT",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeVars.textPrimary,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("memory_back_button")
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
                        modifier = Modifier.testTag("memory_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = if (themeVars.isDark) CyanGlow else themeVars.primary
                        )
                    }

                    // Export data button
                    IconButton(
                        onClick = {
                            val dataJson = buildExportJson(sessions, memories)
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Nova Backup", dataJson))
                            Toast.makeText(context, "Full memory export copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("memory_export_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Export Neural History",
                            tint = themeVars.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            // Tabs Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = themeVars.surfaceCard,
                contentColor = themeVars.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = themeVars.primary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "💬 Chat Sessions (${sessions.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (selectedTab == 0) themeVars.primary else themeVars.textSecondary
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "🧠 Neural Vault (${memories.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (selectedTab == 1) themeVars.primary else themeVars.textSecondary
                        )
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (selectedTab == 0) "🔍 Search past conversations..." else "🔍 Search stored memory facts...",
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
                        .testTag("memory_search_input"),
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

                if (selectedTab == 0) {
                    // SESSIONS TAB
                    if (filteredSessions.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "💬",
                                    fontSize = 40.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No conversation sessions found.",
                                    color = themeVars.textSecondary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredSessions, key = { it.id }) { session ->
                                val agent = AgentRegistry.getAgent(session.agentId)
                                GlassCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onOpenSession(session.id) }
                                        .testTag("history_session_${session.id}"),
                                    shape = RoundedCornerShape(14.dp),
                                    backgroundColor = themeVars.surfaceCard,
                                    contentPadding = 12.dp
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(Color(agent.avatarColor).copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = agent.emoji,
                                                fontSize = 20.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = session.title.ifBlank { "Untitled Chat" },
                                                color = themeVars.textPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = session.lastMessage.ifBlank { "Specialist: ${agent.name}" },
                                                color = themeVars.textSecondary,
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        IconButton(
                                            onClick = { onDeleteSession(session.id) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = themeVars.textSecondary.copy(alpha = 0.6f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // MEMORY VAULT TAB
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ USER-CONTROLLED MEMORY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeVars.sectionHeader,
                            letterSpacing = 1.sp
                        )

                        Row {
                            if (memories.isNotEmpty()) {
                                TextButton(onClick = { showClearConfirmation = true }) {
                                    Text("🗑️ Clear All", color = Color(0xFFFF5252), fontSize = 11.sp)
                                }
                            }
                            Button(
                                onClick = { showAddMemoryDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = themeVars.primary, contentColor = Color.White),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Memory ✨", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (filteredMemories.isEmpty()) {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            backgroundColor = themeVars.surfaceCard,
                            contentPadding = 16.dp
                        ) {
                            Text(
                                text = "🧠 No saved memories yet. Add key preferences, user identity facts, or workflow rules that you want NOVA agents to remember permanently across every conversation.",
                                color = themeVars.textSecondary,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredMemories, key = { it.id }) { mem ->
                                val categoryEmoji = when (mem.category.lowercase()) {
                                    "preference" -> "⚙️"
                                    "identity" -> "👤"
                                    "fact" -> "📋"
                                    "goal" -> "🎯"
                                    else -> "💡"
                                }
                                GlassCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("memory_item_${mem.id}"),
                                    shape = RoundedCornerShape(14.dp),
                                    backgroundColor = themeVars.surfaceCard,
                                    contentPadding = 12.dp
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(themeVars.primary.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = categoryEmoji, fontSize = 16.sp)
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = mem.key,
                                                    color = themeVars.textPrimary,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = themeVars.readyBadgeBg
                                                ) {
                                                    Text(
                                                        text = mem.category.uppercase(),
                                                        color = themeVars.readyBadgeText,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = mem.value,
                                                color = themeVars.textSecondary,
                                                fontSize = 12.sp,
                                                lineHeight = 16.sp
                                            )
                                        }
                                        IconButton(
                                            onClick = { onDeleteMemory(mem.id) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = themeVars.textSecondary.copy(alpha = 0.6f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Memory Dialog
    if (showAddMemoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddMemoryDialog = false },
            title = {
                Text(
                    text = "Add Permanent Memory Fact 🧠",
                    fontWeight = FontWeight.Bold,
                    color = themeVars.textPrimary,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newMemoryKey,
                        onValueChange = { newMemoryKey = it },
                        label = { Text("Topic / Key (e.g., 'Preferred Code Style')") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = themeVars.textPrimary,
                            unfocusedTextColor = themeVars.textPrimary
                        )
                    )
                    OutlinedTextField(
                        value = newMemoryValue,
                        onValueChange = { newMemoryValue = it },
                        label = { Text("Details / Fact (e.g., 'Always use Kotlin Coroutines')") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = themeVars.textPrimary,
                            unfocusedTextColor = themeVars.textPrimary
                        )
                    )
                    OutlinedTextField(
                        value = newMemoryCategory,
                        onValueChange = { newMemoryCategory = it },
                        label = { Text("Category (preference, identity, goal, fact)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = themeVars.textPrimary,
                            unfocusedTextColor = themeVars.textPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newMemoryKey.isNotBlank() && newMemoryValue.isNotBlank()) {
                            onAddMemory(newMemoryKey.trim(), newMemoryValue.trim(), newMemoryCategory.trim())
                            newMemoryKey = ""
                            newMemoryValue = ""
                            showAddMemoryDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = themeVars.primary)
                ) {
                    Text("Save Memory 💾", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMemoryDialog = false }) {
                    Text("Cancel", color = themeVars.textSecondary)
                }
            },
            containerColor = themeVars.surfaceCard
        )
    }

    // Clear confirmation dialog
    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            title = { Text("Clear All Memories? ⚠️", color = themeVars.textPrimary) },
            text = {
                Text(
                    "This will wipe all permanently remembered user facts and preferences from NOVA's memory vault.",
                    color = themeVars.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllMemories()
                        showClearConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("Wipe All", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmation = false }) {
                    Text("Cancel", color = themeVars.textSecondary)
                }
            },
            containerColor = themeVars.surfaceCard
        )
    }

    if (showShareDialog) {
        ShareAppDialog(onDismiss = { showShareDialog = false })
    }
}

private fun buildExportJson(sessions: List<ChatSession>, memories: List<MemoryItem>): String {
    val sb = StringBuilder()
    sb.append("{\n  \"nova_sessions_count\": ${sessions.size},\n  \"nova_memories_count\": ${memories.size},\n  \"memories\": [\n")
    memories.forEachIndexed { index, m ->
        sb.append("    {\"id\": \"${m.id}\", \"key\": \"${m.key}\", \"value\": \"${m.value}\", \"category\": \"${m.category}\"}")
        if (index < memories.size - 1) sb.append(",")
        sb.append("\n")
    }
    sb.append("  ]\n}")
    return sb.toString()
}
