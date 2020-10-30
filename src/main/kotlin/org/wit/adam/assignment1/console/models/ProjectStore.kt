package org.wit.adam.assignment1.console.models

interface ProjectStore {
    fun getAll(): List<ProjectModel>
    fun getOne(id: Long): ProjectModel?
    fun create(projectModel: ProjectModel)
    fun update(projectModel: ProjectModel)
}