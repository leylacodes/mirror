package com.mirror.app.domain.model

import java.time.LocalDate

data class MoodEntry(
    val date: LocalDate,
    val score: Int,
    val keywords: List<String> = emptyList(),
    val isBackfilled: Boolean = false
)
