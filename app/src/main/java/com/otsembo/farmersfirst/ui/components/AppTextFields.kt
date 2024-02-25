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

@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    label: String,
    onTextChange: (String) -> Unit = { },
    onSubmitSearch: (String) -> Unit = {}
) {

    var textItem by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(TextFieldDefaults.MinHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {

        OutlinedTextField(
            modifier = Modifier
                .weight(4f),
            value = textItem,
            onValueChange = { text ->
                textItem = text
                onTextChange(text) },
            label = {
                Text(text = label, style = MaterialTheme.typography.bodyLarge)
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            shape = RoundedCornerShape(10.dp),
            keyboardActions = KeyboardActions(onDone = { onSubmitSearch(textItem) }),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    modifier = Modifier.size(24.dp)
                )
            }
        )

        Button(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 6.dp)
                .weight(1f)
                .height(TextFieldDefaults.MinHeight - 15.dp),
            shape = RoundedCornerShape(50.dp),
            onClick = { onSubmitSearch(textItem) },
        ) {

            Icon(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.icon_size)),
                imageVector = Icons.Default.SavedSearch,
                contentDescription = "Search Item"
            )
        }

    }
}