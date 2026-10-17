package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CheckInResult
import com.example.data.CheckInStatus
import com.example.model.Attendee
import com.example.model.Event
import com.example.ui.components.CoralCtaButton
import com.example.ui.components.EventHubTopBar
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.CardSurface
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryPurpleContainer
import com.example.ui.theme.PrimaryPurpleDark
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusErrorBg
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CheckInScannerScreen(
  event: Event?,
  sampleAttendees: List<Attendee>,
  onScanTicket: (String) -> CheckInResult,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var manualQuery by remember { mutableStateOf("") }
  var lastResult by remember { mutableStateOf<CheckInResult?>(null) }
  var isFlashOn by remember { mutableStateOf(false) }

  val scrollState = rememberScrollState()

  // Laser scanner animation
  val infiniteTransition = rememberInfiniteTransition(label = "scanner_laser")
  val scanProgress by infiniteTransition.animateFloat(
    initialValue = 0.1f,
    targetValue = 0.9f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "laser_y"
  )

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("check_in_scanner_screen"),
    topBar = {
      EventHubTopBar(
        title = "Check-In Scanner",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        actions = {
          IconButton(
            onClick = { isFlashOn = !isFlashOn },
            modifier = Modifier.testTag("flash_toggle_button")
          ) {
            Icon(
              imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
              contentDescription = "Torch",
              tint = if (isFlashOn) AccentCoral else TextSecondary
            )
          }
        }
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
      // Event context banner
      if (event != null) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = PrimaryPurpleContainer,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.QrCodeScanner,
              contentDescription = null,
              tint = PrimaryPurpleDark,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Scanning for: ${event.name}",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = PrimaryPurpleDark
              )
              Text(
                text = event.dateTime,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
              )
            }
          }
        }
      }

      // Camera Viewfinder Frame
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(280.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(DarkBackground)
          .testTag("camera_viewfinder"),
        contentAlignment = Alignment.Center
      ) {
        // Simulated dark camera preview gradient
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.radialGradient(
                listOf(Color(0xFF1E1B4B), Color(0xFF0F172A))
              )
            )
        )

        // Viewfinder reticle container (200x200)
        Box(
          modifier = Modifier
            .size(200.dp)
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
        ) {
          // Corner brackets
          Canvas(modifier = Modifier.fillMaxSize()) {
            val bracketLen = 28.dp.toPx()
            val strokeW = 4.dp.toPx()
            val bracketColor = AccentCoral

            // Top-Left
            drawLine(bracketColor, Offset(0f, 0f), Offset(bracketLen, 0f), strokeW)
            drawLine(bracketColor, Offset(0f, 0f), Offset(0f, bracketLen), strokeW)

            // Top-Right
            drawLine(bracketColor, Offset(size.width, 0f), Offset(size.width - bracketLen, 0f), strokeW)
            drawLine(bracketColor, Offset(size.width, 0f), Offset(size.width, bracketLen), strokeW)

            // Bottom-Left
            drawLine(bracketColor, Offset(0f, size.height), Offset(bracketLen, size.height), strokeW)
            drawLine(bracketColor, Offset(0f, size.height), Offset(0f, size.height - bracketLen), strokeW)

            // Bottom-Right
            drawLine(bracketColor, Offset(size.width, size.height), Offset(size.width - bracketLen, size.height), strokeW)
            drawLine(bracketColor, Offset(size.width, size.height), Offset(size.width, size.height - bracketLen), strokeW)

            // Animated Laser scan line
            val laserY = size.height * scanProgress
            drawLine(
              brush = Brush.horizontalGradient(
                listOf(
                  Color.Transparent,
                  AccentCoral,
                  Color.White,
                  AccentCoral,
                  Color.Transparent
                )
              ),
              start = Offset(0f, laserY),
              end = Offset(size.width, laserY),
              strokeWidth = 3.dp.toPx()
            )
          }

          // Center watermark
          Icon(
            imageVector = Icons.Default.QrCode,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.15f),
            modifier = Modifier
              .size(80.dp)
              .align(Alignment.Center)
          )
        }

        // Subtitle instructions
        Text(
          text = "Align attendee ticket QR code within frame",
          style = MaterialTheme.typography.bodySmall,
          color = Color.White.copy(alpha = 0.8f),
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 12.dp)
        )
      }

      // Quick Simulator Bar (Ideal for rapid emulator testing!)
      Text(
        text = "Quick Scan Simulation (Tap to Test)",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = TextPrimary
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val sample1 = sampleAttendees.firstOrNull()?.ticketNumber ?: "EH-8921-TK"
        val sample2 = sampleAttendees.getOrNull(1)?.ticketNumber ?: "EH-4129-TK"

        Surface(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderStroke, RoundedCornerShape(12.dp))
            .clickable {
              lastResult = onScanTicket(sample1)
            }
            .testTag("scan_sample_1"),
          color = CardSurface
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text("Scan Sample #1", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            Text(sample1, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = PrimaryPurpleDark)
          }
        }

        Surface(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderStroke, RoundedCornerShape(12.dp))
            .clickable {
              lastResult = onScanTicket(sample2)
            }
            .testTag("scan_sample_2"),
          color = CardSurface
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text("Scan Sample #2", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            Text(sample2, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = PrimaryPurpleDark)
          }
        }

        Surface(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderStroke, RoundedCornerShape(12.dp))
            .clickable {
              lastResult = onScanTicket("INVALID-CODE-99")
            }
            .testTag("scan_sample_invalid"),
          color = CardSurface
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text("Scan Fake #3", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            Text("Invalid QR", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = AccentCoral)
          }
        }
      }

      // Feedback States Banner
      AnimatedVisibility(
        visible = lastResult != null,
        enter = fadeIn() + slideInVertically()
      ) {
        if (lastResult != null) {
          val res = lastResult!!
          when (res.status) {
            CheckInStatus.SUCCESS -> {
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("feedback_success"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StatusSuccessBg),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, StatusSuccess)
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = null,
                      tint = StatusSuccess,
                      modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text(
                        text = "Check-in Successful!",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = StatusSuccess
                      )
                      Text(
                        text = res.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                      )
                    }
                  }

                  if (res.attendee != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = Color.White,
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Column {
                          Text(
                            text = res.attendee.fullName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                          )
                          Text(
                            text = "Ticket: ${res.attendee.ticketNumber} • ${res.attendee.ticketType}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                          )
                        }

                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = StatusSuccessBg
                        ) {
                          Text(
                            text = res.attendee.checkedInAt ?: "Now",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = StatusSuccess,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                          )
                        }
                      }
                    }
                  }
                }
              }
            }

            CheckInStatus.ALREADY_CHECKED_IN -> {
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("feedback_already_checked_in"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StatusWarningBg),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, StatusWarning)
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Warning,
                      contentDescription = null,
                      tint = StatusWarning,
                      modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text(
                        text = "Already Checked In",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = StatusWarning
                      )
                      Text(
                        text = res.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                      )
                    }
                  }

                  if (res.attendee != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = Color.White,
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Column {
                          Text(
                            text = res.attendee.fullName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                          )
                          Text(
                            text = "Ticket: ${res.attendee.ticketNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                          )
                        }
                        Text(
                          text = "Duplicate Scan",
                          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                          color = StatusWarning
                        )
                      }
                    }
                  }
                }
              }
            }

            CheckInStatus.NOT_FOUND -> {
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("feedback_not_found"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StatusErrorBg),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, StatusError)
              ) {
                Row(
                  modifier = Modifier.padding(16.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = StatusError,
                    modifier = Modifier.size(28.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = "Invalid Ticket",
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                      color = StatusError
                    )
                    Text(
                      text = res.message,
                      style = MaterialTheme.typography.bodySmall,
                      color = TextPrimary
                    )
                  }
                }
              }
            }
          }
        }
      }

      // Manual Search Fallback
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Text(
            text = "Manual Search Fallback",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
          )
          Text(
            text = "Enter ticket number, attendee full name, or email address if QR is unreadable:",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )

          OutlinedTextField(
            value = manualQuery,
            onValueChange = { manualQuery = it },
            placeholder = { Text("e.g. EH-8921-TK or Alex Rivera") },
            leadingIcon = {
              Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryPurple)
            },
            trailingIcon = {
              if (manualQuery.isNotEmpty()) {
                IconButton(onClick = { manualQuery = "" }) {
                  Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                }
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("manual_checkin_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = PrimaryPurple,
              focusedLabelColor = PrimaryPurple
            )
          )

          CoralCtaButton(
            text = "Verify & Check In",
            enabled = manualQuery.isNotBlank(),
            onClick = {
              lastResult = onScanTicket(manualQuery)
            },
            testTag = "manual_checkin_submit"
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
