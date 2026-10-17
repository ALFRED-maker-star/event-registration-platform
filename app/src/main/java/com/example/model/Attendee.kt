package com.example.model

data class Attendee(
  val id: String,
  val eventId: String,
  val ticketNumber: String,
  val fullName: String,
  val email: String,
  val phone: String,
  val registeredAt: String,
  val isCheckedIn: Boolean = false,
  val checkedInAt: String? = null,
  val ticketType: String = "General Admission",
  val notes: String = ""
)
