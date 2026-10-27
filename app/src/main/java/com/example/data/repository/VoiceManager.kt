package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

private const val TAG = "VoiceManager"

sealed interface VoiceState {
    data object Idle : VoiceState
    data object Listening : VoiceState
    data class Speaking(val text: String) : VoiceState
    data class Error(val message: String) : VoiceState
}

class VoiceManager(private val context: Context) : RecognitionListener, TextToSpeech.OnInitListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    private val _voiceState = MutableStateFlow<VoiceState>(VoiceState.Idle)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _soundLevel = MutableStateFlow(0f)
    val soundLevel: StateFlow<Float> = _soundLevel.asStateFlow()

    private val _transcription = MutableStateFlow("")
    val transcription: StateFlow<String> = _transcription.asStateFlow()

    var selectedLanguage: String = "en-US"
    var speechRate: Float = 1.0f

    var onSpeechRecognized: ((String) -> Unit)? = null

    init {
        initializeTts()
    }

    private fun initializeTts() {
        textToSpeech = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            updateTtsLocale()
            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _voiceState.value = VoiceState.Speaking(transcription.value)
                }

                override fun onDone(utteranceId: String?) {
                    _voiceState.value = VoiceState.Idle
                }

                override fun onError(utteranceId: String?) {
                    _voiceState.value = VoiceState.Idle
                }
            })
        } else {
            Log.e(TAG, "TTS initialization failed: $status")
        }
    }

    fun startListening() {
        stopSpeaking()
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _voiceState.value = VoiceState.Error("Speech recognition is not available on this device.")
            return
        }

        try {
            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(this@VoiceManager)
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, getLocaleForLanguage(selectedLanguage).toLanguageTag())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }

            speechRecognizer?.startListening(intent)
            _voiceState.value = VoiceState.Listening
            _transcription.value = ""
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start speech recognition", e)
            _voiceState.value = VoiceState.Error("Could not start microphone: ${e.localizedMessage}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            _voiceState.value = VoiceState.Idle
            _soundLevel.value = 0f
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping listener", e)
        }
    }

    fun speak(text: String) {
        if (!isTtsReady || textToSpeech == null) {
            Log.w(TAG, "TTS not ready yet")
            return
        }

        stopListening()
        updateTtsLocale()
        textToSpeech?.setSpeechRate(speechRate)

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "nova_voice_msg_${System.currentTimeMillis()}")
        }

        _voiceState.value = VoiceState.Speaking(text)
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "nova_voice_msg")
    }

    fun stopSpeaking() {
        if (textToSpeech?.isSpeaking == true) {
            textToSpeech?.stop()
        }
        if (_voiceState.value is VoiceState.Speaking) {
            _voiceState.value = VoiceState.Idle
        }
    }

    private fun updateTtsLocale() {
        val targetLocale = getLocaleForLanguage(selectedLanguage)
        val result = textToSpeech?.setLanguage(targetLocale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.w(TAG, "Target locale $selectedLanguage not supported in TTS, falling back to US English")
            textToSpeech?.setLanguage(Locale.US)
        }
    }

    private fun getLocaleForLanguage(code: String): Locale {
        return when (code) {
            "hi-IN" -> Locale("hi", "IN")
            "mr-IN" -> Locale("mr", "IN")
            "fr-FR" -> Locale.FRANCE
            "es-ES" -> Locale("es", "ES")
            "de-DE" -> Locale.GERMANY
            else -> Locale.US
        }
    }

    fun destroy() {
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            textToSpeech = null
        } catch (e: Exception) {
            Log.e(TAG, "Error during destroy", e)
        }
    }

    // RecognitionListener Callbacks
    override fun onReadyForSpeech(params: Bundle?) {
        _voiceState.value = VoiceState.Listening
    }

    override fun onBeginningOfSpeech() {}

    override fun onRmsChanged(rmsdB: Float) {
        // Normalize rmsdB for wave animation (typically -2 to 10 dB)
        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.1f, 1.0f)
        _soundLevel.value = normalized
    }

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        _voiceState.value = VoiceState.Idle
        _soundLevel.value = 0f
    }

    override fun onError(error: Int) {
        val message = when (error) {
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Try speaking again."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Listening timed out."
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required."
            else -> "Speech recognition error ($error)"
        }
        _voiceState.value = VoiceState.Error(message)
        _soundLevel.value = 0f
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val recognizedText = matches?.firstOrNull() ?: ""
        _transcription.value = recognizedText
        _voiceState.value = VoiceState.Idle
        _soundLevel.value = 0f
        if (recognizedText.isNotBlank()) {
            onSpeechRecognized?.invoke(recognizedText)
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val text = matches?.firstOrNull() ?: ""
        if (text.isNotBlank()) {
            _transcription.value = text
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}
}
