package io.github.chaosdave34.benzol.ui.preview

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import io.github.chaosdave34.benzol.ui.AppPageBox
import io.github.chaosdave34.benzol.ui.SafetySheetViewModel

context(_: SafetySheetViewModel)
@Composable
fun PreviewPage() {
    AppPageBox { scrollState ->
        val windowSizeClass = currentWindowAdaptiveInfo(true).windowSizeClass
        val modifier = Modifier.verticalScroll(scrollState)

        if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXTRA_LARGE_LOWER_BOUND)) {
            Row(
                modifier,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Page1(modifier = Modifier.weight(1f))
                Page2(modifier = Modifier.weight(1f))
            }
        } else {
            Column(
                modifier,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Page1()
                Page2()
            }
        }
    }
}

@Composable
fun Page(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier
            .padding(12.dp)
            .border(2.dp, MaterialTheme.colorScheme.outlineVariant),
        content = content
    )
}

@Composable
fun ListRow(content: @Composable (RowScope.() -> Unit)) {
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
        content = content
    )
}
