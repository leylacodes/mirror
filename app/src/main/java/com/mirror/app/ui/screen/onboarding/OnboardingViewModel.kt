package com.mirror.app.ui.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mirror.app.AppContainer
import com.mirror.app.domain.model.AvatarType
import com.mirror.app.domain.model.MoodEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class OnboardingViewModel(private val container: AppContainer) : ViewModel() {

    private val _selectedScore = MutableStateFlow(3)
    val selectedScore = _selectedScore.asStateFlow()

    private val _selectedAvatar = MutableStateFlow(AvatarType.TREE)
    val selectedAvatar = _selectedAvatar.asStateFlow()

    fun setScore(score: Int) { _selectedScore.value = score }
    fun setAvatar(type: AvatarType) { _selectedAvatar.value = type }

    fun finish(onDone: () -> Unit) {
        viewModelScope.launch {
            container.userPrefsDataStore.setAvatarType(_selectedAvatar.value)
            container.saveMoodEntryUseCase.execute(
                MoodEntry(date = LocalDate.now(), score = _selectedScore.value)
            )
            container.userPrefsDataStore.setOnboardingComplete(true)
            onDone()
        }
    }
}

class OnboardingViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return OnboardingViewModel(container) as T
    }
}
