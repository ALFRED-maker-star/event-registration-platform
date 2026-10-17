package com.example.model

enum class UserRole(val label: String, val subtitle: String) {
  ORGANIZER("Organizer", "Manage events, track capacity & check-in attendees"),
  ATTENDEE("Attendee", "Discover events, register & access mobile tickets")
}
