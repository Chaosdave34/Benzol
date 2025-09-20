package io.github.chaosdave34.benzol.ui.preview

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun Page2(
    modifier: Modifier = Modifier,
    humanAndEnvironmentDanger: List<String>,
    rulesOfConduct: List<String>,
    inCaseOfDanger: List<String>,
    disposal: List<String>
) {
    Page(
        modifier = modifier
    ) {

        ListWithTitle(
            title = Res.string.human_and_environment_danger,
            list = humanAndEnvironmentDanger
        )
        ListWithTitle(
            title = Res.string.rules_of_conduct,
            list = rulesOfConduct
        )
        ListWithTitle(
            title = Res.string.in_case_of_danger,
            list = inCaseOfDanger
        )
        ListWithTitle(
            title = Res.string.disposal,
            list = disposal
        )

        ListRow {
            SignatureBox(
                weight = 7f,
                signatureDescription = Res.string.signature_1
            )
            SignatureBox(
                weight = 5f,
                signatureDescription = Res.string.signature_2
            )
        }
    }
}

@Composable
private fun ListWithTitle(
    title: StringResource,
    list: List<String>
) {
    val list = list.map { it.trim(); it.replace("\n", "") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            Text(
                text = stringResource(title),
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier.padding(start = 20.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            list.forEach {
                Row {
                    Text("\u2022 ")
                    Text(it)
                }
            }
        }
    }
}

@Composable
private fun RowScope.SignatureBox(
    weight: Float,
    signatureDescription: StringResource
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
            .padding(10.dp)
            .weight(weight)
    ) {
        Text(stringResource(signatureDescription))

        Spacer(Modifier.weight(1f).heightIn(min = 80.dp))

        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(0.7f),
                color = MaterialTheme.colorScheme.outlineVariant, thickness = 2.dp
            )
            Row(
                modifier = Modifier.fillMaxWidth(0.7f).padding(start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(Res.string.signature), Modifier.weight(1f))
                Text(stringResource(Res.string.location_and_date), Modifier.weight(1f))
            }
        }
    }
}