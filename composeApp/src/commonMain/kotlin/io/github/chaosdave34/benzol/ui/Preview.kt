package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
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
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.assistant
import benzol.composeapp.generated.resources.celsius_unit
import benzol.composeapp.generated.resources.disposal
import benzol.composeapp.generated.resources.ghs_pictograms
import benzol.composeapp.generated.resources.h_and_p_phrases_number
import benzol.composeapp.generated.resources.human_and_environment_danger
import benzol.composeapp.generated.resources.in_case_of_danger
import benzol.composeapp.generated.resources.lethal_dose_unit
import benzol.composeapp.generated.resources.location_and_date
import benzol.composeapp.generated.resources.mak_ld50_wgk
import benzol.composeapp.generated.resources.mak_unit
import benzol.composeapp.generated.resources.molar_mass_with_unit
import benzol.composeapp.generated.resources.name_with_plural
import benzol.composeapp.generated.resources.place
import benzol.composeapp.generated.resources.preparation
import benzol.composeapp.generated.resources.preview
import benzol.composeapp.generated.resources.quantity_required
import benzol.composeapp.generated.resources.rules_of_conduct
import benzol.composeapp.generated.resources.signature
import benzol.composeapp.generated.resources.signature_1
import benzol.composeapp.generated.resources.signature_2
import benzol.composeapp.generated.resources.temperatures
import benzol.composeapp.generated.resources.title_h_and_p_phrases
import benzol.composeapp.generated.resources.used_substances
import io.github.chaosdave34.benzol.Substance
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.text.Typography.nbsp

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
    val modifier = Modifier.fillMaxWidth().border(1.dp, Color.White).padding(10.dp)

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
                    modifier = modifier.weight(13f),
                    title = Res.string.name_with_plural,
                    content = name
                )
                TextBlock(
                    modifier = modifier.weight(8f),
                    title = Res.string.place,
                    content = place
                )
                TextBlock(
                    modifier = modifier.weight(13f),
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
                    modifier = modifier.weight(6f).fillMaxHeight(),
                    title = Res.string.used_substances
                )
                TextBlock(
                    modifier = modifier.weight(4f).fillMaxHeight(),
                    title = Res.string.molar_mass_with_unit
                )
                TextBlock(
                    modifier = modifier.weight(4f).fillMaxHeight(),
                    title = Res.string.temperatures
                )
                TextBlock(
                    modifier = modifier.weight(6f).fillMaxHeight(),
                    title = Res.string.ghs_pictograms
                )
                TextBlock(
                    modifier = modifier.weight(6f).fillMaxHeight(),
                    title = Res.string.h_and_p_phrases_number
                )
                TextBlock(
                    modifier = modifier.weight(4f).fillMaxHeight(),
                    title = Res.string.mak_ld50_wgk
                )
                TextBlock(
                    modifier = modifier.weight(4f).fillMaxHeight(),
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
                    modifier = modifier.weight(1f),
                    list = substanceList,
                    transform = { it.hPhrases }
                )
                PhraseList(
                    modifier = modifier.weight(1f),
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
                    modifier = modifier.weight(7f),
                    signatureDescription = Res.string.signature_1
                )
                SignatureBox(
                    modifier = modifier.weight(5f),
                    signatureDescription = Res.string.signature_2
                )
            }
        }

    }
}


@Composable
fun RowScope.SubstanceColumn(weight: Float, content: @Composable (ColumnScope.() -> Unit)) { // Todo move modifier global
    Column(
        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White).padding(10.dp).weight(weight).fillMaxHeight(),
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
fun PhraseList(modifier: Modifier, list: SnapshotStateList<Substance>, transform: (Substance) -> List<Pair<String, String>>) {
    Column(
        modifier = modifier.fillMaxHeight()
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
fun TextBlock(modifier: Modifier, title: StringResource, content: String? = null) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(title))
        if (content != null) {
            Text(content)
        }
    }
}

@Composable
fun SignatureBox(modifier: Modifier, signatureDescription: StringResource) {
    Column(
        modifier = modifier.fillMaxHeight()
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