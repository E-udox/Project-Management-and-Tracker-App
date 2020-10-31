package org.wit.adam.assignment1.console.models

import java.time.LocalDateTime


data class ProjectModel(var id: Long = 0,
                        var name: String = "",
                        var description: String = "",
                        var createdOn: Long = System.currentTimeMillis(),
                        var closedOn: Long = -1,
                        var isActive: Boolean = false, // TODO: should be a setting to change whether a project starts immediately after creation
                        var activeSince: Long = -1, // this will track when a project was flagged as being active.
                        var totalActiveTime: Long = 0,
                        var priority: Int = 0,
                        var closed: Boolean = false,
                        var tasks: ArrayList<TaskModel> = ArrayList<TaskModel>())