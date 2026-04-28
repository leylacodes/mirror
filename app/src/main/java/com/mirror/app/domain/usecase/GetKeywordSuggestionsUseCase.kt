package com.mirror.app.domain.usecase

import com.mirror.app.data.repository.MoodRepository

class GetKeywordSuggestionsUseCase(private val repository: MoodRepository) {

    private val defaultKeywords = listOf(
        "work", "family", "relationships", "illness", "politics",
        "sleep", "exercise", "food", "social", "weather"
    )

    suspend fun execute(score: Int): List<String> {
        val entries = repository.getAllEntries()
        val scoreEntries = entries.filter { it.score == score }

        val frequency = mutableMapOf<String, Int>()
        scoreEntries.forEach { entry ->
            entry.keywords.forEach { kw ->
                frequency[kw] = (frequency[kw] ?: 0) + 1
            }
        }

        val sorted = frequency.entries.sortedByDescending { it.value }.map { it.key }
        val extras = defaultKeywords.filter { it !in sorted }
        return (sorted + extras).distinct()
    }
}
