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

    object Game : QuizziNavDestination("game?roomId={roomId}&roomName={roomName}&categoryId={categoryId}&gameType={gameType}") {
        const val ARG_ROOM_ID = "roomId"
        const val ARG_ROOM_NAME = "roomName"
        const val ARG_CATEGORY_ID = "categoryId"
        const val ARG_GAME_TYPE = "gameType"

        fun createRouteForCreating(
            roomName: String,
            categoryId: String,
            gameType: String,
        ): String = "game?$ARG_ROOM_NAME=$roomName&$ARG_CATEGORY_ID=$categoryId&$ARG_GAME_TYPE=$gameType"

        fun createRouteForJoining(
            roomId: String,
            roomName: String,
        ): String = "game?$ARG_ROOM_ID=$roomId&$ARG_ROOM_NAME=$roomName"
    }
}
