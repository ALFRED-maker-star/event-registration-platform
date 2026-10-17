package com.example.model

data class Event(
  val id: String,
  val name: String,
  val description: String,
  val dateTime: String,
  val venue: String,
  val capacity: Int,
  val ticketPrice: String = "Free",
  val coverDrawableRes: Int? = null,
  val category: String = "Technology",
  val isPublished: Boolean = true,
  val organizerName: String = "EventHub Productions",
  val tags: List<String> = listOf("Featured", "Popular")
)
