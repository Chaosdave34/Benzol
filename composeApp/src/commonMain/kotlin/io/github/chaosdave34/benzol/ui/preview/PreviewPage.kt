package io.github.chaosdave34.benzol.ui.preview

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import io.github.chaosdave34.benzol.Substance
import io.github.chaosdave34.benzol.data.SafetySheetInputState
import io.github.chaosdave34.benzol.ui.AppPageBox

@Composable
fun PreviewPage(
    inputState: SafetySheetInputState,
    substances: List<Substance>,
    humanAndEnvironmentDanger: List<String>,
    rulesOfConduct: List<String>,
    inCaseOfDanger: List<String>,
    disposal: List<String>
) {
    AppPageBox { scrollState ->
        val windowSizeClass = currentWindowAdaptiveInfo(true).windowSizeClass
        val modifier = Modifier.verticalScroll(scrollState)

        if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXTRA_LARGE_LOWER_BOUND)) {
            Row(
                modifier,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Page1(
                    modifier = Modifier.weight(1f),
                    inputState = inputState,
                    substances = substances
                )
                Page2(
                    modifier = Modifier.weight(1f),
                    humanAndEnvironmentDanger = humanAndEnvironmentDanger,
                    rulesOfConduct = rulesOfConduct,
                    inCaseOfDanger = inCaseOfDanger,
                    disposal = disposal
                )
            }
        } else {
            Column(
                modifier,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Page1(
                    inputState = inputState,
                    substances = substances
                )
                Page2(
                    humanAndEnvironmentDanger = humanAndEnvironmentDanger,
                    rulesOfConduct = rulesOfConduct,
                    inCaseOfDanger = inCaseOfDanger,
                    disposal = disposal
                )
            }
        }
    }
}

@Composable
fun Page(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = modifier
    ) {
        Column(
            Modifier
                .padding(12.dp)
                .border(2.dp, MaterialTheme.colorScheme.outlineVariant),
            content = content
        )
    }
}

@Composable
fun ListRow(content: @Composable (RowScope.() -> Unit)) {
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
        content = content
    )
}
