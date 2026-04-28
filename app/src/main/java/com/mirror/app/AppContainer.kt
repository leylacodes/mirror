package com.mirror.app

import android.content.Context
import androidx.room.Room
import com.mirror.app.data.local.MirrorDatabase
import com.mirror.app.data.preferences.UserPreferencesDataStore
import com.mirror.app.data.repository.MoodRepository
import com.mirror.app.data.repository.MoodRepositoryImpl
import com.mirror.app.domain.usecase.ComputeAvatarStateUseCase
import com.mirror.app.domain.usecase.ComputeScarUseCase
import com.mirror.app.domain.usecase.EditMoodEntryUseCase
import com.mirror.app.domain.usecase.GetAvatarMessageUseCase
import com.mirror.app.domain.usecase.GetKeywordSuggestionsUseCase
import com.mirror.app.domain.usecase.GetRecentEntriesUseCase
import com.mirror.app.domain.usecase.HandleInactivityUseCase
import com.mirror.app.domain.usecase.SaveMoodEntryUseCase

class AppContainer(context: Context) {

    val appContext: Context = context.applicationContext

    private val db = Room.databaseBuilder(
        context.applicationContext,
        MirrorDatabase::class.java,
        "mirror.db"
    ).build()

    val userPrefsDataStore = UserPreferencesDataStore(context.applicationContext)

    val moodRepository: MoodRepository = MoodRepositoryImpl(
        moodEntryDao = db.moodEntryDao(),
        scarDao = db.scarDao()
    )

    val computeScarUseCase = ComputeScarUseCase(moodRepository)
    val computeAvatarStateUseCase = ComputeAvatarStateUseCase()
    val saveMoodEntryUseCase = SaveMoodEntryUseCase(moodRepository, computeScarUseCase)
    val editMoodEntryUseCase = EditMoodEntryUseCase(moodRepository, computeScarUseCase)
    val handleInactivityUseCase = HandleInactivityUseCase(moodRepository, computeScarUseCase)
    val getRecentEntriesUseCase = GetRecentEntriesUseCase(moodRepository)
    val getKeywordSuggestionsUseCase = GetKeywordSuggestionsUseCase(moodRepository)
    val getAvatarMessageUseCase = GetAvatarMessageUseCase()
}
