package com.otsembo.farmersfirst.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.otsembo.farmersfirst.common.notNull
import com.otsembo.farmersfirst.ui.theme.FarmersFirstTheme

/**
 * A customizable app bar composable.
 * @param modifier Modifier to be applied to the app bar.
 * @param startIcon Start icon composable.
 * @param endIcon End icon composable.
 * @param title Mid AppBar section composable.
 */
@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    startIcon: @Composable () -> Unit = {},
    endIcon: @Composable () -> Unit = {},
    title: @Composable () ->  Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        startIcon()
        title()
        endIcon()
    }
}

/**
 * A navigation rail composable.
 * @param modifier Modifier to be applied to the navigation rail.
 * @param navRailOptions List of navigation rail options.
 */
@Composable
fun AppNavRail(
    modifier: Modifier = Modifier,
    navRailOptions: List<NavRailOption>,
) {
    var selectedItem by remember { mutableIntStateOf(0) }

    NavigationRail(
        modifier = modifier
            .fillMaxHeight(),
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
    ) {
        navRailOptions.forEachIndexed { index, option ->
            NavigationRailItem(
                modifier = Modifier.padding(top = 4.dp),
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    option.onClick() },
                icon = { option.icon() },
                label = { Text(text = option.title) },
                alwaysShowLabel = false
            )
        }
    }
}

/**
 * An icon used in the app bar.
 * @param icon ImageVector representing the icon.
 * @param onClick Lambda function to be invoked when the icon is clicked.
 */
@Composable
fun AppBarIcon(
    icon: ImageVector,
    tint: Color = Color.Transparent,
    onClick: (() -> Unit)? = null
) {
    if(onClick.notNull()){
        Icon(
            imageVector = icon,
            contentDescription = "Menu drawer",
            modifier = Modifier
                .size(30.dp)
                .clickable { onClick!!() },
            tint = if(tint == Color.Transparent) MaterialTheme.colorScheme.surfaceTint else tint
        )
    } else {
        Icon(
            imageVector = icon,
            contentDescription = "Menu drawer",
            modifier = Modifier
                .size(30.dp),
            tint = if(tint == Color.Transparent) MaterialTheme.colorScheme.surfaceTint else tint
        )
    }
}

/**
 * Data class representing an option in the navigation rail.
 * @param title The title of the option.
 * @param icon ImageVector representing the icon of the option.
 * @param onClick Lambda function to be invoked when the option is clicked.
 */
data class NavRailOption(
    val title: String,
    val icon: @Composable () -> Unit = {},
    val onClick: () -> Unit = {},
)
