package com.mirror.app.ui.screen.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mirror.app.AppContainer
import com.mirror.app.domain.model.MoodEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class CheckInUiState(
    val date: LocalDate = LocalDate.now(),
    val score: Int = 3,
    val selectedKeywords: Set<String> = emptySet(),
    val suggestions: List<String> = emptyList(),
    val isEditingExisting: Boolean = false,
    val isHibernationReassessment: Boolean = false
)

class CheckInViewModel(private val container: AppContainer, private val dateStr: String) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckInUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            if (dateStr == "reassessment") {
                _uiState.value = _uiState.value.copy(isHibernationReassessment = true)
                loadSuggestions(3)
            } else {
                val date = runCatching { LocalDate.parse(dateStr) }.getOrDefault(LocalDate.now())
                val existing = container.moodRepository.getEntryForDate(date)
                _uiState.value = _uiState.value.copy(
                    date = date,
                    score = existing?.score ?: 3,
                    selectedKeywords = existing?.keywords?.toSet() ?: emptySet(),
                    isEditingExisting = existing != null
                )
                loadSuggestions(existing?.score ?: 3)
            }
        }
    }

    fun setScore(score: Int) {
        _uiState.value = _uiState.value.copy(score = score)
        viewModelScope.launch { loadSuggestions(score) }
    }

    fun toggleKeyword(keyword: String) {
        val current = _uiState.value.selectedKeywords
        _uiState.value = _uiState.value.copy(
            selectedKeywords = if (keyword in current) current - keyword else current + keyword
        )
    }

    fun save(onDone: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.isHibernationReassessment) {
                container.handleInactivityUseCase.execute(state.score)
            } else {
                val entry = MoodEntry(
                    date = state.date,
                    score = state.score,
                    keywords = state.selectedKeywords.toList()
                )
                if (state.isEditingExisting) {
                    container.editMoodEntryUseCase.execute(entry)
                } else {
                    container.saveMoodEntryUseCase.execute(entry)
                }
            }
            onDone()
        }
    }

    private suspend fun loadSuggestions(score: Int) {
        val suggestions = container.getKeywordSuggestionsUseCase.execute(score)
        _uiState.value = _uiState.value.copy(suggestions = suggestions)
    }
}

class CheckInViewModelFactory(private val container: AppContainer, private val date: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CheckInViewModel(container, date) as T
    }
}
