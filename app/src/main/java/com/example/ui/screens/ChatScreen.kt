package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentRegistry
import com.example.data.model.ChatMessage
import com.example.data.model.SpecialistAgent
import com.example.ui.components.AnimatedCyberBackground
import com.example.ui.components.GlassCard
import com.example.ui.components.RobotAvatar
import com.example.ui.components.ShareAppDialog
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.NeonTeal
import com.example.ui.theme.NovaTheme
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    currentAgent: SpecialistAgent,
    messages: List<ChatMessage>,
    streamingText: String?,
    isGenerating: Boolean,
    onSendMessage: (String) -> Unit,
    onRegenerate: () -> Unit,
    onDeleteMessage: (String) -> Unit,
    onSelectAgent: (String) -> Unit,
    onSpeakMessage: (String) -> Unit,
    onVoiceInputClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var showAgentMenu by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var editingMessageId by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    val themeVars = NovaTheme.vars

    // Auto-scroll when new messages arrive or when streaming
    LaunchedEffect(messages.size, streamingText) {
        if (messages.isNotEmpty() || !streamingText.isNullOrEmpty()) {
            val target = (messages.size + if (streamingText != null) 1 else 0) - 1
            if (target >= 0) {
                listState.animateScrollToItem(target)
            }
        }
    }

    AnimatedCyberBackground(isDarkTheme = themeVars.isDark) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding()
                .testTag("chat_screen")
        ) {
            // Chat Top Bar
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showAgentMenu = true }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(currentAgent.avatarColor).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = currentAgent.emoji, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentAgent.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = themeVars.textPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "▾",
                                    color = if (themeVars.isDark) CyanGlow else themeVars.primary,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = if (isGenerating) "⚡ Streaming neural thoughts..." else "✨ Gemini 3.8 Flash • Online",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isGenerating) themeVars.primaryGlow else themeVars.secondary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("chat_back_button")
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
                        modifier = Modifier.testTag("chat_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = if (themeVars.isDark) CyanGlow else themeVars.primary
                        )
                    }
                    IconButton(
                        onClick = { showAgentMenu = true },
                        modifier = Modifier.testTag("chat_agent_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Switch Agent",
                            tint = themeVars.textPrimary
                        )
                    }

                    DropdownMenu(
                        expanded = showAgentMenu,
                        onDismissRequest = { showAgentMenu = false },
                        modifier = Modifier.background(themeVars.surfaceCard)
                    ) {
                        AgentRegistry.agents.forEach { agent ->
                            DropdownMenuItem(
                                leadingIcon = { Text(agent.emoji, fontSize = 18.sp) },
                                text = {
                                    Column {
                                        Text(
                                            text = agent.name,
                                            fontWeight = FontWeight.Bold,
                                            color = if (agent.id == currentAgent.id) themeVars.primary else themeVars.textPrimary
                                        )
                                        Text(
                                            text = agent.title,
                                            fontSize = 11.sp,
                                            color = themeVars.textSecondary
                                        )
                                    }
                                },
                                onClick = {
                                    onSelectAgent(agent.id)
                                    showAgentMenu = false
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeVars.surfaceCard.copy(alpha = 0.95f)
                )
            )

            // Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    // Agent introduction card
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = themeVars.surfaceCard,
                        contentPadding = 12.dp
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RobotAvatar(size = 46.dp, showMoodBadge = false)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = currentAgent.emoji, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${currentAgent.name} • ${currentAgent.title}",
                                        color = if (themeVars.isDark) CyanGlow else themeVars.primary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentAgent.greeting,
                                    color = themeVars.textPrimary,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                items(messages, key = { it.id.ifBlank { it.hashCode().toString() } }) { msg ->
                    ChatMessageItem(
                        message = msg,
                        agent = currentAgent,
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Chat message", msg.content))
                            Toast.makeText(context, "Copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                        },
                        onSpeak = { onSpeakMessage(msg.content) },
                        onEdit = {
                            if (msg.role == "user") {
                                inputText = msg.content
                                editingMessageId = msg.id
                            }
                        },
                        onDelete = { onDeleteMessage(msg.id) }
                    )
                }

                // Real-time streaming bubble
                if (!streamingText.isNullOrBlank()) {
                    item {
                        StreamingMessageItem(
                            content = streamingText,
                            agent = currentAgent
                        )
                    }
                } else if (isGenerating) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = if (themeVars.isDark) CyanGlow else themeVars.primary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "${currentAgent.emoji} ${currentAgent.name} is synthesizing...",
                                color = if (themeVars.isDark) CyanGlow else themeVars.primary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Quick Follow-Up Suggestions with emojis
            AnimatedVisibility(visible = messages.isNotEmpty() && !isGenerating) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FollowUpChip(
                        label = "Explain further 🔍",
                        onClick = { onSendMessage("Can you elaborate on that further with more context?") }
                    )
                    FollowUpChip(
                        label = "Code implementation 💻",
                        onClick = { onSendMessage("Can you provide a working code implementation for this?") }
                    )
                    FollowUpChip(
                        label = "Bullet summary 📋",
                        onClick = { onSendMessage("Summarize the key takeaways in clean bullet points.") }
                    )
                }
            }

            // Input Field & Action Buttons Bar
            Surface(
                color = themeVars.surfaceCard.copy(alpha = 0.98f),
                border = androidx.compose.foundation.BorderStroke(1.dp, themeVars.surfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Voice input button
                    IconButton(
                        onClick = onVoiceInputClick,
                        modifier = Modifier.testTag("chat_voice_input_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = if (themeVars.isDark) NeonTeal else themeVars.secondary
                        )
                    }

                    // Input Text Field
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = "Message ${currentAgent.name}... ✨",
                                color = themeVars.textSecondary.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = themeVars.textPrimary,
                            unfocusedTextColor = themeVars.textPrimary,
                            focusedBorderColor = themeVars.primary,
                            unfocusedBorderColor = themeVars.surfaceBorder,
                            focusedContainerColor = themeVars.surface,
                            unfocusedContainerColor = themeVars.surface
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Send Button
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank() && !isGenerating) {
                                val textToSend = inputText.trim()
                                inputText = ""
                                editingMessageId = null
                                onSendMessage(textToSend)
                            }
                        },
                        enabled = inputText.isNotBlank() && !isGenerating,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (inputText.isNotBlank() && !isGenerating)
                                    themeVars.primary
                                else
                                    themeVars.surfaceBorder
                            )
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank() && !isGenerating)
                                Color.White
                            else
                                themeVars.textSecondary.copy(alpha = 0.4f)
                        )
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
private fun ChatMessageItem(
    message: ChatMessage,
    agent: SpecialistAgent,
    onCopy: () -> Unit,
    onSpeak: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isUser = message.role == "user"
    val themeVars = NovaTheme.vars

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chat_message_${message.id}"),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            if (!isUser) {
                Text(text = agent.emoji, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = agent.name,
                    color = if (themeVars.isDark) Color(agent.avatarColor) else themeVars.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "You 👤",
                    color = themeVars.textSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Message Bubble with high contrast
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (themeVars.isDark) {
                if (isUser) SurfaceCard.copy(alpha = 0.95f) else SurfaceNavy.copy(alpha = 0.92f)
            } else {
                if (isUser) Color(0xFFE0F2FE) else Color.White
            },
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (themeVars.isDark) {
                    if (isUser) CyanPrimary.copy(alpha = 0.4f) else Color(0x2200E5FF)
                } else {
                    if (isUser) Color(0xFFBAE6FD) else themeVars.surfaceBorder
                }
            ),
            shadowElevation = if (themeVars.isDark) 0.dp else 2.dp,
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                FormattedMessageContent(content = message.content)

                Spacer(modifier = Modifier.height(8.dp))

                // Actions row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy message",
                            tint = themeVars.textSecondary.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    if (!isUser) {
                        IconButton(onClick = onSpeak, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Read aloud",
                                tint = themeVars.textSecondary.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    } else {
                        IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit prompt",
                                tint = themeVars.textSecondary.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = themeVars.textSecondary.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StreamingMessageItem(
    content: String,
    agent: SpecialistAgent
) {
    val themeVars = NovaTheme.vars

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("streaming_message_bubble"),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Text(text = agent.emoji, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${agent.name} (Live)",
                color = if (themeVars.isDark) CyanGlow else themeVars.primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Surface(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp),
            color = if (themeVars.isDark) SurfaceNavy.copy(alpha = 0.95f) else Color.White,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (themeVars.isDark) CyanGlow.copy(alpha = 0.6f) else themeVars.primary.copy(alpha = 0.4f)
            ),
            shadowElevation = if (themeVars.isDark) 0.dp else 2.dp,
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                FormattedMessageContent(content = content)
            }
        }
    }
}

@Composable
private fun FormattedMessageContent(content: String) {
    val blocks = remember(content) { parseMarkdownBlocks(content) }
    val themeVars = NovaTheme.vars

    blocks.forEach { block ->
        when (block) {
            is MarkdownBlock.CodeBlock -> {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DeepNavy,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4400E5FF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        if (block.language.isNotBlank()) {
                            Text(
                                text = "💻 ${block.language.uppercase()}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonTeal
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Text(
                            text = block.code,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = Color(0xFF80D8FF),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
            is MarkdownBlock.TextBlock -> {
                Text(
                    text = block.text,
                    color = themeVars.textPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun FollowUpChip(
    label: String,
    onClick: () -> Unit
) {
    val themeVars = NovaTheme.vars

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (themeVars.isDark) Color(0x2200E5FF) else Color(0xFFE0F2FE),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (themeVars.isDark) CyanGlow.copy(alpha = 0.4f) else Color(0xFFBAE6FD)
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (themeVars.isDark) CyanGlow else themeVars.primary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

private sealed interface MarkdownBlock {
    data class TextBlock(val text: String) : MarkdownBlock
    data class CodeBlock(val language: String, val code: String) : MarkdownBlock
}

private fun parseMarkdownBlocks(rawText: String): List<MarkdownBlock> {
    val blocks = mutableListOf<MarkdownBlock>()
    val lines = rawText.split("\n")
    var inCodeBlock = false
    var currentLanguage = ""
    val codeBuilder = StringBuilder()
    val textBuilder = StringBuilder()

    for (line in lines) {
        if (line.trimStart().startsWith("```")) {
            if (!inCodeBlock) {
                if (textBuilder.isNotEmpty()) {
                    blocks.add(MarkdownBlock.TextBlock(textBuilder.toString().trimEnd()))
                    textBuilder.clear()
                }
                inCodeBlock = true
                currentLanguage = line.trimStart().removePrefix("```").trim()
            } else {
                inCodeBlock = false
                blocks.add(MarkdownBlock.CodeBlock(currentLanguage, codeBuilder.toString().trimEnd()))
                codeBuilder.clear()
                currentLanguage = ""
            }
        } else {
            if (inCodeBlock) {
                if (codeBuilder.isNotEmpty()) codeBuilder.append("\n")
                codeBuilder.append(line)
            } else {
                if (textBuilder.isNotEmpty()) textBuilder.append("\n")
                textBuilder.append(line)
            }
        }
    }

    if (codeBuilder.isNotEmpty()) {
        blocks.add(MarkdownBlock.CodeBlock(currentLanguage, codeBuilder.toString().trimEnd()))
    }
    if (textBuilder.isNotEmpty()) {
        blocks.add(MarkdownBlock.TextBlock(textBuilder.toString().trimEnd()))
    }

    return blocks
}
