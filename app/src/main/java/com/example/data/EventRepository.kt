package com.example.data

import com.example.R
import com.example.model.Attendee
import com.example.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class CheckInStatus {
  SUCCESS,
  ALREADY_CHECKED_IN,
  NOT_FOUND
}

data class CheckInResult(
  val status: CheckInStatus,
  val attendee: Attendee? = null,
  val event: Event? = null,
  val message: String
)

class EventRepository {

  private val _events = MutableStateFlow<List<Event>>(emptyList())
  val events: StateFlow<List<Event>> = _events.asStateFlow()

  private val _attendees = MutableStateFlow<List<Attendee>>(emptyList())
  val attendees: StateFlow<List<Attendee>> = _attendees.asStateFlow()

  init {
    seedInitialData()
  }

  private fun seedInitialData() {
    val initialEvents = listOf(
      Event(
        id = "evt-101",
        name = "Future Tech Connect 2026",
        description = "Join top AI architects, software leaders, and cloud pioneers for a full day of keynotes, tech breakdowns, and elite industry networking.",
        dateTime = "Sat, Oct 24, 2026 • 9:00 AM - 5:30 PM",
        venue = "Silicon Valley Convention Center, Hall A, San Jose, CA",
        capacity = 150,
        ticketPrice = "$149",
        coverDrawableRes = R.drawable.cover_tech_summit,
        category = "Tech & AI",
        isPublished = true,
        organizerName = "TechConnect Global",
        tags = listOf("AI & Cloud", "Keynote", "Networking")
      ),
      Event(
        id = "evt-102",
        name = "Sundown Sounds Indie Festival",
        description = "An unforgettable sunset music showcase featuring indie rock, synth-wave sets, food trucks, and craft beverage pavilions under the open sky.",
        dateTime = "Fri, Nov 6, 2026 • 4:00 PM - 11:00 PM",
        venue = "Golden Gate Park Meadow, San Francisco, CA",
        capacity = 300,
        ticketPrice = "$65",
        coverDrawableRes = R.drawable.cover_music_festival,
        category = "Music & Arts",
        isPublished = true,
        organizerName = "Sundown Collective",
        tags = listOf("Live Music", "Festival", "Food & Drinks")
      ),
      Event(
        id = "evt-103",
        name = "Global AI & Robotics Summit",
        description = "Hands-on exhibition showcasing cutting-edge autonomous robotics, physical AI systems, and generative agent architectures with live demos.",
        dateTime = "Wed, Dec 2, 2026 • 10:00 AM - 4:00 PM",
        venue = "Metropolitan Tech Pavilion, Austin, TX",
        capacity = 100,
        ticketPrice = "Free",
        coverDrawableRes = R.drawable.cover_tech_summit,
        category = "Robotics & AI",
        isPublished = true,
        organizerName = "Autonomous World Labs",
        tags = listOf("Robotics", "Hands-on Demos", "Free Entry")
      ),
      Event(
        id = "evt-104",
        name = "Design Leadership & Product Mixer",
        description = "An intimate gathering of design directors, UX leaders, and product innovators discussing the future of human-centered intelligent software.",
        dateTime = "Thu, Dec 10, 2026 • 6:30 PM - 9:30 PM",
        venue = "Skyline Lounge & Rooftop, New York, NY",
        capacity = 80,
        ticketPrice = "$35",
        coverDrawableRes = R.drawable.cover_music_festival,
        category = "Design & UX",
        isPublished = true,
        organizerName = "Product Guild NYC",
        tags = listOf("Product Design", "Cocktails", "VIP")
      )
    )

    val initialAttendees = listOf(
      // Attendees for evt-101
      Attendee(
        id = "att-1",
        eventId = "evt-101",
        ticketNumber = "EH-8921-TK",
        fullName = "Alex Rivera",
        email = "alex.rivera@example.com",
        phone = "+1 (555) 234-5678",
        registeredAt = "Oct 12, 2026",
        isCheckedIn = true,
        checkedInAt = "9:14 AM",
        ticketType = "VIP Pass"
      ),
      Attendee(
        id = "att-2",
        eventId = "evt-101",
        ticketNumber = "EH-4129-TK",
        fullName = "Jordan Chen",
        email = "jordan.chen@example.com",
        phone = "+1 (555) 987-6543",
        registeredAt = "Oct 14, 2026",
        isCheckedIn = false,
        ticketType = "General Admission"
      ),
      Attendee(
        id = "att-3",
        eventId = "evt-101",
        ticketNumber = "EH-7312-TK",
        fullName = "Elena Rostova",
        email = "elena.r@example.com",
        phone = "+1 (555) 456-7890",
        registeredAt = "Oct 15, 2026",
        isCheckedIn = true,
        checkedInAt = "9:32 AM",
        ticketType = "Speaker / VIP"
      ),
      Attendee(
        id = "att-4",
        eventId = "evt-101",
        ticketNumber = "EH-1894-TK",
        fullName = "Marcus Vance",
        email = "marcus.v@example.com",
        phone = "+1 (555) 678-1234",
        registeredAt = "Oct 16, 2026",
        isCheckedIn = false,
        ticketType = "General Admission"
      ),
      Attendee(
        id = "att-5",
        eventId = "evt-101",
        ticketNumber = "EH-6541-TK",
        fullName = "Sofia Taylor",
        email = "sofia.t@example.com",
        phone = "+1 (555) 345-6789",
        registeredAt = "Oct 16, 2026",
        isCheckedIn = false,
        ticketType = "Early Bird"
      ),
      // Attendees for evt-102
      Attendee(
        id = "att-6",
        eventId = "evt-102",
        ticketNumber = "EH-9932-TK",
        fullName = "Alex Rivera",
        email = "alex.rivera@example.com",
        phone = "+1 (555) 234-5678",
        registeredAt = "Oct 10, 2026",
        isCheckedIn = false,
        ticketType = "General Admission"
      ),
      Attendee(
        id = "att-7",
        eventId = "evt-102",
        ticketNumber = "EH-2481-TK",
        fullName = "Liam O'Connor",
        email = "liam.oc@example.com",
        phone = "+1 (555) 876-5432",
        registeredAt = "Oct 11, 2026",
        isCheckedIn = true,
        checkedInAt = "4:15 PM",
        ticketType = "VIP All-Access"
      )
    )

    _events.value = initialEvents
    _attendees.value = initialAttendees
  }

  fun getEventById(id: String): Event? {
    return _events.value.find { it.id == id }
  }

  fun getAttendeesForEvent(eventId: String): List<Attendee> {
    return _attendees.value.filter { it.eventId == eventId }
  }

  fun getAttendeeCountForEvent(eventId: String): Int {
    return _attendees.value.count { it.eventId == eventId }
  }

  fun getCheckedInCountForEvent(eventId: String): Int {
    return _attendees.value.count { it.eventId == eventId && it.isCheckedIn }
  }

  fun saveEvent(event: Event) {
    val current = _events.value.toMutableList()
    val index = current.indexOfFirst { it.id == event.id }
    if (index >= 0) {
      current[index] = event
    } else {
      current.add(0, event)
    }
    _events.value = current
  }

  fun deleteEvent(eventId: String) {
    _events.value = _events.value.filter { it.id != eventId }
    _attendees.value = _attendees.value.filter { it.eventId != eventId }
  }

  fun registerAttendee(
    eventId: String,
    fullName: String,
    email: String,
    phone: String,
    ticketType: String = "General Admission"
  ): Attendee {
    val randomNum = (1000..9999).random()
    val ticketNumber = "EH-$randomNum-TK"
    val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())

    val newAttendee = Attendee(
      id = "att-${UUID.randomUUID().toString().take(8)}",
      eventId = eventId,
      ticketNumber = ticketNumber,
      fullName = fullName.trim(),
      email = email.trim().lowercase(),
      phone = phone.trim(),
      registeredAt = dateStr,
      isCheckedIn = false,
      ticketType = ticketType
    )

    _attendees.value = listOf(newAttendee) + _attendees.value
    return newAttendee
  }

  fun checkInTicket(query: String, targetEventId: String? = null): CheckInResult {
    val trimmed = query.trim().uppercase()
    val list = _attendees.value

    // Search by exact ticket number, or ticket substring, or attendee ID, or attendee email/name
    val match = list.find { attendee ->
      (targetEventId == null || attendee.eventId == targetEventId) &&
          (attendee.ticketNumber.uppercase() == trimmed ||
              attendee.id.uppercase() == trimmed ||
              attendee.email.uppercase() == trimmed ||
              attendee.fullName.uppercase().contains(trimmed))
    } ?: list.find { attendee ->
      // Fallback: match without event restriction if needed
      attendee.ticketNumber.uppercase() == trimmed
    }

    if (match == null) {
      return CheckInResult(
        status = CheckInStatus.NOT_FOUND,
        message = "No valid ticket found matching \"$query\""
      )
    }

    val event = getEventById(match.eventId)

    if (match.isCheckedIn) {
      return CheckInResult(
        status = CheckInStatus.ALREADY_CHECKED_IN,
        attendee = match,
        event = event,
        message = "Already checked in! First checked in at ${match.checkedInAt ?: "earlier"}"
      )
    }

    // Perform check-in
    val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    val updatedMatch = match.copy(isCheckedIn = true, checkedInAt = timeStr)
    _attendees.value = _attendees.value.map { if (it.id == match.id) updatedMatch else it }

    return CheckInResult(
      status = CheckInStatus.SUCCESS,
      attendee = updatedMatch,
      event = event,
      message = "Check-in Confirmed! Welcome, ${updatedMatch.fullName}!"
    )
  }

  fun toggleCheckIn(attendeeId: String) {
    val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    _attendees.value = _attendees.value.map { att ->
      if (att.id == attendeeId) {
        if (att.isCheckedIn) {
          att.copy(isCheckedIn = false, checkedInAt = null)
        } else {
          att.copy(isCheckedIn = true, checkedInAt = timeStr)
        }
      } else {
        att
      }
    }
  }

  fun getTicketsForUser(email: String = "alex.rivera@example.com"): List<Pair<Event, Attendee>> {
    val userEmail = email.trim().lowercase()
    val userAttendees = _attendees.value.filter { it.email.lowercase() == userEmail }
    return userAttendees.mapNotNull { attendee ->
      val event = getEventById(attendee.eventId)
      if (event != null) event to attendee else null
    }
  }

  fun exportCsv(eventId: String): String {
    val event = getEventById(eventId)
    val list = getAttendeesForEvent(eventId)
    val sb = StringBuilder()
    sb.append("Ticket Number,Full Name,Email,Phone,Ticket Type,Registration Date,Checked In,Checked In Time\n")
    for (att in list) {
      sb.append("\"${att.ticketNumber}\",")
      sb.append("\"${att.fullName.replace("\"", "\"\"")}\",")
      sb.append("\"${att.email}\",")
      sb.append("\"${att.phone}\",")
      sb.append("\"${att.ticketType}\",")
      sb.append("\"${att.registeredAt}\",")
      sb.append("\"${if (att.isCheckedIn) "YES" else "NO"}\",")
      sb.append("\"${att.checkedInAt ?: ""}\"\n")
    }
    return sb.toString()
  }
}
