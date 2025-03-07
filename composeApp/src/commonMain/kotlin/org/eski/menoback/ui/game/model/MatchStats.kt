package org.eski.menoback.ui.game.model

data class MatchStats(
    val correctMatches: Int = 0,
    val correctNonMatches: Int = 0,
    val incorrectMatches: Int = 0,
    val missedMatches: Int = 0
) {
    val totalDecisions: Int = correctMatches + correctNonMatches + incorrectMatches + missedMatches

    val totalMatchOpportunities: Int = correctMatches + missedMatches

    val totalNonMatchOpportunities: Int = correctNonMatches + incorrectMatches

    val accuracyPercentage: Float =
        if (totalDecisions > 0) ((correctMatches + correctNonMatches).toFloat() / totalDecisions) * 100
        else 0f

    fun formatAccuracy(): String {
        val accuracy = accuracyPercentage.toString()
        return if (!accuracy.contains('.')) "$accuracy%"
            else "${accuracy.subSequence(0, accuracy.indexOf('.') + 2)}%"
    }
}