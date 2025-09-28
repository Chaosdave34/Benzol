package io.github.chaosdave34.benzol.ui.safetysheet.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.window.core.layout.WindowSizeClass
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.LocalSnackbarHostState
import io.github.chaosdave34.benzol.data.Substance
import io.github.chaosdave34.benzol.search.Gestis
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.rememberResourceEnvironment
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GestisSearch(
    onResult: (Substance) -> Unit,
    currentCasNumbers: List<String>
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val resourceEnvironment = rememberResourceEnvironment()

    var chemicalName by rememberSaveable { mutableStateOf("") }
    var casNumber by rememberSaveable { mutableStateOf("") }
    var molecularFormula by rememberSaveable { mutableStateOf("") }
    var fullText by rememberSaveable { mutableStateOf("") }

    var searchResults by rememberSaveable { mutableStateOf(listOf<Gestis.SearchResult>()) }

    var resultsDialogVisible by rememberSaveable { mutableStateOf(false) }
    var loading by rememberSaveable { mutableStateOf(false) }

    val onSelectResult = fun(result: Gestis.SearchResult) {
        if (result.casNumber in currentCasNumbers) {
            scope.launch {
                snackbarHostState.showSnackbar(getString(resourceEnvironment, Res.string.substance_exists))
            }
        } else {
            loading = true
            chemicalName = ""
            casNumber = ""
            molecularFormula = ""
            fullText = ""
            scope.launch {
                Gestis.getSubstanceInformation(result)?.let {
                    onResult(it.getSubstance())
                }
                loading = false
            }
        }
    }

    val onSearch = fun(exactSearch: Boolean) {
        loading = true
        scope.launch {
            val search = Gestis.search(
                listOf(
                    Pair(Gestis.SearchType.ChemicalName, chemicalName),
                    Pair(Gestis.SearchType.CasNumber, casNumber),
                    Pair(Gestis.SearchType.MolecularFormula, molecularFormula),
                    Pair(Gestis.SearchType.FullText, fullText)
                ),
                exactSearch
            )
            loading = false

            if (search == null) {
                snackbarHostState.showSnackbar(getString(resourceEnvironment, Res.string.failed_search))
            } else {
                when (search.size) {
                    0 -> snackbarHostState.showSnackbar(getString(resourceEnvironment, Res.string.no_search_results))
                    1 -> onSelectResult(search[0])
                    else -> {
                        searchResults = search
                        resultsDialogVisible = true
                    }
                }
            }
        }
    }

    if (loading) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            LoadingIndicator(Modifier.size(100.dp))
        }
    }

    SearchResultsDialog(
        visible = resultsDialogVisible,
        searchResults = searchResults,
        onDismissRequest = { resultsDialogVisible = false },
        onSelectResult = onSelectResult
    )

    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(Res.string.gestis_hint))

        val maxItemsInEachRow = if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) 2 else 1

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = maxItemsInEachRow
        ) {
            SearchBar(
                modifier = Modifier.weight(0.5f),
                searchType = Gestis.SearchType.ChemicalName,
                value = chemicalName,
                onValueChange = { chemicalName = it },
                onSearch = { onSearch(false) }
            )
            SearchBar(
                modifier = Modifier.weight(0.5f),
                searchType = Gestis.SearchType.CasNumber,
                value = casNumber,
                onValueChange = { casNumber = it },
                onSearch = { onSearch(false) }
            )
            SearchBar(
                modifier = Modifier.weight(0.5f),
                searchType = Gestis.SearchType.MolecularFormula,
                value = molecularFormula,
                onValueChange = { molecularFormula = it },
                onSearch = { onSearch(false) }
            )
            SearchBar(
                modifier = Modifier.weight(0.5f),
                searchType = Gestis.SearchType.FullText,
                value = fullText,
                onValueChange = { fullText = it },
                onSearch = { onSearch(false) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = { onSearch(false) },
            ) {
                Text(stringResource(Res.string.do_search))
            }
            Button(
                onClick = { onSearch(true) }
            ) {
                Text(stringResource(Res.string.exact_search))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    searchType: Gestis.SearchType,
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var suggestions by remember { mutableStateOf(emptyList<String>()) }

    LaunchedEffect(value) {
        suggestions = if (value.length >= 3) Gestis.getSearchSuggestions(searchType, value) else emptyList()
    }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                .onKeyEvent { event ->
                    if (event.key == Key.Enter) {
                        onSearch()
                        expanded = false
                        return@onKeyEvent true
                    }
                    false
                },
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            label = { Text(stringResource(searchType.label), maxLines = 1, overflow = TextOverflow.Ellipsis) },
            leadingIcon = { Icon(Icons.Rounded.Search, null) },
            trailingIcon = {
                IconButton(
                    onClick = { onValueChange("") },
                    enabled = value.isNotEmpty(),
                ) {
                    Icon(Icons.Rounded.Clear, stringResource(Res.string.clear))
                }
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
        )

        ExposedDropdownMenu(
            modifier = Modifier.heightIn(max = (5 * 48 + 16).dp),
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            suggestions.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onValueChange(it)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}