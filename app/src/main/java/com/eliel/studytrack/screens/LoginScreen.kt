package com.eliel.studytrack.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.eliel.studytrack.R
import com.eliel.studytrack.auth.AuthViewModel
import com.eliel.studytrack.auth.GoogleAuthHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    activity: Activity,
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var googleLoading by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val googleClient = remember(activity) { GoogleAuthHelper.getClient(activity) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        googleLoading = false
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken.isNullOrBlank()) {
                    errorMessage = "Não foi possível obter o token do Google."
                    return@rememberLauncherForActivityResult
                }
                googleLoading = true
                viewModel.signInWithGoogle(idToken) { success, msg ->
                    googleLoading = false
                    if (success) {
                        context.getSharedPreferences("studytrack_prefs", android.content.Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean("remember_me", rememberMe)
                            .apply()
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        errorMessage = msg ?: "Falha ao autenticar com Google."
                    }
                }
            } catch (e: ApiException) {
                errorMessage = "Falha no Google Sign-In (código ${e.statusCode})."
            } catch (e: Exception) {
                errorMessage = "Erro inesperado no Google Sign-In."
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            errorMessage = "Login com Google cancelado."
        } else {
            errorMessage = "Falha ao retornar do Google Sign-In."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(150.dp)
        )
        Spacer(Modifier.height(32.dp))
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                loading = true
                viewModel.loginUser(email, password) { success, message ->
                    loading = false
                    if (success) {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                        context.getSharedPreferences("studytrack_prefs", android.content.Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean("remember_me", rememberMe)
                            .apply()
                    } else {
                        errorMessage = message
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(if (loading) "Entrando..." else "Entrar")
        }

        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
            Spacer(Modifier.width(8.dp))
            Text("Lembrar de mim")
        }

        OutlinedButton(
            onClick = {
                googleLoading = true
                val signInIntent = googleClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google_logo),
                contentDescription = "Google",
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(if (googleLoading) "Carregando..." else "Entrar com Google")
        }

        errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        TextButton(onClick = { navController.navigate("forgot_password") }) {
            Text("Esqueceu a senha?")
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ainda não tem conta? ")
            TextButton(onClick = { navController.navigate("register") }) {
                Text("Cadastre-se")
            }
        }
    }
}