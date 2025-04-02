package com.github.denofevil.aurelia.index

import com.github.denofevil.aurelia.Aurelia
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.lang.javascript.psi.ecmal4.JSClass
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.asSafely
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.ID

object AureliaIndexUtil {

    fun resolveCustomElementClasses(componentName: String, project: Project): List<JSClass> {
        return resolveAnnotatedClasses(componentName, project, AureliaCustomElementIndexKey) {
            resolveClassCustomElementNameByAnnotation(it)
        }
    }

    fun resolveCustomAttributeClasses(attributeName: String, project: Project): List<JSClass> {
        return resolveAnnotatedClasses(attributeName, project, AureliaCustomAttributeIndexKey) {
            resolveClassCustomAttributeNameByAnnotation(it)
        }
    }

    fun getAllCustomAttributeNames(project: Project): Collection<String> {
        return FileBasedIndex.getInstance().getAllKeys(AureliaCustomAttributeIndexKey, project)
    }

    private fun resolveAnnotatedClasses(
        searchName: String,
        project: Project,
        index: ID<String, Void>,
        resolver: (JSClass) -> String?
    ): List<JSClass> {
        val result = mutableListOf<JSClass>()
        val scope = GlobalSearchScope.allScope(project)

        FileBasedIndex.getInstance().getContainingFiles(index, searchName.lowercase(), scope)
            .forEach { virtualFile ->
                val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
                psiFile?.accept(object : PsiRecursiveElementVisitor() {
                    override fun visitElement(jsClass: PsiElement) {
                        super.visitElement(jsClass)
                        if (jsClass is JSClass) {
                            if (resolver(jsClass)?.lowercase() == searchName.lowercase()) {
                                result.add(jsClass)
                            }
                        }
                    }
                })
            }
        return result
    }

    fun resolveClassCustomElementNameByAnnotation(jsClass: JSClass): String? {
        val annotation = jsClass.attributeList?.decorators?.find { it.decoratorName == Aurelia.CUSTOM_ELEMENT_DECORATOR }
        val value = annotation?.expression?.asSafely<JSCallExpression>()
            ?.argumentList?.arguments?.firstOrNull() as? JSLiteralExpression
        return value?.stringValue
    }

    fun resolveClassCustomAttributeNameByAnnotation(jsClass: JSClass): String? {
        val annotation = jsClass.attributeList?.decorators?.find { it.decoratorName == Aurelia.CUSTOM_ATTRIBUTE_DECORATOR }
        val value = annotation?.expression?.asSafely<JSCallExpression>()
            ?.argumentList?.arguments?.firstOrNull() as? JSLiteralExpression
        return value?.stringValue
    }
}