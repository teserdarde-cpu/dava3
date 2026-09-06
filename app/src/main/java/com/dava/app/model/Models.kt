package com.dava.app.model

data class Contact(
    val id: Long = 0,
    val name: String,
    val role: String = "",
    val district: String = "",
    val category: String = "Kişi",
    val phone: String = "",
    val lastContact: String = "",
    val nextContact: String = "",
    val note: String = ""
)

data class FollowUp(
    val id: Long = 0,
    val title: String,
    val context: String = "",
    val dueDate: String = "",
    val status: String = "Açık",
    val priority: Int = 1,
    val progress: Int = 0
)

data class Project(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val progress: Int = 0,
    val status: String = "Planlama"
)

data class NoteItem(
    val id: Long = 0,
    val title: String,
    val body: String = "",
    val category: String = "Not",
    val createdAt: String = ""
)
