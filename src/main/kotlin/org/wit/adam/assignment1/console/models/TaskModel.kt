package org.wit.adam.assignment1.console.models

data class TaskModel(var id: Long = 0,
                     var projectId: Long = 0,
                     var title: String = "",
                     var description: String = "",
                     var createdOn: Long = System.currentTimeMillis(),
                     var closedOn: Long = -1)