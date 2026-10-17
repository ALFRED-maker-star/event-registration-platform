package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CheckInStatus
import com.example.data.EventRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("EventHub", appName)
  }

  @Test
  fun `verify event repository registration and check in`() {
    val repository = EventRepository()
    val events = repository.events.value
    assertTrue("Should have initial events", events.isNotEmpty())

    val firstEvent = events.first()
    val attendee = repository.registerAttendee(
      eventId = firstEvent.id,
      fullName = "Jordan Reed",
      email = "jordan.reed@test.com",
      phone = "+1 (555) 333-4444",
      ticketType = "VIP Pass"
    )

    assertNotNull(attendee)
    assertEquals("Jordan Reed", attendee.fullName)
    assertTrue(attendee.ticketNumber.startsWith("EH-"))

    // First check-in should succeed
    val checkIn1 = repository.checkInTicket(attendee.ticketNumber, firstEvent.id)
    assertEquals(CheckInStatus.SUCCESS, checkIn1.status)

    // Second check-in should show already checked in
    val checkIn2 = repository.checkInTicket(attendee.ticketNumber, firstEvent.id)
    assertEquals(CheckInStatus.ALREADY_CHECKED_IN, checkIn2.status)

    // CSV export test
    val csv = repository.exportCsv(firstEvent.id)
    assertTrue(csv.contains("Jordan Reed"))
    assertTrue(csv.contains(attendee.ticketNumber))
  }
}
