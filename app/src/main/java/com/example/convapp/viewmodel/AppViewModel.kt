package com.example.convapp.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.speech.SpeechRecognizer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.convapp.data.ProgressRepository
import com.example.convapp.data.ScenarioRepository
import com.example.convapp.data.SettingsRepository
import com.example.convapp.engine.ConversationEngine
import com.example.convapp.model.AppSettings
import com.example.convapp.model.Node
import com.example.convapp.model.Scenario
import com.example.convapp.speech.SpeechManager

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val scenarioRepo = ScenarioRepository(application)
    private val progressRepo = ProgressRepository(application)
    private val settingsRepo = SettingsRepository(application)
    private val speechManager = SpeechManager(application)
    private val engine = ConversationEngine(scenarioRepo.getNodes())
    private val handler = Handler(Looper.getMainLooper())

    // ── Settings ────────────────────────────────────────────────────────────────
    private val _settings = MutableLiveData(settingsRepo.load())
    val settings: LiveData<AppSettings> = _settings

    // ── Active conversation ──────────────────────────────────────────────────────
    private val _currentNode = MutableLiveData<Node?>()
    val currentNode: LiveData<Node?> = _currentNode

    private val _currentScenario = MutableLiveData<Scenario?>()
    val currentScenario: LiveData<Scenario?> = _currentScenario

    private val _micState = MutableLiveData(MicState.IDLE)
    val micState: LiveData<MicState> = _micState

    private val _transcript = MutableLiveData("")
    val transcript: LiveData<String> = _transcript

    /** Current prompt step number (1-based), resets per scenario. */
    private val _stepProgress = MutableLiveData(0)
    val stepProgress: LiveData<Int> = _stepProgress

    /** Stars earned in the most recent completed scenario session. */
    private val _sessionStars = MutableLiveData(0)
    val sessionStars: LiveData<Int> = _sessionStars

    /** One-shot event: navigate to Results screen. */
    private val _navigateToResults = MutableLiveData(false)
    val navigateToResults: LiveData<Boolean> = _navigateToResults

    /** True once TTS engine is initialized. */
    private val _ttsReady = MutableLiveData(false)
    val ttsReady: LiveData<Boolean> = _ttsReady

    /** True if recognition hardware is available on this device. */
    private val _sttAvailable = MutableLiveData(false)
    val sttAvailable: LiveData<Boolean> = _sttAvailable

    private var hintMode = false
    private var promptStepCounter = 0

    // ── Init ─────────────────────────────────────────────────────────────────────
    init {
        engine.onNavigate = { node ->
            handler.post { onNewNode(node) }
        }

        speechManager.onSpeechDone = {
            handler.post { onTtsDone() }
        }

        speechManager.onResult = { alternatives ->
            handler.post { onSttResult(alternatives) }
        }

        speechManager.onSpeechError = { error ->
            handler.post { onSttError(error) }
        }

        speechManager.init { ready ->
            _ttsReady.postValue(ready)
            _sttAvailable.postValue(speechManager.isRecognitionAvailable())
            // Apply saved settings to TTS
            val s = _settings.value!!
            speechManager.speechRate = s.speechRate
            speechManager.pitch = s.pitch
        }
    }

    // ── Public API ───────────────────────────────────────────────────────────────

    fun loadScenario(scenarioId: String) {
        val scenario = scenarioRepo.getScenario(scenarioId) ?: return
        _currentScenario.value = scenario
        _sessionStars.value = 0
        promptStepCounter = 0
        hintMode = false
        engine.reset()
        engine.loadScenario(scenario.startNode)
    }

    /** Called when mic button is tapped. */
    fun handleMicTap() {
        when (_micState.value) {
            MicState.IDLE, MicState.ERROR -> doStartListening()
            MicState.LISTENING -> {
                speechManager.stopListening()
                _micState.value = MicState.IDLE
            }
            else -> {}
        }
    }

    /** Speak hint aloud without consuming a retry. */
    fun handleHintRequested() {
        val hint = _currentNode.value?.hint ?: return
        hintMode = true
        speechManager.cancelSpeech()
        speechManager.speak(hint)
    }

    fun updateSettings(newSettings: AppSettings) {
        _settings.value = newSettings
        settingsRepo.save(newSettings)
        speechManager.speechRate = newSettings.speechRate
        speechManager.pitch = newSettings.pitch
    }

    /** Reset the navigate-to-results one-shot event after navigation. */
    fun onNavigateToResultsHandled() {
        _navigateToResults.value = false
    }

    // ── Data accessors for fragments ────────────────────────────────────────────
    fun getScenarios(): List<Scenario> = scenarioRepo.getScenarios()
    fun getTotalStars(): Int = progressRepo.getTotalStars()
    fun isScenarioCompleted(id: String): Boolean = progressRepo.isScenarioCompleted(id)
    fun resetAllProgress() = progressRepo.resetAll()

    // ── Internal handlers ────────────────────────────────────────────────────────

    private fun onNewNode(node: Node) {
        _currentNode.value = node
        if (node.type == "prompt") {
            promptStepCounter++
            _stepProgress.value = promptStepCounter
        }
        _micState.value = MicState.IDLE
        speechManager.speak(node.say)
    }

    private fun onTtsDone() {
        if (hintMode) {
            hintMode = false
            _micState.value = MicState.IDLE
            return
        }

        val node = _currentNode.value ?: return
        when (node.type) {
            "prompt" -> {
                if (_settings.value?.autoMic == true) {
                    doStartListening()
                }
                // else: wait for mic button tap
            }
            "teach" -> handler.postDelayed({ engine.advanceFromAutoNode() }, 1500)
            "bridge" -> handler.postDelayed({ engine.advanceFromAutoNode() }, 500)
            "celebrate" -> {
                _sessionStars.value = (_sessionStars.value ?: 0) + 1
                progressRepo.addStar()
                progressRepo.markScenarioCompleted(_currentScenario.value?.id ?: "")
                handler.postDelayed({
                    _navigateToResults.value = true
                }, 2500)
            }
        }
    }

    private fun doStartListening() {
        _micState.value = MicState.LISTENING
        _transcript.value = ""
        speechManager.startListening()
    }

    private fun onSttResult(alternatives: List<String>) {
        _micState.value = MicState.PROCESSING
        if (alternatives.isNotEmpty()) _transcript.value = alternatives[0]
        engine.handleSpeechResult(alternatives)
    }

    private fun onSttError(error: Int) {
        when (error) {
            SpeechRecognizer.ERROR_NO_MATCH,
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                _micState.value = MicState.IDLE
                engine.handleNoSpeech()
            }
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                _micState.value = MicState.ERROR
            }
            else -> {
                // Network or audio errors: treat as no-speech (gentle retry)
                _micState.value = MicState.IDLE
                engine.handleNoSpeech()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.release()
        handler.removeCallbacksAndMessages(null)
    }
}
