package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.GHSPictogram
import io.github.chaosdave34.benzol.Substance
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditSubstanceDialog(
    list: SnapshotStateList<Substance>,
    index: Int,
    onClose: () -> Unit
) {
    val scrollState = rememberScrollState()

    val substance = list[index]

    val name = remember { mutableStateOf(substance.name) }
    val casNumber = remember { mutableStateOf(substance.casNumber) }
    val molecularFormula = remember { mutableStateOf(substance.molecularFormula) }
    val formattedMolecularFormula = remember { mutableStateOf(substance.formattedMolecularFormula) }
    val wgk = remember { mutableStateOf(substance.wgk) }
    val signalWord = remember { mutableStateOf(substance.signalWord) }

    val molarMass = remember { mutableStateOf(substance.molarMass) }
    val lethalDose = remember { mutableStateOf(substance.lethalDose) }
    val mak = remember { mutableStateOf(substance.mak) }
    val meltingPoint = remember { mutableStateOf(substance.meltingPoint) }
    val boilingPoint = remember { mutableStateOf(substance.boilingPoint) }
    val quantity = remember { mutableStateOf(substance.quantity.value) }
    val quantityUnit = remember { mutableStateOf(substance.quantity.unit) }

    val hPhrases = remember { mutableStateListOf<Pair<String, String>>() }
    val pPhrases = remember { mutableStateListOf<Pair<String, String>>() }
    val ghsPictograms = remember { mutableStateListOf<GHSPictogram>() }

    hPhrases.addAll(substance.hPhrases)
    pPhrases.addAll(substance.pPhrases)
    ghsPictograms.addAll(substance.ghsPictograms)

    val fillSubstanceOnClose = {
        list[index] = substance.copyAsModified(
            name.value,
            casNumber.value,
            molecularFormula.value,
            formattedMolecularFormula.value,
            wgk.value,
            signalWord.value,
            molarMass.value,
            lethalDose.value,
            mak.value,
            meltingPoint.value,
            boilingPoint.value,
            Substance.Quantity(quantity.value, quantityUnit.value),
            hPhrases,
            pPhrases,
            ghsPictograms
        )
        onClose()
    }

    Dialog(
        onDismissRequest = { onClose() }
    ) {
        Card(
            modifier = Modifier.fillMaxHeight(0.7f),
        ) {
            Column(
                Modifier.padding(start = 10.dp, top = 10.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        modifier = Modifier.verticalScroll(scrollState).weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DefaultColumn {
                            Input(
                                value = name,
                                label = Res.string.name
                            )
                            Input(
                                value = casNumber,
                                label = Res.string.cas_number
                            )
                            Input(
                                value = molecularFormula,
                                label = Res.string.molecular_formula
                            )
                            Input(
                                value = formattedMolecularFormula,
                                label = Res.string.formatted_molecular_formula_with_hint,
                                supportingText = {
                                    if (formattedMolecularFormula.value.isNotBlank()) {
                                        substance.FormattedMolecularFormula(formula = formattedMolecularFormula.value)
                                    }
                                }
                            )
                            Input(
                                value = wgk,
                                label = Res.string.wgk
                            )
                            Input(
                                value = signalWord,
                                label = Res.string.signal_word
                            )
                        }

                        DefaultColumn {
                            InputWithUnit(
                                value = molarMass,
                                label = Res.string.molar_mass,
                                unit = Res.string.molar_mass_unit
                            )
                            InputWithUnit(
                                value = lethalDose,
                                label = Res.string.lethal_dose,
                                unit = Res.string.lethal_dose_unit
                            )
                            InputWithUnit(
                                value = mak,
                                label = Res.string.mak,
                                unit = Res.string.mak_unit
                            )
                            InputWithUnit(
                                value = meltingPoint,
                                label = Res.string.melting_point,
                                unit = Res.string.celsius_unit
                            )
                            InputWithUnit(
                                value = boilingPoint,
                                label = Res.string.boiling_point,
                                unit = Res.string.celsius_unit
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Input(
                                    modifier = Modifier.weight(0.8f),
                                    value = quantity,
                                    label = Res.string.quantity
                                )
                                Column(
                                    modifier = Modifier.weight(0.2f).padding(start = 10.dp)
                                ) {
                                    Input(
                                        value = quantityUnit,
                                        label = Res.string.unit
                                    )
                                }
                            }
                        }

                        PhraseInput(
                            phraseName = Res.string.h_phrases,
                            phrases = hPhrases
                        )
                        PhraseInput(
                            phraseName = Res.string.p_phrases,
                            phrases = pPhrases
                        )

                        DefaultColumn {
                            Text(stringResource(Res.string.ghs_pictograms))
                            Text(stringResource(Res.string.ghs_pictograms_hint))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                GHSPictogram(
                                    modifier = Modifier.weight(0.3f),
                                    pictogram = GHSPictogram.EXPLOSION,
                                    selected = ghsPictograms
                                )
                                GHSPictogram(
                                    modifier = Modifier.weight(0.3f),
                                    pictogram = GHSPictogram.FLAME,
                                    selected = ghsPictograms
                                )
                                GHSPictogram(
                                    modifier = Modifier.weight(0.3f),
                                    pictogram = GHSPictogram.FLAME_OVER_CIRCLE,
                                    selected = ghsPictograms
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                GHSPictogram(
                                    modifier = Modifier.weight(0.3f),
                                    pictogram = GHSPictogram.GAS_BOTTLE,
                                    selected = ghsPictograms
                                )
                                GHSPictogram(
                                    modifier = Modifier.weight(0.3f),
                                    pictogram = GHSPictogram.ACID,
                                    selected = ghsPictograms
                                )
                                GHSPictogram(
                                    modifier = Modifier.weight(0.3f),
                                    pictogram = GHSPictogram.SKULL,
                                    selected = ghsPictograms
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                GHSPictogram(
                                    modifier = Modifier.weight(0.3f),
                                    pictogram = GHSPictogram.EXCLAMATION,
                                    selected = ghsPictograms
                                )
                                GHSPictogram(
                                    modifier = Modifier.weight(0.3f),
                                    pictogram = GHSPictogram.SILHOUETTE,
                                    selected = ghsPictograms
                                )
                                GHSPictogram(
                                    modifier = Modifier.weight(0.3f),
                                    pictogram = GHSPictogram.NATURE,
                                    selected = ghsPictograms
                                )
                            }
                        }
                    }

                    Scrollbar(scrollState)
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(end=20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onClose() }
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }

                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = {
                            name.value = substance.namePair.original
                            casNumber.value = substance.casNumberPair.original
                            molecularFormula.value = substance.molecularFormulaPair.original
                            formattedMolecularFormula.value = substance.formattedMolecularFormulaPair.original
                            wgk.value = substance.wgkPair.original
                            signalWord.value = substance.signalWordPair.original

                            molarMass.value = substance.molarMassPair.original
                            lethalDose.value = substance.lethalDosePair.original
                            mak.value = substance.makPair.original
                            meltingPoint.value = substance.meltingPointPair.original
                            boilingPoint.value = substance.boilingPointPair.original
                            quantity.value = Substance.Quantity().value
                            quantityUnit.value = Substance.Quantity().unit

                            hPhrases.clear()
                            hPhrases.addAll(substance.hPhrasesPair.original)
                            pPhrases.clear()
                            pPhrases.addAll(substance.pPhrasesPair.original)
                            ghsPictograms.clear()
                            ghsPictograms.addAll(substance.ghsPictogramsPair.original)
                        }
                    ) {
                        Icon(Icons.Rounded.Refresh, stringResource(Res.string.reset))
                    }
                    Button(
                        onClick = fillSubstanceOnClose
                    ) {
                        Icon(Icons.Rounded.Check, stringResource(Res.string.accept))
                    }
                }
            }
        }
    }
}

@Composable
fun InputWithUnit(
    value: MutableState<String>,
    onChange: (String) -> Unit = { value.value = it },
    label: StringResource,
    unit: StringResource
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Input(
            modifier = Modifier.weight(0.8f),
            value = value,
            onChange = onChange,
            label = label
        )
        Text(
            modifier = Modifier.weight(0.2f),
            text = stringResource(unit),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PhraseInput(
    phraseName: StringResource,
    phrases: SnapshotStateList<Pair<String, String>>
) {
    DefaultColumn {
        Text(stringResource(phraseName))

        phrases.forEachIndexed { index, phrase ->
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextField(
                    modifier = Modifier.weight(0.3f),
                    value = phrase.first,
                    singleLine = true,
                    onValueChange = { phrases[index] = phrase.copy(first = it) },
                )
                TextField(
                    modifier = Modifier.weight(0.7f),
                    value = phrase.second,
                    singleLine = true,
                    onValueChange = { phrases[index] = phrase.copy(second = it) }
                )
                RemoveListElementButton(
                    list = phrases,
                    index = index
                )
            }
        }
        AddListElementButton(
            list = phrases,
            element = Pair("", "")
        )
    }
}

@Composable
fun GHSPictogram(modifier: Modifier = Modifier, pictogram: GHSPictogram, selected: SnapshotStateList<GHSPictogram>) {
    val isSelected = selected.contains(pictogram)
    Image(
        modifier = modifier.clickable(onClick = {
            if (isSelected) selected.remove(pictogram) else selected.add(pictogram)
        }),
        painter = painterResource(pictogram.drawableResource),
        contentDescription = pictogram.alt,
        colorFilter = if (!isSelected) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null
    )
}
