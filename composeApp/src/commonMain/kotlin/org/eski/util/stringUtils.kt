package org.eski.util

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.max

inline fun String.rightPad(finalLength: Int): String {
  val spacesToAdd = max(0, finalLength - this.length)
  return "$this${" ".repeat(spacesToAdd)}"
}

@Composable fun StringResource.value() = stringResource(this)
@Composable fun StringResource.with(vararg formatArgs: Any) = stringResource(this, formatArgs)