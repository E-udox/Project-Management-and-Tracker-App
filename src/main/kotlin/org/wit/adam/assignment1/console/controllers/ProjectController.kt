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
        var optionChosen: Int

        optionChosen = projectView.viewProjects()
        when(optionChosen) {
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
            if(projectView.updateProjectData(project)) {
                projects.update(project)
                projectView.showProject(project)
            }
            else
                logger.info("Project not updated")
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