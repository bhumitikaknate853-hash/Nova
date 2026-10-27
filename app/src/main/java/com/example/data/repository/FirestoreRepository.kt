package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSession
import com.example.data.model.CreationItem
import com.example.data.model.MemoryItem
import com.example.data.model.UserPreference
import com.example.data.model.UserProfile
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreRepository(
    private val db: FirebaseFirestore
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun currentUserId(): String? = auth.currentUser?.uid

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing cloud Firestore.")
    }

    // --- User Profile ---

    suspend fun saveUserProfile(displayName: String, email: String, photoUrl: String = "") {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val payload = mapOf(
            "userId" to uid,
            "displayName" to displayName,
            "email" to email,
            "photoUrl" to photoUrl,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    // --- Chat Sessions ---

    fun observeChatSessions(): Flow<List<ChatSession>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/chat_sessions"
        val query = db.collection("users").document(uid).collection("chat_sessions")
            .orderBy("updatedAt", Query.Direction.DESCENDING)

        emitAll(
            query.snapshots()
                .map { snapshot -> snapshot.toObjects(ChatSession::class.java) }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun createChatSession(
        title: String,
        agentId: String,
        initialMessage: String = ""
    ): String {
        val uid = requireUserId()
        val sessionsRef = db.collection("users").document(uid).collection("chat_sessions")
        val newDoc = sessionsRef.document()
        val payload = mapOf(
            "id" to newDoc.id,
            "userId" to uid,
            "title" to title,
            "agentId" to agentId,
            "lastMessage" to initialMessage,
            "messageCount" to if (initialMessage.isNotBlank()) 1L else 0L,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            newDoc.set(payload).await()
            return newDoc.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, sessionsRef.path)
            throw e
        }
    }

    suspend fun deleteChatSession(sessionId: String) {
        val uid = requireUserId()
        val sessionRef = db.collection("users").document(uid).collection("chat_sessions").document(sessionId)
        try {
            sessionRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, sessionRef.path)
            throw e
        }
    }

    // --- Chat Messages ---

    fun observeMessages(sessionId: String): Flow<List<ChatMessage>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/chat_sessions/$sessionId/messages"
        val query = db.collection("users").document(uid).collection("chat_sessions")
            .document(sessionId).collection("messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)

        emitAll(
            query.snapshots()
                .map { snapshot -> snapshot.toObjects(ChatMessage::class.java) }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun addMessage(
        sessionId: String,
        role: String,
        content: String,
        agentId: String,
        hasImage: Boolean = false,
        imageUrl: String = ""
    ): String {
        val uid = requireUserId()
        val messagesRef = db.collection("users").document(uid).collection("chat_sessions")
            .document(sessionId).collection("messages")
        val newDoc = messagesRef.document()
        val payload = mapOf(
            "id" to newDoc.id,
            "userId" to uid,
            "sessionId" to sessionId,
            "role" to role,
            "content" to content,
            "agentId" to agentId,
            "hasImage" to hasImage,
            "imageUrl" to imageUrl,
            "createdAt" to FieldValue.serverTimestamp()
        )

        try {
            newDoc.set(payload).await()

            // Update parent session's lastMessage and timestamp
            val sessionRef = db.collection("users").document(uid).collection("chat_sessions").document(sessionId)
            val sessionUpdate = mapOf(
                "lastMessage" to content.take(150),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            sessionRef.update(sessionUpdate).await()

            return newDoc.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, messagesRef.path)
            throw e
        }
    }

    suspend fun deleteMessage(sessionId: String, messageId: String) {
        val uid = requireUserId()
        val msgRef = db.collection("users").document(uid).collection("chat_sessions")
            .document(sessionId).collection("messages").document(messageId)
        try {
            msgRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, msgRef.path)
            throw e
        }
    }

    // --- Memories ---

    fun observeMemories(): Flow<List<MemoryItem>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/memories"
        val query = db.collection("users").document(uid).collection("memories")
            .orderBy("updatedAt", Query.Direction.DESCENDING)

        emitAll(
            query.snapshots()
                .map { snapshot -> snapshot.toObjects(MemoryItem::class.java) }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun addMemory(key: String, value: String, category: String = "fact") {
        val uid = requireUserId()
        val memRef = db.collection("users").document(uid).collection("memories")
        val newDoc = memRef.document()
        val payload = mapOf(
            "id" to newDoc.id,
            "userId" to uid,
            "key" to key,
            "value" to value,
            "category" to category,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            newDoc.set(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, memRef.path)
            throw e
        }
    }

    suspend fun deleteMemory(memoryId: String) {
        val uid = requireUserId()
        val memDoc = db.collection("users").document(uid).collection("memories").document(memoryId)
        try {
            memDoc.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, memDoc.path)
            throw e
        }
    }

    suspend fun clearAllMemories(memories: List<MemoryItem>) {
        val uid = requireUserId()
        val batch = db.batch()
        for (item in memories) {
            val ref = db.collection("users").document(uid).collection("memories").document(item.id)
            batch.delete(ref)
        }
        try {
            batch.commit().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, "users/$uid/memories")
            throw e
        }
    }

    // --- Creative Creations ---

    fun observeCreations(): Flow<List<CreationItem>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/creations"
        val query = db.collection("users").document(uid).collection("creations")
            .orderBy("createdAt", Query.Direction.DESCENDING)

        emitAll(
            query.snapshots()
                .map { snapshot -> snapshot.toObjects(CreationItem::class.java) }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun saveCreation(prompt: String, aspectRatio: String, style: String, imageUrl: String): String {
        val uid = requireUserId()
        val creationsRef = db.collection("users").document(uid).collection("creations")
        val newDoc = creationsRef.document()
        val payload = mapOf(
            "id" to newDoc.id,
            "userId" to uid,
            "prompt" to prompt,
            "aspectRatio" to aspectRatio,
            "style" to style,
            "status" to "completed",
            "imageUrl" to imageUrl,
            "createdAt" to FieldValue.serverTimestamp()
        )
        try {
            newDoc.set(payload).await()
            return newDoc.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, creationsRef.path)
            throw e
        }
    }

    suspend fun deleteCreation(creationId: String) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("creations").document(creationId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    // --- Preferences ---

    fun observePreferences(): Flow<UserPreference?> = flow {
        val uid = requireUserId()
        val path = "users/$uid/preferences/profile"
        val docRef = db.collection("users").document(uid).collection("preferences").document("profile")

        emitAll(
            docRef.snapshots()
                .map { snapshot -> snapshot.toObject(UserPreference::class.java) }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.GET, path)
                    throw error
                }
        )
    }

    suspend fun savePreferences(pref: UserPreference) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("preferences").document("profile")
        val payload = mapOf(
            "id" to "profile",
            "userId" to uid,
            "voiceName" to pref.voiceName,
            "speechSpeed" to pref.speechSpeed,
            "language" to pref.language,
            "theme" to pref.theme,
            "systemInstruction" to pref.systemInstruction,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }
}

// Flow helper for emitAll
private suspend fun <T> kotlinx.coroutines.flow.FlowCollector<T>.emitAll(flow: Flow<T>) {
    flow.collect { value -> emit(value) }
}
