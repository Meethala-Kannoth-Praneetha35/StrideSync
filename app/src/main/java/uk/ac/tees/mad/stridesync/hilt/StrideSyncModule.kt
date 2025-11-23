package uk.ac.tees.mad.stridesync.hilt

import android.content.Context
import androidx.room.Room
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import uk.ac.tees.mad.stridesync.data.local.AppDatabase

@Module
@InstallIn(SingletonComponent::class)
object StrideSyncModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext app: Context): AppDatabase =
        Room.databaseBuilder(app, AppDatabase::class.java, "steps_db").build()

    @Provides
    fun provideStepDao(db: AppDatabase) = db.stepDao()

    @Provides
    fun providesFirestore() : FirebaseFirestore = Firebase.firestore

    @Provides
    fun providesFirebaseAuthentication() : FirebaseAuth= Firebase.auth
}