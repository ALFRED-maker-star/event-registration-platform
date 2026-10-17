package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Attendee
import com.example.model.Event
import com.example.ui.components.EventHubTopBar
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.CardSurface
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryPurpleContainer
import com.example.ui.theme.PrimaryPurpleDark
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AttendeeListScreen(
  event: Event,
  attendees: List<Attendee>,
  onToggleCheckIn: (String) -> Unit,
  onExportCsv: () -> String,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var filterStatus by remember { mutableStateOf("All") } // "All", "Checked In", "Pending"
  var showExportDialog by remember { mutableStateOf(false) }
  var exportedCsvContent by remember { mutableStateOf("") }

  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  val filteredAttendees = remember(attendees, searchQuery, filterStatus) {
    attendees.filter { attendee ->
      val matchesSearch = attendee.fullName.contains(searchQuery, ignoreCase = true) ||
          attendee.email.contains(searchQuery, ignoreCase = true) ||
          attendee.ticketNumber.contains(searchQuery, ignoreCase = true)

      val matchesStatus = when (filterStatus) {
        "Checked In" -> attendee.isCheckedIn
        "Pending" -> !attendee.isCheckedIn
        else -> true
      }
      matchesSearch && matchesStatus
    }
  }

  val checkedInCount = attendees.count { it.isCheckedIn }
  val pendingCount = attendees.size - checkedInCount

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("attendee_list_screen"),
    topBar = {
      EventHubTopBar(
        title = "Attendee List",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        actions = {
          // Export Button
          Surface(
            modifier = Modifier
              .padding(end = 8.dp)
              .clip(RoundedCornerShape(12.dp))
              .border(1.dp, PrimaryPurple, RoundedCornerShape(12.dp))
              .clickable {
                exportedCsvContent = onExportCsv()
                showExportDialog = true
              }
              .testTag("export_csv_button"),
            color = PrimaryPurpleContainer
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.FileDownload,
                contentDescription = "Export CSV",
                tint = PrimaryPurpleDark,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Export",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = PrimaryPurpleDark
              )
            }
          }
        }
      )
    },
    containerColor = AppBackground
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Event Header Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = CardSurface),
          border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = event.name,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "${attendees.size} Total Registrations • ${event.venue}",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }
        }
      }

      // Search field
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search by name, email, or ticket code...") },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
              }
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("attendee_search_field"),
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

      // Status Filter Pills
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val filterOptions = listOf(
            Triple("All", attendees.size, PrimaryPurple),
            Triple("Checked In", checkedInCount, StatusSuccess),
            Triple("Pending", pendingCount, StatusWarning)
          )

          filterOptions.forEach { (label, count, color) ->
            val isSelected = filterStatus == label
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (isSelected) color else CardSurface,
              border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderStroke),
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .clickable { filterStatus = label }
                .testTag("filter_${label.replace(" ", "_")}")
            ) {
              Column(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "$count",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = if (isSelected) Color.White else TextPrimary
                )
                Text(
                  text = label,
                  style = MaterialTheme.typography.labelSmall,
                  color = if (isSelected) Color.White.copy(alpha = 0.9f) else TextSecondary
                )
              }
            }
          }
        }
      }

      // Attendees list table rows
      if (filteredAttendees.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(180.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(CardSurface)
              .border(1.dp, BorderStroke, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "No attendees found",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Try adjusting your search query or status filter.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
            }
          }
        }
      } else {
        items(filteredAttendees, key = { it.id }) { attendee ->
          AttendeeTableRow(
            attendee = attendee,
            onToggleCheckIn = { onToggleCheckIn(attendee.id) }
          )
        }
      }
    }
  }

  // Export CSV Dialog Modal
  if (showExportDialog) {
    AlertDialog(
      onDismissRequest = { showExportDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.FileDownload,
            contentDescription = null,
            tint = PrimaryPurpleDark
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "Export Registrants (CSV)", fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "Generated CSV format with ${attendees.size} registrant records ready for download or external spreadsheet tools:",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(10.dp))
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .height(160.dp)
              .clip(RoundedCornerShape(8.dp)),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = exportedCsvContent,
              style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
              ),
              modifier = Modifier.padding(10.dp)
            )
          }
        }
      },
      confirmButton = {
        Row {
          OutlinedButton(
            onClick = {
              clipboardManager.setText(AnnotatedString(exportedCsvContent))
              showExportDialog = false
            }
          ) {
            Text("Copy CSV")
          }
          Spacer(modifier = Modifier.width(8.dp))
          TextButton(
            onClick = {
              val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, exportedCsvContent)
                type = "text/plain"
              }
              val shareIntent = Intent.createChooser(sendIntent, "Share EventHub Registrants CSV")
              context.startActivity(shareIntent)
              showExportDialog = false
            }
          ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Share")
          }
        }
      },
      dismissButton = {
        TextButton(onClick = { showExportDialog = false }) {
          Text("Close")
        }
      }
    )
  }
}

@Composable
fun AttendeeTableRow(
  attendee: Attendee,
  onToggleCheckIn: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .testTag("attendee_row_${attendee.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = CardSurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left info
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = attendee.fullName,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
          )
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = PrimaryPurpleContainer
          ) {
            Text(
              text = attendee.ticketType,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = PrimaryPurpleDark,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "${attendee.email} • ${attendee.phone}",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "Ticket: ${attendee.ticketNumber} • Registered ${attendee.registeredAt}",
          style = MaterialTheme.typography.labelSmall,
          color = TextMuted
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      // Status column and quick action
      Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
      ) {
        Surface(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onToggleCheckIn)
            .testTag("toggle_status_${attendee.id}"),
          color = if (attendee.isCheckedIn) StatusSuccessBg else StatusWarningBg,
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (attendee.isCheckedIn) StatusSuccess.copy(alpha = 0.5f) else StatusWarning.copy(alpha = 0.5f)
          )
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = if (attendee.isCheckedIn) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
              contentDescription = null,
              tint = if (attendee.isCheckedIn) StatusSuccess else StatusWarning,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (attendee.isCheckedIn) "Checked In" else "Pending",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = if (attendee.isCheckedIn) StatusSuccess else StatusWarning
            )
          }
        }

        if (attendee.isCheckedIn && attendee.checkedInAt != null) {
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = attendee.checkedInAt,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
          )
        }
      }
    }
  }
}
