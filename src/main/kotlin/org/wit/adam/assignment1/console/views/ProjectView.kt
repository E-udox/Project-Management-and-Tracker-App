package org.wit.adam.assignment1.console.views

import org.wit.adam.assignment1.console.helpers.ConsoleColors
import org.wit.adam.assignment1.console.models.ProjectJSONStore
import org.wit.adam.assignment1.console.models.ProjectModel
import org.wit.adam.assignment1.console.models.TaskModel
import java.text.SimpleDateFormat
import java.util.*

class ProjectView {

    // some variables for limiting the min/max length of project/tasks names
    var projectNameMaxLength: Int = 30
    var projectDescriptionMaxLength: Int = 60
    var projectNameMinLength: Int = 5
    var projectDescriptionMinLength: Int = 10

    fun menu() : Int {
        var option: Int

        ConsoleColors.highlight("<b>PROJECT MANAGEMENT AND TRACKER MENU</b>")
        ConsoleColors.highlight(" <b>1.</b> Start a new project")
        ConsoleColors.highlight(" <b>2.</b> View projects")
        ConsoleColors.highlight(" <b>3.</b> Edit a project")
        ConsoleColors.highlight(" <b>0.</b> Exit")
        println()
        option = waitForValidResponse("Enter an option: ", true, 0, 4) as Int

        return option
    }

    // prints the menu associated with editing a project
    fun showProjectOptions(project: ProjectModel): Int {
        var input: Int

        var activeStr: String
        if (project.isActive) {
            activeStr = "active"
        }
        else
            activeStr = "not active"

        ConsoleColors.highlight("<b>UPDATE PROJECT OPTIONS</b>")
        ConsoleColors.highlight(" <b>1.</b> Add a new task")
        ConsoleColors.highlight(" <b>2.</b> Set project as active/inactive (currently $activeStr)")
        ConsoleColors.highlight(" <b>3.</b> Edit the project name")
        ConsoleColors.highlight(" <b>4.</b> Edit the project description")
        ConsoleColors.highlight(" <b>5.</b> View/Edit tasks (${project.tasks.size} tasks)")
        ConsoleColors.highlight(" <b>6.</b> Close the project")
        ConsoleColors.highlight(" <b>7.</b> Delete the project")
        ConsoleColors.highlight(" <b>0.</b> Cancel")

        input = waitForValidResponse("Select an option from above: ", true, 0, 7) as Int
        return input
    }

    // prints a project
    fun showProject(project : ProjectModel) {
        if (project != null) {
            println()
            ConsoleColors.highlight("<b>Project name:</b> ${project.name}")
            ConsoleColors.highlight("<b>Project descripton:</b> ${project.description}")

            var defaultTimeFormat = SimpleDateFormat("dd.MM.YY")
            var timeStr: String = defaultTimeFormat.format(Date(project.createdOn))
            ConsoleColors.highlight("<b>Project created on:</b> $timeStr")

            var activeStr: String
            if (project.isActive) {
                activeStr = "Yes"
            }
            else
                activeStr = "No"
            ConsoleColors.highlight("<b>Project active:</b> $activeStr")


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
            ConsoleColors.highlight("<b>Total active time:</b> $activeTimeStr")

            ConsoleColors.highlight("<b>Number of tasks:</b> ${project.tasks.size}")

            var priorityStr: String = ""
            when(project.priority) {
                1 -> priorityStr = "|*    |"
                2 -> priorityStr = "|**   |"
                3 -> priorityStr = "|***  |"
                4 -> priorityStr = "|**** |"
                5 -> priorityStr = "|*****|"
            }
            ConsoleColors.highlight("<b>Project priority:</b> $priorityStr")

            var closedStr: String
            if (project.closed) {
                closedStr = "Yes"
            }
            else
                closedStr = "No"

            ConsoleColors.highlight("<b>Project ID:</b> ${project.id}")
            ConsoleColors.highlight("<b>Closed:</b> $closedStr")
        }
    }


    // prints a task
    fun showTask(task: TaskModel) {
        ConsoleColors.highlight("<b>Description:</b> ${task.description}")

        var defaultTimeFormat = SimpleDateFormat("dd.MM.YY")
        var timeStr: String = defaultTimeFormat.format(Date(task.createdOn))
        ConsoleColors.highlight("<b>Created on:</b> $timeStr")

        if (task.closedOn == -1L) {
            ConsoleColors.highlight("<b>Closed:</b> No")
        }
        else {
            var timeStr: String = defaultTimeFormat.format(Date(task.closedOn))
            println("Closed on: $timeStr")
        }
        ConsoleColors.highlight("<b>Task ID:</b> ${task.id}")
        println()
    }

    // allows the user to update a task description
    fun promptUpdateTask(task: TaskModel) {
        println("Current description for task ${task.description}")
        task.description = waitForValidResponse("Please enter a new description for this task: ", false, 10, 20) as String
    }

    // prints the menu associated with editing tasks
    fun showTaskMenu(): Int {
        var input: Int

        ConsoleColors.highlight("<b>TASK MENU OPTIONS</b>")
        ConsoleColors.highlight(" <b>1.</b> Edit task description")
        ConsoleColors.highlight(" <b>2.</b> Close task")
        ConsoleColors.highlight(" <b>3.</b> Delete task")
        ConsoleColors.highlight(" <b>0.</b> Cancel")

        input = waitForValidResponse("Select an option from above: ", true, 0, 3) as Int
        return input
    }

    // allows the user to search for a project by name
    fun promptSearchForProject(): String {
        var searchStr: String

        searchStr = waitForValidResponse("What is the name of the project: ", false, 5, 15) as String

        return searchStr
    }

    // prints a menu, showing options for filtering projects
    fun showFilterProjectsMenu(): Int {
        var input: Int

        ConsoleColors.highlight("<b>What do you want to view?</b>")
        ConsoleColors.highlight(" <b>1.</b> All projects")
        ConsoleColors.highlight(" <b>2.</b> Active Projects")
        ConsoleColors.highlight(" <b>3.</b> Inactive projects")
        ConsoleColors.highlight(" <b>4.</b> Closed Projects")
        ConsoleColors.highlight(" <b>5.</b> Search for a specific project by name")
        ConsoleColors.highlight(" <b>0.</b> Cancel")

        input = waitForValidResponse("Select an option from above: ", true, 0, 5) as Int

        return input
    }


    // prints a list of projects
    fun listProjects(projects: List<ProjectModel>) {
        println("-------------------------------------------------")
        projects.forEach { p -> showProject(p)}
        println("-------------------------------------------------")
    }

    // prints a project name and its ID
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


    // generates a table of projects and their ID
    fun listProjectsAndIds(projects: List<ProjectModel>) {
        println("------------------------------------------------------")
        ConsoleColors.highlight("PROJECT".padEnd(30) +
                "IS ACTIVE".padEnd(15) +
                "ID")
        projects.forEach { p -> showProjectAndId(p)}
        println("------------------------------------------------------")
    }

    // prints a task desc and its ID
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

    // generates a table of tasks and their ID
    fun listTasksAndIds(tasks: List<TaskModel>) {
        println("------------------------------------------------------")
        ConsoleColors.highlight("TASK".padEnd(30) +
                "IS ACTIVE".padEnd(15) +
                "ID")
        tasks.forEach { t -> showTaskAndId(t) }
        println("------------------------------------------------------")
    }

    /*
        waits for a correct response given question, an expected data type and min/max
        where min/max either means between two numbers or the string length
     */
    fun waitForValidResponse(prompt: String, intValue: Boolean, min: Int, max: Int): Any {
        var value: String

        while (true) {
            ConsoleColors.prompt(prompt)
            value = readLine()!!

            if (value.isEmpty()) {
                ConsoleColors.warning("You must specify a number between $min and $max.")
            }
            else if (intValue) {
                var tempInt: Int?
                tempInt = value.toIntOrNull()

                if (tempInt == null) {
                    ConsoleColors.warning("You must specify a number between $min and $max.")
                } else if (tempInt < min || tempInt > max) {
                    ConsoleColors.warning("You must specify a number between $min and $max.")
                }
                else
                    return tempInt
            } else
                if (value.length < min) {
                    ConsoleColors.warning("You must specify a number between $min and $max.")
                } else if (value.length > max) {
                    ConsoleColors.warning("You must specify a number between $min and $max.")
                } else
                    return value
        }
    }

    // Takes in a question and waits for the user to respond to it
    fun confirmResponse(prompt: String): Boolean {
        val response: String

        ConsoleColors.prompt(prompt+" y/n: ")
        response = readLine()!!

        when (response.toUpperCase()) {
            "Y" -> return true
            "N" -> return false
            else -> {
                ConsoleColors.warning("This is an invalid response...")
                return confirmResponse(prompt)
            }
        }
    }

    // Walks the user through creating a new project
    fun addProjectData(project : ProjectModel) : Boolean {
        project.name = waitForValidResponse("Enter a project name: ", false, projectNameMinLength, projectNameMaxLength) as String
        project.description = waitForValidResponse("Enter a project description: ", false, projectDescriptionMinLength, projectDescriptionMaxLength) as String
        project.priority = waitForValidResponse("Give your project a priority between 1 (low) and 5 (high): ", true, 1, 5) as Int


/*      I started using the waitForValidResponse function to stop this code below from popping up everywhere
        while (tempPriority == null || tempPriority > 5 || tempPriority < 0) {
            print("Invalid input...</b> the priority must be a number between 1 (low) and 5 (high): ")
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
            ConsoleColors.prompt("Enter the ID of the task: ")
        }
        else
            ConsoleColors.prompt("Enter the ID of the project: ")

        strId = readLine()!!
        searchId = if (strId.toLongOrNull() != null && !strId.isEmpty())
            strId.toLong()
        else
            -9
        return searchId
    }
}