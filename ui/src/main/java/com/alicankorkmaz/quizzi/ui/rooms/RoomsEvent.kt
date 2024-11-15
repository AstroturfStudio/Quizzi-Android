package com.alicankorkmaz.quizzi.ui.rooms

sealed interface RoomsEvent {
    data class NavigateToRoom(val roomId: String) : RoomsEvent
}