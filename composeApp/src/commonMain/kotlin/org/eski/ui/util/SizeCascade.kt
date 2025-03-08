package org.eski.ui.util

import androidx.compose.ui.unit.Dp

class SizeCascade(val specs: List<Spec>) {
  class Spec(val requiredMin: Dp, val output: Dp)

  fun output(input: Dp): Dp = specs.firstOrNull { it.requiredMin <= input }?.output ?: specs.last().output
}