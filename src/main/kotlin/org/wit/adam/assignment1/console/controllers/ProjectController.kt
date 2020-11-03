package org.wit.adam.assignment1.console.controllers

import mu.KotlinLogging
import org.wit.adam.assignment1.console.helpers.ConsoleColors
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
                0 -> ConsoleColors.success("Exiting the app...")
                else -> ConsoleColors.warning("Your option $input is invalid, try again")
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

    // uses to display back a filtered list of projects based on what a user wants to see
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

    // deals with the user interacting with tasks
    fun taskMenu(project: ProjectModel) {
        var task: TaskModel?

        if (project.tasks.size == 0) {
            if (projectView.confirmResponse("This project has no tasks... would you like to add some?")) {
                projectView.addTasksToProject(project)
            }
            else
                return
        }

        /*
         if there's more than one task we need to ask which one the user wants to see,
         otherwise let's just automatically select the only one available
         */
        if (project.tasks.size > 1) {
            projectView.listTasksAndIds(project.tasks)
            var taskId: Long = projectView.getId(true)
            task = project.tasks.find { t -> t.id == taskId }
        }
        else {
            ConsoleColors.success("Only one task available, automatically selecting...")
            task = project.tasks.first()
            println()
            projectView.showTask(task)
        }

        if (task != null) {
            if (task.closedOn != -1L) {
                ConsoleColors.warning("This task has been closed, you cannot further edit it.")
                return
            }

            do {
                var input: Int = projectView.showTaskMenu()

                when(input) {
                    1 -> projectView.promptUpdateTask(task)
                    2 -> {
                        task.closedOn = System.currentTimeMillis()
                        ConsoleColors.success("Task has been closed.")
                        return
                    }
                    3 -> {
                        if (projectView.confirmResponse("Are you sure you want to delete this task?")) {
                            project.tasks.remove(task)
                            ConsoleColors.success("Task has been deleted.")
                            return
                        }
                    }
                }
            } while (input != 0)
        }
        else {
            ConsoleColors.warning("There was no task with this ID found... try again")
            taskMenu(project)
        }
    }

    // handles the user editing a project
    fun update() {
        var project: ProjectModel?

        if (projects.projects.size == 0) {
            if (projectView.confirmResponse("You have no projects... would you like to create one?")) {
                add()
            }
            else
                return
        }

        /*
         if there's more than one project we need to ask which one the user wants to see,
         otherwise let's just automatically select the only one available
         */
        if (projects.projects.size > 1) {
            projectView.listProjectsAndIds(projects.getProjectsForListingIds())
            var searchId = projectView.getId(false)
            project = search(searchId)
        }
        else {
            ConsoleColors.success("Only one project available, automatically selecting...")
            project = projects.projects.first()
        }

        if(project != null) {
            if (project.closed) {
                ConsoleColors.warning("This project was closed, you cannot edit it again.")
            }

            do {
                var input: Int = projectView.showProjectOptions(project)

                when (input) {
                    1 -> projectView.addTasksToProject(project)
                    2 -> {
                        project.isActive = !project.isActive
                        project.activeSince = System.currentTimeMillis()
                        ConsoleColors.success("Project activity updated.")
                    }
                    3 -> {
                        project.name = projectView.updateProjectData(project.name, "name")
                        ConsoleColors.success("Project name updated.")
                    }
                    4 -> {
                        project.description = projectView.updateProjectData(project.description, "description")
                        ConsoleColors.success("Project description updated.")
                    }
                    5 -> taskMenu(project)
                    6 -> {
                        if (projectView.confirmResponse("Are you sure you want to close this project?")) {
                            project.closed = true
                            project.isActive = false
                            project.closedOn = System.currentTimeMillis()
                            ConsoleColors.success("Project has been closed...")
                            return
                        }
                    }
                    7 -> {
                        if (projectView.confirmResponse("Are you sure you want to delete this project?")) {
                            projects.projects.remove(project)
                            ConsoleColors.success("Project has been deleted...")
                        }
                    }
                    0 -> ConsoleColors.success("Going back to main menu...")
                    else -> ConsoleColors.warning("Invalid input, try again.")
                }
            } while (input != 0)
        }
        else
            ConsoleColors.warning("A project with this ID wasn't found :(")
    }


    fun search(id: Long) : ProjectModel? {
        var project = projects.getOne(id)
        return project
    }

    fun settings() {
        // TODO: set up settings
    }
}