package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Attendee
import com.example.model.Event
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
fun PublicRegisterScreen(
  event: Event,
  onNavigateBack: () -> Unit,
  onRegistrationComplete: (eventId: String, name: String, email: String, phone: String, ticketType: String) -> Attendee,
  onViewMyTickets: () -> Unit,
  modifier: Modifier = Modifier
) {
  var fullName by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var selectedTicketType by remember { mutableStateOf("General Admission") }

  // State after registration completion
  var confirmedAttendee by remember { mutableStateOf<Attendee?>(null) }

  val scrollState = rememberScrollState()
  val ticketTypes = listOf("General Admission", "VIP Pass", "Early Bird Access")

  val isFormValid = fullName.isNotBlank() && email.contains("@") && phone.isNotBlank()

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("public_register_screen"),
    topBar = {
      EventHubTopBar(
        title = if (confirmedAttendee == null) "Event Registration" else "Ticket Confirmed",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack
      )
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
      if (confirmedAttendee == null) {
        // --- 1. Registration Mode ---
        // Cover image & Header
        EventImageBanner(
          drawableRes = event.coverDrawableRes,
          title = event.name,
          category = event.category,
          height = 180.dp
        )

        // Event info summary
        Column {
          Text(
            text = event.name,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CalendarMonth,
              contentDescription = null,
              tint = PrimaryPurple,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = event.dateTime,
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
              color = TextSecondary
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = AccentCoral,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = event.venue,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }
        }

        // Registration form card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = CardSurface),
          border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke)
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Text(
              text = "Attendee Registration",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = TextPrimary
            )
            Text(
              text = "Please enter your details below to secure your instant digital pass.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )

            // Full Name
            OutlinedTextField(
              value = fullName,
              onValueChange = { fullName = it },
              label = { Text("Full Name *") },
              placeholder = { Text("e.g. Alex Rivera") },
              leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryPurple)
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("attendee_fullname_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryPurple,
                focusedLabelColor = PrimaryPurple
              )
            )

            // Email
            OutlinedTextField(
              value = email,
              onValueChange = { email = it },
              label = { Text("Email Address *") },
              placeholder = { Text("e.g. alex@example.com") },
              leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = PrimaryPurple)
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("attendee_email_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryPurple,
                focusedLabelColor = PrimaryPurple
              )
            )

            // Phone
            OutlinedTextField(
              value = phone,
              onValueChange = { phone = it },
              label = { Text("Phone Number *") },
              placeholder = { Text("+1 (555) 000-0000") },
              leadingIcon = {
                Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryPurple)
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("attendee_phone_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryPurple,
                focusedLabelColor = PrimaryPurple
              )
            )

            // Ticket Type Selection
            Text(
              text = "Ticket Tier",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = TextPrimary
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              ticketTypes.forEach { tier ->
                val isSelected = selectedTicketType == tier
                Surface(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                      1.5.dp,
                      if (isSelected) PrimaryPurple else BorderStroke,
                      RoundedCornerShape(12.dp)
                    )
                    .clickable { selectedTicketType = tier }
                    .testTag("tier_${tier.replace(" ", "_")}"),
                  color = if (isSelected) PrimaryPurpleContainer else CardSurface
                ) {
                  Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                        imageVector = Icons.Default.ConfirmationNumber,
                        contentDescription = null,
                        tint = if (isSelected) PrimaryPurpleDark else TextMuted,
                        modifier = Modifier.size(18.dp)
                      )
                      Spacer(modifier = Modifier.width(10.dp))
                      Text(
                        text = tier,
                        style = MaterialTheme.typography.bodyMedium.copy(
                          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) PrimaryPurpleDark else TextPrimary
                      )
                    }

                    Text(
                      text = if (tier.contains("VIP")) "${event.ticketPrice} + VIP perks" else event.ticketPrice,
                      style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                      color = if (isSelected) PrimaryPurpleDark else TextSecondary
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Register CTA Button
            CoralCtaButton(
              text = "+ Register Now (${event.ticketPrice})",
              enabled = isFormValid,
              onClick = {
                val created = onRegistrationComplete(
                  event.id,
                  fullName,
                  email,
                  phone,
                  selectedTicketType
                )
                confirmedAttendee = created
              },
              testTag = "submit_registration_button"
            )
          }
        }
      } else {
        // --- 2. Confirmation Screen with QR Ticket ---
        AnimatedVisibility(
          visible = true,
          enter = fadeIn() + slideInVertically()
        ) {
          val attendee = confirmedAttendee!!

          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            // Success Header
            Box(
              modifier = Modifier
                .size(64.dp)
                .background(StatusSuccessBg, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = StatusSuccess,
                modifier = Modifier.size(40.dp)
              )
            }

            Text(
              text = "You're Registered!",
              style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
              color = TextPrimary
            )

            Text(
              text = "A digital pass has been generated. Present this QR code at the door for instant check-in.",
              style = MaterialTheme.typography.bodyMedium,
              color = TextSecondary,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Boarding Pass Ticket Card
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .testTag("confirmed_ticket_card"),
              shape = RoundedCornerShape(22.dp),
              colors = CardDefaults.cardColors(containerColor = CardSurface),
              elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
              Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                // Header pill
                Surface(
                  shape = RoundedCornerShape(20.dp),
                  color = PrimaryPurpleContainer
                ) {
                  Text(
                    text = attendee.ticketType.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryPurpleDark,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                  )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                  text = event.name,
                  style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                  ),
                  color = TextPrimary,
                  textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                  text = event.dateTime,
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                  color = PrimaryPurpleDark
                )

                Spacer(modifier = Modifier.height(16.dp))

                // The QR Code
                QrCodeView(
                  content = "${attendee.ticketNumber}|${event.id}|${attendee.fullName}",
                  size = 190.dp,
                  modifier = Modifier.testTag("ticket_qr_code")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                  text = attendee.ticketNumber,
                  style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                  ),
                  color = TextPrimary
                )

                Text(
                  text = "Ticket Code",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dashed separator look
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BorderStroke)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Attendee Info Table
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column {
                    Text(
                      text = "ATTENDEE",
                      style = MaterialTheme.typography.labelSmall,
                      color = TextMuted
                    )
                    Text(
                      text = attendee.fullName,
                      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                      color = TextPrimary
                    )
                  }

                  Column(horizontalAlignment = Alignment.End) {
                    Text(
                      text = "REGISTERED ON",
                      style = MaterialTheme.typography.labelSmall,
                      color = TextMuted
                    )
                    Text(
                      text = attendee.registeredAt,
                      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                      color = TextPrimary
                    )
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column {
                    Text(
                      text = "VENUE",
                      style = MaterialTheme.typography.labelSmall,
                      color = TextMuted
                    )
                    Text(
                      text = event.venue,
                      style = MaterialTheme.typography.bodySmall,
                      color = TextSecondary,
                      maxLines = 1
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            CoralCtaButton(
              text = "View in 'My Tickets'",
              onClick = onViewMyTickets,
              testTag = "view_in_my_tickets_button"
            )

            OutlinedButton(
              onClick = onNavigateBack,
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text("Back to Event")
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
