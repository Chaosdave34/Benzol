package io.github.chaosdave34.benzol.files

import io.github.chaosdave34.benzol.Substance

data class InputData(
    val fileName: String,
    val documentTitle: String,
    val organisation: String,
    val course: String,
    val name: String,
    val place: String,
    val assistant: String,
    val preparation: String,
    val substances: List<Substance>,
    val humanAndEnvironmentDanger: List<String>,
    val rulesOfConduct: List<String>,
    val inCaseOfDanger: List<String>,
    val disposal: List<String>,
)
