package com.eliel.studytrack.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.eliel.studytrack.R

@Composable
fun PremiumScreenUI(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            IconButton(onClick = {

                navController.popBackStack(route = "home", inclusive = false)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Fechar",
                    tint = Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }
        }


        Text(
            text = "Assine o Premium",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        PremiumFeatureItem("Estatísticas detalhadas de estudo")
        PremiumFeatureItem("Sincronização em múltiplos dispositivos")
        PremiumFeatureItem("Temas exclusivos")
        Spacer(modifier = Modifier.height(24.dp))
        PlanCard(
            title = "Plano Mensal",
            price = "R$ 9,90/mês",
            features = listOf("Acesso total a todos os recursos", "Cancelamento a qualquer momento")
        )
        Spacer(modifier = Modifier.height(16.dp))
        PlanCard(
            title = "Plano Anual",
            price = "R$ 99,90/ano",
            features = listOf("Economize 15%", "Acesso total a todos os recursos")
        )
    }
}

@Composable
fun PremiumFeatureItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color(0xFF4CAF50)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 16.sp, color = Color.DarkGray)
    }
}

@Composable
fun PlanCard(title: String, price: String, features: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(
                text = price,
                fontSize = 18.sp,
                color = Color(0xFF4CAF50),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            features.forEach {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.weight(1f),
                        softWrap = true
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { /* ação de assinatura */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Assinar")
            }
        }
    }
}
