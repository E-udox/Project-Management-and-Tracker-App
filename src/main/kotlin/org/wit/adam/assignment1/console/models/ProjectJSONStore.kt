package org.wit.adam.assignment1.console.models

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import mu.KotlinLogging
import org.wit.adam.assignment1.console.helpers.*
import java.util.*

private val logger = KotlinLogging.logger {}
val JSON_FILE = "projects.json"
val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
val listType = object : TypeToken<java.util.ArrayList<ProjectModel>>() {}.type

fun generateRandomId(): Long {
    var id: Long = Random().nextLong()
    if (id > 0) {
        return id
    }
    else return generateRandomId()
}

class ProjectJSONStore : ProjectStore {

    var projects = mutableListOf<ProjectModel>()

    init {
        if (exists(JSON_FILE)) {
            deserialize()
        }
    }

    override fun getAll(): List<ProjectModel> {
        var projects = projects.sortedByDescending { p -> p.priority }

        return projects
    }

    override fun getOne(id: Long) : ProjectModel? {
        var foundProject: ProjectModel? = projects.find { p -> p.id == id }
        return foundProject
    }

    fun getProjectsByName(searchStr: String): List<ProjectModel> {
        var projectsFound = projects.filter{ p -> p.name.contains(searchStr, true)}

        return projectsFound
    }

    fun getProjectsByStatus(active: Boolean, closed: Boolean): List<ProjectModel> {
        var projectsFound = projects.filter{ p -> p.isActive == active && p.closed == closed }.sortedByDescending{ p -> p.priority }

        return projectsFound
    }

    fun getProjectsForListingIds(): List<ProjectModel> {
        var projects = projects.sortedByDescending { p -> p.priority }.filter { p -> p.closed == false }

        return projects
    }

    override fun create(project: ProjectModel) {
        project.id = generateRandomId()
        projects.add(project)
        serialize()
    }

    override fun update(project: ProjectModel) {
        var foundProject = getOne(project.id!!)
        if (foundProject != null) {
            foundProject.name = project.name
            foundProject.description = project.description
            foundProject.activeSince = project.activeSince
            foundProject.priority = project.priority
            foundProject.tasks = project.tasks
            foundProject.closed = project.closed
            if (foundProject.isActive) {
                foundProject.totalActiveTime = project.totalActiveTime + (System.currentTimeMillis() - project.activeSince)
            }
            else
                foundProject.totalActiveTime = project.totalActiveTime
            foundProject.isActive = project.isActive
        }
        serialize()
    }

    internal fun logAll() {
        projects.forEach { logger.info("${it}") }
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(projects, listType)
        write(JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(JSON_FILE)
        projects = Gson().fromJson(jsonString, listType)
    }
}