package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.Substance
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.text.Typography.nbsp

private val modifier = Modifier.fillMaxWidth().border(1.dp, Color.White).padding(10.dp)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Preview(
    documentTitle: String,
    organisation: String,
    course: String,
    name: String,
    place: String,
    assistant: String,
    preparation: String,
    substanceList: SnapshotStateList<Substance>,
    humanAndEnvironmentDanger: SnapshotStateList<String>,
    rulesOfConduct: SnapshotStateList<String>,
    inCaseOfDanger: SnapshotStateList<String>,
    disposal: SnapshotStateList<String>
) {
    Text(stringResource(Res.string.preview))

    FlowRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Page 1
        Column(
            modifier = Modifier.padding(5.dp).widthIn(max = 900.dp).weight(1f)
        ) {
            Text(
                modifier = modifier,
                text = documentTitle,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = modifier,
                text = organisation,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = modifier,
                text = course,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            ListRow {
                TextBlock(
                    weight = 13f,
                    title = Res.string.name_with_plural,
                    content = name
                )
                TextBlock(
                    weight = 8f,
                    title = Res.string.place,
                    content = place
                )
                TextBlock(
                    weight = 13f,
                    title = Res.string.assistant,
                    content = assistant
                )
            }
            Column(
                modifier = modifier
            ) {
                Text(stringResource(Res.string.preparation))
                Text(
                    text = preparation,
                    fontWeight = FontWeight.Bold
                )
            }
            ListRow {
                TextBlock(
                    weight = 6f,
                    title = Res.string.used_substances
                )
                TextBlock(
                    weight = 4f,
                    title = Res.string.molar_mass_with_unit
                )
                TextBlock(
                    weight = 4f,
                    title = Res.string.temperatures
                )
                TextBlock(
                    weight = 6f,
                    title = Res.string.ghs_pictograms
                )
                TextBlock(
                    weight = 6f,
                    title = Res.string.h_and_p_phrases_number
                )
                TextBlock(
                    weight = 4f,
                    title = Res.string.mak_ld50_wgk
                )
                TextBlock(
                    weight = 4f,
                    title = Res.string.quantity_required
                )
            }
            substanceList.forEach {
                ListRow {
                    SubstanceColumn(6f) {
                        Text(it.name)
                        if (it.formattedMolecularFormula.isNotBlank()) {
                            it.FormattedMolecularFormula()
                        } else {
                            Text(it.molecularFormula)
                        }
                    }
                    SubstanceColumn(4f) {
                        Text(valueOrDash(it.molarMass))
                    }
                    SubstanceColumn(4f) {
                        Text(valueOrDash(it.boilingPoint, stringResource(Res.string.celsius_unit)))
                        Text(valueOrDash(it.meltingPoint, stringResource(Res.string.celsius_unit)))
                    }
                    SubstanceColumn(6f) {
                        Row {
                            it.ghsPictograms.forEach {
                                Column(
                                    modifier = Modifier.sizeIn(maxWidth = 50.dp, maxHeight = 50.dp).weight(1f)
                                ) {
                                    Image(
                                        painter = painterResource(it.drawableResource),
                                        contentDescription = it.alt
                                    )
                                }
                            }
                        }
                        Text(it.signalWord)
                    }
                    SubstanceColumn(6f) {
                        Text(it.hPhrases.joinToString("-") { it.first })
                        Text("")
                        Text(it.pPhrases.joinToString("-") { it.first })
                    }
                    SubstanceColumn(4f) {
                        Text(valueOrDash(it.mak, stringResource(Res.string.mak_unit)))
                        Text(valueOrDash(it.lethalDose, stringResource(Res.string.lethal_dose_unit)))
                        Text(valueOrDash(it.wgk))
                    }
                    SubstanceColumn(4f) {
                        Text(if (it.quantity.value.isNotBlank()) "${it.quantity.value} ${it.quantity.unit}" else "")
                    }
                }
            }
            Text(
                modifier = modifier,
                text = stringResource(Res.string.title_h_and_p_phrases),
                textAlign = TextAlign.Center
            )
            ListRow {
                PhraseList(
                    weight = 1f,
                    list = substanceList,
                    transform = { it.hPhrases }
                )
                PhraseList(
                    weight = 1f,
                    list = substanceList,
                    transform = { it.pPhrases }
                )
            }

            Row(
                modifier = modifier
            ) {
                Text(
                    text = "Quellen: ",
                    fontWeight = FontWeight.Bold
                )
                Text(Substance.formatSource(substanceList))
            }
        }

        // Page 2
        Column(
            modifier = Modifier.padding(5.dp).widthIn(max = 900.dp).weight(1f)
        ) {
            ListWithTitle(
                modifier = modifier,
                title = Res.string.human_and_environment_danger,
                list = humanAndEnvironmentDanger
            )
            ListWithTitle(
                modifier = modifier,
                title = Res.string.rules_of_conduct,
                list = rulesOfConduct
            )
            ListWithTitle(
                modifier = modifier,
                title = Res.string.in_case_of_danger,
                list = inCaseOfDanger
            )
            ListWithTitle(
                modifier = modifier,
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
}


@Composable
fun RowScope.SubstanceColumn(weight: Float, content: @Composable (ColumnScope.() -> Unit)) {
    Column(
        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White).padding(10.dp).fillMaxHeight().weight(weight),
        verticalArrangement = Arrangement.Center,
        content = content
    )
}

@Composable
fun ListWithTitle(modifier: Modifier, title: StringResource, list: SnapshotStateList<String>) {
    val list = list.map { it.trim(); it.replace("\n", "") }
    Column(
        modifier = modifier.padding(10.dp)
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
fun RowScope.PhraseList(weight: Float, list: SnapshotStateList<Substance>, transform: (Substance) -> List<Pair<String, String>>) {
    Column(
        modifier = modifier.weight(weight).fillMaxHeight()
    ) {
        Substance.formatPhrases(list, transform).iterator().forEach {
            Text("${it.first}: ${it.second}")
        }
    }
}

@Composable
fun ListRow(content: @Composable (RowScope.() -> Unit)) {
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
        content = content
    )
}

@Composable
fun RowScope.TextBlock(weight: Float, title: StringResource, content: String? = null) {
    Column(
        modifier = modifier.weight(weight).fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(title))
        if (content != null) {
            Text(content)
        }
    }
}

@Composable
fun RowScope.SignatureBox(weight: Float, signatureDescription: StringResource) {
    Column(
        modifier = modifier.weight(weight).fillMaxHeight()
    ) {
        Text(stringResource(signatureDescription))

        Spacer(Modifier.weight(1f).heightIn(min = 80.dp))

        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(0.7f),
                color = MaterialTheme.colorScheme.onBackground, thickness = 2.dp
            )
            Row(
                modifier = Modifier.fillMaxWidth(0.7f).padding(start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(Res.string.signature))
                Text(stringResource(Res.string.location_and_date))
            }
        }
    }
}

private fun valueOrDash(value: String, unit: String = ""): String {
    return if (value.isBlank()) "-"
    else if (unit.isBlank()) value else value + nbsp + unit
}