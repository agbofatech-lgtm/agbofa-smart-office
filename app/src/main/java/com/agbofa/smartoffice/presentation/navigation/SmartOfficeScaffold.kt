package com.agbofa.smartoffice.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.agbofa.smartoffice.presentation.components.AgbofaIcons
import com.agbofa.smartoffice.presentation.components.IconMark
import com.agbofa.smartoffice.presentation.settings.LocalOfficePreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartOfficeScaffold(
    destination: AppDestination,
    onDestination: (AppDestination) -> Unit,
    content: @Composable (Modifier) -> Unit,
) {
    val officeName = LocalOfficePreferences.current.officeName
    Scaffold(
        modifier = Modifier.semantics { contentDescription = officeName },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        IconMark()
                        Text(
                            officeName,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onDestination(AppDestination.ANALYTICS) }) {
                        Icon(AgbofaIcons.Analytics, contentDescription = "Office performance")
                    }
                    IconButton(onClick = { onDestination(AppDestination.INTELLIGENCE) }) {
                        Icon(AgbofaIcons.Intelligence, contentDescription = "Advisory intelligence")
                    }
                    TextButton(
                        onClick = { onDestination(AppDestination.SETTINGS) },
                        modifier = Modifier.semantics { contentDescription = "Settings" },
                    ) {
                        Text("Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        bottomBar = { BottomNav(current = destination, onSelect = onDestination) },
    ) { padding ->
        content(Modifier.padding(padding))
    }
}
