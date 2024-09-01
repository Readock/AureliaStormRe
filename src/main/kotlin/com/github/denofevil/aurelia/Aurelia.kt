package com.github.denofevil.aurelia

import com.intellij.json.psi.JsonFile
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootModificationTracker
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.json.psi.JsonObject
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.PsiManager

/**
 * @author Dennis.Ushakov
 */
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

    fun present(project: Project) = CachedValuesManager.getManager(project).getCachedValue(project) {
        val aureliaLoaded = hasDependency(project)
        CachedValueProvider.Result
            .create(aureliaLoaded, VirtualFileManager.VFS_STRUCTURE_MODIFICATIONS, ProjectRootModificationTracker.getInstance(project))
    }!!

    private fun hasDependency(project: Project): Boolean {
        var hasDependency = false

        project.guessProjectDir()?.let {
            VfsUtilCore.iterateChildrenRecursively(it, null) { virtualFile ->
                if (!virtualFile.isDirectory && virtualFile.name == "package.json") {
                    val jsonFile = PsiManager.getInstance(project).findFile(virtualFile)
                    val jsonObject = (jsonFile as? JsonFile)?.topLevelValue as? JsonObject

                    val dependencies = jsonObject?.findProperty("dependencies")?.value as? JsonObject
                    val devDependencies = jsonObject?.findProperty("devDependencies")?.value as? JsonObject

                    if (hasAureliaDependency(dependencies) || hasAureliaDependency(devDependencies)) {
                        hasDependency = true
                        return@iterateChildrenRecursively false
                    }
                }

                true
            }
        }

        return hasDependency
    }

    private fun hasAureliaDependency(jsonObject: JsonObject?): Boolean {
        if (jsonObject == null) return false

        val aureliaDependency = jsonObject.findProperty("aurelia")
        val aureliaCliDependency = jsonObject.findProperty("aurelia-cli")

        return aureliaDependency != null || aureliaCliDependency != null
    }
}
