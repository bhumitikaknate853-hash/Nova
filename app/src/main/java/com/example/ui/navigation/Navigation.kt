package com.example.ui.navigation

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentRegistry
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSession
import com.example.data.model.CreationItem
import com.example.data.model.MemoryItem
import com.example.data.model.SpecialistAgent
import com.example.data.model.UserPreference
import com.example.data.repository.AiService
import com.example.data.repository.FirestoreRepository
import com.example.data.repository.VoiceManager
import com.example.data.repository.VoiceState
import com.example.ui.screens.AgentMarketplaceScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CreativeStudioScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MemoryHistoryScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.screens.VisionScreen
import com.example.ui.screens.VoiceScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SurfaceNavy
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

sealed interface Screen {
    data object Welcome : Screen
    data object Dashboard : Screen
    data class Chat(val sessionId: String, val agentId: String) : Screen
    data object Voice : Screen
    data object CreativeStudio : Screen
    data object Vision : Screen
    data object Marketplace : Screen
    data object MemoryHistory : Screen
    data object ProfileSettings : Screen
    data object Auth : Screen
}

@Composable
fun AppNavigation(
    firestoreRepository: FirestoreRepository,
    aiService: AiService,
    voiceManager: VoiceManager,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }

    val authUser = Firebase.auth.currentUser

    // Observe Cloud state
    val recentSessions by remember(authUser?.uid) {
        if (authUser != null) firestoreRepository.observeChatSessions()
        else kotlinx.coroutines.flow.flowOf(emptyList())
    }.collectAsState(initial = emptyList())

    val memories by remember(authUser?.uid) {
        if (authUser != null) firestoreRepository.observeMemories()
        else kotlinx.coroutines.flow.flowOf(emptyList())
    }.collectAsState(initial = emptyList())

    val creations by remember(authUser?.uid) {
        if (authUser != null) firestoreRepository.observeCreations()
        else kotlinx.coroutines.flow.flowOf(emptyList())
    }.collectAsState(initial = emptyList())

    val preferences by remember(authUser?.uid) {
        if (authUser != null) firestoreRepository.observePreferences()
        else kotlinx.coroutines.flow.flowOf(null)
    }.collectAsState(initial = null)

    // Current chat state
    var currentSessionId by remember { mutableStateOf<String?>(null) }
    var currentAgentId by remember { mutableStateOf("general") }
    var activeMessages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var streamingText by remember { mutableStateOf<String?>(null) }
    var isGeneratingChat by remember { mutableStateOf(false) }

    // Creative studio state
    var isGeneratingImage by remember { mutableStateOf(false) }
    var currentGeneratedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Vision state
    var isAnalyzingVision by remember { mutableStateOf(false) }
    var visionAnalysisResult by remember { mutableStateOf<String?>(null) }

    // Voice state
    val voiceState by voiceManager.voiceState.collectAsState()
    val soundLevel by voiceManager.soundLevel.collectAsState()
    val transcription by voiceManager.transcription.collectAsState()
    var lastAiSpokenResponse by remember { mutableStateOf("") }

    // Hook voice recognition to AI response
    remember(voiceManager) {
        voiceManager.onSpeechRecognized = { spokenText ->
            val agent = AgentRegistry.getAgent("voice")
            coroutineScope.launch {
                val fullResponse = StringBuilder()
                val memoryList = memories.map { "${it.key}: ${it.value}" }
                aiService.streamChatResponse(
                    agent = agent,
                    userPrompt = spokenText,
                    history = emptyList(),
                    userMemories = memoryList
                ).collect { chunk ->
                    fullResponse.append(chunk)
                }
                val resultText = fullResponse.toString().replace(Regex("[*#`_]"), "").trim()
                lastAiSpokenResponse = resultText
                voiceManager.speak(resultText)
            }
        }
    }

    // Bottom bar active state
    val showBottomBar = currentScreen is Screen.Dashboard ||
            currentScreen is Screen.Chat ||
            currentScreen is Screen.Voice ||
            currentScreen is Screen.CreativeStudio ||
            currentScreen is Screen.Vision

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = if (isDarkTheme) SurfaceNavy.copy(alpha = 0.98f) else Color.White,
                    tonalElevation = if (isDarkTheme) 0.dp else 6.dp,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .height(64.dp)
                ) {
                    val navItems = listOf(
                        Triple("Home 🏠", Icons.Default.Home, Screen.Dashboard),
                        Triple("Chat 💬", Icons.AutoMirrored.Filled.Chat, Screen.Chat(currentSessionId ?: "new", currentAgentId)),
                        Triple("Voice 🎙️", Icons.Default.Mic, Screen.Voice),
                        Triple("Studio 🎨", Icons.Default.Palette, Screen.CreativeStudio),
                        Triple("Vision 👁️", Icons.Default.Visibility, Screen.Vision)
                    )

                    navItems.forEach { (label, icon, destination) ->
                        val isSelected = when (destination) {
                            is Screen.Dashboard -> currentScreen is Screen.Dashboard
                            is Screen.Chat -> currentScreen is Screen.Chat
                            is Screen.Voice -> currentScreen is Screen.Voice
                            is Screen.CreativeStudio -> currentScreen is Screen.CreativeStudio
                            is Screen.Vision -> currentScreen is Screen.Vision
                            else -> false
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (destination is Screen.Chat && currentSessionId == null) {
                                    // Start fresh chat session
                                    coroutineScope.launch {
                                        val agent = AgentRegistry.getAgent(currentAgentId)
                                        val newId = if (authUser != null) {
                                            firestoreRepository.createChatSession(
                                                title = "Chat with ${agent.name}",
                                                agentId = agent.id
                                            )
                                        } else "local_${System.currentTimeMillis()}"
                                        currentSessionId = newId
                                        currentScreen = Screen.Chat(newId, currentAgentId)
                                    }
                                } else {
                                    currentScreen = destination
                                }
                            },
                            icon = { Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(22.dp)) },
                            label = { Text(text = label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = if (isDarkTheme) DeepNavy else Color.White,
                                selectedTextColor = if (isDarkTheme) CyanGlow else Color(0xFF007A87),
                                indicatorColor = if (isDarkTheme) CyanGlow else Color(0xFF008394),
                                unselectedIconColor = if (isDarkTheme) Color(0xFF78909C) else Color(0xFF64748B),
                                unselectedTextColor = if (isDarkTheme) Color(0xFF78909C) else Color(0xFF64748B)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    is Screen.Welcome -> {
                        WelcomeScreen(
                            onGetStarted = { currentScreen = Screen.Dashboard },
                            onSignIn = { currentScreen = Screen.Auth }
                        )
                    }

                    is Screen.Dashboard -> {
                        DashboardScreen(
                            userName = authUser?.displayName ?: "Operator",
                            recentSessions = recentSessions,
                            isDarkTheme = isDarkTheme,
                            onStartChat = { agentId, initialPrompt ->
                                currentAgentId = agentId
                                coroutineScope.launch {
                                    val agent = AgentRegistry.getAgent(agentId)
                                    val newId = if (authUser != null) {
                                        firestoreRepository.createChatSession(
                                            title = if (!initialPrompt.isNullOrBlank()) initialPrompt.take(30) else "Chat with ${agent.name}",
                                            agentId = agentId
                                        )
                                    } else "local_${System.currentTimeMillis()}"

                                    currentSessionId = newId
                                    activeMessages = emptyList()
                                    currentScreen = Screen.Chat(newId, agentId)

                                    if (!initialPrompt.isNullOrBlank()) {
                                        // Execute first message
                                        val userMsg = ChatMessage(
                                            id = "msg_${System.currentTimeMillis()}",
                                            userId = authUser?.uid ?: "guest",
                                            sessionId = newId,
                                            role = "user",
                                            content = initialPrompt,
                                            agentId = agentId
                                        )
                                        activeMessages = listOf(userMsg)
                                        if (authUser != null) {
                                            firestoreRepository.addMessage(newId, "user", initialPrompt, agentId)
                                        }

                                        isGeneratingChat = true
                                        val accumulated = StringBuilder()
                                        val memoryList = memories.map { "${it.key}: ${it.value}" }
                                        aiService.streamChatResponse(
                                            agent = agent,
                                            userPrompt = initialPrompt,
                                            history = emptyList(),
                                            userMemories = memoryList
                                        ).collect { chunk ->
                                            accumulated.append(chunk)
                                            streamingText = accumulated.toString()
                                        }

                                        val modelMsg = ChatMessage(
                                            id = "msg_${System.currentTimeMillis() + 1}",
                                            userId = authUser?.uid ?: "guest",
                                            sessionId = newId,
                                            role = "model",
                                            content = accumulated.toString(),
                                            agentId = agentId
                                        )
                                        activeMessages = activeMessages + modelMsg
                                        if (authUser != null) {
                                            firestoreRepository.addMessage(newId, "model", accumulated.toString(), agentId)
                                        }
                                        streamingText = null
                                        isGeneratingChat = false
                                    }
                                }
                            },
                            onOpenSession = { sessionId ->
                                currentSessionId = sessionId
                                val session = recentSessions.find { it.id == sessionId }
                                currentAgentId = session?.agentId ?: "general"
                                coroutineScope.launch {
                                    if (authUser != null) {
                                        activeMessages = firestoreRepository.observeMessages(sessionId).firstOrNull() ?: emptyList()
                                    }
                                    currentScreen = Screen.Chat(sessionId, currentAgentId)
                                }
                            },
                            onDeleteSession = { sessionId ->
                                coroutineScope.launch {
                                    if (authUser != null) {
                                        firestoreRepository.deleteChatSession(sessionId)
                                    }
                                }
                            },
                            onOpenVoice = { currentScreen = Screen.Voice },
                            onOpenCreativeStudio = { currentScreen = Screen.CreativeStudio },
                            onOpenVision = { currentScreen = Screen.Vision },
                            onOpenMarketplace = { currentScreen = Screen.Marketplace },
                            onOpenMemory = { currentScreen = Screen.MemoryHistory },
                            onOpenSettings = { currentScreen = Screen.ProfileSettings }
                        )
                    }

                    is Screen.Chat -> {
                        val agent = AgentRegistry.getAgent(currentAgentId)
                        ChatScreen(
                            currentAgent = agent,
                            messages = activeMessages,
                            streamingText = streamingText,
                            isGenerating = isGeneratingChat,
                            onSendMessage = { prompt ->
                                coroutineScope.launch {
                                    val sessId = currentSessionId ?: "local_${System.currentTimeMillis()}"
                                    val userMsg = ChatMessage(
                                        id = "msg_${System.currentTimeMillis()}",
                                        userId = authUser?.uid ?: "guest",
                                        sessionId = sessId,
                                        role = "user",
                                        content = prompt,
                                        agentId = agent.id
                                    )
                                    activeMessages = activeMessages + userMsg
                                    if (authUser != null) {
                                        firestoreRepository.addMessage(sessId, "user", prompt, agent.id)
                                    }

                                    isGeneratingChat = true
                                    val accumulated = StringBuilder()
                                    val memoryList = memories.map { "${it.key}: ${it.value}" }
                                    aiService.streamChatResponse(
                                        agent = agent,
                                        userPrompt = prompt,
                                        history = activeMessages,
                                        userMemories = memoryList,
                                        customSystemInstruction = preferences?.systemInstruction ?: ""
                                    ).collect { chunk ->
                                        accumulated.append(chunk)
                                        streamingText = accumulated.toString()
                                    }

                                    val modelMsg = ChatMessage(
                                        id = "msg_${System.currentTimeMillis() + 1}",
                                        userId = authUser?.uid ?: "guest",
                                        sessionId = sessId,
                                        role = "model",
                                        content = accumulated.toString(),
                                        agentId = agent.id
                                    )
                                    activeMessages = activeMessages + modelMsg
                                    if (authUser != null) {
                                        firestoreRepository.addMessage(sessId, "model", accumulated.toString(), agent.id)
                                    }
                                    streamingText = null
                                    isGeneratingChat = false
                                }
                            },
                            onRegenerate = {
                                val lastUserMsg = activeMessages.lastOrNull { it.role == "user" }
                                if (lastUserMsg != null) {
                                    coroutineScope.launch {
                                        val sessId = currentSessionId ?: "local"
                                        isGeneratingChat = true
                                        val accumulated = StringBuilder()
                                        aiService.streamChatResponse(
                                            agent = agent,
                                            userPrompt = lastUserMsg.content,
                                            history = activeMessages.dropLast(1)
                                        ).collect { chunk ->
                                            accumulated.append(chunk)
                                            streamingText = accumulated.toString()
                                        }
                                        val modelMsg = ChatMessage(
                                            id = "msg_${System.currentTimeMillis()}",
                                            userId = authUser?.uid ?: "guest",
                                            sessionId = sessId,
                                            role = "model",
                                            content = accumulated.toString(),
                                            agentId = agent.id
                                        )
                                        activeMessages = activeMessages + modelMsg
                                        streamingText = null
                                        isGeneratingChat = false
                                    }
                                }
                            },
                            onDeleteMessage = { msgId ->
                                activeMessages = activeMessages.filterNot { it.id == msgId }
                                if (authUser != null && currentSessionId != null) {
                                    coroutineScope.launch {
                                        firestoreRepository.deleteMessage(currentSessionId!!, msgId)
                                    }
                                }
                            },
                            onSelectAgent = { newAgentId ->
                                currentAgentId = newAgentId
                            },
                            onSpeakMessage = { text ->
                                voiceManager.speak(text.replace(Regex("[*#`_]"), ""))
                            },
                            onVoiceInputClick = {
                                currentScreen = Screen.Voice
                            },
                            onBack = { currentScreen = Screen.Dashboard }
                        )
                    }

                    is Screen.Voice -> {
                        VoiceScreen(
                            voiceState = voiceState,
                            soundLevel = soundLevel,
                            transcription = transcription,
                            lastAiSpokenResponse = lastAiSpokenResponse,
                            selectedLanguage = voiceManager.selectedLanguage,
                            onLanguageSelected = { lang ->
                                voiceManager.selectedLanguage = lang
                            },
                            onStartListening = { voiceManager.startListening() },
                            onStopListening = { voiceManager.stopListening() },
                            onStopSpeaking = { voiceManager.stopSpeaking() },
                            onBack = { currentScreen = Screen.Dashboard }
                        )
                    }

                    is Screen.CreativeStudio -> {
                        CreativeStudioScreen(
                            creations = creations,
                            isGenerating = isGeneratingImage,
                            currentGeneratedBitmap = currentGeneratedBitmap,
                            onGenerateImage = { prompt, style, ratio ->
                                coroutineScope.launch {
                                    isGeneratingImage = true
                                    val result = aiService.generateImage(prompt, style)
                                    result.onSuccess { bmp ->
                                        currentGeneratedBitmap = bmp
                                    }.onFailure { err ->
                                        // Generate fallback canvas bitmap if quota limit
                                        currentGeneratedBitmap = createArtisticPlaceholder(prompt, style)
                                    }
                                    isGeneratingImage = false
                                }
                            },
                            onSaveCreation = { prompt, style, ratio, bitmap ->
                                coroutineScope.launch {
                                    if (authUser != null) {
                                        firestoreRepository.saveCreation(prompt, ratio, style, "local_stored")
                                    }
                                }
                            },
                            onDeleteCreation = { creationId ->
                                coroutineScope.launch {
                                    if (authUser != null) {
                                        firestoreRepository.deleteCreation(creationId)
                                    }
                                }
                            },
                            onBack = { currentScreen = Screen.Dashboard }
                        )
                    }

                    is Screen.Vision -> {
                        VisionScreen(
                            isAnalyzing = isAnalyzingVision,
                            analysisResult = visionAnalysisResult,
                            onAnalyzeImage = { bitmap, prompt, mode ->
                                coroutineScope.launch {
                                    isAnalyzingVision = true
                                    val result = aiService.analyzeImage(bitmap, prompt, mode)
                                    result.onSuccess { findings ->
                                        visionAnalysisResult = findings
                                    }.onFailure { err ->
                                        visionAnalysisResult = "⚡ **Optic Sensor Local Telemetry**\n\n- Detected Resolution: ${bitmap.width}x${bitmap.height} px\n- Primary Visual Tones: Cyan / Slate / Deep Navy\n- Mode Active: ${mode.uppercase()}\n\n*Error details: ${err.localizedMessage}*"
                                    }
                                    isAnalyzingVision = false
                                }
                            },
                            onBack = { currentScreen = Screen.Dashboard }
                        )
                    }

                    is Screen.Marketplace -> {
                        AgentMarketplaceScreen(
                            onSelectAgent = { agentId ->
                                currentAgentId = agentId
                                coroutineScope.launch {
                                    val agent = AgentRegistry.getAgent(agentId)
                                    val newId = if (authUser != null) {
                                        firestoreRepository.createChatSession(
                                            title = "Chat with ${agent.name}",
                                            agentId = agentId
                                        )
                                    } else "local_${System.currentTimeMillis()}"

                                    currentSessionId = newId
                                    activeMessages = emptyList()
                                    currentScreen = Screen.Chat(newId, agentId)
                                }
                            },
                            onBack = { currentScreen = Screen.Dashboard }
                        )
                    }

                    is Screen.MemoryHistory -> {
                        MemoryHistoryScreen(
                            sessions = recentSessions,
                            memories = memories,
                            onOpenSession = { sessionId ->
                                currentSessionId = sessionId
                                val session = recentSessions.find { it.id == sessionId }
                                currentAgentId = session?.agentId ?: "general"
                                coroutineScope.launch {
                                    if (authUser != null) {
                                        activeMessages = firestoreRepository.observeMessages(sessionId).firstOrNull() ?: emptyList()
                                    }
                                    currentScreen = Screen.Chat(sessionId, currentAgentId)
                                }
                            },
                            onDeleteSession = { sessionId ->
                                coroutineScope.launch {
                                    if (authUser != null) {
                                        firestoreRepository.deleteChatSession(sessionId)
                                    }
                                }
                            },
                            onAddMemory = { key, value, category ->
                                coroutineScope.launch {
                                    if (authUser != null) {
                                        firestoreRepository.addMemory(key, value, category)
                                    }
                                }
                            },
                            onDeleteMemory = { memId ->
                                coroutineScope.launch {
                                    if (authUser != null) {
                                        firestoreRepository.deleteMemory(memId)
                                    }
                                }
                            },
                            onClearAllMemories = {
                                coroutineScope.launch {
                                    if (authUser != null) {
                                        firestoreRepository.clearAllMemories(memories)
                                    }
                                }
                            },
                            onBack = { currentScreen = Screen.Dashboard }
                        )
                    }

                    is Screen.ProfileSettings -> {
                        ProfileSettingsScreen(
                            userEmail = authUser?.email ?: "",
                            userDisplayName = authUser?.displayName ?: "Guest Operator",
                            currentPreferences = preferences ?: UserPreference(),
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = onToggleTheme,
                            onSavePreferences = { newPref ->
                                coroutineScope.launch {
                                    if (authUser != null) {
                                        firestoreRepository.savePreferences(newPref)
                                    }
                                }
                            },
                            onSignOut = {
                                Firebase.auth.signOut()
                                currentScreen = Screen.Welcome
                            },
                            onBack = { currentScreen = Screen.Dashboard }
                        )
                    }

                    is Screen.Auth -> {
                        AuthScreen(
                            onAuthSuccess = {
                                currentScreen = Screen.Dashboard
                            },
                            onContinueGuest = {
                                currentScreen = Screen.Dashboard
                            },
                            onBack = { currentScreen = Screen.Dashboard }
                        )
                    }
                }
            }
        }
    }
}

private fun createArtisticPlaceholder(prompt: String, style: String): Bitmap {
    val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val paint = android.graphics.Paint().apply {
        isAntiAlias = true
    }

    // Gradient background
    paint.shader = android.graphics.LinearGradient(
        0f, 0f, 512f, 512f,
        android.graphics.Color.parseColor("#0A1128"),
        android.graphics.Color.parseColor("#00E5FF"),
        android.graphics.Shader.TileMode.CLAMP
    )
    canvas.drawRect(0f, 0f, 512f, 512f, paint)

    paint.shader = null
    paint.color = android.graphics.Color.WHITE
    paint.textSize = 28f
    paint.textAlign = android.graphics.Paint.Align.CENTER
    canvas.drawText("NOVA CREATIVE STUDIO", 256f, 220f, paint)

    paint.color = android.graphics.Color.parseColor("#00F5FF")
    paint.textSize = 20f
    canvas.drawText("Style: $style", 256f, 260f, paint)

    paint.color = android.graphics.Color.parseColor("#CFD8DC")
    paint.textSize = 16f
    canvas.drawText(prompt.take(35), 256f, 300f, paint)

    return bitmap
}
