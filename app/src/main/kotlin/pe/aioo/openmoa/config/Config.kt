package pe.aioo.openmoa.config

data class Config (
    val longPressRepeatTime: Long = 50L,
    val longPressThresholdTime: Long = 500L,
    val gestureThreshold: Float = 50f,
    val spaceCursorHoldTime: Long = 400L,
    // Horizontal travel per cursor step after holding space.
    val spaceCursorStepDp: Float = 12f,
    val hapticFeedback: Boolean = true,
    val maxSuggestionCount: Int = 10,
)
