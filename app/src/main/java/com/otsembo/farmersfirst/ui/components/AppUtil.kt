package com.otsembo.farmersfirst.ui.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HourglassEmpty
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

/**
 * Composable function to display a loading screen with a circular progress indicator and a text message.
 *
 * @param modifier Modifier for configuring the layout behavior of this component.
 */
@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    // Mutable state variable to track progress of the loading indicator
    var progress by remember { mutableFloatStateOf(0.0f) }

    // Animatable for dynamically changing the color of the loading indicator
    val animatedColor = remember { Animatable(Color.Blue) }

    // Launch effect to animate the color change of the loading indicator continuously
    LaunchedEffect(Unit) {
        animatedColor.animateTo(
            targetValue = Color.Red,
            animationSpec = repeatable(
                iterations = Int.MAX_VALUE,
                animation = tween(durationMillis = 1000, easing = LinearEasing)
            )
        )
    }

    // Box composable to contain the loading indicator and text message
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Circular progress indicator
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                progress = { progress },
                color = animatedColor.value,
                strokeWidth = 5.dp,
            )
            // Text message below the loading indicator
            Text(
                text = "Please wait. Loading ...",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 8.dp),
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
        }
    }

    // Launch effect to continuously update the progress of the loading indicator
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

/**
 * Composable function to display an error screen with an error icon and an error message.
 *
 * @param errorMessage The error message to be displayed.
 */
@Composable
fun ErrorScreen(
    errorMessage: String
) {
    // Column composable to display the error icon and message vertically centered
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Error icon
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(50.dp),
            tint = MaterialTheme.colorScheme.error
        )
        // Error message below the error icon
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Composable function to display an empty entity message with an optional icon.
 * @param modifier Modifier to be applied to the composable.
 * @param message The message to be displayed.
 * @param icon Lambda function to display the icon. Defaults to an hourglass empty icon.
 */
@Composable
fun EmptyEntityMessage(
    modifier: Modifier = Modifier,
    message: String,
    icon: @Composable () -> Unit  = {
        Icon(
            imageVector = Icons.Default.HourglassEmpty,
            contentDescription = message,
            modifier = Modifier.size(50.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}
