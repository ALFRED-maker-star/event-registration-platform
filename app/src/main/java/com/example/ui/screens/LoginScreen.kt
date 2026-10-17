package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.UserRole
import com.example.ui.components.CoralCtaButton
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

@Composable
fun LoginScreen(
  onLoginSuccess: (UserRole, String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedRole by remember { mutableStateOf(UserRole.ORGANIZER) }
  var email by remember { mutableStateOf("admin@eventhub.io") }
  var password by remember { mutableStateOf("••••••••") }

  val scrollState = rememberScrollState()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(AppBackground)
      .testTag("login_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 24.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(24.dp))

      // App Logo Badge
      Box(
        modifier = Modifier
          .size(88.dp)
          .clip(RoundedCornerShape(26.dp))
          .background(
            Brush.linearGradient(
              listOf(PrimaryPurpleDark, PrimaryPurple, AccentCoral)
            )
          )
          .padding(3.dp),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_eventhub_logo),
          contentDescription = "EventHub Logo",
          modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(23.dp))
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "EventHub",
        style = MaterialTheme.typography.displayLarge.copy(
          fontWeight = FontWeight.Black,
          letterSpacing = (-0.8).sp
        ),
        color = TextPrimary
      )

      Text(
        text = "Next-gen event operations & registration platform",
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
      )

      Spacer(modifier = Modifier.height(28.dp))

      // Experience Selector (Organizer / Attendee)
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .border(1.dp, BorderStroke, RoundedCornerShape(16.dp))
          .testTag("role_toggle_container"),
        color = CardSurface
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // Organizer Pill
          val isOrg = selectedRole == UserRole.ORGANIZER
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isOrg) PrimaryPurple else Color.Transparent)
              .clickable {
                selectedRole = UserRole.ORGANIZER
                email = "admin@eventhub.io"
              }
              .padding(vertical = 12.dp)
              .testTag("toggle_organizer"),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = if (isOrg) Color.White else TextSecondary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Organizer",
                style = MaterialTheme.typography.labelLarge.copy(
                  fontWeight = if (isOrg) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isOrg) Color.White else TextSecondary
              )
            }
          }

          // Attendee Pill
          val isAtt = selectedRole == UserRole.ATTENDEE
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isAtt) PrimaryPurple else Color.Transparent)
              .clickable {
                selectedRole = UserRole.ATTENDEE
                email = "alex.rivera@example.com"
              }
              .padding(vertical = 12.dp)
              .testTag("toggle_attendee"),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = if (isAtt) Color.White else TextSecondary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Attendee",
                style = MaterialTheme.typography.labelLarge.copy(
                  fontWeight = if (isAtt) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isAtt) Color.White else TextSecondary
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Experience Info Banner
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (selectedRole == UserRole.ORGANIZER) PrimaryPurpleContainer else Color(0xFFFFE4E6)
        )
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = if (selectedRole == UserRole.ORGANIZER) Icons.Default.Stars else Icons.Default.QrCode,
            contentDescription = null,
            tint = if (selectedRole == UserRole.ORGANIZER) PrimaryPurpleDark else AccentCoral,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = if (selectedRole == UserRole.ORGANIZER) "Organizer Portal" else "Attendee Portal",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = TextPrimary
            )
            Text(
              text = selectedRole.subtitle,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Form Inputs
      OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email Address") },
        leadingIcon = {
          Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = TextMuted)
        },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("login_email_input"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = PrimaryPurple,
          focusedLabelColor = PrimaryPurple
        )
      )

      Spacer(modifier = Modifier.height(14.dp))

      OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Password") },
        leadingIcon = {
          Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = TextMuted)
        },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("login_password_input"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = PrimaryPurple,
          focusedLabelColor = PrimaryPurple
        )
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Coral CTA Button
      CoralCtaButton(
        text = "Enter as ${selectedRole.label}",
        onClick = {
          onLoginSuccess(selectedRole, email)
        },
        testTag = "login_submit_button"
      )

      Spacer(modifier = Modifier.height(18.dp))

      Text(
        text = "Switch between Organizer & Attendee roles anytime via the top bar.",
        style = MaterialTheme.typography.bodySmall,
        color = TextMuted,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}
