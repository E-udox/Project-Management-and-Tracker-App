package org.wit.adam.assignment1.console.main
import mu.KotlinLogging
import org.wit.adam.assignment1.console.controllers.ProjectController


val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    logger.info { "Launching Project Management and Tracker App (Console Version)" }
    ProjectController().start()
}