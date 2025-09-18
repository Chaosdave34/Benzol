package io.github.chaosdave34.benzol.ui.safetysheet.search

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.gestis
import io.github.chaosdave34.benzol.Substance
import io.github.chaosdave34.benzol.ui.SafetySheetViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun SubstanceSearch(
    viewModel: SafetySheetViewModel,
    onSearch: (Substance) -> Unit,
    currentCasNumbers: List<String>
) {
    var selectedSearchIndex by remember { mutableIntStateOf(0) }

    PrimaryTabRow(
        selectedTabIndex = selectedSearchIndex,
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Tab(
            selected = selectedSearchIndex == 0,
            onClick = { selectedSearchIndex = 0 },
            text = { Text(stringResource(Res.string.gestis)) }
        )
    }

    when (selectedSearchIndex) {
        0 -> GestisSearch(
            viewModel = viewModel,
            onResult = onSearch,
            currentCasNumbers = currentCasNumbers
        )
    }
}