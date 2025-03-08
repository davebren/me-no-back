package org.eski.ui.views

import androidx.compose.ui.unit.dp
import org.eski.ui.util.SizeCascade
import org.eski.ui.util.grid
import org.eski.ui.util.grid2

enum class FloatingButton(
  val unpressedSize: SizeCascade,
  val pressedSize: SizeCascade,
  val bottomMargin: SizeCascade,
) {
  large(
    unpressedSize = SizeCascade(
      listOf(
        SizeCascade.Spec(380.dp, 96.dp),
        SizeCascade.Spec(0.dp, 72.dp),
      )
    ),
    pressedSize = SizeCascade(
      listOf(
        SizeCascade.Spec(380.dp, 112.dp),
        SizeCascade.Spec(0.dp, 88.dp),
      )
    ),
    bottomMargin = SizeCascade(
      listOf(
        SizeCascade.Spec(380.dp, grid2),
        SizeCascade.Spec(0.dp, grid)
      )
    )
  ),

  small(
    unpressedSize = SizeCascade(
      listOf(
        SizeCascade.Spec(0.dp, 72.dp),
      )
    ),
    pressedSize = SizeCascade(
      listOf(
        SizeCascade.Spec(0.dp, 88.dp),
      )
    ),
    bottomMargin = SizeCascade(
      listOf(
        SizeCascade.Spec(0.dp, grid)
      )
    )
  )
}


