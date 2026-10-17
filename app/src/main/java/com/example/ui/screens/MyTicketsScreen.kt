package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Attendee
import com.example.model.Event
import com.example.model.UserRole
import com.example.ui.components.CoralCtaButton
import com.example.ui.components.EventHubTopBar
import com.example.ui.components.EventImageBanner
import com.example.ui.components.QrCodeView
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.CardSurface
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryPurpleContainer
import com.example.ui.theme.PrimaryPurpleDark
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MyTicketsScreen(
  userEmail: String,
  registeredTickets: List<Pair<Event, Attendee>>,
  allEvents: List<Event>,
  onEventClickForRegistration: (Event) -> Unit,
  onSwitchToOrganizer: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: My Tickets, 1: Discover Events
  val context = LocalContext.current

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("my_tickets_screen"),
    topBar = {
      EventHubTopBar(
        title = "Attendee Portal",
        currentRole = UserRole.ATTENDEE,
        onToggleRole = onSwitchToOrganizer
      )
    },
    containerColor = AppBackground
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Tab Row
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = CardSurface,
        contentColor = PrimaryPurple,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = PrimaryPurple
          )
        }
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.ConfirmationNumber, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("My Tickets (${registeredTickets.size})", fontWeight = FontWeight.Bold)
            }
          },
          modifier = Modifier.testTag("tab_my_tickets")
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Discover Events", fontWeight = FontWeight.Bold)
            }
          },
          modifier = Modifier.testTag("tab_discover_events")
        )
      }

      if (selectedTab == 0) {
        // --- 1. My Tickets List ---
        if (registeredTickets.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(24.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Box(
                modifier = Modifier
                  .size(80.dp)
                  .background(PrimaryPurpleContainer, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.ConfirmationNumber,
                  contentDescription = null,
                  tint = PrimaryPurpleDark,
                  modifier = Modifier.size(44.dp)
                )
              }
              Spacer(modifier = Modifier.height(16.dp))
              Text(
                text = "No Tickets Yet",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "You haven't registered for any events yet. Check out the Discover tab to get your passes!",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
              )
              Spacer(modifier = Modifier.height(20.dp))
              CoralCtaButton(
                text = "Discover Events",
                onClick = { selectedTab = 1 },
                modifier = Modifier.width(220.dp)
              )
            }
          }
        } else {
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
          ) {
            item {
              Text(
                text = "Your Mobile Passes",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
              )
              Text(
                text = "Show the QR code at the door for contactless entry.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
            }

            items(registeredTickets, key = { it.second.id }) { (event, attendee) ->
              AttendeeTicketPassCard(
                event = event,
                attendee = attendee,
                onShareTicket = {
                  val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                      Intent.EXTRA_TEXT,
                      "Here is my ticket for ${event.name}!\nTicket Code: ${attendee.ticketNumber}\nDate: ${event.dateTime}\nVenue: ${event.venue}"
                    )
                    type = "text/plain"
                  }
                  context.startActivity(Intent.createChooser(shareIntent, "Share Event Pass"))
                }
              )
            }
          }
        }
      } else {
        // --- 2. Discover Events List ---
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          item {
            Text(
              text = "Upcoming Events",
              style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
              color = TextPrimary
            )
            Text(
              text = "Explore upcoming conferences, festivals & meetups.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }

          items(allEvents, key = { it.id }) { event ->
            AttendeeDiscoverCard(
              event = event,
              onRegisterClick = { onEventClickForRegistration(event) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun AttendeeTicketPassCard(
  event: Event,
  attendee: Attendee,
  onShareTicket: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(22.dp))
      .testTag("ticket_pass_${attendee.ticketNumber}"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = CardSurface),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Column {
      // Event Mini Banner Header
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(90.dp)
      ) {
        if (event.coverDrawableRes != null) {
          Image(
            painter = painterResource(id = event.coverDrawableRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Color.Black.copy(alpha = 0.5f)
            )
        )
        Row(
          modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = event.name,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = Color.White,
              maxLines = 1
            )
            Text(
              text = event.category,
              style = MaterialTheme.typography.labelSmall,
              color = Color.White.copy(alpha = 0.8f)
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (attendee.isCheckedIn) StatusSuccessBg else PrimaryPurpleContainer
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (attendee.isCheckedIn) {
                Icon(
                  Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = StatusSuccess,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
              }
              Text(
                text = if (attendee.isCheckedIn) "CHECKED IN" else "READY FOR ENTRY",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (attendee.isCheckedIn) StatusSuccess else PrimaryPurpleDark
              )
            }
          }
        }
      }

      // Ticket Body
      Column(
        modifier = Modifier.padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Date & Venue
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = event.dateTime, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.LocationOn, contentDescription = null, tint = AccentCoral, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = event.venue, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Center QR Code
        QrCodeView(
          content = "${attendee.ticketNumber}|${event.id}|${attendee.fullName}",
          size = 180.dp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = attendee.ticketNumber,
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
          ),
          color = TextPrimary
        )

        Text(
          text = "${attendee.fullName} • ${attendee.ticketType}",
          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
          color = TextMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Share button
        OutlinedButton(
          onClick = onShareTicket,
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Share / Save Ticket Pass")
        }
      }
    }
  }
}

@Composable
fun AttendeeDiscoverCard(
  event: Event,
  onRegisterClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp)),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = CardSurface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column {
      EventImageBanner(
        drawableRes = event.coverDrawableRes,
        title = event.name,
        category = event.category,
        height = 150.dp
      )

      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = event.name,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary,
            modifier = Modifier.weight(1f)
          )
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = PrimaryPurpleContainer
          ) {
            Text(
              text = event.ticketPrice,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = PrimaryPurpleDark,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = event.dateTime,
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )
        Text(
          text = event.venue,
          style = MaterialTheme.typography.bodySmall,
          color = TextMuted,
          maxLines = 1
        )

        Spacer(modifier = Modifier.height(14.dp))

        CoralCtaButton(
          text = "+ Register for Event",
          onClick = onRegisterClick,
          testTag = "register_for_${event.id}"
        )
      }
    }
  }
}
