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
    return Random().nextLong()
}

class ProjectJSONStore : ProjectStore {

    var projects = mutableListOf<ProjectModel>()

    init {
        if (exists(JSON_FILE)) {
            deserialize()
        }
    }

    override fun getAll(): MutableList<ProjectModel> {
        return projects
    }

    override fun getOne(id: Long) : ProjectModel? {
        var foundProject: ProjectModel? = projects.find { p -> p.id == id }
        return foundProject
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
            foundProject.closedOn = project.closedOn
            foundProject.priority = project.priority
            foundProject.tasks = project.tasks
            foundProject.totalActiveTme = project.totalActiveTme
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