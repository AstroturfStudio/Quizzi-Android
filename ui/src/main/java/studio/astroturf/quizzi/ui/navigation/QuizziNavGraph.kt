package studio.astroturf.quizzi.ui.navigation

sealed class QuizziNavGraph(
    val route: String,
) {
    data object CreateRoom : QuizziNavGraph("Graph_CreateRoom")
}
