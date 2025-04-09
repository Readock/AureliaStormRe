package com.github.denofevil.aurelia

import com.intellij.javascript.nodejs.PackageJsonData
import com.intellij.javascript.nodejs.packageJson.PackageJsonFileManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.tylerthrailkill.helpers.prettyprint.get

object Aurelia {
    val ICON = IconLoader.getIcon("/icons/aurelia-icon.svg", Aurelia::class.java)

    val INJECTABLE = arrayOf("bind", "one-way", "two-way", "one-time", "delegate", "trigger", "call", "from-view")
    const val REPEAT_FOR = "repeat.for"
    const val VIRTUAL_REPEAT_FOR = "virtual-repeat.for"
    const val AURELIA_APP = "aurelia-app"
    const val CASE = "case"
    const val REF = "ref"
    const val ELSE = "else"
    const val PROMISE = "promise"
    const val THEN = "then"
    val CUSTOM_ELEMENTS = arrayOf("let")

    fun present(project: Project): Boolean {
        return hasDependency(project)
    }

    private fun hasDependency(project: Project): Boolean {
        val manager = PackageJsonFileManager.getInstance(project)

        return manager.validPackageJsonFiles.any { virtualFile ->
            val data = PackageJsonData.getOrCreate(virtualFile)
            val dependencies = data.allDependencies;

            dependencies["aurelia"] != null || dependencies["aurelia-cli"] != null
        }
    }

}
