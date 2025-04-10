package com.github.denofevil.aurelia

import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.roots.ProjectRootModificationTracker
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager

/**
 * Utility class and holder of aurelia syntax strings
 */
object Aurelia {
    val ICON = IconLoader.getIcon("/icons/aurelia-icon.svg", Aurelia::class.java)

    private val PROPERTY_BINDINGS = listOf("bind", "one-way", "two-way", "one-time", "from-view", "to-view", "dispatch")
    val INJECTABLE = listOf("delegate", "trigger", "call", "for", "ref") + PROPERTY_BINDINGS
    val REPEAT_FOR = listOf("repeat.for", "virtual-repeat.for", "reorderable-repeat.for")
    const val REPEAT_FOR_OF_KEYWORD = " of "


    object CustomAttribute {
        const val ANNOTATION = "customAttribute"
        const val CLASS_SUFFIX = "CustomAttribute"
    }

    object CustomElement {
        const val ANNOTATION = "customElement"
        const val CLASS_SUFFIX = "CustomElement"
    }

    val IMPORT_ELEMENTS = listOf("require", "import")
    const val IMPORT_ELEMENT_ATTRIBUTE = "from"
    val CUSTOM_ELEMENTS = listOf("let", "template") + IMPORT_ELEMENTS
    val WHITE_LIST_ATTRIBUTES = listOf(
        "name", "innerhtml", "containerless", "model", "element"
    )
    val ATTRIBUTES_WITHOUT_VALUE = listOf("else", "disabled", "containerless")
    val CUSTOM_ELEMENT_ATTRIBUTES = listOf("element.ref", "controller.ref", "view.ref", "view-model.ref", "component.ref")

    fun isPresentFor(project: Project): Boolean = CachedValuesManager.getManager(project).getCachedValue(project) {
        val aureliaRootFolders = getAureliaRootFolders(project)
        CachedValueProvider.Result
            .create(
                aureliaRootFolders.isNotEmpty(),
                VirtualFileManager.VFS_STRUCTURE_MODIFICATIONS,
                ProjectRootModificationTracker.getInstance(project)
            )
    }

    /**
     * Weather this element is child of any aurelia root folders
     */
    fun isFrameworkCandidate(element: PsiElement): Boolean {
        return isPresentFor(element.containingFile)
    }

    /**
     * Weather this element is child of any aurelia root folders
     */
    fun isPresentFor(element: PsiFile?): Boolean {
        if (element == null) return false
        return CachedValuesManager.getManager(element.project).getCachedValue(element) {
            var isPresent = false
            val project = element.project
            val aureliaRootFolders = getAureliaRootFolders(project)

            if (aureliaRootFolders.isNotEmpty()) {
                val fileVirtualFile: VirtualFile? = element.virtualFile
                isPresent = fileVirtualFile != null && aureliaRootFolders.stream().anyMatch { isChildOf(fileVirtualFile, it) }
            }
            CachedValueProvider.Result.create(isPresent, VirtualFileManager.VFS_STRUCTURE_MODIFICATIONS)
        }
    }

    fun camelToKebabCase(camel: String): String {
        return camel.replace(Regex("([a-z])([A-Z])"), "$1-$2").lowercase()
    }

    private fun isChildOf(source: VirtualFile, target: VirtualFile): Boolean {
        var currentFile = source.parent
        while (currentFile != null) {
            if (currentFile == target) {
                return true
            }
            currentFile = currentFile.parent
        }
        return false
    }

    private fun getAureliaRootFolders(project: Project): ArrayList<VirtualFile> {
        return CachedValuesManager.getManager(project).getCachedValue(project) {
            val aureliaRootFolder = findAureliaRootFolders(project)
            CachedValueProvider.Result.create(aureliaRootFolder, VirtualFileManager.VFS_STRUCTURE_MODIFICATIONS)
        }
    }

    private fun findAureliaRootFolders(project: Project): ArrayList<VirtualFile> {
        val aureliaRoots: ArrayList<VirtualFile> = arrayListOf()
        for (moduleRoot in getModuleRootDirs(project)) {
            moduleRoot.let { it ->
                VfsUtilCore.iterateChildrenRecursively(it, null) { virtualFile ->
                    val isPackageJson = !virtualFile.isDirectory && virtualFile.name == "package.json"
                    if (isPackageJson && hasPackageJsonAureliaDependencies(project, virtualFile)) {
                        aureliaRoots.add(virtualFile.parent)
                    }
                    true
                }
            }
        }
        return aureliaRoots
    }

    private fun hasPackageJsonAureliaDependencies(project: Project, virtualFile: VirtualFile): Boolean {
        val jsonFile = PsiManager.getInstance(project).findFile(virtualFile)
        val jsonObject = (jsonFile as? JsonFile)?.topLevelValue as? JsonObject

        val dependencies = jsonObject?.findProperty("dependencies")?.value as? JsonObject
        val devDependencies = jsonObject?.findProperty("devDependencies")?.value as? JsonObject

        return hasAureliaDependency(dependencies) || hasAureliaDependency(devDependencies)
    }

    private fun getModuleRootDirs(project: Project): List<VirtualFile> {
        val modules = ModuleManager.getInstance(project).modules
        return modules.mapNotNull { module ->
            module.rootManager.contentRoots.firstOrNull()
        }
    }

    private fun hasAureliaDependency(jsonObject: JsonObject?): Boolean {
        if (jsonObject == null) return false

        val aureliaDependency = jsonObject.findProperty("aurelia")
        val aureliaFrameworkDependency = jsonObject.findProperty("aurelia-framework")
        val aureliaCliDependency = jsonObject.findProperty("aurelia-cli")

        return aureliaDependency != null || aureliaFrameworkDependency != null || aureliaCliDependency != null
    }
}
