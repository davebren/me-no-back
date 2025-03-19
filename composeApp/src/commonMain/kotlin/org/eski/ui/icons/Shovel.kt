package org.eski.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Shovel: ImageVector
  get() {
    if (_shovel != null) {
      return _shovel!!
    }
    _shovel = Builder(
      name = "Shovel",
      defaultWidth = 24.0.dp,
      defaultHeight = 24.0.dp,
      viewportWidth = 24.0f,
      viewportHeight = 24.0f
    ).apply {
      path(
        fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
        pathFillType = NonZero
      ) {
        moveTo(15.1f, 1.81f)
        lineToRelative(-2.83f, 2.83f)
        curveToRelative(-0.77f, 0.78f, -0.77f, 2.05f, 0.0f, 2.83f)
        lineToRelative(1.41f, 1.41f)
        lineToRelative(-4.55f, 4.55f)
        lineToRelative(-2.82f, -2.83f)
        lineTo(4.89f, 12.0f)
        curveToRelative(-4.95f, 5.0f, -1.39f, 8.5f, -1.39f, 8.5f)
        reflectiveCurveTo(7.0f, 24.0f, 12.0f, 19.09f)
        lineToRelative(1.41f, -1.41f)
        lineToRelative(-2.8f, -2.8f)
        lineToRelative(4.54f, -4.54f)
        lineToRelative(1.39f, 1.39f)
        curveToRelative(0.78f, 0.77f, 2.05f, 0.77f, 2.83f, 0.0f)
        lineTo(22.2f, 8.9f)
        close()
        moveToRelative(2.83f, 8.47f)
        lineTo(16.55f, 8.9f)
        lineToRelative(-1.44f, -1.44f)
        lineToRelative(-1.4f, -1.4f)
        lineToRelative(1.41f, -1.41f)
        lineToRelative(4.23f, 4.23f)
        close()
      }
    }
      .build()
    return _shovel!!
  }

private var _shovel: ImageVector? = null
