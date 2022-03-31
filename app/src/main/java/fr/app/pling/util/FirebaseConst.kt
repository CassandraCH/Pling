package fr.app.pling.util

// COLLECTION
const val COLLECTION_PROJECT = "Project"
const val COLLECTION_TASK = "Task"
const val COLLECTION_USER = "User"
const val COLLECTION_NOTIFICATION = "Notification"

// FIELD

// PROJECTS

// TASKS
enum class NotificationType {
   NONE, ADDED_TO_PROJECT, REMOVE_FROM_PROJECT, ADDED_TO_TASK, REMOVE_FROM_TASK;

   open fun get(index: Int): NotificationType {
      return values()[index]
   }
}