package io.github.chaosdave34.benzol.ui.safetysheet.search

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.Substance
import io.github.chaosdave34.benzol.search.Gestis
import io.github.chaosdave34.benzol.ui.CustomScrollbar
import io.github.chaosdave34.benzol.ui.SafetySheetViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GestisSearch(
    viewModel: SafetySheetViewModel,
    onResult: (Substance) -> Unit,
    currentCasNumbers: List<String>
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState by viewModel.snackbarHostState.collectAsState()

    val allowedSearchTypes = rememberSaveable { Gestis.SearchType.entries.toMutableStateList() }
    val searchArguments = rememberSaveable { mutableStateListOf(Gestis.SearchArgument(allowedSearchTypes.removeFirst(), "")) }

    var searchState by rememberSaveable { mutableStateOf(SearchState.INPUT) }

    var exactSearch by rememberSaveable { mutableStateOf(false) }
    var searchResults by rememberSaveable { mutableStateOf(listOf<Gestis.SearchResult>()) }

    var selectedResult: Gestis.SearchResult? by rememberSaveable { mutableStateOf(null) }

    selectedResult?.let {
        scope.launch {
            val result = Gestis.getSubstanceInformation(it)
            if (result != null) onResult(result.getSubstance())
            selectedResult = null
        }
        Dialog(onDismissRequest = {}) {
            LoadingIndicator(Modifier.size(100.dp))
        }
    }

    LaunchedEffect(searchState, exactSearch) {
        if (searchState == SearchState.SEARCH) {
            val search = Gestis.search(Gestis.Search(searchArguments, exactSearch))

            if (search == null) {
                searchState = SearchState.ERROR
            } else {
                searchResults = search
                searchState = SearchState.SUCCESS
            }
        }
    }

    if (searchState != SearchState.INPUT) {
        val alreadyExists = stringResource(Res.string.substance_exists)

        SearchDialog(
            searchState = searchState,
            searchResults = searchResults,
            onDismissRequest = { searchState = SearchState.INPUT },
            onSelectResult = { result ->
                if (result.casNumber in currentCasNumbers) {
                    scope.launch {
                        snackbarHostState.showSnackbar(alreadyExists)
                    }
                } else {
                    selectedResult = result
                }

                searchState = SearchState.INPUT

                searchArguments.forEach {
                    it.value = ""
                }

                exactSearch = false
            },
            exactSearch = exactSearch,
            onExactSearchChange = {
                exactSearch = it
                searchState = SearchState.SEARCH
            }
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(Res.string.gestis_hint))

        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            searchArguments.forEachIndexed { index, argument ->
                var dropdownExpanded by remember { mutableStateOf(false) }

                var suggestionsExpanded by remember { mutableStateOf(false) }
                var suggestions by rememberSaveable { mutableStateOf(emptyList<String>()) }

                LaunchedEffect(argument, searchState) {
                    if (argument.value.length >= 3 && searchState == SearchState.INPUT) {
                        suggestions = Gestis.getSearchSuggestions(argument)
                        if (suggestions.isNotEmpty()) suggestionsExpanded = true
                    } else {
                        suggestions = emptyList()
                        suggestionsExpanded = false
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            value = stringResource(argument.searchType.stringResource),
                            readOnly = true,
                            onValueChange = {},
                            label = { Text(stringResource(Res.string.search_option)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropdownExpanded) },
                            enabled = searchArguments.size < 4
                        )

                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            Gestis.SearchType.entries.forEach {
                                DropdownMenuItem(
                                    text = { Text(stringResource(it.stringResource)) },
                                    onClick = {
                                        allowedSearchTypes.add(argument.searchType)
                                        allowedSearchTypes.remove(it)

                                        searchArguments[index] = argument.copy(searchType = it)
                                        dropdownExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        modifier = Modifier.fillMaxWidth(),
                        expanded = suggestionsExpanded,
                        onExpandedChange = { suggestionsExpanded = it }
                    ) {

                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                                .onKeyEvent { event ->
                                    if (event.key == Key.Enter) {
                                        searchState = SearchState.SEARCH
                                        suggestionsExpanded = false
                                        return@onKeyEvent true
                                    }
                                    false
                                },
                            value = argument.value,
                            onValueChange = { searchArguments[index] = argument.copy(value = it) },
                            singleLine = true,
                            label = { Text(stringResource(Res.string.search)) },
                            leadingIcon = { Icon(Icons.Rounded.Search, null) },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        searchArguments.removeAt(index)
                                        allowedSearchTypes.add(argument.searchType)
                                    },
                                    enabled = searchArguments.size != 1
                                ) {
                                    Icon(Icons.Rounded.Delete, stringResource(Res.string.delete))
                                }
                            }
                        )

                        ExposedDropdownMenu(
                            modifier = Modifier.heightIn(max = 300.dp),
                            expanded = suggestionsExpanded,
                            onDismissRequest = { suggestionsExpanded = false },
                        ) {
                            suggestions.forEach {
                                DropdownMenuItem(
                                    text = { Text(it) },
                                    onClick = {
                                        searchArguments[index] = argument.copy(value = it)
                                        suggestionsExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FilledIconButton(
                onClick = { searchArguments.add(Gestis.SearchArgument(allowedSearchTypes.removeFirst(), "")) },
                enabled = searchArguments.size < 4
            ) {
                Icon(Icons.Rounded.Add, stringResource(Res.string.add))
            }

            Button(
                onClick = { searchState = SearchState.SEARCH },
            ) { Text(stringResource(Res.string.do_search)) }

            Spacer(Modifier.width(ButtonDefaults.MinWidth))
        }
    }
}

@Composable
private fun SearchDialog(
    searchState: SearchState,
    searchResults: List<Gestis.SearchResult>,
    onDismissRequest: () -> Unit,
    onSelectResult: (Gestis.SearchResult) -> Unit,
    exactSearch: Boolean,
    onExactSearchChange: (Boolean) -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
        ) {
            Scaffold(
                Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                topBar = {
                    Text(
                        modifier = Modifier.padding(12.dp),
                        text = stringResource(Res.string.search_results),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                bottomBar = {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(Res.string.exact_search))
                        Switch(
                            checked = exactSearch,
                            onCheckedChange = onExactSearchChange
                        )

                        Spacer(Modifier.weight(1f))

                        TextButton(
                            onClick = onDismissRequest
                        ) {
                            Text(stringResource(Res.string.close))
                        }
                    }
                }
            ) { contentPadding ->
                Box(
                    Modifier
                        .padding(contentPadding)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(12.dp)
                ) {
                    val scrollState = rememberScrollState()

                    when (searchState) {
                        SearchState.INPUT -> {}
                        SearchState.SEARCH -> Loading(scrollState)
                        SearchState.SUCCESS -> SearchResults(
                            searchResult = searchResults,
                            onSelectResult = onSelectResult,
                            scrollState = scrollState
                        )

                        SearchState.ERROR -> Error(scrollState)
                    }

                    if (!(searchState == SearchState.SEARCH && searchResults.isNotEmpty())) {
                        CustomScrollbar(
                            scrollState = scrollState,
                            offset = 12.dp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Loading(
    scrollState: ScrollState
) {
    Box(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        contentAlignment = Alignment.Center
    ) {
        LoadingIndicator()
    }
}

@Composable
private fun Error(
    scrollState: ScrollState
) {
    Box(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        contentAlignment = Alignment.Center
    ) {
        Text(stringResource(Res.string.failed_search))
    }
}

@Composable
private fun BoxScope.SearchResults(
    searchResult: List<Gestis.SearchResult>,
    onSelectResult: (Gestis.SearchResult) -> Unit,
    scrollState: ScrollState
) {
    if (searchResult.isEmpty()) {
        Box(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(Res.string.no_search_results))
        }
    } else {
        val lazyListState = rememberLazyListState()
        LazyColumn(
            Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            state = lazyListState
        ) {
            items(items = searchResult.sortedBy { it.rank }, key = { it.rank }) {
                ListItem(
                    modifier = Modifier.clickable(
                        onClick = { onSelectResult(it) }
                    ),
                    headlineContent = { Text(it.name) },
                    supportingContent = { Text(it.casNumber ?: "-") },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                )
            }
        }

        CustomScrollbar(
            lazyListState = lazyListState,
            offset = 12.dp
        )
    }
}

private enum class SearchState {
    INPUT,
    SEARCH,
    SUCCESS,
    ERROR,
}