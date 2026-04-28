package com.mirror.app.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mirror.app.AppContainer
import com.mirror.app.domain.model.AvatarType
import com.mirror.app.domain.model.UserPreferences
import com.mirror.app.worker.DailyReminderWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class SettingsViewModel(private val container: AppContainer) : ViewModel() {

    private val _prefs = MutableStateFlow(
        UserPreferences(false, AvatarType.TREE, 20, 0, false)
    )
    val prefs = _prefs.asStateFlow()

    init {
        viewModelScope.launch {
            container.userPrefsDataStore.userPreferences.collect {
                _prefs.value = it
            }
        }
    }

    fun setNotifTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            container.userPrefsDataStore.setNotifTime(hour, minute)
            if (_prefs.value.notifEnabled) scheduleWork(hour, minute)
        }
    }

    fun setNotifEnabled(enabled: Boolean) {
        viewModelScope.launch {
            container.userPrefsDataStore.setNotifEnabled(enabled)
            if (enabled) {
                scheduleWork(_prefs.value.notifHour, _prefs.value.notifMinute)
            } else {
                WorkManager.getInstance(container.appContext).cancelUniqueWork(DailyReminderWorker.WORK_NAME)
            }
        }
    }

    fun setAvatarType(type: AvatarType) {
        viewModelScope.launch {
            container.userPrefsDataStore.setAvatarType(type)
        }
    }

    private fun scheduleWork(hour: Int, minute: Int) {
        val now = LocalTime.now()
        val target = LocalTime.of(hour, minute)
        val delay = if (target.isAfter(now)) {
            Duration.between(now, target).toMillis()
        } else {
            Duration.between(now, target).plusHours(24).toMillis()
        }

        val request = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(container.appContext).enqueueUniquePeriodicWork(
            DailyReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}

class SettingsViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SettingsViewModel(container) as T
    }
}
