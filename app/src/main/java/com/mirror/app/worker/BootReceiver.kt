package com.mirror.app.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mirror.app.MirrorApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val container = (context.applicationContext as MirrorApp).container
        CoroutineScope(Dispatchers.IO).launch {
            val prefs = container.userPrefsDataStore.userPreferences.first()
            if (!prefs.notifEnabled) return@launch

            val now = LocalTime.now()
            val target = LocalTime.of(prefs.notifHour, prefs.notifMinute)
            val delay = if (target.isAfter(now)) {
                Duration.between(now, target).toMillis()
            } else {
                Duration.between(now, target).plusHours(24).toMillis()
            }

            val request = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                DailyReminderWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }
}
