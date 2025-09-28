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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.delete
import benzol.composeapp.generated.resources.edit_substance
import io.github.chaosdave34.benzol.data.Substance
import io.github.chaosdave34.benzol.search.Source
import io.github.chaosdave34.benzol.ui.adaptive.AdaptiveButton
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SubstanceList(
    substances: List<Substance>,
    onSubstanceClick: (Int) -> Unit,
    onRemove: (Int) -> Unit
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
                        if (substance.casNumber.isNotBlank()) {
                            Text(substance.casNumber)
                        }

                        val formula = substance.formattedMolecularFormula
                        if (formula.isNotBlank()) {
                            FormattedMolecularFormula(formula = formula)
                        } else {
                            Text(substance.molecularFormula)
                        }
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
                            onClick = { onRemove(index) },
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(Res.string.delete))
                        }
                    }
                }
            )
        }
    }
}
