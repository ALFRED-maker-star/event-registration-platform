package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AccentCoralDark
import com.example.ui.theme.CardSurface
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryPurpleContainer
import com.example.ui.theme.PrimaryPurpleDark
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventHubTopBar(
  title: String,
  modifier: Modifier = Modifier,
  canNavigateBack: Boolean = false,
  onNavigateBack: () -> Unit = {},
  currentRole: UserRole? = null,
  onToggleRole: (() -> Unit)? = null,
  actions: @Composable () -> Unit = {}
) {
  TopAppBar(
    modifier = modifier.testTag("eventhub_top_bar"),
    title = {
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.3).sp
          ),
          color = TextPrimary
        )
      }
    },
    navigationIcon = {
      if (canNavigateBack) {
        IconButton(
          onClick = onNavigateBack,
          modifier = Modifier.testTag("back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = TextPrimary
          )
        }
      }
    },
    actions = {
      if (currentRole != null && onToggleRole != null) {
        RoleSwitchBadge(
          currentRole = currentRole,
          onToggle = onToggleRole
        )
      }
      actions()
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.background
    )
  )
}

@Composable
fun RoleSwitchBadge(
  currentRole: UserRole,
  onToggle: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .padding(end = 12.dp)
      .clip(RoundedCornerShape(20.dp))
      .clickable(onClick = onToggle)
      .testTag("role_switch_badge"),
    color = PrimaryPurpleContainer,
    tonalElevation = 2.dp
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .background(
            if (currentRole == UserRole.ORGANIZER) PrimaryPurple else AccentCoral,
            CircleShape
          )
      )
      Text(
        text = currentRole.label,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = PrimaryPurpleDark
      )
      Icon(
        imageVector = Icons.Default.SwapHoriz,
        contentDescription = "Switch Role",
        tint = PrimaryPurpleDark,
        modifier = Modifier.size(16.dp)
      )
    }
  }
}

@Composable
fun CoralCtaButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  icon: ImageVector? = null,
  testTag: String = "coral_cta_button"
) {
  Button(
    onClick = onClick,
    enabled = enabled,
    modifier = modifier
      .fillMaxWidth()
      .height(52.dp)
      .testTag(testTag),
    shape = RoundedCornerShape(14.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = AccentCoral,
      contentColor = Color.White,
      disabledContainerColor = AccentCoral.copy(alpha = 0.5f)
    ),
    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 6.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.3.sp
        )
      )
    }
  }
}

@Composable
fun EventCapacityProgress(
  registered: Int,
  capacity: Int,
  modifier: Modifier = Modifier,
  showLabel: Boolean = true
) {
  val ratio = if (capacity > 0) (registered.toFloat() / capacity).coerceIn(0f, 1f) else 0f
  val animatedRatio by animateFloatAsState(
    targetValue = ratio,
    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
    label = "capacity_progress"
  )

  val barColor = when {
    ratio >= 0.95f -> AccentCoral
    ratio >= 0.75f -> StatusWarning
    else -> PrimaryPurple
  }

  Column(modifier = modifier.fillMaxWidth()) {
    if (showLabel) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Registrations",
          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
          color = TextSecondary
        )
        Text(
          text = "$registered / $capacity (${(ratio * 100).toInt()}%)",
          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
          color = if (ratio >= 0.95f) AccentCoralDark else TextPrimary
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
    }

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(8.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(animatedRatio)
          .fillMaxHeight()
          .clip(RoundedCornerShape(4.dp))
          .background(barColor)
      )
    }
  }
}

@Composable
fun EventImageBanner(
  drawableRes: Int?,
  modifier: Modifier = Modifier,
  height: Dp = 190.dp,
  title: String = "",
  category: String = ""
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(height)
      .clip(RoundedCornerShape(16.dp))
      .background(
        brush = Brush.linearGradient(
          listOf(PrimaryPurpleDark, PrimaryPurple, AccentCoral)
        )
      )
  ) {
    if (drawableRes != null) {
      Image(
        painter = painterResource(id = drawableRes),
        contentDescription = title,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )
      // Gradient vignette overlay for readability
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              listOf(
                Color.Transparent,
                Color.Black.copy(alpha = 0.2f),
                Color.Black.copy(alpha = 0.75f)
              )
            )
          )
      )
    } else {
      // Modern geometric fallback banner
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.linearGradient(
              listOf(Color(0xFF7C3AED), Color(0xFF8B5CF6), Color(0xFFFB7185))
            )
          )
      )
    }

    if (category.isNotEmpty()) {
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.Black.copy(alpha = 0.55f),
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(12.dp)
      ) {
        Text(
          text = category,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = Color.White,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
      }
    }
  }
}
