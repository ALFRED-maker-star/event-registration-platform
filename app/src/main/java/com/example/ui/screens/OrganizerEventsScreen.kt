package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Event
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
fun OrganizerEventsScreen(
  events: List<Event>,
  getAttendeeCount: (String) -> Int,
  getCheckedInCount: (String) -> Int,
  onEventClick: (Event) -> Unit,
  onCreateEventClick: () -> Unit,
  onSwitchToAttendee: () -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("All") }

  val categories = listOf("All", "Tech & AI", "Music & Arts", "Robotics & AI", "Design & UX")

  val filteredEvents = remember(events, searchQuery, selectedCategory) {
    events.filter { event ->
      val matchesSearch = event.name.contains(searchQuery, ignoreCase = true) ||
          event.venue.contains(searchQuery, ignoreCase = true)
      val matchesCategory = selectedCategory == "All" || event.category == selectedCategory
      matchesSearch && matchesCategory
    }
  }

  val totalRegistrations = remember(events) {
    events.sumOf { getAttendeeCount(it.id) }
  }

  val totalCheckedIn = remember(events) {
    events.sumOf { getCheckedInCount(it.id) }
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("organizer_events_screen"),
    topBar = {
      EventHubTopBar(
        title = "Organizer Dashboard",
        currentRole = com.example.model.UserRole.ORGANIZER,
        onToggleRole = onSwitchToAttendee
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = onCreateEventClick,
        containerColor = AccentCoral,
        contentColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("create_event_fab")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Create Event",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
          )
        }
      }
    },
    containerColor = AppBackground
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Metric KPI cards row
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Total Events
          Surface(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(16.dp))
              .border(1.dp, BorderStroke, RoundedCornerShape(16.dp)),
            color = CardSurface
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "Live Events",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
              Text(
                text = "${events.size}",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = PrimaryPurpleDark
              )
            }
          }

          // Total Registrations
          Surface(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(16.dp))
              .border(1.dp, BorderStroke, RoundedCornerShape(16.dp)),
            color = CardSurface
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "Registrations",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
              Text(
                text = "$totalRegistrations",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = AccentCoral
              )
            }
          }

          // Checked In
          Surface(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(16.dp))
              .border(1.dp, BorderStroke, RoundedCornerShape(16.dp)),
            color = CardSurface
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "Checked In",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
              Text(
                text = "$totalCheckedIn",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = StatusSuccess
              )
            }
          }
        }
      }

      // Search field
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search your events or venues...") },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("event_search_field"),
          shape = RoundedCornerShape(14.dp),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryPurple,
            unfocusedBorderColor = BorderStroke,
            focusedContainerColor = CardSurface,
            unfocusedContainerColor = CardSurface
          )
        )
      }

      // Category filters
      item {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(vertical = 4.dp)
        ) {
          items(categories) { category ->
            val isSelected = selectedCategory == category
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = if (isSelected) PrimaryPurple else CardSurface,
              border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderStroke),
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable { selectedCategory = category }
            ) {
              Text(
                text = category,
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isSelected) Color.White else TextSecondary,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
              )
            }
          }
        }
      }

      // Grid of event cards
      if (filteredEvents.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(200.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(CardSurface)
              .border(1.dp, BorderStroke, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "No events found",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Tap '+ Create Event' below to publish a new event.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
            }
          }
        }
      } else {
        items(filteredEvents, key = { it.id }) { event ->
          val registered = getAttendeeCount(event.id)
          OrganizerEventCard(
            event = event,
            registeredCount = registered,
            onClick = { onEventClick(event) }
          )
        }
      }
    }
  }
}

@Composable
fun OrganizerEventCard(
  event: Event,
  registeredCount: Int,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .clickable(onClick = onClick)
      .testTag("event_card_${event.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = CardSurface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, hoveredElevation = 6.dp)
  ) {
    Column {
      // Cover image
      EventImageBanner(
        drawableRes = event.coverDrawableRes,
        title = event.name,
        category = event.category,
        height = 160.dp
      )

      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = event.name,
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = (-0.3).sp
            ),
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
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

        Spacer(modifier = Modifier.height(10.dp))

        // Date row
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            tint = PrimaryPurple,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = event.dateTime,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = TextSecondary
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Venue row
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = AccentCoral,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = event.venue,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Registered count / capacity progress
        EventCapacityProgress(
          registered = registeredCount,
          capacity = event.capacity
        )
      }
    }
  }
}
