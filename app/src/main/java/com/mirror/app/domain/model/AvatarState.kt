package com.mirror.app.domain.model

data class AvatarState(
    val moodScore: Int,
    val healthScore: Float,
    val recentAvg: Float,
    val isHibernating: Boolean,
    val scars: List<Scar>,
    val hasLowMoodCounter: Boolean,
    val lowMoodDays: Int
) {
    companion object {
        val Empty = AvatarState(
            moodScore = 3,
            healthScore = 0.5f,
            recentAvg = 0.5f,
            isHibernating = false,
            scars = emptyList(),
            hasLowMoodCounter = false,
            lowMoodDays = 0
        )
    }
}
