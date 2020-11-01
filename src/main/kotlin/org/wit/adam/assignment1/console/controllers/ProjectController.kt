package org.wit.adam.assignment1.console.controllers

import javafx.concurrent.Task
import mu.KotlinLogging
import org.wit.adam.assignment1.console.models.ProjectJSONStore
import org.wit.adam.assignment1.console.models.ProjectModel
import org.wit.adam.assignment1.console.models.TaskModel
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

    fun taskMenu(project: ProjectModel) {
        var taskId: Long = projectView.getId(true)
        var task: TaskModel? = project.tasks.find { t -> t.id == taskId }

        if (task != null) {
            if (task.closedOn != -1L) {
                println("This task has been closed, you cannot further edit it.")
                return
            }

            do {
                var input: Int = projectView.showTaskMenu()

                when(input) {
                    1 -> projectView.updateTask(task)
                    2 -> {
                        task.closedOn = System.currentTimeMillis()
                        println("Task has been closed.")
                        return
                    }
                    3 -> {
                        if (projectView.confirmResponse("Are you sure you want to delete this task?")) {
                            project.tasks.remove(task)
                            println("Task has been deleted.")
                            return
                        }
                    }
                }
            } while (input != 0)
        }
        else {
            println("There was no task with this ID found... try again")
            taskMenu(project)
        }
    }

    fun update() {
        var searchId = projectView.getId(true)
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
                    project.activeSince = System.currentTimeMillis()
                }
                3 -> project.name = projectView.updateProjectData(project.name, "name")
                4 -> project.description = projectView.updateProjectData(project.description, "description")
                5 -> {
                    if (projectView.listProjectTasks(project) && projectView.confirmResponse("Would you like to interact with these tasks?")) {
                        taskMenu(project)
                    }
                }
                6 -> {
                    if (projectView.confirmResponse("Are you sure you want to close this project?")) {
                        project.closed = true
                        project.isActive = false
                        project.closedOn = System.currentTimeMillis()
                        println("Project has been closed...")
                    }
                }
                7 -> {
                    projects.projects.remove(project)
                    println("Project has been deleted...")
                }
                else -> return
            }
        }
        else
            println("A project with this ID wasn't found :(")
    }

    fun search() {
        val project = search(projectView.getId(true))!!
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