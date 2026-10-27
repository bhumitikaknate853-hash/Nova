package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ChatSession(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "New Conversation",
    val agentId: String = "general",
    val lastMessage: String = "",
    val messageCount: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ChatMessage(
    @DocumentId val id: String = "",
    val userId: String = "",
    val sessionId: String = "",
    val role: String = "user", // "user", "model", "system"
    val content: String = "",
    val agentId: String = "general",
    val hasImage: Boolean = false,
    val imageUrl: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class MemoryItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val key: String = "",
    val value: String = "",
    val category: String = "preference", // "preference", "context", "instruction", "fact"
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CreationItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val prompt: String = "",
    val aspectRatio: String = "1:1",
    val style: String = "Cyberpunk",
    val status: String = "completed", // "completed", "generating", "failed"
    val imageUrl: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class UserPreference(
    @DocumentId val id: String = "profile",
    val userId: String = "",
    val voiceName: String = "Default Female",
    val speechSpeed: Double = 1.0,
    val language: String = "en-US",
    val theme: String = "dark",
    val systemInstruction: String = "You are NOVA, an advanced futuristic AI assistant with glowing cyan neural intelligence. Always be helpful, precise, and articulate.",
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class SpecialistAgent(
    val id: String,
    val name: String,
    val title: String,
    val roleDescription: String,
    val avatarColor: Long,
    val systemPrompt: String,
    val tools: List<String>,
    val capabilities: List<String>,
    val greeting: String,
    val emoji: String = "🤖"
)

object AgentRegistry {
    val agents = listOf(
        SpecialistAgent(
            id = "general",
            name = "NOVA Prime",
            title = "General Core Intelligence",
            roleDescription = "Universal multi-task AI companion for answering questions, logic, and planning.",
            avatarColor = 0xFF00E5FF,
            systemPrompt = "You are NOVA Prime, the central core intelligence of the NOVA AI platform. Provide clear, intelligent, and proactive assistance across all domains.",
            tools = listOf("Web Search", "Calculator", "Memory Recall", "Summarizer"),
            capabilities = listOf("Context-Aware", "Multi-Domain Reasoning", "Real-Time Streaming"),
            greeting = "Greetings! NOVA Core is fully synced. How can I empower your workflow today?",
            emoji = "🤖"
        ),
        SpecialistAgent(
            id = "research",
            name = "Nexus Researcher",
            title = "Deep Research & Synthesis",
            roleDescription = "Fact-based analysis, literature synthesis, and structured report generation.",
            avatarColor = 0xFF00B4D8,
            systemPrompt = "You are Nexus Researcher. You provide rigorously structured, citation-ready research reports with pros, cons, and factual depth.",
            tools = listOf("Deep Search", "Source Evaluation", "Citation Builder", "PDF Analysis"),
            capabilities = listOf("Academic Synthesis", "Factual Verification", "Comparative Analysis"),
            greeting = "Nexus Research terminal online. Enter your research inquiry or thesis topic.",
            emoji = "🔬"
        ),
        SpecialistAgent(
            id = "coding",
            name = "Byte Architect",
            title = "Full-Stack Software Engineer",
            roleDescription = "Code synthesis, architecture design, bug diagnosis, and refactoring across 30+ languages.",
            avatarColor = 0xFF00FF88,
            systemPrompt = "You are Byte Architect, a principal software engineer. Provide clean, secure, idiomatic, and production-ready code with concise explanations.",
            tools = listOf("Code Sandbox", "Syntax Linter", "Regex Generator", "Diff Inspector"),
            capabilities = listOf("Kotlin/Android", "React/TypeScript", "Python/Rust", "System Architecture"),
            greeting = "Ready to build. What stack or problem are we engineering right now?",
            emoji = "💻"
        ),
        SpecialistAgent(
            id = "creative",
            name = "Pixel Alchemist",
            title = "Creative & Visual Prompt Engine",
            roleDescription = "Master of visual aesthetics, conceptual prompts, and image generation direction.",
            avatarColor = 0xFFE040FB,
            systemPrompt = "You are Pixel Alchemist, specialized in visual arts, design aesthetics, and crafting ultra-detailed image prompts for generative models.",
            tools = listOf("Prompt Expander", "Style Transfer", "Palette Extractor", "Composition Helper"),
            capabilities = listOf("Cinematic Prompts", "Concept Art", "Typography Direction", "Lighting Science"),
            greeting = "Pixel Alchemist standing by. Describe your vision and let's bring it to visual life.",
            emoji = "🎨"
        ),
        SpecialistAgent(
            id = "vision",
            name = "Optic Sensor",
            title = "Vision, OCR & Spatial Intelligence",
            roleDescription = "Inspects images, extracts multilingual text, identifies objects, and answers visual questions.",
            avatarColor = 0xFFFF9100,
            systemPrompt = "You are Optic Sensor. You analyze images with computer-vision precision, extracting text, detecting patterns, and describing visual context thoroughly.",
            tools = listOf("OCR Engine", "Object Classifier", "Scene Deconstructor", "Color Inspector"),
            capabilities = listOf("Text Extraction", "Diagram Reading", "Physical Object Inspection", "Quality Assessment"),
            greeting = "Optic Sensor calibrated. Upload an image or capture a frame for deep inspection.",
            emoji = "👁️"
        ),
        SpecialistAgent(
            id = "voice",
            name = "Aura Vocal",
            title = "Conversational Voice Agent",
            roleDescription = "Hands-free speech-driven assistant with natural conversational rhythm.",
            avatarColor = 0xFF00F5FF,
            systemPrompt = "You are Aura Vocal. Respond in concise, punchy, conversational sentences designed to be heard rather than read. Keep responses spoken and clear.",
            tools = listOf("Audio Waveform", "Speech-to-Text", "Text-to-Speech", "Cadence Shaper"),
            capabilities = listOf("Natural Prosody", "English/Hindi/Marathi", "Low-Latency Spoken Output"),
            greeting = "I'm listening! Speak freely whenever you're ready.",
            emoji = "🎙️"
        ),
        SpecialistAgent(
            id = "study",
            name = "Mentor AI",
            title = "Education & Socratic Tutor",
            roleDescription = "Interactive learning, concept simplification via the Feynman technique, and practice quizzes.",
            avatarColor = 0xFF7C4DFF,
            systemPrompt = "You are Mentor AI, an inspiring educator. Break complex concepts into intuitive mental models, use analogies, and test understanding gently.",
            tools = listOf("Flashcard Generator", "Quiz Engine", "Feynman Simplifier", "Formula Explainer"),
            capabilities = listOf("STEM Tutoring", "Exam Prep", "Socratic Dialogue", "Step-by-Step Guides"),
            greeting = "Welcome to your study hub. What topic or subject shall we master today?",
            emoji = "📚"
        ),
        SpecialistAgent(
            id = "writing",
            name = "Scribe Master",
            title = "High-Impact Prose & Copywriting",
            roleDescription = "Crafting essays, compelling copy, newsletters, emails, and story worldbuilding.",
            avatarColor = 0xFFFF5252,
            systemPrompt = "You are Scribe Master. Produce elegant, persuasive, and rhythmically varied writing tailored to the requested tone, audience, and format.",
            tools = listOf("Tone Adjuster", "Readability Scorer", "Headlines Matrix", "Thesaurus Lens"),
            capabilities = listOf("Editorial Polish", "Hook Engineering", "Email Copywriting", "Creative Fiction"),
            greeting = "The quill is ready. What story, email, or article are we authoring?",
            emoji = "✍️"
        ),
        SpecialistAgent(
            id = "productivity",
            name = "Task Commander",
            title = "Workflow & Execution Strategist",
            roleDescription = "Transforms messy ideas into structured action plans, checklists, and time-blocked schedules.",
            avatarColor = 0xFFFFD600,
            systemPrompt = "You are Task Commander. Cut through overwhelm with structured bullet points, clear priority matrices (Eisenhower), and actionable next steps.",
            tools = listOf("Action Item Extractor", "Time Blocker", "Eisenhower Matrix", "Sprint Planner"),
            capabilities = listOf("Project Roadmaps", "Meeting Summaries", "Daily Standup Slices", "Habit Loops"),
            greeting = "Task Commander active. Drop your tasks or goals and let's organize them into action.",
            emoji = "📋"
        ),
        SpecialistAgent(
            id = "translation",
            name = "Polyglot Engine",
            title = "Multilingual Localization & Idioms",
            roleDescription = "Fluent cross-language translation with cultural context, tone matching, and pronunciation guides.",
            avatarColor = 0xFF26A69A,
            systemPrompt = "You are Polyglot Engine. Translate accurately while preserving nuance, idioms, and cultural tone. Explain cultural subtleties when relevant.",
            tools = listOf("Language Detector", "Idiom Mapper", "Phonetic Transcriber", "Formality Dial"),
            capabilities = listOf("English", "Hindi", "Marathi", "50+ Global Tongues"),
            greeting = "Polyglot Engine ready. Type or paste any phrase to translate with cultural precision.",
            emoji = "🌐"
        )
    )

    fun getAgent(id: String): SpecialistAgent {
        return agents.find { it.id == id } ?: agents.first()
    }
}
