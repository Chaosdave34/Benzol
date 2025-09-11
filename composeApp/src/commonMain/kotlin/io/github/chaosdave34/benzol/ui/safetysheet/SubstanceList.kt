package io.github.chaosdave34.benzol.ui.safetysheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Source
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.Substance
import io.github.chaosdave34.benzol.search.Source
import io.github.chaosdave34.benzol.ui.FormattedMolecularFormula
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
        Row {
            Text(
                modifier = Modifier.weight(0.25f),
                text = stringResource(Res.string.name),
                style = MaterialTheme.typography.titleMediumEmphasized
            )
            Text(
                modifier = Modifier.weight(0.25f),
                text = stringResource(Res.string.molecular_formula),
                style = MaterialTheme.typography.titleMediumEmphasized
            )
            Text(
                modifier = Modifier.weight(0.5f),
                text = stringResource(Res.string.cas_number),
                style = MaterialTheme.typography.titleMediumEmphasized
            )
        }

        Spacer(Modifier.height(8.dp))

        substances.forEachIndexed { index, substance ->
            HorizontalDivider(thickness = 2.dp)
            Row(
                Modifier
                    .clickable(
                        onClick = { onSubstanceClick(index) }
                    )
                    .padding(start = 8.dp)
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(0.25f),
                    text = substance.name,
                )

                val formula = substance.formattedMolecularFormula
                if (formula.isNotBlank()) {
                    FormattedMolecularFormula(
                        Modifier.weight(0.25f),
                        formula = formula
                    )
                } else {
                    Text(
                        modifier = Modifier.weight(0.25f),
                        text = substance.molecularFormula,
                    )
                }
                Text(
                    modifier = Modifier.weight(0.25f),
                    text = substance.casNumber,
                )
                Row(
                    Modifier.weight(0.25f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
//                    MoveUpAndDown( // Todo think about this
//                        list = substances,
//                        index = index
//                    )
                    FilledIconButton(
                        onClick = { if (index >= 0 && index <= substances.lastIndex) substances.removeAt(index) },
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(Res.string.delete))
                    }
                    Button(
                        onClick = {
                            uriHandler.openUri(substance.source.second)
                        },
                        enabled = substance.source.first != Source.CUSTOM,
                    ) {
                        Icon(
                            Icons.Filled.Source,
                            contentDescription = stringResource(Res.string.source),
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(
                            substance.source.first.displayName,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

