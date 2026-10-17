package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Event
import com.example.ui.components.CoralCtaButton
import com.example.ui.components.EventCapacityProgress
import com.example.ui.components.EventHubTopBar
import com.example.ui.components.EventImageBanner
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.CardSurface
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryPurpleContainer
import com.example.ui.theme.PrimaryPurpleDark
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun EventDetailScreen(
  event: Event,
  registeredCount: Int,
  checkedInCount: Int,
  onNavigateBack: () -> Unit,
  onViewAttendees: () -> Unit,
  onCheckIn: () -> Unit,
  onEditEvent: () -> Unit,
  onPublicRegisterPreview: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("event_detail_screen"),
    topBar = {
      EventHubTopBar(
        title = "Event Overview",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        actions = {
          IconButton(
            onClick = onEditEvent,
            modifier = Modifier.testTag("edit_event_button")
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Edit Event",
              tint = PrimaryPurpleDark
            )
          }
        }
      )
    },
    bottomBar = {
      // Primary Action Bar: "View Attendees" and "Check-In"
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardSurface,
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // View Attendees button
            OutlinedButton(
              onClick = onViewAttendees,
              modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .testTag("view_attendees_button"),
              shape = RoundedCornerShape(14.dp),
              border = androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryPurple),
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = PrimaryPurple
              )
            ) {
              Icon(
                imageVector = Icons.Default.People,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Attendees ($registeredCount)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
            }

            // Check-In button with Coral CTA
            Button(
              onClick = onCheckIn,
              modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .testTag("check_in_button"),
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = AccentCoral,
                contentColor = Color.White
              ),
              elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
              Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Check-In",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
            }
          }
        }
      }
    },
    containerColor = AppBackground
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(scrollState)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Cover banner
      EventImageBanner(
        drawableRes = event.coverDrawableRes,
        title = event.name,
        category = event.category,
        height = 200.dp
      )

      // Title & Price Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = event.name,
            style = MaterialTheme.typography.headlineLarge.copy(
              fontWeight = FontWeight.Black,
              letterSpacing = (-0.5).sp
            ),
            color = TextPrimary
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Organized by ${event.organizerName}",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
          )
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = PrimaryPurpleContainer,
          modifier = Modifier.padding(start = 8.dp)
        ) {
          Text(
            text = event.ticketPrice,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = PrimaryPurpleDark,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          )
        }
      }

      // Registration & Capacity Progress Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Live Registration Status",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = TextPrimary
            )
            Text(
              text = "${event.capacity - registeredCount} spots left",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
              color = AccentCoral
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          EventCapacityProgress(
            registered = registeredCount,
            capacity = event.capacity,
            showLabel = true
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Attendance stats row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant)
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "$registeredCount",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = PrimaryPurpleDark
              )
              Text(
                text = "Registered",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
              )
            }

            Box(
              modifier = Modifier
                .width(1.dp)
                .height(30.dp)
                .background(BorderStroke)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "$checkedInCount",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = StatusSuccess
              )
              Text(
                text = "Checked In",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
              )
            }

            Box(
              modifier = Modifier
                .width(1.dp)
                .height(30.dp)
                .background(BorderStroke)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              val rate = if (registeredCount > 0) ((checkedInCount.toFloat() / registeredCount) * 100).toInt() else 0
              Text(
                text = "$rate%",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
              )
              Text(
                text = "Arrival Rate",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
              )
            }
          }
        }
      }

      // Date & Venue Cards
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryPurpleContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = PrimaryPurpleDark,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Date & Time",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TextMuted
              )
              Text(
                text = event.dateTime,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = TextPrimary
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFFFE4E6)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = AccentCoral,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Location / Venue",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TextMuted
              )
              Text(
                text = event.venue,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = TextPrimary
              )
            }
          }
        }
      }

      // About Event
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "About This Event",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = event.description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            lineHeight = 22.sp
          )
        }
      }

      // Public Registration Link / Preview Tester
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .border(1.dp, PrimaryPurple.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
        color = PrimaryPurpleContainer.copy(alpha = 0.5f)
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Public Attendee View",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = PrimaryPurpleDark
            )
            Text(
              text = "Test registration form and live QR generation",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }
          OutlinedButton(
            onClick = onPublicRegisterPreview,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("preview_register_button")
          ) {
            Icon(
              imageVector = Icons.Default.PersonAdd,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Register")
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
