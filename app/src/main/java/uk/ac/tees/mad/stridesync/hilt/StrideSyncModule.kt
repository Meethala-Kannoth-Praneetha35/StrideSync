package uk.ac.tees.mad.stridesync.hilt

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object StrideSyncModule {

    @Provides
    fun providesFirestore() : FirebaseFirestore = Firebase.firestore

    @Provides
    fun providesFirebaseAuthentication() : FirebaseAuth= Firebase.auth
}