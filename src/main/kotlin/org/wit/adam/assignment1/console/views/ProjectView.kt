package org.wit.adam.assignment1.console.views

import javafx.concurrent.Task
import org.wit.adam.assignment1.console.helpers.read
import org.wit.adam.assignment1.console.models.ProjectJSONStore
import org.wit.adam.assignment1.console.models.ProjectModel
import org.wit.adam.assignment1.console.models.TaskModel

class ProjectView {

    fun menu() : Int {

        var option: Int
        var input: String?

        println("PROJECT MANAGEMENT AND TRACKER MENU")
        println(" 1. Start a new project")
        println(" 2. View projects")
        println(" 3. Edit a project")
        println(" 4. Settings")
        println(" 0. Exit")
        println()
        print("Enter an option: ")
        input = readLine()!!
        option = if (input.toIntOrNull() != null && !input.isEmpty())
            input.toInt()
        else
            -9
        return option
    }

    fun showProject(project : ProjectModel) {
        if(project != null)
            println("Placemark Details [ $project ]")
        else
            println("Placemark Not Found...")
    }

    fun showAllProjects(projects: ProjectJSONStore) {
        projects.logAll()
    }

    fun addProjectData(project : ProjectModel) : Boolean {
        var setActive: String
        var tempPriority: Int
        var addTasksNow: String

        println()
        print("Enter a project name: ")
        project.name = readLine()!!
        print("Enter a description: ")
        project.description = readLine()!!

        do {
            print("Give your project a priority (1-5): ")
            tempPriority = readLine()?.toIntOrNull()!!
        }
        while (tempPriority == null || tempPriority > 5 || tempPriority < 0)
        project.priority = tempPriority

        print("Do you want to add tasks to this project now? y/n: ")
        addTasksNow = readLine()!!
        if (addTasksNow.toUpperCase() == "Y") {
            addTasksToProject(project)
        }

        print("Do you want to set this project as active now? y/n: ")
        setActive = readLine()!!
        if (setActive.toUpperCase() == "Y") {
            project.isActive = true
            project.activeSince = System.currentTimeMillis()
        }

        return project.name.isNotEmpty() && project.description.isNotEmpty()
    }

    fun addTasksToProject(project: ProjectModel): Boolean {
        var addAnotherTask: String = ""

        do {
            var task: TaskModel = TaskModel()

            println()
            print("Enter the task description: ")
            task.description = readLine()!!

            if (task.description.isNotEmpty()) {
                project.tasks.add(task)
                print("Task added. Do you want to add another task? y/n: ")
                addAnotherTask = readLine()!!
            }
            else
                println("You have to add a task description...")

        } while (addAnotherTask.toUpperCase() == "Y")

        return true
    }

    fun updateProjectData(project : ProjectModel) : Boolean {

        var tempName: String?
        var tempDescription: String?

        if (project != null) {
            print("Enter a new name for this project: ")
            tempName = readLine()!!
            print("Enter a new description for the project: ")
            tempDescription = readLine()!!

            if (!tempName.isNullOrEmpty() && !tempDescription.isNullOrEmpty()) {
                project.name = tempName
                project.description = tempDescription
                return true
            }
        }
        return false
    }

    fun getId() : Long {
        var strId : String? // String to hold user input
        var searchId : Long // Long to hold converted id
        print("Enter id of the project: ")
        strId = readLine()!!
        searchId = if (strId.toLongOrNull() != null && !strId.isEmpty())
            strId.toLong()
        else
            -9
        return searchId
    }
}