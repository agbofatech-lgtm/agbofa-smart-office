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
import com.agbofa.smartoffice.presentation.theme.BrandBackground
import com.agbofa.smartoffice.presentation.theme.BrandPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartOfficeScaffold(
    destination: AppDestination,
    onDestination: (AppDestination) -> Unit,
    content: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        modifier = Modifier.semantics { contentDescription = "AGBOFA Smart Office" },
        containerColor = BrandBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        IconMark()
                        Text(destination.topTitle(), style = MaterialTheme.typography.titleLarge, color = BrandPrimary, maxLines = 1)
                    }
                },
                actions = {
                    IconButton(onClick = { onDestination(AppDestination.ANALYTICS) }) {
                        Icon(AgbofaIcons.Analytics, contentDescription = "Office performance")
                    }
                    IconButton(onClick = { onDestination(AppDestination.INTELLIGENCE) }) {
                        Icon(AgbofaIcons.Intelligence, contentDescription = "Advisory intelligence")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandBackground),
            )
        },
        bottomBar = { BottomNav(current = destination, onSelect = onDestination) },
    ) { padding ->
        content(Modifier.padding(padding))
    }
}

private fun AppDestination.topTitle(): String = when (this) {
    AppDestination.DASHBOARD -> "AGBOFA"
    AppDestination.JOURNAL -> "Journal"
    AppDestination.DECISION -> "Decisions"
    AppDestination.SEARCH -> "Search"
    AppDestination.ANALYTICS -> "Analytics"
    AppDestination.INTELLIGENCE -> "Intelligence"
}
