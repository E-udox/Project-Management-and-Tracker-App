package org.wit.adam.assignment1.console.views

import org.wit.adam.assignment1.console.helpers.ConsoleColors
import org.wit.adam.assignment1.console.models.ProjectJSONStore
import org.wit.adam.assignment1.console.models.ProjectModel
import org.wit.adam.assignment1.console.models.TaskModel
import java.text.SimpleDateFormat
import java.util.*

class ProjectView {

    var projectNameMaxLength: Int = 30
    var projectDescriptionMaxLength: Int = 60
    var projectNameMinLength: Int = 5
    var projectDescriptionMinLength: Int = 10

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

    fun showProjectOptions(project: ProjectModel): Int {
        var input: Int

        var activeStr: String
        if (project.isActive) {
            activeStr = "active"
        }
        else
            activeStr = "not active"

        println("UPDATE PROJECT OPTIONS")
        println(" 1. Add a new task")
        println(" 2. Set project as active/inactive (currently $activeStr)")
        println(" 3. Edit the project name")
        println(" 4. Edit the project description")
        println(" 5. View/Edit tasks (${project.tasks.size} tasks)")
        println(" 6. Close the project")
        println(" 7. Delete the project")
        println(" 0. Cancel")

        input = waitForValidResponse("Select an option from above: ", true, 0, 7) as Int
        return input
    }

    fun showProject(project : ProjectModel) {
        if (project != null) {
            println()
            println("Project name: ${project.name}")
            println("Project descripton: ${project.description}")

            var defaultTimeFormat = SimpleDateFormat("dd.MM.YY")
            var timeStr: String = defaultTimeFormat.format(Date(project.createdOn))
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
                // can't figure out why but the hour part of the date always starts with one hour, so I've hardcoded -1 to negate that...
                activeTimeStr = "${Date(project.totalActiveTime).hours - 1}h ${Date(project.totalActiveTime).minutes}m ${Date(project.totalActiveTime).seconds}s"
            }
            println("Total active time: $activeTimeStr")

            println("Number of tasks: ${project.tasks.size}")

            var priorityStr: String = ""
            when(project.priority) {
                1 -> priorityStr = "|*    |"
                2 -> priorityStr = "|**   |"
                3 -> priorityStr = "|***  |"
                4 -> priorityStr = "|**** |"
                5 -> priorityStr = "|*****|"
            }
            println("Project priority: $priorityStr")

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

    fun showTask(task: TaskModel) {
        println("Description: ${task.description}")

        var defaultTimeFormat = SimpleDateFormat("dd.MM.YY")
        var timeStr: String = defaultTimeFormat.format(Date(task.createdOn))
        println("Created on: $timeStr")

        if (task.closedOn == -1L) {
            println("Closed: No")
        }
        else {
            var timeStr: String = defaultTimeFormat.format(Date(task.closedOn))
            println("Closed on: $timeStr")
        }
        println("Task ID: ${task.id}")
        println()
    }

    fun promptUpdateTask(task: TaskModel) {
        println("Current description for task ${task.description}")
        task.description = waitForValidResponse("Please enter a new description for this task: ", false, 10, 20) as String
    }

    fun showTaskMenu(): Int {
        var input: Int

        println("TASK MENU OPTIONS")
        println(" 1. Edit task description")
        println(" 2. Close task")
        println(" 3. Delete task")
        println(" 0. Cancel")

        input = waitForValidResponse("Select an option from above: ", true, 0, 3) as Int
        return input
    }


    fun promptSearchForProject(): String {
        var searchStr: String

        searchStr = waitForValidResponse("What is the name of the project: ", false, 5, 15) as String

        return searchStr
    }

    fun showFilterProjectsMenu(): Int {
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

    fun showProjectAndId(project: ProjectModel) {
        var projectStr: String = project.name
        var projectStatus: String = "Inactive"
        if (project.isActive) {
            projectStatus = "Active"
        }

        println(projectStr.padEnd(30) +
                projectStatus.padEnd(15) +
                project.id)
    }

    fun listProjectsAndIds(projects: List<ProjectModel>) {
        println("------------------------------------------------------")
        println("PROJECT".padEnd(30) +
                "IS ACTIVE".padEnd(15) +
                "ID")
        projects.forEach { p -> showProjectAndId(p)}
        println("------------------------------------------------------")
    }

    fun showTaskAndId(task: TaskModel) {
        var taskDescriptionStr: String = task.description
        var taskStatusStr: String = "Closed"
        if (task.closedOn == -1L) {
            taskStatusStr = "Active"
        }

        println(taskDescriptionStr.padEnd(30) +
                taskStatusStr.padEnd(15) +
                task.id)
    }

    fun listTasksAndIds(tasks: List<TaskModel>) {
        println("------------------------------------------------------")
        println("TASK".padEnd(30) +
                "IS ACTIVE".padEnd(15) +
                "ID")
        tasks.forEach { t -> showTaskAndId(t) }
        println("------------------------------------------------------")
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
        project.name = waitForValidResponse("Enter a project name: ", false, projectNameMinLength, projectNameMaxLength) as String
        project.description = waitForValidResponse("Enter a project description: ", false, projectDescriptionMinLength, projectDescriptionMaxLength) as String
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

    // TODO: Please please take this away :pensive:
    fun generateRandomId(): Long {
        var id: Long = Random().nextLong()
        if (id > 0) {
            return id
        }
        else return generateRandomId()
    }

    fun addTasksToProject(project: ProjectModel) {
        var task: TaskModel = TaskModel()

        task.description = waitForValidResponse("Enter the task description: ", false, projectDescriptionMinLength, projectDescriptionMaxLength) as String
        task.id = generateRandomId() // :(
        project.tasks.add(task)


        if (confirmResponse("Add another task to this project?")) {
            addTasksToProject(project)
        }
    }

    fun updateProjectData(currentValue: String, propertyName: String): String {
        var input: String
        var maxLength: Int = projectNameMaxLength
        var minLength: Int = projectNameMinLength

        if (propertyName == "description") {
            maxLength = projectDescriptionMaxLength
            minLength = projectDescriptionMinLength
        }

        println("Current $propertyName for this project is '$currentValue'")
        input = waitForValidResponse("Enter a new $propertyName for this project: ", false, minLength, maxLength) as String
        return input
    }

    fun getId(isTask: Boolean) : Long {
        var strId : String? // String to hold user input
        var searchId : Long // Long to hold converted id
        if (isTask) {
            print("Enter the ID of the task: ")
        }
        else
            print("Enter the ID of the project: ")

        strId = readLine()!!
        searchId = if (strId.toLongOrNull() != null && !strId.isEmpty())
            strId.toLong()
        else
            -9
        return searchId
    }
}