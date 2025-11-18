package uk.ac.tees.mad.stridesync.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.stridesync.model.User

@HiltViewModel
class AuthViewModel @Inject constructor(
    val authentication : FirebaseAuth,
    val firestore : FirebaseFirestore
) : ViewModel() {

    init {

    }

    val loading = mutableStateOf(false)

    fun getCurrentUserInformation(){

    }

    fun login(context: Context, email : String, password : String){
        loading.value = true
        viewModelScope.launch {
            authentication.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    loading.value = false
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                    getCurrentUserInformation()
                }
                .addOnFailureListener {
                    loading.value = false
                    Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun signUp(context : Context, email: String, password: String){
        loading.value = true
        viewModelScope.launch {
            authentication.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    firestore.collection("users").document(it.user!!.uid).set(
                        User(
                            email = email,
                            password = password
                        )
                    ).addOnSuccessListener {
                        loading.value = false
                        Toast.makeText(context, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                        getCurrentUserInformation()
                    }.addOnFailureListener {
                        loading.value = false
                        Toast.makeText(context, "Sign Up Failed", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    loading.value = false
                    Toast.makeText(context, "Sign Up Failed", Toast.LENGTH_SHORT).show()
                }
        }

    }
}