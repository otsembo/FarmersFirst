package com.otsembo.farmersfirst.ui.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.otsembo.farmersfirst.ui.theme.FarmersFirstTheme
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableFloatStateOf(0.0f) }

    val animatedColor = remember { Animatable(Color.Blue) }

    LaunchedEffect(Unit) {
        animatedColor.animateTo(
            targetValue = Color.Red,
            animationSpec = repeatable(
                iterations = Int.MAX_VALUE,
                animation = tween(durationMillis = 1000, easing = LinearEasing)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                progress = { progress },
                color = animatedColor.value,
                strokeWidth = 5.dp,
            )
            Text(
                text = "Please wait. Loading ...",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 8.dp),
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
                )
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5)
            progress += 0.01f
            if (progress >= 1.0f) {
                progress = 0.0f
            }
        }
    }
}


@Composable
fun ErrorScreen(
    errorMessage: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(50.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
fun ErrorScreenPreview() {
    FarmersFirstTheme {
        ErrorScreen(errorMessage = "An error occurred. Please try again.")
    }
}


@Preview(showSystemUi = true)
@Composable
fun LoadingScreenPreview() {
    LoadingScreen()
}