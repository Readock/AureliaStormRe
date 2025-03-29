package com.github.denofevil.aurelia.require

import com.github.denofevil.aurelia.Aurelia
import com.intellij.lang.javascript.psi.ecmal4.JSClass
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlElement
import com.intellij.psi.xml.XmlTag
import kotlin.jvm.optionals.getOrElse

object DeclarationResolverUtil {

    fun resolveAttributeDeclaration(attribute: XmlAttribute): PsiElement? {
        return resolveClassDeclaration(attribute, attribute.name, Aurelia.CUSTOM_ATTRIBUTE_DECORATOR)
    }

    fun resolveComponentDeclaration(tag: XmlTag): PsiElement? {
        return resolveClassDeclaration(tag, tag.name, Aurelia.CUSTOM_ELEMENT_DECORATOR)
    }

    private fun resolveClassDeclaration(element: XmlElement, name: String, decorator: String): PsiElement? {
        val project = element.project
        val scope = GlobalSearchScope.allScope(project)

        val jsImportFiles = RequireImportUtil.resolveImportByName(element, name)
        if (jsImportFiles.isNotEmpty()) {
            // if possible we take declarations from a <require from=""> tag
            return jsImportFiles.map { findClassByDecorator(it, name, decorator) }.firstOrNull();
        }
        // no matching require tag so we will search for a fitting ts file
        val tsFilesWithComponentName = FilenameIndex.getVirtualFilesByName(
            "${name}.ts", scope
        )
        if (!tsFilesWithComponentName.isEmpty()) {
            val files = tsFilesWithComponentName.stream().map { f -> f.findPsiFile(project) }
                .map { f -> findClassByDecorator(f, name, decorator) }.toList()
                .filterNotNull().toList()
            return files.firstOrNull()
        }
        return null
    }

    private fun findClassByDecorator(tsFile: PsiFile?, elementName: String, decoratorName: String): PsiElement? {
        val jsClasses = PsiTreeUtil.findChildrenOfType(tsFile, JSClass::class.java) as Collection<JSClass>
        return jsClasses.stream().filter { matchesWithCustomComponent(it, elementName, decoratorName) }.findFirst()
            .getOrElse { jsClasses.firstOrNull() }
    }

    private fun matchesWithCustomComponent(jsClass: JSClass, elementName: String, decoratorName: String): Boolean {
        val matchingClassName = jsClass.name != null && (Aurelia.camelToKebabCase(jsClass.name!!)) == elementName
        if (matchingClassName) {
            return true;
        }
        return jsClass.attributeList?.decorators?.any {
            it.decoratorName == decoratorName && it.expression?.text?.contains(elementName) == true
        } ?: false
    }

}