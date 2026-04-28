package com.mirror.app.domain.usecase

import com.mirror.app.domain.model.AvatarState
import com.mirror.app.domain.model.AvatarType

class GetAvatarMessageUseCase {

    fun execute(state: AvatarState, avatarType: AvatarType): String {
        if (state.isHibernating) {
            return when (avatarType) {
                AvatarType.TREE -> "You've been away for a while. Even trees need tending. How are you feeling today?"
                AvatarType.ROBOT -> "System hibernation detected. Time to reboot and run diagnostics. How are you feeling?"
                AvatarType.WATER_BUCKET -> "The water has gone still. Let's stir things up again. How are you feeling?"
                AvatarType.PILE_OF_SPOONS -> "The spoons have been gathering dust. Let's count them again. How are you feeling?"
            }
        }

        if (state.hasLowMoodCounter) {
            return when (avatarType) {
                AvatarType.TREE -> "You've been running low for ${state.lowMoodDays} days. Even the mightiest trees need water and sunlight. Have you had some today?"
                AvatarType.ROBOT -> "Warning: ${state.lowMoodDays} days of low charge detected. Time for some maintenance and maybe a little oil."
                AvatarType.WATER_BUCKET -> "The water's been murky for ${state.lowMoodDays} days. What could help clear things up?"
                AvatarType.PILE_OF_SPOONS -> "Your spoon count has been low for ${state.lowMoodDays} days. Remember: even one spoon is a start."
            }
        }

        return when {
            state.moodScore >= 4 -> getPositiveMessage(avatarType, state.moodScore)
            state.moodScore == 3 -> getNeutralMessage(avatarType)
            state.moodScore in 1..2 -> getLowMessage(avatarType)
            else -> getDefaultMessage(avatarType)
        }
    }

    private fun getPositiveMessage(avatarType: AvatarType, score: Int): String {
        val excellent = score == 5
        return when (avatarType) {
            AvatarType.TREE -> if (excellent) "Radiant! Your leaves are practically glowing today." else "Looking green and lush. Keep soaking up the good stuff."
            AvatarType.ROBOT -> if (excellent) "All systems optimal! You're running at peak performance." else "Gears turning smoothly. Good diagnostics today."
            AvatarType.WATER_BUCKET -> if (excellent) "Crystal clear and brimming! You're positively sparkling." else "Looking clear and full. Good flow today."
            AvatarType.PILE_OF_SPOONS -> if (excellent) "A towering pile! You've got spoons to spare today." else "A solid stack. You've got what you need."
        }
    }

    private fun getNeutralMessage(avatarType: AvatarType): String = when (avatarType) {
        AvatarType.TREE -> "Steady in the breeze. Not every day needs to be sunny."
        AvatarType.ROBOT -> "Running within normal parameters. Steady as she goes."
        AvatarType.WATER_BUCKET -> "Still waters. A calm day is still a good day."
        AvatarType.PILE_OF_SPOONS -> "A manageable pile. You've got enough for what today needs."
    }

    private fun getLowMessage(avatarType: AvatarType): String = when (avatarType) {
        AvatarType.TREE -> "A bit droopy today. Some water and shade might help."
        AvatarType.ROBOT -> "Running low on fuel. Maybe some rest and recharging is in order."
        AvatarType.WATER_BUCKET -> "The water's a bit murky today. That's okay — it can clear."
        AvatarType.PILE_OF_SPOONS -> "Running low on spoons. Be gentle with yourself today."
    }

    private fun getDefaultMessage(avatarType: AvatarType): String = when (avatarType) {
        AvatarType.TREE -> "How are the roots feeling today?"
        AvatarType.ROBOT -> "Ready for today's diagnostic?"
        AvatarType.WATER_BUCKET -> "How are the waters today?"
        AvatarType.PILE_OF_SPOONS -> "How's your spoon count looking?"
    }
}
