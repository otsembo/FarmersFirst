package com.otsembo.farmersfirst.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.otsembo.farmersfirst.R
import com.otsembo.farmersfirst.ui.navigation.AppRoutes
import com.otsembo.farmersfirst.ui.theme.FarmersFirstTheme

@Composable
fun AuthScreen(
    modifier: Modifier =  Modifier,
    isWideScreen: Boolean = false,
    viewModel: AuthScreenVM,
    navController: NavController,
) {

    val uiState: AuthUiState by viewModel.authUiState.collectAsState()

    LaunchedEffect(key1 = uiState, block = {
        if(uiState.isSignedIn) navController.navigate(AppRoutes.Products)
    })


    Box(
        modifier = modifier
    ) {

        Image(
            painter = painterResource(id = R.drawable.auth_background),
            contentDescription = "A farmer looking into a tablet",
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        
        if(isWideScreen){

            ElevatedCard(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxWidth(0.4f)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(
                    topStart = dimensionResource(id = R.dimen.banner_card_radius),
                    bottomStart = dimensionResource(id = R.dimen.banner_card_radius)),
            ) {
                AuthCardUi(isWideScreen, onOAuthSignInRequest = { viewModel.handleAuthActions(AuthActions.RequestSignIn) })
            }

        } else {

            ElevatedCard(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(
                    topStart = dimensionResource(id = R.dimen.banner_card_radius),
                    topEnd = dimensionResource(id = R.dimen.banner_card_radius)),
            ) {

                AuthCardUi(isWideScreen, onOAuthSignInRequest = { viewModel.handleAuthActions(AuthActions.RequestSignIn) })

            }

        }

    }
}

@Composable
fun AuthCardUi(
    isWideScreen: Boolean = false,
    onOAuthSignInRequest: () -> Unit = {},
) {

    Column {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {

            Text(
                text = "The next generation of farming",
                style = if(isWideScreen) MaterialTheme.typography.displayMedium else MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = "We are safe heaven for your supplies and agricultural tech.",
                style = if(isWideScreen) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )

            ElevatedButton(
                modifier = Modifier
                    .padding(top = if(isWideScreen) 40.dp else 20.dp),
                onClick = { onOAuthSignInRequest() },
                colors = ButtonDefaults.buttonColors()
            ) {

                Text(text = "Sign In", style = if(isWideScreen) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.bodyLarge)

                Icon(
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.icon_size))
                        .padding(start = 8.dp),
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google Sign In BUTTON"
                )

            }
        }
    }

}
