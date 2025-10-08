package com.eliel.studytrack.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eliel.studytrack.data.firestore.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()


    fun registerUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        viewModelScope.launch {
                            try {
                                UserRepository.createUser(
                                    uid = user.uid,
                                    name = user.displayName ?: email.substringBefore("@"),
                                    email = email
                                )
                                onResult(true, null)
                            } catch (e: Exception) {
                                onResult(false, e.message)
                            }
                        }
                    } else {
                        onResult(false, "Erro ao obter usuário atual.")
                    }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }


    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onResult(true, null)
                else onResult(false, task.exception?.message)
            }
    }


    fun signInWithGoogle(idToken: String, onResult: (Boolean, String?) -> Unit) {
        GoogleAuthHelper.firebaseAuthWithGoogle(idToken) { success, message ->
            if (success) {
                val user = auth.currentUser
                if (user != null) {
                    viewModelScope.launch {
                        try {

                            val existingUser = UserRepository.getCurrentUser()
                            if (existingUser == null) {
                                UserRepository.createUser(
                                    uid = user.uid,
                                    name = user.displayName ?: user.email?.substringBefore("@") ?: "",
                                    email = user.email ?: ""
                                )
                            }
                            onResult(true, null)
                        } catch (e: Exception) {
                            onResult(false, e.message)
                        }
                    }
                } else {
                    onResult(false, "Erro ao obter usuário do Google.")
                }
            } else {
                onResult(false, message)
            }
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null
}
