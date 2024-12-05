package studio.astroturf.quizzi.ui.di

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageModule {
    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
    ): ImageLoader =
        ImageLoader
            .Builder(context)
            .diskCache {
                DiskCache
                    .Builder()
                    .directory(context.cacheDir.resolve("flag_images"))
                    .maxSizePercent(0.05)
                    .build()
            }.memoryCache {
                MemoryCache
                    .Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }.crossfade(true)
            .build()
}
