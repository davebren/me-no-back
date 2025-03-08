package org.eski.animations

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset

fun Float.tweenTo(end: Float, tween: Float) = ((end - this) * tween) + this

fun DpOffset.tweenTo(end: DpOffset, tween: Float) = DpOffset(this.x.tweenTo(end.x, tween), this.y.tweenTo(end.y, tween))
fun Dp.tweenTo(end: Dp, tween: Float) = Dp(this.value.tweenTo(end.value, tween))