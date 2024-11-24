package studio.astroturf.quizzi.domain.model.statemachine

interface StateMachine<State, Intent, Effect> {
    fun getCurrentState(): State
    fun reduce(intent: Intent)
    fun effect(effect: Effect)
}