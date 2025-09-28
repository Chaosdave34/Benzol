package io.github.chaosdave34.benzol.data

data class SafetySheetInputState(
    val filename: String = "",
    val documentTitle: String = "",
    val organisation: String = "",
    val course: String = "",
    val name: String = "",
    val place: String = "",
    val assistant: String = "",
    val preparation: String = "",
    val substances: List<Substance> = emptyList(),
    val humanAndEnvironmentDanger: List<String> = emptyList(),
    val inCaseOfDanger: List<String> = emptyList(),
    val rulesOfConduct: List<String> = emptyList(),
    val disposal: List<String> = emptyList()
)