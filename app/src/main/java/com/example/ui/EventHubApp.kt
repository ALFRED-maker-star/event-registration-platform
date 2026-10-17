package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.EventRepository
import com.example.model.Event
import com.example.model.UserRole
import com.example.ui.screens.AttendeeListScreen
import com.example.ui.screens.CheckInScannerScreen
import com.example.ui.screens.CreateEditEventScreen
import com.example.ui.screens.EventDetailScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MyTicketsScreen
import com.example.ui.screens.OrganizerEventsScreen
import com.example.ui.screens.PublicRegisterScreen
import com.example.ui.theme.EventHubTheme

sealed interface Screen {
  data object Login : Screen
  data object OrganizerEvents : Screen
  data class CreateEditEvent(val eventId: String? = null) : Screen
  data class EventDetail(val eventId: String) : Screen
  data class PublicRegister(val eventId: String) : Screen
  data class AttendeeList(val eventId: String) : Screen
  data class CheckInScanner(val eventId: String? = null) : Screen
  data object MyTickets : Screen
}

@Composable
fun EventHubApp(
  repository: EventRepository = remember { EventRepository() }
) {
  val events by repository.events.collectAsState()
  val attendees by repository.attendees.collectAsState()

  var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
  var currentUserRole by remember { mutableStateOf(UserRole.ORGANIZER) }
  var currentUserEmail by remember { mutableStateOf("admin@eventhub.io") }
  val backStack = remember { mutableListOf<Screen>() }

  fun navigateTo(screen: Screen) {
    backStack.add(currentScreen)
    currentScreen = screen
  }

  fun navigateBack() {
    if (backStack.isNotEmpty()) {
      currentScreen = backStack.removeAt(backStack.size - 1)
    } else {
      currentScreen = if (currentUserRole == UserRole.ORGANIZER) Screen.OrganizerEvents else Screen.MyTickets
    }
  }

  EventHubTheme {
    Surface(modifier = Modifier.fillMaxSize()) {
      Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
        when (screen) {
          is Screen.Login -> {
            LoginScreen(
              onLoginSuccess = { role, email ->
                currentUserRole = role
                currentUserEmail = email
                backStack.clear()
                if (role == UserRole.ORGANIZER) {
                  currentScreen = Screen.OrganizerEvents
                } else {
                  currentScreen = Screen.MyTickets
                }
              }
            )
          }

          is Screen.OrganizerEvents -> {
            BackHandler {
              backStack.clear()
              currentScreen = Screen.Login
            }

            OrganizerEventsScreen(
              events = events,
              getAttendeeCount = { repository.getAttendeeCountForEvent(it) },
              getCheckedInCount = { repository.getCheckedInCountForEvent(it) },
              onEventClick = { event ->
                navigateTo(Screen.EventDetail(event.id))
              },
              onCreateEventClick = {
                navigateTo(Screen.CreateEditEvent(null))
              },
              onSwitchToAttendee = {
                currentUserRole = UserRole.ATTENDEE
                currentUserEmail = "alex.rivera@example.com"
                navigateTo(Screen.MyTickets)
              }
            )
          }

          is Screen.CreateEditEvent -> {
            BackHandler { navigateBack() }

            val existingEvent = screen.eventId?.let { repository.getEventById(it) }

            CreateEditEventScreen(
              existingEvent = existingEvent,
              onPublish = { newEvent ->
                repository.saveEvent(newEvent)
                navigateBack()
              },
              onCancel = { navigateBack() }
            )
          }

          is Screen.EventDetail -> {
            BackHandler { navigateBack() }

            val event = repository.getEventById(screen.eventId) ?: events.firstOrNull()
            if (event != null) {
              val registered = repository.getAttendeeCountForEvent(event.id)
              val checkedIn = repository.getCheckedInCountForEvent(event.id)

              EventDetailScreen(
                event = event,
                registeredCount = registered,
                checkedInCount = checkedIn,
                onNavigateBack = { navigateBack() },
                onViewAttendees = {
                  navigateTo(Screen.AttendeeList(event.id))
                },
                onCheckIn = {
                  navigateTo(Screen.CheckInScanner(event.id))
                },
                onEditEvent = {
                  navigateTo(Screen.CreateEditEvent(event.id))
                },
                onPublicRegisterPreview = {
                  navigateTo(Screen.PublicRegister(event.id))
                }
              )
            }
          }

          is Screen.PublicRegister -> {
            BackHandler { navigateBack() }

            val event = repository.getEventById(screen.eventId) ?: events.firstOrNull()
            if (event != null) {
              PublicRegisterScreen(
                event = event,
                onNavigateBack = { navigateBack() },
                onRegistrationComplete = { eventId, name, email, phone, ticketType ->
                  repository.registerAttendee(eventId, name, email, phone, ticketType)
                },
                onViewMyTickets = {
                  currentUserRole = UserRole.ATTENDEE
                  currentUserEmail = "alex.rivera@example.com"
                  navigateTo(Screen.MyTickets)
                }
              )
            }
          }

          is Screen.AttendeeList -> {
            BackHandler { navigateBack() }

            val event = repository.getEventById(screen.eventId) ?: events.firstOrNull()
            if (event != null) {
              val eventAttendees = repository.getAttendeesForEvent(event.id)

              AttendeeListScreen(
                event = event,
                attendees = eventAttendees,
                onToggleCheckIn = { attId ->
                  repository.toggleCheckIn(attId)
                },
                onExportCsv = {
                  repository.exportCsv(event.id)
                },
                onNavigateBack = { navigateBack() }
              )
            }
          }

          is Screen.CheckInScanner -> {
            BackHandler { navigateBack() }

            val event = screen.eventId?.let { repository.getEventById(it) }
            val sampleAttendees = if (event != null) {
              repository.getAttendeesForEvent(event.id)
            } else {
              attendees.take(3)
            }

            CheckInScannerScreen(
              event = event,
              sampleAttendees = sampleAttendees,
              onScanTicket = { query ->
                repository.checkInTicket(query, event?.id)
              },
              onNavigateBack = { navigateBack() }
            )
          }

          is Screen.MyTickets -> {
            BackHandler {
              backStack.clear()
              currentScreen = Screen.Login
            }

            val tickets = repository.getTicketsForUser(currentUserEmail).ifEmpty {
              // If none for specific email, show all user-registered tickets for rich preview
              attendees.take(2).mapNotNull { att ->
                repository.getEventById(att.eventId)?.let { ev -> ev to att }
              }
            }

            MyTicketsScreen(
              userEmail = currentUserEmail,
              registeredTickets = tickets,
              allEvents = events,
              onEventClickForRegistration = { event ->
                navigateTo(Screen.PublicRegister(event.id))
              },
              onSwitchToOrganizer = {
                currentUserRole = UserRole.ORGANIZER
                currentUserEmail = "admin@eventhub.io"
                navigateTo(Screen.OrganizerEvents)
              }
            )
          }
        }
      }
    }
  }
}
