package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.Substance
import io.github.chaosdave34.benzol.search.Gestis
import io.github.chaosdave34.benzol.ui.components.DefaultColumn
import io.github.chaosdave34.benzol.ui.components.Scrollbar
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestisSearch(
    onResult: (Substance) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    val allowedSearchTypes = remember { Gestis.SearchType.entries.toMutableStateList() }
    val searchArguments = remember { mutableStateListOf(Gestis.SearchArgument(allowedSearchTypes.removeFirst(), "")) }

    var exactSearch by remember { mutableStateOf(false) }

    var searchResultOpen by remember { mutableStateOf(false) }
    var getSubstanceInformation: String? by remember { mutableStateOf(null) }

    val searchResults = remember { mutableStateListOf<Gestis.SearchResult>() }
    var failed by remember { mutableStateOf(false) }

    fun search() {
        searchResultOpen = true
        searchResults.clear()
        failed = false

        coroutineScope.launch {
            val results = Gestis.search(Gestis.Search(searchArguments, exactSearch))

            if (results.isNotEmpty()) searchResults.addAll(results)
            else failed = true
        }
    }

    if (searchResultOpen) {
        SearchResultDialog(
            exactSearch = exactSearch,
            toggleExactSearch = {
                exactSearch = it
                search()
            },
            result = searchResults,
            failed = failed,
            onClose = { searchResultOpen = false },
            onSelect = { getSubstanceInformation = it.zgvNumber }
        )
    }

    getSubstanceInformation?.let {
        coroutineScope.launch {
            val substanceInformation = Gestis.getSubstanceInformation(it)
            if (substanceInformation != null) onResult(substanceInformation.getSubstance())
            getSubstanceInformation = null
        }
        Dialog(onDismissRequest = {}) {
            CircularProgressIndicator()
        }
    }

    Text(stringResource(Res.string.gestis_hint))

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        searchArguments.forEachIndexed { index, argument ->
            var searchDropDownExpanded by remember { mutableStateOf(false) }
            val suggestions = remember { mutableStateListOf<String>() }
            var focused by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = searchDropDownExpanded,
                    onExpandedChange = { searchDropDownExpanded = it }
                ) {
                    TextField(
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        value = stringResource(argument.searchType.stringResource),
                        readOnly = true,
                        onValueChange = {},
                        label = { Text(stringResource(Res.string.search_option)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(searchDropDownExpanded) }
                    )

                    ExposedDropdownMenu(
                        expanded = searchDropDownExpanded,
                        onDismissRequest = { searchDropDownExpanded = false }
                    ) {
                        Gestis.SearchType.entries.forEach {
                            DropdownMenuItem(
                                text = { Text(stringResource(it.stringResource)) },
                                onClick = {
                                    allowedSearchTypes.add(argument.searchType)
                                    allowedSearchTypes.remove(it)

                                    searchArguments[index] = argument.copy(searchType = it)
                                    searchDropDownExpanded = false
                                },
                                enabled = allowedSearchTypes.contains(it)
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    modifier = Modifier.weight(1f),
                    expanded = argument.value.length >= 3 && focused,
                    onExpandedChange = {}
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryEditable)
                            .onFocusChanged { focused = it.hasFocus }
                            .onKeyEvent { event ->
                                if (event.key == Key.Enter) {
                                    search()
                                    focusManager.clearFocus()
                                    return@onKeyEvent true
                                }
                                false
                            },
                        value = argument.value,
                        onValueChange = {
                            searchArguments[index] = argument.copy(value = it)

                            if (it.length >= 3) {
                                coroutineScope.launch {
                                    val foundSuggestions = Gestis.getSearchSuggestions(argument)
                                    suggestions.clear()
                                    suggestions.addAll(foundSuggestions)
                                }
                            } else {
                                suggestions.clear()
                            }

                        },
                        singleLine = true,
                        label = { Text(stringResource(Res.string.search)) },
                        leadingIcon = { Icon(Icons.Rounded.Search, null) },
                    )

                    ExposedDropdownMenu(
                        modifier = Modifier.heightIn(max = 300.dp),
                        expanded = argument.value.length >= 3 && focused,
                        onDismissRequest = {}
                    ) {
                        suggestions.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    searchArguments[index] = argument.copy(value = it)
                                    focused = false
                                }
                            )
                        }

                    }
                }
                Button(
                    onClick = {
                        searchArguments.removeAt(index)
                        allowedSearchTypes.add(argument.searchType)
                    },
                    enabled = searchArguments.size != 1
                ) {
                    Icon(Icons.Rounded.Delete, stringResource(Res.string.delete))
                }
            }
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = { searchArguments.add(Gestis.SearchArgument(allowedSearchTypes.removeFirst(), "")) },
            enabled = searchArguments.size != 4
        ) {
            Icon(Icons.Rounded.Add, stringResource(Res.string.add))
        }

        Button(
            onClick = { search() },
        ) { Text(stringResource(Res.string.do_search)) }

        Spacer(Modifier.width(ButtonDefaults.MinWidth))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchResultDialog(
    exactSearch: Boolean,
    toggleExactSearch: (Boolean) -> Unit,
    onClose: () -> Unit,
    result: List<Gestis.SearchResult>,
    failed: Boolean,
    onSelect: (Gestis.SearchResult) -> Unit,
) {
    Dialog(
        onDismissRequest = onClose
    ) {
        val scrollState = rememberScrollState()

        Card {
            Column(
                Modifier.padding(start = 10.dp, bottom = 10.dp, top = 10.dp).fillMaxHeight(0.7f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (result.isEmpty()) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (failed) {
                            Text(stringResource(Res.string.failed_search))
                        } else {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.verticalScroll(scrollState).weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            DefaultColumn {
                                Row {
                                    Text(
                                        text = stringResource(Res.string.cas_number),
                                        modifier = Modifier.width(120.dp)
                                    )
                                    Text(stringResource(Res.string.name))
                                }
                                HorizontalDivider()
                                result.forEach {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(
                                                onClick = {
                                                    onSelect(it)
                                                    onClose()
                                                }
                                            )
                                    ) {
                                        Text(
                                            text = it.casNumber ?: "-",
                                            modifier = Modifier.width(100.dp)
                                        )
                                        Spacer(Modifier.width(20.dp))
                                        Text(it.name)
                                    }
                                }
                            }
                        }
                        Scrollbar(scrollState)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(end = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = exactSearch,
                        onCheckedChange = toggleExactSearch
                    )
                    Text(stringResource(Res.string.exact_search))
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = onClose
                    ) {
                        Icon(Icons.Rounded.Close, stringResource(Res.string.close))
                    }
                }
            }
        }
    }
}