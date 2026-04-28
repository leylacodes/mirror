package com.mirror.app.domain.model

data class UserPreferences(
    val onboardingComplete: Boolean,
    val avatarType: AvatarType,
    val notifHour: Int,
    val notifMinute: Int,
    val notifEnabled: Boolean
)
