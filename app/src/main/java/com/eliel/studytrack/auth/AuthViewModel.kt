package com.eliel.studytrack.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eliel.studytrack.data.firestore.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    fun registerUser(
        email: String,
        password: String,
        name: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            onResult(false, "Preencha todos os campos.")
            return
        }

        if (password.length < 6) {
            onResult(false, "A senha deve ter pelo menos 6 caracteres.")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val profileUpdates = userProfileChangeRequest {
                            displayName = name
                        }

                        user.updateProfile(profileUpdates).addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                viewModelScope.launch {
                                    try {
                                        UserRepository.createUser(
                                            uid = user.uid,
                                            name = name,
                                            email = email
                                        )
                                        onResult(true, null)
                                    } catch (e: Exception) {
                                        onResult(false, e.message ?: "Erro ao salvar usuário.")
                                    }
                                }
                            } else {
                                onResult(false, "Erro ao atualizar perfil.")
                            }
                        }
                    } else {
                        onResult(false, "Erro ao obter usuário atual.")
                    }
                } else {
                    onResult(false, task.exception?.message ?: "Falha ao registrar.")
                }
            }
    }


    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onResult(false, "Por favor, preencha email e senha.")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    val errorMsg = when {
                        task.exception?.message?.contains("password") == true ->
                            "Senha incorreta."
                        task.exception?.message?.contains("no user record") == true ->
                            "Usuário não encontrado."
                        else ->
                            task.exception?.message ?: "Falha ao fazer login."
                    }
                    onResult(false, errorMsg)
                }
            }
    }

    fun resetPassword(email: String, callback: (Boolean, String) -> Unit) {
        if (email.isBlank()) {
            callback(false, "Por favor, insira um email válido.")
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Um link para redefinir sua senha foi enviado para $email.")
                } else {
                    callback(false, task.exception?.message ?: "Falha ao enviar e-mail.")
                }
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
                            onResult(false, e.message ?: "Erro ao salvar usuário do Google.")
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