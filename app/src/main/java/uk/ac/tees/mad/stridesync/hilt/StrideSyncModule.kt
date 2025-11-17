package uk.ac.tees.mad.stridesync.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object StrideSyncModule {

    @Provides
    fun providesFirestore = Firebase.firestore
}