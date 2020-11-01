package org.wit.adam.assignment1.console.views

import org.wit.adam.assignment1.console.models.ProjectJSONStore
import org.wit.adam.assignment1.console.models.ProjectModel
import org.wit.adam.assignment1.console.models.TaskModel
import java.text.SimpleDateFormat
import java.util.*

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

    fun updateOptions(project: ProjectModel): Int {
        var input: Int

        var activeStr: String
        if (project.isActive) {
            activeStr = "active"
        }
        else
            activeStr = "not active"

        println("UPDATE PROJECT OPTIONS")
        println(" 1. Add a new task")
        println(" 2. Set project as active/inactive (currently $activeStr")
        println(" 3. Edit the project name")
        println(" 4. Edit the project description")
        println(" 5. Close the project")
        println(" 0. Cancel")

        input = waitForValidResponse("Select an option from above: ", true, 0, 5) as Int
        return input
    }

    fun showProject(project : ProjectModel) {
        if (project != null) {
            println()
            println("Project name: ${project.name}")
            println("Project descripton: ${project.description}")

            val defaultTimeFormat = SimpleDateFormat("dd.MM.YY")
            val timeStr: String = defaultTimeFormat.format(Date(project.createdOn))
            println("Project created on: $timeStr")

            var activeStr: String
            if (project.isActive) {
                activeStr = "Yes"
            }
            else
                activeStr = "No"
            println("Project active: $activeStr")


            var activeTimeStr: String = ""

            // if the project is currently active I'm adding the amount of time it's been active to the recorded total time it already has
            if (project.isActive) {
                project.totalActiveTime = project.totalActiveTime + (System.currentTimeMillis() - project.activeSince)
            }

            if (project.totalActiveTime == 0L) {
                activeTimeStr = "This project has never been active"
            }
            else {
                activeTimeStr = "${Date(project.totalActiveTime).hours}h ${Date(project.totalActiveTime).minutes}m ${Date(project.totalActiveTime).seconds}s"
            }
            println("Total active time: $activeTimeStr")

            println("Number of tasks: ${project.tasks.size}")

            var closedStr: String
            if (project.closed) {
                closedStr = "Yes"
            }
            else
                closedStr = "No"

            println("Project ID: ${project.id}")
            println("Closed: $closedStr")
        }
    }

    fun searchForProject(): String {
        var searchStr: String

        searchStr = waitForValidResponse("What is the name of the project: ", false, 5, 15) as String

        return searchStr
    }

    fun viewProjects(): Int {
        var input: Int

        println("What do you want to view?")
        println(" 1. All projects")
        println(" 2. Active Projects")
        println(" 3. Inactive projects")
        println(" 4. Closed Projects")
        println(" 5. Search for a specific project by name")
        println(" 0. Cancel")

        input = waitForValidResponse("Select an option from above: ", true, 0, 5) as Int

        return input
    }

    fun listProjects(projects: List<ProjectModel>) {
        println("-------------------------------------------------")
        projects.forEach { p -> showProject(p)}
        println("-------------------------------------------------")
    }

    fun waitForValidResponse(prompt: String, intValue: Boolean, min: Int, max: Int): Any {
        var value: String

        while (true) {
            print(prompt)
            value = readLine()!!

            if (value.isEmpty()) {
                System.out.println("You can't skip this, sorry. :(")
            }
            else if (intValue) {
                var tempInt: Int?
                tempInt = value.toIntOrNull()

                if (tempInt == null) {
                    println("You must specify a valid number.")
                } else if (tempInt < min || tempInt > max) {
                    println("You must specify a number between $min and $max.")
                }
                else
                    return tempInt
            } else
                if (value.length < min) {
                    println("Sorry, this is too short. Try something longer (min length is $min)")
                } else if (value.length > max) {
                    println("Sorry, this is too long. Try something shorter (max length is $max)")
                } else
                    return value
        }
    }

    fun confirmResponse(prompt: String): Boolean {
        val response: String

        print(prompt+" y/n: ")
        response = readLine()!!

        when (response.toUpperCase()) {
            "Y" -> return true
            "N" -> return false
            else -> {
                println("This is an invalid response...")
                return confirmResponse(prompt)
            }
        }
    }

    fun addProjectData(project : ProjectModel) : Boolean {
        project.name = waitForValidResponse("Enter a project name: ", false, 5, 20) as String
        project.description = waitForValidResponse("Enter a project description: ", false, 10, 30) as String
        project.priority = waitForValidResponse("Give your project a priority between 1 (low) and 5 (high): ", true, 1, 5) as Int


/*      I started using the waitForValidResponse function to stop this code below from popping up everywhere
        while (tempPriority == null || tempPriority > 5 || tempPriority < 0) {
            print("Invalid input... the priority must be a number between 1 (low) and 5 (high): ")
            tempPriority = readLine()!!.toIntOrNull()
        }
        project.priority = tempPriority*/

        if (confirmResponse("Do you want to add tasks to this project now?")) {
            addTasksToProject(project)
        }

        if (confirmResponse("Do you want to set this project as active now?")) {
            project.isActive = true
            project.activeSince = System.currentTimeMillis()
        }

        return true
    }

    fun addTasksToProject(project: ProjectModel) {
        var task: TaskModel = TaskModel()

        task.description = waitForValidResponse("Enter the task description: ", false, 5, 20) as String
        project.tasks.add(task)

        if (confirmResponse("Add another task to this project?")) {
            addTasksToProject(project)
        }
    }

    fun updateProjectData(currentValue: String, propertyName: String): String {
        var input: String

        println("Current $propertyName for this project is '$currentValue'")
        input = waitForValidResponse("Enter a new $propertyName for this project: ", false, 5, 15) as String
        return input
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