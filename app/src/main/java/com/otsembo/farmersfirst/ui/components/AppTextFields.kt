package com.otsembo.farmersfirst.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SavedSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.otsembo.farmersfirst.R

/**
 * Composable function for rendering a search field.
 *
 * @param modifier Modifier for the search field.
 * @param label Label displayed as a hint in the search field.
 * @param text Current text value of the search field.
 * @param onTextChange Callback function invoked when the text in the search field changes.
 * @param onSubmitSearch Callback function invoked when the search is submitted.
 */
@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    onTextChange: (String) -> Unit = { },
    onSubmitSearch: (String) -> Unit = { }
) {
    Row(
        modifier = modifier.fillMaxWidth().height(TextFieldDefaults.MinHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search TextField
        TextField(
            modifier = Modifier.weight(4f),
            value = text,
            onValueChange = { newText -> onTextChange(newText) },
            label = {
                Text(text = label, style = MaterialTheme.typography.labelLarge)
            },
            textStyle = MaterialTheme.typography.bodySmall,
            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
            keyboardActions = KeyboardActions(onDone = { onSubmitSearch(text) }),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    modifier = Modifier.size(24.dp)
                )
            }
        )

        // Search Button
        Button(
            modifier = Modifier.align(Alignment.CenterVertically)
                .padding(start = 6.dp)
                .weight(1f)
                .height(TextFieldDefaults.MinHeight - 15.dp),
            shape = RoundedCornerShape(10.dp),
            onClick = { onSubmitSearch(text) },
        ) {
            Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                imageVector = Icons.Default.SavedSearch,
                contentDescription = "Search Item"
            )
        }
    }
}
