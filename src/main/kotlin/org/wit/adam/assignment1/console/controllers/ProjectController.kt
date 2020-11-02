package org.wit.adam.assignment1.console.controllers

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

        input = projectView.showFilterProjectsMenu()
        when(input) {
            1 -> projectView.listProjects(projects.getAll())
            2 -> projectView.listProjects(projects.getProjectsByStatus(true, false))
            3 -> projectView.listProjects(projects.getProjectsByStatus(false, false))
            4 -> projectView.listProjects(projects.getProjectsByStatus(false, true))
            5 -> projectView.listProjects(projects.getProjectsByName(projectView.promptSearchForProject()))
            else -> return
        }
    }

    fun taskMenu(project: ProjectModel) {
        var task: TaskModel?

        if (project.tasks.size == 0) {
            if (projectView.confirmResponse("This project has no tasks... would you like to add some?")) {
                projectView.addTasksToProject(project)
            }
            else
                return
        }

        if (project.tasks.size > 1) {
            projectView.listTasksAndIds(project.tasks)
            var taskId: Long = projectView.getId(true)
            task = project.tasks.find { t -> t.id == taskId }
        }
        else {
            println("Only one task available, automatically selecting...")
            task = project.tasks.first()
            println()
            projectView.showTask(task)
        }

        if (task != null) {
            if (task.closedOn != -1L) {
                println("This task has been closed, you cannot further edit it.")
                return
            }

            do {
                var input: Int = projectView.showTaskMenu()

                when(input) {
                    1 -> projectView.promptUpdateTask(task)
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
        projectView.listProjectsAndIds(projects.getProjectsForListingIds())

        var searchId = projectView.getId(false)
        val project = search(searchId)

        if(project != null) {
            if (project.closed) {
                println("This project was closed, you cannot edit it again.")
            }

            do {
                var input: Int = projectView.showProjectOptions(project)

                when (input) {
                    1 -> projectView.addTasksToProject(project)
                    2 -> {
                        project.isActive = !project.isActive
                        project.activeSince = System.currentTimeMillis()
                        System.out.println("Project activity updated.")
                    }
                    3 -> {
                        project.name = projectView.updateProjectData(project.name, "name")
                        println("Project name updated.")
                    }
                    4 -> {
                        project.description = projectView.updateProjectData(project.description, "description")
                        println("Project description updated.")
                    }
                    5 -> taskMenu(project)
                    6 -> {
                        if (projectView.confirmResponse("Are you sure you want to close this project?")) {
                            project.closed = true
                            project.isActive = false
                            project.closedOn = System.currentTimeMillis()
                            println("Project has been closed...")
                            return
                        }
                    }
                    7 -> {
                        if (projectView.confirmResponse("Are you sure you want to delete this project?")) {
                            projects.projects.remove(project)
                            println("Project has been deleted...")
                        }
                    }
                    else -> println("Invalid input, try again.")
                }
            } while (input != 0)
        }
        else
            println("A project with this ID wasn't found :(")
    }


    fun search(id: Long) : ProjectModel? {
        var foundPlacemark = projects.getOne(id)
        return foundPlacemark
    }

    fun settings() {
        // TODO: set up settings
    }
}