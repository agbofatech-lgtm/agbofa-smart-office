package com.agbofa.smartoffice.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.agbofa.smartoffice.presentation.theme.BrandAccent
import com.agbofa.smartoffice.presentation.theme.BrandMuted
import com.agbofa.smartoffice.presentation.theme.BrandPrimary
import com.agbofa.smartoffice.presentation.theme.BrandSecondary
import com.agbofa.smartoffice.presentation.theme.BrandSurfaceRaised

enum class PillTone { Neutral, Positive, Attention, Critical, Authority }

@Composable
fun AgbofaSectionHeader(title: String, subtitle: String? = null, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(Modifier.width(28.dp).height(3.dp).clip(RoundedCornerShape(2.dp)).background(BrandAccent))
        Text(title, style = MaterialTheme.typography.titleLarge)
        if (!subtitle.isNullOrBlank()) Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = BrandMuted)
    }
}

@Composable
fun AgbofaPageHeader(eyebrow: String, title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(eyebrow.uppercase(), style = MaterialTheme.typography.labelMedium, color = BrandPrimary)
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = BrandMuted)
    }
}

@Composable
fun AgbofaSurfaceCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = BrandSurfaceRaised), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }
}

@Composable
fun AgbofaCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) = AgbofaSurfaceCard(modifier, content)

@Composable
fun AgbofaEmptyState(title: String, body: String, modifier: Modifier = Modifier) {
    AgbofaSurfaceCard(modifier) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(body, style = MaterialTheme.typography.bodyMedium, color = BrandMuted)
    }
}

@Composable
fun AgbofaPrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier.fillMaxWidth(), enabled: Boolean = true) {
    Button(onClick = onClick, enabled = enabled, modifier = modifier, colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary), shape = RoundedCornerShape(12.dp)) { Text(text) }
}

@Composable
fun AgbofaSecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier.fillMaxWidth(), enabled: Boolean = true) {
    OutlinedButton(onClick = onClick, enabled = enabled, modifier = modifier, shape = RoundedCornerShape(12.dp)) { Text(text) }
}

@Composable
fun AgbofaStatusPill(text: String, tone: PillTone = PillTone.Neutral, modifier: Modifier = Modifier) {
    val background = when (tone) {
        PillTone.Neutral -> MaterialTheme.colorScheme.surfaceVariant
        PillTone.Positive -> BrandSecondary.copy(alpha = 0.16f)
        PillTone.Attention -> BrandAccent.copy(alpha = 0.22f)
        PillTone.Critical -> MaterialTheme.colorScheme.error.copy(alpha = 0.16f)
        PillTone.Authority -> BrandPrimary.copy(alpha = 0.14f)
    }
    val foreground = when (tone) {
        PillTone.Neutral -> BrandMuted
        PillTone.Positive -> BrandSecondary
        PillTone.Attention -> BrandPrimary
        PillTone.Critical -> MaterialTheme.colorScheme.error
        PillTone.Authority -> BrandPrimary
    }
    Text(text = text, style = MaterialTheme.typography.labelMedium, color = foreground, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = modifier.clip(RoundedCornerShape(50)).background(background).padding(horizontal = 10.dp, vertical = 4.dp))
}

@Composable
fun IconMark(modifier: Modifier = Modifier) {
    Box(modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(BrandPrimary), contentAlignment = Alignment.Center) {
        Text("A", color = Color.White, style = MaterialTheme.typography.titleMedium)
    }
}
