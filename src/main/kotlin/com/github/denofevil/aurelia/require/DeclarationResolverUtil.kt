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

        val importedFile = RequireImportUtil.resolveImportByName(element, name)
            .map { findClassByDecorator(it, name, decorator) }.firstOrNull()
        if (importedFile != null) {
            // if possible we take declarations from a <require from=""> tag
            return importedFile
        }
        // no matching require tag so we will search for a fitting ts file
        val tsFilesWithComponentName = FilenameIndex.getVirtualFilesByName(
            "${name}.ts", scope
        )
        return tsFilesWithComponentName.firstNotNullOfOrNull { findClassByDecorator(it.findPsiFile(project), name, decorator) }
    }

    fun findClassByDecorator(tsFile: PsiFile?, elementName: String, decoratorName: String): JSClass? {
        val jsClasses = PsiTreeUtil.findChildrenOfType(tsFile, JSClass::class.java) as Collection<JSClass>
        return jsClasses.firstOrNull { matchesWithDecorator(it, elementName, decoratorName) } // highest priority has the decorator
            ?: jsClasses.firstOrNull { matchesWithName(it, elementName) } // then class name
            ?: jsClasses.firstOrNull() // as fallback take first class in the file
    }

    private fun matchesWithDecorator(jsClass: JSClass, elementName: String, decoratorName: String): Boolean {
        return jsClass.attributeList?.decorators?.any {
            it.decoratorName == decoratorName && it.expression?.text?.contains(elementName) == true
        } ?: false
    }

    private fun matchesWithName(jsClass: JSClass, elementName: String): Boolean {
        return jsClass.name != null && (Aurelia.camelToKebabCase(jsClass.name!!)) == elementName
    }

}