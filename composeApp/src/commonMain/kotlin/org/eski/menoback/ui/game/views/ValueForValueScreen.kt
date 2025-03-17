package org.eski.menoback.ui.game.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.eski.menoback.ui.game.vm.ValueForValueViewModel
import org.eski.ui.animation.AnimateView
import org.eski.ui.util.PlatformBackHandler
import org.eski.ui.util.grid2
import org.eski.ui.views.spacer


// Define custom colors for better visual appeal
private val primaryCardColor = Color(0xFF2C3E50)
private val accentColor = Color(0xFF3498DB)
private val timeColor = Color(0xFF27AE60)
private val talentColor = Color(0xFFE67E22)
private val treasureColor = Color(0xFFE74C3C)
private val textColor = Color.White
private val sectionHeaderColor = Color(0xFFECF0F1)
private val dividerColor = Color(0x33FFFFFF)

@Composable fun ValueForValueScreen(vm: ValueForValueViewModel, visible: Boolean) {
  val scrollState = rememberScrollState()
  val uriHandler = LocalUriHandler.current

  Box(
    modifier = Modifier.fillMaxWidth().padding(grid2)
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().verticalScroll(scrollState)
    ) {
      Spacer(modifier = Modifier.height(grid2 * 2))

      // Title
      AnimateView(
        modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
        visible = visible,
        enter = fadeIn(animationSpec = tween(300, 200)),
        exit = fadeOut(animationSpec = tween(150, 0))
      ) {
        Text(
          "Value for Value",
          fontSize = 28.sp,
          fontWeight = FontWeight.Bold,
          color = textColor,
        )
      }

      spacer(height = grid2)

      // Main description card
      AnimateView(
        modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
        visible = visible,
        enter = slideInHorizontally(animationSpec = tween(300, 200), initialOffsetX = { width -> width / 2 }),
        exit = slideOutHorizontally(targetOffsetX = { width -> width / 2 })
      ) {
        Card(
          backgroundColor = primaryCardColor,
          shape = RoundedCornerShape(12.dp),
          elevation = 4.dp,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              "MeNoBack is built on the value for value model. If you receive value from this game, consider giving value back in one of the following ways:",
              color = textColor,
              fontSize = 16.sp,
              lineHeight = 24.sp
            )
          }
        }
      }

      spacer(height = grid2 * 1.5f)
      Treasure(visible, uriHandler)
      spacer(height = grid2)
      Time(visible)
      spacer(height = grid2)
      Talent(visible, uriHandler)
      spacer(height = grid2 * 1.5f)

      // Feature request section
      AnimateView(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300, 700)),
        exit = fadeOut(animationSpec = tween(150, 0))
      ) {
        Card(
          backgroundColor = primaryCardColor,
          shape = RoundedCornerShape(12.dp),
          elevation = 4.dp,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              "Upcoming Features",
              color = sectionHeaderColor,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(bottom = 8.dp)
            )

            Divider(color = dividerColor, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            Text(
              "Here are features we're planning to implement. When donating, let us know which ones you'd like to see!",
              color = textColor,
              fontSize = 16.sp,
              lineHeight = 22.sp,
              modifier = Modifier.padding(bottom = 12.dp)
            )

            FeatureList()
          }
        }
      }

      spacer(height = grid2 * 3)
    }

    // Back button
    BackButton(vm, visible)
  }
}

@Composable
private fun ValueSection(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  color: Color,
  visible: Boolean,
  delay: Int,
  content: @Composable () -> Unit
) {
  AnimateView(
    visible = visible,
    enter = slideInHorizontally(animationSpec = tween(300, delay), initialOffsetX = { width -> width }),
    exit = slideOutHorizontally(targetOffsetX = { width -> width })
  ) {
    Card(
      backgroundColor = primaryCardColor,
      shape = RoundedCornerShape(12.dp),
      elevation = 4.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        // Title row with icon
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(bottom = 8.dp)
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(28.dp)
          )

          Spacer(modifier = Modifier.width(12.dp))

          Text(
            title,
            color = color,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Divider(color = dividerColor, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

        // Section content
        content()
      }
    }
  }
}

@Composable
private fun BulletPoint(text: String) {
  Row(
    verticalAlignment = Alignment.Top,
    modifier = Modifier.padding(vertical = 4.dp)
  ) {
    Text(
      "•",
      color = textColor,
      fontSize = 16.sp,
      modifier = Modifier.padding(end = 8.dp, top = 2.dp)
    )

    Text(
      text,
      color = textColor,
      fontSize = 16.sp
    )
  }
}

@Composable
private fun FeatureList() {
  val features = listOf(
    "Icon and color tuning",
    "Third n-back stimulus",
    "Windows build + testing",
    "Linux build + testing",
    "Configurable theming",
    "Mac toolbar styling",
    "Recursive gravity",
    "Add invisible top row",
    "Dig mode",
    "Multiplayer",
    "Blind mode",
    "Add sound effects + mute button",
    "Achievements (level progressions, high scores)",
    "Extra time before lock",
    "Mobile friendly version"
  )

  features.forEach { feature ->
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(vertical = 4.dp)
    ) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .background(accentColor, shape = RoundedCornerShape(50))
          .align(Alignment.CenterVertically)
      )

      Spacer(modifier = Modifier.width(12.dp))

      Text(
        feature,
        color = textColor,
        fontSize = 15.sp
      )
    }
  }

  spacer(height = 12.dp)

//  Button(
//    onClick = { /* TODO: Open a feedback form or email */ },
//    modifier = Modifier.align(Alignment.CenterHorizontally)
//  ) {
//    Text("Vote on Features")
//  }
}

@Composable
private fun ColumnScope.Treasure(visible: Boolean, uriHandler: UriHandler) {
  // Treasure section
  ValueSection(
    title = "Treasure",
    icon = Icons.Default.MonetizationOn,
    color = treasureColor,
    visible = visible,
    delay = 500
  ) {
    Text(
      "Financial support helps ensure continued development:",
      color = textColor,
      fontSize = 16.sp,
      modifier = Modifier.padding(bottom = 8.dp)
    )

    BulletPoint("One-time donations help fund new features")
    BulletPoint("Small recurring donations provide sustainable support")
    BulletPoint("Your contributions directly fund development time")

    spacer(height = 12.dp)

    Button(
      onClick = { uriHandler.openUri("https://www.paypal.com/donate/?hosted_button_id=LJDNZTM4YZYL4") },
      modifier = Modifier.align(Alignment.CenterHorizontally)
    ) {
      Text("Support via PayPal")
    }
  }
}

@Composable fun ColumnScope.Time(visible: Boolean) {
  // Time section
  ValueSection(
    title = "Time",
    icon = Icons.Default.AccessTime,
    color = timeColor,
    visible = visible,
    delay = 300
  ) {
    Text(
      "Spread the word about MeNoBack! Some ways to contribute your time:",
      color = textColor,
      fontSize = 16.sp,
      modifier = Modifier.padding(bottom = 8.dp)
    )

    BulletPoint("Tell your friends and family about the game")
    BulletPoint("Share your experience on social media")
    BulletPoint("Create videos or tutorials about MeNoBack")
    BulletPoint("Write a review on app stores or gaming platforms")
  }
}

@Composable fun ColumnScope.Talent(visible: Boolean, uriHandler: UriHandler) {
  // Talent section
  ValueSection(
    title = "Talent",
    icon = Icons.Default.Code,
    color = talentColor,
    visible = visible,
    delay = 400
  ) {
    Text(
      "Contribute your skills to help improve MeNoBack:",
      color = textColor,
      fontSize = 16.sp,
      modifier = Modifier.padding(bottom = 8.dp)
    )

    BulletPoint("Contribute code or fix bugs")
    BulletPoint("Improve UI/UX design")
    BulletPoint("Create artwork or sound effects")
    BulletPoint("Help with testing on different platforms")
    BulletPoint("Translate the game to other languages")

    spacer(height = 12.dp)

    Button(
      onClick = { uriHandler.openUri("https://github.com/davebren/me-no-back") },
      modifier = Modifier.align(Alignment.CenterHorizontally)
    ) {
      Text("Visit GitHub Repository")
    }
  }
}

@Composable
private fun BackButton(vm: ValueForValueViewModel, visible: Boolean) {
  if (visible) PlatformBackHandler(onBack = { vm.dismissed() })

  AnimatedVisibility(
    visible = visible,
    enter = fadeIn(animationSpec = tween(300, 200)),
    exit = fadeOut(animationSpec = tween(150, 0))
  ) {
    IconButton(onClick = { vm.dismissed() }) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "Back",
        tint = textColor,
        modifier = Modifier.size(28.dp)
      )
    }
  }
}