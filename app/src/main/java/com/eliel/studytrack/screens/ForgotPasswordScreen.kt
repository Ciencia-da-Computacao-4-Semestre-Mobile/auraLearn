package com.eliel.studytrack.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.eliel.studytrack.R
import com.eliel.studytrack.auth.AuthViewModel

@Composable
fun ForgotPasswordScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(150.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.recuperar_senha), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.digite_seu_email)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                loading = true
                viewModel.resetPassword(email) { success, msg ->
                    loading = false
                    message = msg
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        ) {
            Text(if (loading) stringResource(R.string.enviando) else stringResource(R.string.enviar_link_de_redefini_o))
        }

        Spacer(Modifier.height(16.dp))

        message?.let {
            Text(it, color = if (it.contains(stringResource(R.string.enviado), ignoreCase = true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(24.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text(stringResource(R.string.voltar_para_o_login))
        }
    }
}
