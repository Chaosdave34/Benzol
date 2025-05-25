package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.open_settings
import io.github.chaosdave34.benzol.Substance
import io.github.chaosdave34.benzol.search.Source
import org.jetbrains.compose.resources.stringResource

@Composable
fun SubstanceList(
    substanceList: SnapshotStateList<Substance>
) {
    val uriHandler = LocalUriHandler.current

    var dialogOpen: Int? by remember { mutableStateOf(null) }

    substanceList.forEachIndexed { index, substance ->
        HorizontalDivider(thickness = 2.dp)
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = { dialogOpen = index }),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(0.25f),
                text = substance.name
            )

            val formula = substance.formattedMolecularFormula
            if (formula.isNotBlank()) {
                substance.FormattedMolecularFormula(Modifier.weight(0.25f))
            } else {
                Text(
                    modifier = Modifier.weight(0.25f),
                    text = substance.molecularFormula
                )
            }
            Text(
                modifier = Modifier.weight(0.15f),
                text = substance.casNumber
            )
            Row(
                modifier = Modifier.weight(0.35f).padding(end = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End)
            ) {
                Button(
                    onClick = {
                        uriHandler.openUri(substance.source.second)
                    },
                    enabled = substance.source.first != Source.CUSTOM
                ) {
                    Text(substance.source.first.displayName)
                }
                Button(
                    onClick = { dialogOpen = index }
                ) {
                    Icon(Icons.Rounded.Edit, stringResource(Res.string.open_settings))
                }
                MoveUpAndDown(
                    list = substanceList,
                    index = index
                )
                RemoveListElementButton(
                    list = substanceList,
                    index = index
                )
            }
        }
    }

    val index = dialogOpen
    if (index != null) {
        EditSubstanceDialog(
            list = substanceList,
            index = index,
            onClose = { dialogOpen = null }
        )

    }
}