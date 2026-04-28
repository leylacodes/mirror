package com.mirror.app.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mirror.app.AppContainer
import com.mirror.app.domain.model.AvatarState
import com.mirror.app.domain.model.AvatarType
import com.mirror.app.domain.model.MoodEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class HistoryUiState(
    val entries: List<MoodEntry> = emptyList(),
    val avatarType: AvatarType = AvatarType.TREE,
    val currentMonth: YearMonth = YearMonth.now(),
    val historicalStates: Map<LocalDate, AvatarState> = emptyMap()
)

class HistoryViewModel(private val container: AppContainer) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                container.moodRepository.observeAllEntries(),
                container.moodRepository.observeScars(),
                container.userPrefsDataStore.userPreferences
            ) { entries, scars, prefs ->
                val sorted = entries.sortedBy { it.date }
                val historicalStates = sorted.associate { entry ->
                    entry.date to container.computeAvatarStateUseCase.computeForDate(sorted, scars, entry.date)
                }
                HistoryUiState(
                    entries = sorted,
                    avatarType = prefs.avatarType,
                    currentMonth = YearMonth.now(),
                    historicalStates = historicalStates
                )
            }.collect { _uiState.value = it }
        }
    }

    fun nextMonth() {
        _uiState.value = _uiState.value.copy(currentMonth = _uiState.value.currentMonth.plusMonths(1))
    }

    fun prevMonth() {
        _uiState.value = _uiState.value.copy(currentMonth = _uiState.value.currentMonth.minusMonths(1))
    }
}

class HistoryViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HistoryViewModel(container) as T
    }
}
