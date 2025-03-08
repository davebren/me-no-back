package org.eski.menoback.ui.game.model

data class MatchStats(
    val correctShapeMatches: Int = 0,
    val correctShapeNonMatches: Int = 0,
    val incorrectShapeMatches: Int = 0,
    val missedShapeMatches: Int = 0,

    val correctColorMatches: Int = 0,
    val correctColorNonMatches: Int = 0,
    val incorrectColorMatches: Int = 0,
    val missedColorMatches: Int = 0
) {
    val totalDecisions: Int
        get() = correctShapeMatches + correctShapeNonMatches +
            incorrectShapeMatches + missedShapeMatches +
            correctColorMatches + correctColorNonMatches +
            incorrectColorMatches + missedColorMatches

    val totalShapeDecisions: Int
        get() = correctShapeMatches + correctShapeNonMatches +
            incorrectShapeMatches + missedShapeMatches

    val totalShapeMatchOpportunities: Int
        get() = correctShapeMatches + missedShapeMatches

    val shapeAccuracyPercentage: Float
        get() = if (totalShapeDecisions > 0)
            ((correctShapeMatches + correctShapeNonMatches).toFloat() / totalShapeDecisions) * 100
        else 0f

    val totalColorDecisions: Int
        get() = correctColorMatches + correctColorNonMatches +
            incorrectColorMatches + missedColorMatches

    val totalColorMatchOpportunities: Int
        get() = correctColorMatches + missedColorMatches

    val colorAccuracyPercentage: Float
        get() = if (totalColorDecisions > 0)
            ((correctColorMatches + correctColorNonMatches).toFloat() / totalColorDecisions) * 100
        else 0f

    val accuracyPercentage: Float
        get() = if (totalDecisions > 0)
            ((correctShapeMatches + correctShapeNonMatches +
                correctColorMatches + correctColorNonMatches).toFloat() / totalDecisions) * 100
        else 0f

    val correctMatches: Int
        get() = correctShapeMatches + correctColorMatches
    val incorrectMatches: Int
        get() = incorrectColorMatches + incorrectShapeMatches
    val missedMatches: Int
        get() = missedShapeMatches + missedColorMatches
    val correctNonMatches: Int
        get() = correctShapeNonMatches + correctColorNonMatches

    fun formatAccuracy(): String = formatPercentage(accuracyPercentage)
    fun formatShapeAccuracy(): String = formatPercentage(shapeAccuracyPercentage)
    fun formatColorAccuracy(): String = formatPercentage(colorAccuracyPercentage)

    private fun formatPercentage(floatPercentage: Float): String {
        val accuracy = floatPercentage.toString()
        return if (!accuracy.contains('.')) "$accuracy%"
        else "${accuracy.subSequence(0, accuracy.indexOf('.') + 2)}%"
    }
}