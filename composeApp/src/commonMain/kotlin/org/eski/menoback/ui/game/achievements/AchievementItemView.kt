package org.eski.menoback.ui.game.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AchievementItem(achievement: Achievement) {
  if (!achievement.unlocked && achievement.secret) return

  val backgroundColor = if (!achievement.unlocked) {
    Color(0xFF444444)
  } else {
    Color(0xFF1E5F74)
  }

  Card(
    backgroundColor = backgroundColor,
    shape = RoundedCornerShape(8.dp),
    elevation = 2.dp,
    modifier = Modifier.fillMaxWidth()
  ) {
    Box {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          AchievementIcon(
            achievement = achievement,
            modifier = Modifier.size(48.dp)
          )

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = achievement.title,
              color = if (!achievement.unlocked) Color.Gray else Color.White,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            Text(
              text = achievement.description,
              color = if (!achievement.unlocked) Color.Gray.copy(alpha = 0.7f) else Color.LightGray,
              fontSize = 14.sp,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )

            if (achievement.unlocked && achievement.progress != null) {
              Spacer(modifier = Modifier.height(4.dp))

              LinearProgressIndicator(
                progress = achievement.progress ?: 0f,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(4.dp)
                  .clip(RoundedCornerShape(2.dp)),
                color = Color.Cyan,
                backgroundColor = Color.DarkGray.copy(alpha = 0.5f)
              )

              Text(
                text = "${((achievement.progress ?: 0f) * 100).toInt()}%",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
              )
            }
          }

          if (achievement.unlocked) {
            Spacer(modifier = Modifier.width(8.dp))

            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Completed",
              tint = Color.Green,
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }
    }
  }
}