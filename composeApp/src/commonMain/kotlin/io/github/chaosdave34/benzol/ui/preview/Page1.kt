package io.github.chaosdave34.benzol.ui.preview

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.data.Substance
import io.github.chaosdave34.benzol.ui.SafetySheetViewModel
import io.github.chaosdave34.benzol.ui.safetysheet.FormattedMolecularFormula
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.text.Typography.nbsp

context(viewModel: SafetySheetViewModel)
@Composable
fun Page1(modifier: Modifier = Modifier) {
    Page(
        modifier = modifier
    ) {
        val inputState by viewModel.inputState.collectAsState()

        val modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant).padding(10.dp)

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
        inputState.substances.forEach { substance ->
            ListRow {
                SubstanceColumn(6f) {
                    CenteredText(substance.name)
                    if (substance.formattedMolecularFormula.isNotBlank()) {
                        FormattedMolecularFormula(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            formula = substance.formattedMolecularFormula
                        )
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
                list = inputState.substances,
                transform = { it.hPhrases }
            )
            PhraseList(
                weight = 1f,
                list = inputState.substances,
                transform = { it.pPhrases }
            )
        }

        Row(
            modifier = modifier
        ) {
            Text(
                text = stringResource(Res.string.sources) + ": ",
                fontWeight = FontWeight.Bold
            )
            @Suppress("SimplifiableCallChain")
            Text(Substance.sources(inputState.substances).map { stringResource(it.label) }.joinToString(", "))
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
private fun RowScope.TextBlock(
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
private fun CenteredText(content: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = content,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun RowScope.SubstanceColumn(weight: Float, content: @Composable (ColumnScope.() -> Unit)) {
    Column(
        modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant).padding(10.dp).fillMaxHeight().weight(weight),
        verticalArrangement = Arrangement.Center,
        content = content
    )
}

@Composable
private fun RowScope.PhraseList(
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

private fun valueOrDash(
    value: String,
    unit: String = ""
): String {
    return if (value.isBlank()) "-"
    else if (unit.isBlank()) value else value + nbsp + unit
}
