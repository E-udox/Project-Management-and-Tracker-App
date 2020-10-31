package org.wit.adam.assignment1.console.controllers

import mu.KotlinLogging
import org.wit.adam.assignment1.console.models.ProjectJSONStore
import org.wit.adam.assignment1.console.models.ProjectModel
import org.wit.adam.assignment1.console.views.ProjectView

class ProjectController {

    val projects = ProjectJSONStore()
    val projectView = ProjectView()
    val logger = KotlinLogging.logger {}

    fun menu() :Int { return projectView.menu() }

    fun start() {
        var input: Int

        do {
            input = menu()
            when(input) {
                1 -> add()
                2 -> list()
                3 -> update()
                4 -> settings()
                0 -> println("Exiting the app...")
                else -> println("Your option $input is invalid, try again")
            }
            println()
        } while (input != 0)


        logger.info { "Shutting down..." }
    }

    fun add(){
        var project = ProjectModel()

        if (projectView.addProjectData(project))
            projects.create(project)
        else
            logger.info("Project not created.")
    }



    fun list() {
        var input: Int

        input = projectView.viewProjects()
        when(input) {
            1 -> projectView.listProjects(projects.getAll())
            2 -> projectView.listProjects(projects.getProjectsByStatus(true, false))
            3 -> projectView.listProjects(projects.getProjectsByStatus(false, false))
            4 -> projectView.listProjects(projects.getProjectsByStatus(false, true))
            5 -> projectView.listProjects(projects.getProjectsByName(projectView.searchForProject()))
            else -> return
        }
    }

    fun update() {
        var searchId = projectView.getId()
        val project = search(searchId)

        if(project != null) {
            if (project.closed) {
                println("This project was closed, you cannot edit it again.")
            }
            var input: Int = projectView.updateOptions(project)

            when(input) {
                1 -> projectView.addTasksToProject(project)
                2 -> {
                    project.isActive = !project.isActive
                    project.activeSince = System.currentTimeMillis() //
                }
                3 -> project.name = projectView.updateProjectData(project.name, "name")
                4 -> project.description = projectView.updateProjectData(project.description, "description")
                5 -> {
                    if (projectView.confirmResponse("Are you sure you want to close this project?")) {
                        project.closed = true
                        project.isActive = false
                        project.closedOn = System.currentTimeMillis()
                    }
                }
                else -> return
            }
        }
        else
            println("A project with this ID wasn't found :(")
    }

    fun search() {
        val project = search(projectView.getId())!!
        projectView.showProject(project)
    }


    fun search(id: Long) : ProjectModel? {
        var foundPlacemark = projects.getOne(id)
        return foundPlacemark
    }

    fun settings() {
        // TODO: set up settings
    }
}