package studio.astroturf.quizzi.ui.navigation

sealed class QuizziNavDestination(
    val route: String,
) {
    data object Onboarding : QuizziNavDestination("onboarding")

    data object Landing : QuizziNavDestination("landing")

    data object Search : QuizziNavDestination("search")

    data object Statistics : QuizziNavDestination("statistics")

    data object Profile : QuizziNavDestination("profile")

    data object Rooms : QuizziNavDestination("rooms")

    data object CreateRoom : QuizziNavDestination("createRoom") {
        const val ARG_SELECTED_QUIZ_CATEGORY = "selectedQuizCategory"
        const val ARG_SELECTED_GAME_TYPE = "selectedGameType"
    }

    data object CategorySelection : QuizziNavDestination("categorySelection")

    data object GameTypeSelection : QuizziNavDestination("gameTypeSelection")

    object Game : QuizziNavDestination("game") {
        const val ROUTE_PATTERN = "game?roomId={roomId}"
        const val ARG_ROOM_ID = "roomId"

        fun createRoute(roomId: String? = null): String = if (roomId != null) "game?roomId=$roomId" else "game"
    }
}
