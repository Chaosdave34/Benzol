package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomCard(
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    headlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard {
        Column(Modifier.padding(12.dp)) {
            headlineContent?.let {
                ProvideContentColorTextStyle(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    textStyle = MaterialTheme.typography.titleMediumEmphasized,
                    content = it
                )
            }
            supportingContent?.let {
                ProvideContentColorTextStyle(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    textStyle = MaterialTheme.typography.titleSmall,
                    content = it
                )
            }
            if (headlineContent != null || supportingContent != null) {
                Spacer(Modifier.height(16.dp))
            }
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = horizontalAlignment,
            ) {
                content()
            }
        }
    }
}

@Composable
private fun ProvideContentColorTextStyle(
    contentColor: Color,
    textStyle: TextStyle,
    content: @Composable () -> Unit,
) {
    val mergedStyle = LocalTextStyle.current.merge(textStyle)
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalTextStyle provides mergedStyle,
        content = content,
    )
}