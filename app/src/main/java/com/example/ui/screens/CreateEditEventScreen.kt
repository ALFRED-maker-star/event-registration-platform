package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Event
import com.example.ui.components.CoralCtaButton
import com.example.ui.components.EventHubTopBar
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.CardSurface
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryPurpleContainer
import com.example.ui.theme.PrimaryPurpleDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.UUID

@Composable
fun CreateEditEventScreen(
  existingEvent: Event? = null,
  onPublish: (Event) -> Unit,
  onCancel: () -> Unit,
  modifier: Modifier = Modifier
) {
  var name by remember { mutableStateOf(existingEvent?.name ?: "") }
  var description by remember { mutableStateOf(existingEvent?.description ?: "") }
  var dateTime by remember { mutableStateOf(existingEvent?.dateTime ?: "Sat, Nov 14, 2026 • 2:00 PM") }
  var venue by remember { mutableStateOf(existingEvent?.venue ?: "Metropolitan Event Center, Suite 400") }
  var capacityStr by remember { mutableStateOf(existingEvent?.capacity?.toString() ?: "100") }
  var ticketPrice by remember { mutableStateOf(existingEvent?.ticketPrice ?: "$25") }
  var selectedCoverRes by remember {
    mutableStateOf(existingEvent?.coverDrawableRes ?: R.drawable.cover_tech_summit)
  }
  var category by remember { mutableStateOf(existingEvent?.category ?: "Technology") }

  val coverOptions = listOf(
    Pair("Tech Stage", R.drawable.cover_tech_summit),
    Pair("Festival Vibes", R.drawable.cover_music_festival),
    Pair("Branded Badge", R.drawable.ic_eventhub_logo)
  )

  val categories = listOf("Technology", "Music & Arts", "Business", "Design & UX", "Community")
  val scrollState = rememberScrollState()

  val isFormValid = name.isNotBlank() && venue.isNotBlank() && (capacityStr.toIntOrNull() ?: 0) > 0

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("create_edit_event_screen"),
    topBar = {
      EventHubTopBar(
        title = if (existingEvent == null) "Create New Event" else "Edit Event",
        canNavigateBack = true,
        onNavigateBack = onCancel
      )
    },
    bottomBar = {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardSurface,
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke)
      ) {
        Box(modifier = Modifier.padding(16.dp)) {
          CoralCtaButton(
            text = if (existingEvent == null) "Publish Event" else "Save Changes",
            enabled = isFormValid,
            onClick = {
              val newEvent = Event(
                id = existingEvent?.id ?: "evt-${UUID.randomUUID().toString().take(6)}",
                name = name.trim(),
                description = description.trim().ifEmpty { "Join us for an exciting gathering at $venue." },
                dateTime = dateTime.trim(),
                venue = venue.trim(),
                capacity = capacityStr.toIntOrNull() ?: 100,
                ticketPrice = ticketPrice.trim().ifEmpty { "Free" },
                coverDrawableRes = selectedCoverRes,
                category = category,
                isPublished = true
              )
              onPublish(newEvent)
            },
            testTag = "publish_event_button"
          )
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
      // Cover Image Upload / Selection Card
      Text(
        text = "Cover Image",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = TextPrimary
      )

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
          .clip(RoundedCornerShape(16.dp))
          .border(2.dp, PrimaryPurple, RoundedCornerShape(16.dp))
      ) {
        Image(
          painter = painterResource(id = selectedCoverRes),
          contentDescription = "Event Cover Preview",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        Surface(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(12.dp),
          shape = RoundedCornerShape(10.dp),
          color = Color.Black.copy(alpha = 0.65f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.AddPhotoAlternate,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Selected Cover",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
          }
        }
      }

      // Quick Choose Preset Covers
      Text(
        text = "Select Cover Artwork Preset",
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        color = TextSecondary
      )

      LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(coverOptions) { (label, resId) ->
          val isSelected = selectedCoverRes == resId
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .border(
                2.dp,
                if (isSelected) AccentCoral else BorderStroke,
                RoundedCornerShape(12.dp)
              )
              .clickable { selectedCoverRes = resId },
            color = CardSurface
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = AccentCoral,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
              }
              Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) AccentCoral else TextPrimary
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Event Name
      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Event Name *") },
        placeholder = { Text("e.g. NextGen AI Hackathon 2026") },
        leadingIcon = {
          Icon(Icons.Default.Title, contentDescription = null, tint = PrimaryPurple)
        },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("event_name_input"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = PrimaryPurple,
          focusedLabelColor = PrimaryPurple
        )
      )

      // Category Selection
      Text(
        text = "Category",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = TextPrimary
      )

      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { cat ->
          val isSelected = category == cat
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) PrimaryPurple else CardSurface,
            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderStroke),
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .clickable { category = cat }
          ) {
            Text(
              text = cat,
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              ),
              color = if (isSelected) Color.White else TextSecondary,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
          }
        }
      }

      // Date and Time
      OutlinedTextField(
        value = dateTime,
        onValueChange = { dateTime = it },
        label = { Text("Date & Time *") },
        placeholder = { Text("e.g. Sat, Nov 14, 2026 • 2:00 PM") },
        leadingIcon = {
          Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = PrimaryPurple)
        },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("event_datetime_input"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = PrimaryPurple,
          focusedLabelColor = PrimaryPurple
        )
      )

      // Venue
      OutlinedTextField(
        value = venue,
        onValueChange = { venue = it },
        label = { Text("Venue / Location *") },
        placeholder = { Text("e.g. Hall B, Moscone West, San Francisco, CA") },
        leadingIcon = {
          Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryPurple)
        },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("event_venue_input"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = PrimaryPurple,
          focusedLabelColor = PrimaryPurple
        )
      )

      // Capacity & Ticket Price Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedTextField(
          value = capacityStr,
          onValueChange = { capacityStr = it.filter { char -> char.isDigit() } },
          label = { Text("Capacity *") },
          leadingIcon = {
            Icon(Icons.Default.Groups, contentDescription = null, tint = PrimaryPurple)
          },
          modifier = Modifier
            .weight(1f)
            .testTag("event_capacity_input"),
          shape = RoundedCornerShape(12.dp),
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryPurple,
            focusedLabelColor = PrimaryPurple
          )
        )

        OutlinedTextField(
          value = ticketPrice,
          onValueChange = { ticketPrice = it },
          label = { Text("Ticket Price") },
          placeholder = { Text("e.g. Free or $49") },
          leadingIcon = {
            Icon(Icons.Default.AttachMoney, contentDescription = null, tint = PrimaryPurple)
          },
          modifier = Modifier
            .weight(1f)
            .testTag("event_price_input"),
          shape = RoundedCornerShape(12.dp),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryPurple,
            focusedLabelColor = PrimaryPurple
          )
        )
      }

      // Description
      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Event Description") },
        placeholder = { Text("Tell attendees what makes your event extraordinary...") },
        leadingIcon = {
          Icon(Icons.Default.Description, contentDescription = null, tint = PrimaryPurple)
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp)
          .testTag("event_description_input"),
        shape = RoundedCornerShape(12.dp),
        maxLines = 5,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = PrimaryPurple,
          focusedLabelColor = PrimaryPurple
        )
      )

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
