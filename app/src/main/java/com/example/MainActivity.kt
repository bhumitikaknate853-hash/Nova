package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.repository.AiService
import com.example.data.repository.FirestoreRepository
import com.example.data.repository.VoiceManager
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.NovaAiTheme
import com.example.util.configureAppCheck

class MainActivity : ComponentActivity() {

    private lateinit var voiceManager: VoiceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Mandatory App Check configuration with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        val firestoreRepository = FirestoreRepository(this)
        val aiService = AiService()
        voiceManager = VoiceManager(this)

        setContent {
            var isDarkTheme by remember { mutableStateOf(true) }

            NovaAiTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(
                        firestoreRepository = firestoreRepository,
                        aiService = aiService,
                        voiceManager = voiceManager,
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = { isDarkTheme = it }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::voiceManager.isInitialized) {
            voiceManager.destroy()
        }
    }
}
