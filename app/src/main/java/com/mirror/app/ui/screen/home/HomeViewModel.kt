package com.mirror.app.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mirror.app.AppContainer
import com.mirror.app.domain.model.AvatarState
import com.mirror.app.domain.model.AvatarType
import com.mirror.app.domain.model.MoodEntry
import com.mirror.app.domain.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeUiState(
    val avatarState: AvatarState = AvatarState.Empty,
    val avatarType: AvatarType = AvatarType.TREE,
    val message: String = "",
    val recentEntries: List<MoodEntry> = emptyList(),
    val todayEntry: MoodEntry? = null,
    val userPreferences: UserPreferences = UserPreferences(false, AvatarType.TREE, 20, 0, false)
)

class HomeViewModel(private val container: AppContainer) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                container.moodRepository.observeAllEntries(),
                container.moodRepository.observeScars(),
                container.userPrefsDataStore.userPreferences
            ) { entries, scars, prefs ->
                val avatarState = container.computeAvatarStateUseCase.compute(entries, scars)
                val recent = entries.sortedByDescending { it.date }.take(7)
                val today = entries.find { it.date == LocalDate.now() }
                val message = container.getAvatarMessageUseCase.execute(avatarState, prefs.avatarType)
                HomeUiState(
                    avatarState = avatarState,
                    avatarType = prefs.avatarType,
                    message = message,
                    recentEntries = recent,
                    todayEntry = today,
                    userPreferences = prefs
                )
            }.collect { _uiState.value = it }
        }
    }
}

class HomeViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HomeViewModel(container) as T
    }
}
