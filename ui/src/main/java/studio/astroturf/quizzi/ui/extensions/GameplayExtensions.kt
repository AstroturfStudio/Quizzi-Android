import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object GameplayUtils {
    fun calculateScore(
        timeRemaining: Long,
        difficulty: Float,
        combo: Int
    ): Int {
        val baseScore = 100
        val timeBonus = (timeRemaining / 1000) * 10
        val difficultyMultiplier = difficulty * 1.5f
        val comboMultiplier = 1 + (combo * 0.1f)

        return (baseScore + timeBonus * difficultyMultiplier * comboMultiplier).toInt()
    }

    fun generateRewards(score: Int): List<Reward> {
        return buildList {
            if (score > 1000) add(Reward.GoldTrophy)
            else if (score > 500) add(Reward.SilverTrophy)
            else add(Reward.BronzeTrophy)

            if (score > 750) add(Reward.Coins(100))
            else add(Reward.Coins(50))
        }
    }
}

sealed class Reward {
    object GoldTrophy : Reward()
    object SilverTrophy : Reward()
    object BronzeTrophy : Reward()
    data class Coins(val amount: Int) : Reward()
}

fun Flow<Int>.countdownTimer(
    initialValue: Int,
    intervalMs: Long = 1000
): Flow<Int> = flow {
    var currentValue = initialValue
    while (currentValue > 0) {
        emit(currentValue)
        delay(intervalMs)
        currentValue--
    }
    emit(0)
} 