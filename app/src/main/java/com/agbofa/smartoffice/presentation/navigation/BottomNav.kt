package com.agbofa.smartoffice.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import com.agbofa.smartoffice.presentation.components.AgbofaIcons
import com.agbofa.smartoffice.presentation.theme.BrandPrimary

@Composable
fun BottomNav(current: AppDestination, onSelect: (AppDestination) -> Unit) {
    NavigationBar(modifier = Modifier.semantics { contentDescription = "Primary office destinations" }) {
        AppDestination.primary.forEach { item ->
            NavigationBarItem(
                selected = current == item,
                onClick = { onSelect(item) },
                icon = { Icon(item.icon(), contentDescription = item.label) },
                label = { Text(item.shortLabel, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                colors = NavigationBarItemDefaults.colors(indicatorColor = BrandPrimary.copy(alpha = 0.14f)),
            )
        }
    }
}

internal fun AppDestination.icon(): ImageVector = when (this) {
    AppDestination.DASHBOARD -> AgbofaIcons.Home
    AppDestination.JOURNAL -> AgbofaIcons.Journal
    AppDestination.DECISION -> AgbofaIcons.Decision
    AppDestination.SEARCH -> AgbofaIcons.Search
    AppDestination.ANALYTICS -> AgbofaIcons.Analytics
    AppDestination.INTELLIGENCE -> AgbofaIcons.Intelligence
    AppDestination.SETTINGS -> AgbofaIcons.Home
}
