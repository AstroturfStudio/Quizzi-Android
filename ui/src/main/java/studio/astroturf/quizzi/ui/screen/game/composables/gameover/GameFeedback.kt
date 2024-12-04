package studio.astroturf.quizzi.ui.screen.game.composables.gameover

data class GameFeedback(
    val enjoymentRating: Int,
    val difficultyRating: Int,
    val additionalFeedback: String?,
    val bugReport: BugReport?,
)

data class BugReport(
    val description: String,
    val deviceInfo: DeviceInfo,
    val gameState: GameState,
)

data class DeviceInfo(
    val manufacturer: String = android.os.Build.MANUFACTURER,
    val model: String = android.os.Build.MODEL,
    val androidVersion: String = android.os.Build.VERSION.RELEASE,
    val appVersion: String,
)

data class GameState(
    val gameId: String,
    val roundCount: Int,
    val players: List<String>,
)
