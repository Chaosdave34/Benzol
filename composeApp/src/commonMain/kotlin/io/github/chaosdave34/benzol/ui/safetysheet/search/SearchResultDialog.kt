package io.github.chaosdave34.benzol.ui.safetysheet.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.search_results
import io.github.chaosdave34.benzol.search.Gestis
import io.github.chaosdave34.benzol.ui.CustomScrollbar
import io.github.chaosdave34.benzol.ui.adaptive.AdaptiveDialog
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchResultsDialog(
    visible: Boolean,
    searchResults: List<Gestis.SearchResult>,
    onDismissRequest: () -> Unit,
    onSelectResult: (Gestis.SearchResult) -> Unit,
) {
    if (visible) {
        AdaptiveDialog(
            title = stringResource(Res.string.search_results),
            onDismissRequest = onDismissRequest
        ) {
            val lazyListState = rememberLazyListState()
            LazyColumn(
                Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                state = lazyListState,
            ) {
                items(items = searchResults.sortedBy { it.rank }, key = { it.rank }) {
                    ListItem(
                        modifier = Modifier.clickable(
                            onClick = {
                                onSelectResult(it)
                                onDismissRequest()
                            }
                        ),
                        headlineContent = { Text(it.name) },
                        supportingContent = { Text(it.casNumber ?: "-") },
                    )
                }
            }

            CustomScrollbar(rememberScrollbarAdapter(lazyListState))
        }
    }
}

