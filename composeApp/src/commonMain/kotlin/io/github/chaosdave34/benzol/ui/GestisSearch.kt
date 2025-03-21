package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.add
import benzol.composeapp.generated.resources.close
import benzol.composeapp.generated.resources.delete
import benzol.composeapp.generated.resources.do_search
import benzol.composeapp.generated.resources.exact_search
import benzol.composeapp.generated.resources.gestis_hint
import benzol.composeapp.generated.resources.search
import benzol.composeapp.generated.resources.search_option
import io.github.chaosdave34.benzol.Substance
import io.github.chaosdave34.benzol.search.Gestis
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
    val searchArguments = remember { mutableStateListOf<Gestis.SearchArgument>(Gestis.SearchArgument(allowedSearchTypes.removeFirst(), "")) }

    var exactSearch by remember { mutableStateOf(false) }

    var doSearch by remember { mutableStateOf(false) }
    var searchResultOpen by remember { mutableStateOf(false) }
    var getSubstanceInformation: String? by remember { mutableStateOf(null) }

    val searchResults = remember { mutableStateListOf<Gestis.SearchResult>() }

    if (doSearch) {
        searchResultOpen = true
        doSearch = false
        searchResults.clear()

        coroutineScope.launch {
            searchResults.addAll(Gestis.search(Gestis.Search(searchArguments, exactSearch)))
        }
    }

    if (searchResultOpen) {
        SearchResultDialog(
            exactSearch = exactSearch,
            toggleExactSearch = {
                exactSearch = it
                doSearch = true
            },
            result = searchResults,
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
                        value = argument.searchType.displayText,
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
                                text = { Text(it.displayText) },
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
                                    doSearch = true
                                    focusManager.clearFocus()
                                    true
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
            onClick = { doSearch = true },
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = exactSearch,
                        onCheckedChange = toggleExactSearch
                    )
                    Text(stringResource(Res.string.exact_search))
                }
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        modifier = Modifier.verticalScroll(scrollState).weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (result.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {

                            DefaultColumn {
                                result.forEach {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(
                                                onClick = {
                                                    onSelect(it)
                                                    onClose()
                                                }
                                            )
                                    ) {

                                        Text("${it.casNumber ?: "-"} ${it.name}")
                                    }
                                }
                            }
                        }
                    }

                    Scrollbar(scrollState)

                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(end = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End)
                ) {
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