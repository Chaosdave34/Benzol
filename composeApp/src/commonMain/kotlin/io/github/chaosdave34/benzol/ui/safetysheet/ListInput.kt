package io.github.chaosdave34.benzol.ui.safetysheet

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.add
import benzol.composeapp.generated.resources.delete
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ListInput(
    list: List<String>,
    onRemove: (Int) -> Unit,
    onValueChange: (Int, String) -> Unit,
    onAdd: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        list.forEachIndexed { index, element ->
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("•")
                Spacer(Modifier.width(12.dp))
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = element,
                    onValueChange = { onValueChange(index, it) },
                    trailingIcon = {
                        IconButton(
                            onClick = { onRemove(index) },
                        ) {
                            Icon(Icons.Filled.Delete, stringResource(Res.string.delete))
                        }
                    }
                )
                Spacer(Modifier.width(4.dp))
//                MoveUpAndDown( Todo think about this
//                    list = list,
//                    index = index,
//                    padding = 10.dp
//                )
            }
        }
        FilledIconButton(
            onClick = onAdd,
        ) {
            Icon(Icons.Filled.Add, stringResource(Res.string.add))
        }
    }
}