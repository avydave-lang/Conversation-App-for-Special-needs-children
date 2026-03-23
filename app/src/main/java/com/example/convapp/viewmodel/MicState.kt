package com.example.convapp.viewmodel

enum class MicState {
    IDLE,        // waiting for user to tap
    LISTENING,   // mic open, waiting for speech
    PROCESSING,  // speech received, engine evaluating
    ERROR        // recognition not available
}
