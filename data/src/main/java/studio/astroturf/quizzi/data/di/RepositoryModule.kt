package studio.astroturf.quizzi.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import studio.astroturf.quizzi.data.repository.FirebaseFeedbackRepository
import studio.astroturf.quizzi.domain.repository.FeedbackRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideFeedbackRepository(): FeedbackRepository = FirebaseFeedbackRepository()
}
