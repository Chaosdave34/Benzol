package io.github.chaosdave34.benzol.ui.preview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.Substance
import io.github.chaosdave34.benzol.ui.AppPageBox
import io.github.chaosdave34.benzol.ui.SafetySheetViewModel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.text.Typography.nbsp

@Composable
fun PreviewPage(
    viewModel: SafetySheetViewModel
) {
    val modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant).padding(10.dp)

    val inputState by viewModel.inputState.collectAsState()

    val substances by viewModel.substances.collectAsState()
    val humanAndEnvironmentDanger by viewModel.humanAndEnvironmentDanger.collectAsState()
    val rulesOfConduct by viewModel.rulesOfConduct.collectAsState()
    val inCaseOfDanger by viewModel.inCaseOfDanger.collectAsState()
    val disposal by viewModel.disposal.collectAsState()

    AppPageBox(
        title = stringResource(Res.string.preview)
    ) { scrollState ->
        FlowRow(
            Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Page {
                HeaderTitle(inputState.documentTitle)
                HeaderTitle(inputState.organisation)
                HeaderTitle(inputState.course)

                ListRow {
                    TextBlock(
                        weight = 13f,
                        title = Res.string.name_with_plural,
                        content = inputState.name.trim()
                    )
                    TextBlock(
                        weight = 8f,
                        title = Res.string.place,
                        content = inputState.place.trim()
                    )
                    TextBlock(
                        weight = 13f,
                        title = Res.string.assistant,
                        content = inputState.assistant.trim()
                    )
                }
                Column(
                    modifier = modifier
                ) {
                    Text(stringResource(Res.string.preparation))
                    Text(
                        text = inputState.preparation.trim(),
                        fontWeight = FontWeight.Bold
                    )
                }
                ListRow {
                    TextBlock(
                        weight = 6f,
                        title = Res.string.used_substances,
                        textAlign = TextAlign.Center
                    )
                    TextBlock(
                        weight = 4f,
                        title = Res.string.molar_mass_with_unit,
                        textAlign = TextAlign.Center
                    )
                    TextBlock(
                        weight = 4f,
                        title = Res.string.temperatures,
                        textAlign = TextAlign.Center
                    )
                    TextBlock(
                        weight = 6f,
                        title = Res.string.ghs_pictograms,
                        textAlign = TextAlign.Center
                    )
                    TextBlock(
                        weight = 6f,
                        title = Res.string.h_and_p_phrases_number,
                        textAlign = TextAlign.Center
                    )
                    TextBlock(
                        weight = 4f,
                        title = Res.string.mak_ld50_wgk,
                        textAlign = TextAlign.Center
                    )
                    TextBlock(
                        weight = 4f,
                        title = Res.string.quantity_required,
                        textAlign = TextAlign.Center
                    )
                }
                substances.forEach { substance ->
                    ListRow {
                        SubstanceColumn(6f) {
                            CenteredText(substance.name)
                            if (substance.formattedMolecularFormula.isNotBlank()) {
                                substance.FormattedMolecularFormula(modifier = Modifier.fillMaxWidth(), align = TextAlign.Center)
                            } else {
                                CenteredText(substance.molecularFormula)
                            }
                        }
                        SubstanceColumn(4f) {
                            CenteredText(valueOrDash(substance.molarMass))
                        }
                        SubstanceColumn(4f) {
                            CenteredText(valueOrDash(substance.boilingPoint, stringResource(Res.string.celsius_unit)))
                            CenteredText(valueOrDash(substance.meltingPoint, stringResource(Res.string.celsius_unit)))
                        }
                        SubstanceColumn(6f) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                substance.ghsPictograms.forEach {
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
                            CenteredText(substance.signalWord)
                        }
                        SubstanceColumn(6f) {
                            CenteredText(substance.hPhrases.joinToString("-") { it.first })
                            Text("")
                            CenteredText(substance.pPhrases.joinToString("-") { it.first })
                        }
                        SubstanceColumn(4f) {
                            CenteredText(valueOrDash(substance.mak, stringResource(Res.string.mak_unit)))
                            CenteredText(valueOrDash(substance.lethalDose, stringResource(Res.string.lethal_dose_unit)))
                            CenteredText(valueOrDash(substance.wgk))
                        }
                        SubstanceColumn(4f) {
                            CenteredText(if (substance.quantity.value.isNotBlank()) "${substance.quantity.value} ${substance.quantity.unit}" else "")
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
                        list = substances,
                        transform = { it.hPhrases }
                    )
                    PhraseList(
                        weight = 1f,
                        list = substances,
                        transform = { it.pPhrases }
                    )
                }

                Row(
                    modifier = modifier
                ) {
                    Text(
                        text = stringResource(Res.string.sources) + " ",
                        fontWeight = FontWeight.Bold
                    )
                    Text(Substance.formatSource(substances))
                }
            }

            Page {
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
    }
}

@Composable
private fun HeaderTitle(text: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
            .padding(10.dp),
        text = text.trim(),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun RowScope.Page(
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(12.dp)
            .widthIn(max = 900.dp)
            .weight(1f),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            Modifier
                .border(2.dp, MaterialTheme.colorScheme.outlineVariant),
            content = content
        )
    }
}


@Composable
fun CenteredText(content: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = content,
        textAlign = TextAlign.Center
    )
}

@Composable
fun RowScope.SubstanceColumn(weight: Float, content: @Composable (ColumnScope.() -> Unit)) {
    Column(
        modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant).padding(10.dp).fillMaxHeight().weight(weight),
        verticalArrangement = Arrangement.Center,
        content = content
    )
}

@Composable
fun ListWithTitle(
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
fun RowScope.PhraseList(
    weight: Float,
    list: List<Substance>,
    transform: (Substance) -> List<Pair<String, String>>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
            .padding(10.dp)
            .weight(weight)
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
fun RowScope.TextBlock(
    weight: Float,
    title: StringResource,
    content: String? = null,
    textAlign: TextAlign = TextAlign.Start
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
            .padding(10.dp)
            .weight(weight),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(title),
            textAlign = textAlign
        )
        if (content != null) {
            Text(content)
        }
    }
}

@Composable
fun RowScope.SignatureBox(
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

private fun valueOrDash(
    value: String,
    unit: String = ""
): String {
    return if (value.isBlank()) "-"
    else if (unit.isBlank()) value else value + nbsp + unit
}