package io.github.chaosdave34.benzol.data

import benzol.composeapp.generated.resources.*
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.getStringArray

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
) {
    companion object {
        suspend fun default(): SafetySheetInputState {
            return SafetySheetInputState(
                documentTitle = getString(Res.string.document_title_default),
                organisation = getString(Res.string.organisation_default),
                course = getString(Res.string.course_default),

                inCaseOfDanger = getStringArray(Res.array.in_case_of_danger_defaults),
                rulesOfConduct = getStringArray(Res.array.rules_of_conduct_defaults),
            )
        }
    }
}