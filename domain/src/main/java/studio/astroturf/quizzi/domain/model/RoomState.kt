package studio.astroturf.quizzi.domain.model

enum class RoomState {
    WAITING,
    COUNTDOWN,
    PLAYING,
    PAUSED,    // Yeni durum
    ROUND_END,
    FINISHED
} 