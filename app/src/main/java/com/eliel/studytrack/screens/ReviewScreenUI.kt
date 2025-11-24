package com.eliel.studytrack.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.eliel.studytrack.R
import com.eliel.studytrack.data.ReviewUiState
import com.eliel.studytrack.data.ReviewViewModel



@Composable
fun ReviewScreenUI(navController: NavHostController, materia: String, tema: String, dayText: String) {
    val vm: ReviewViewModel = viewModel()
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(materia, tema, dayText) {
        vm.generateFlashcards(materia, tema, dayText)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Revisão", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Text("$materia • $tema", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (dayText.isNotBlank()) {
                    Text(dayText, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        when (uiState) {
            is ReviewUiState.Loading -> {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ReviewUiState.Error -> {
                val msg = (uiState as ReviewUiState.Error).message
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text(msg)
                }
            }
            is ReviewUiState.Success -> {
                val cards = (uiState as ReviewUiState.Success).cards
                val pagerState = rememberPagerState(initialPage = 0, pageCount = { cards.size })
                val currentPage = pagerState.currentPage
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) { page ->
                        val card = cards[page]
                        var showAnswer by rememberSaveable(card.front, card.back) { mutableStateOf(false) }
                        val rotation by animateFloatAsState(
                            targetValue = if (showAnswer) 180f else 0f,
                            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                            label = "cardFlip"
                        )
                        val isFront = rotation <= 90f
                        val questionGradient = remember {
                            Brush.verticalGradient(listOf(Color(0xFF56CCF2), Color(0xFF2F80ED)))
                        }
                        val answerGradient = remember {
                            Brush.verticalGradient(listOf(Color(0xFFFFA726), Color(0xFFFF7043)))
                        }
                        val cameraDistance = with(LocalDensity.current) { 48.dp.toPx() }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .aspectRatio(0.85f)
                                .graphicsLayer {
                                    rotationY = rotation
                                    this.cameraDistance = cameraDistance
                                }
                                .clickable { showAnswer = !showAnswer },
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(if (isFront) questionGradient else answerGradient)
                                    .graphicsLayer {
                                        rotationY = if (isFront) 0f else 180f
                                    }
                                    .padding(24.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (isFront) "PERGUNTA" else "RESPOSTA",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = if (isFront) card.front else card.back,
                                    fontSize = 20.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 28.sp
                                )
                                Spacer(Modifier.height(24.dp))
                                Text(
                                    text = if (isFront) "Toque para ver a resposta" else "Toque para voltar à pergunta",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Contador estilizado abaixo do card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 20.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_book_open),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "${currentPage + 1}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = " / ${cards.size}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            else -> {}
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { navController.popBackStack(route = "home", inclusive = false) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Fechar")
        }
    }
}