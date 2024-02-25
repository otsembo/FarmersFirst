package com.otsembo.farmersfirst.ui.screens.products

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.otsembo.farmersfirst.ui.components.AppBar
import com.otsembo.farmersfirst.ui.components.AppBarIcon
import com.otsembo.farmersfirst.ui.components.AppNavRail
import com.otsembo.farmersfirst.ui.components.NavRailOption

@Composable
fun ProductsScreen(
    modifier: Modifier =  Modifier,
    isWideScreen: Boolean = false
    ) {

    if(!isWideScreen){

        Row(
            modifier = modifier.fillMaxSize()
        ) {

            AppBar(
                startIcon = {
                    AppBarIcon(icon = Icons.Default.Menu)
                },
                endIcon = {
                    AppBarIcon(icon = Icons.Default.NotificationImportant)
                }
            )

        }

    } else {

        Column(
            modifier = modifier.fillMaxSize()
        ) {

            AppNavRail(
                navRailOptions = listOf(
                    NavRailOption("Home", icon = Icons.Default.Home),
                    NavRailOption("Notifications", icon = Icons.Default.NotificationImportant)
                )
            )

        }

    }



}

