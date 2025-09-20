package io.github.chaosdave34.benzol.ui.safetysheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Source
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.delete
import benzol.composeapp.generated.resources.edit_substance
import io.github.chaosdave34.benzol.Substance
import io.github.chaosdave34.benzol.search.Source
import io.github.chaosdave34.benzol.ui.FormattedMolecularFormula
import io.github.chaosdave34.benzol.ui.adaptive.AdaptiveButton
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SubstanceList(
    substances: SnapshotStateList<Substance>,
    onSubstanceClick: (Int) -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    Text(stringResource(Res.string.edit_substance))

    Column {
        substances.forEachIndexed { index, substance ->
            ListItem(
                modifier = Modifier
                    .clickable(
                        onClick = { onSubstanceClick(index) }
                    ),
                headlineContent = {
                    Text(substance.name)
                },
                supportingContent = {
                    Column {
                        val formula = substance.formattedMolecularFormula
                        if (formula.isNotBlank()) {
                            FormattedMolecularFormula(
                                Modifier.weight(0.25f),
                                formula = formula
                            )
                        } else {
                            Text(substance.molecularFormula)
                        }
                        Text(substance.casNumber)
                    }
                },
                trailingContent = {
                    Row(
                        Modifier.weight(0.25f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
//                    MoveUpAndDown( // Todo think about this
//                        list = substances,
//                        index = index
//                    )
                        AdaptiveButton(
                            icon = Icons.Filled.Source,
                            label = stringResource(substance.source.first.label),
                            onClick = {
                                uriHandler.openUri(substance.source.second)
                            },
                            enabled = substance.source.first != Source.Custom
                        )
                        FilledIconButton(
                            onClick = { if (index >= 0 && index <= substances.lastIndex) substances.removeAt(index) },
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(Res.string.delete))
                        }
                    }
                }
            )
        }
    }
}
