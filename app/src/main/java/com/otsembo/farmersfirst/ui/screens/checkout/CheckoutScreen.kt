package com.otsembo.farmersfirst.ui.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.otsembo.farmersfirst.ui.navigation.AppRoutes

/**
 * Composable function to display the checkout screen.
 *
 * @param navController NavHostController to handle navigation within the app.
 */
@Composable
fun CheckoutScreen(navController: NavHostController) {
    Column(
        modifier =
            Modifier
                .background(color = MaterialTheme.colorScheme.surface)
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Checkout Success",
            modifier = Modifier.size(100.dp),
            tint = Color.Green,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            style = MaterialTheme.typography.headlineSmall,
            text = "Checkout Successful!",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = "Please have the total amount ready when delivery happens in the next two days.",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = "One of our agents will call you for confirmation of the order.",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Light,
        )
        Spacer(modifier = Modifier.height(24.dp))
        ElevatedButton(
            onClick = { navController.navigate(AppRoutes.Home.Products) },
            colors = ButtonDefaults.buttonColors(),
        ) {
            Text(text = "Back to Home")
        }
    }
}
