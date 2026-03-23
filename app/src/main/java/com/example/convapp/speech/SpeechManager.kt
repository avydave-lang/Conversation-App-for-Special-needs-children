package com.example.convapp.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale
import java.util.UUID

class SpeechManager(private val context: Context) {

    private var tts: TextToSpeech? = null
    private var recognizer: SpeechRecognizer? = null
    private var ttsReady = false

    var speechRate: Float = 0.85f
        set(value) {
            field = value
            tts?.setSpeechRate(value)
        }

    var pitch: Float = 1.1f
        set(value) {
            field = value
            tts?.setPitch(value)
        }

    // Callbacks — set by ViewModel
    var onSpeechDone: (() -> Unit)? = null
    var onSpeechStart: (() -> Unit)? = null
    var onResult: ((List<String>) -> Unit)? = null
    var onSpeechError: ((Int) -> Unit)? = null

    /**
     * Must be called on the main thread.
     * onReady(true) = TTS ready, onReady(false) = TTS failed.
     */
    fun init(onReady: (Boolean) -> Unit) {
        tts = TextToSpeech(context) { status ->
            ttsReady = (status == TextToSpeech.SUCCESS)
            if (ttsReady) {
                tts?.language = Locale.US
                tts?.setSpeechRate(speechRate)
                tts?.setPitch(pitch)
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        onSpeechStart?.invoke()
                    }
                    override fun onDone(utteranceId: String?) {
                        onSpeechDone?.invoke()
                    }
                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        onSpeechDone?.invoke() // treat error as done so app doesn't freeze
                    }
                })
            }
            onReady(ttsReady)
        }

        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(context)
            recognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}

                override fun onError(error: Int) {
                    onSpeechError?.invoke(error)
                }

                override fun onResults(results: Bundle?) {
                    val alternatives = results
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?: arrayListOf()
                    onResult?.invoke(alternatives)
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    fun speak(text: String) {
        if (!ttsReady) return
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString())
    }

    fun cancelSpeech() {
        tts?.stop()
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1000L)
        }
        recognizer?.startListening(intent)
    }

    fun stopListening() {
        recognizer?.stopListening()
    }

    fun isRecognitionAvailable(): Boolean =
        SpeechRecognizer.isRecognitionAvailable(context)

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        recognizer?.destroy()
        recognizer = null
    }
}
