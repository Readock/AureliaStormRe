package com.github.denofevil.aurelia.require

import com.github.denofevil.aurelia.Aurelia
import com.github.denofevil.aurelia.attribute.AttributeUtil
import com.github.denofevil.aurelia.config.AureliaSettings
import com.intellij.lang.javascript.psi.JSElement
import com.intellij.lang.javascript.psi.JSRecordType.PropertySignature
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeListOwner
import com.intellij.lang.javascript.psi.ecmal4.JSClass
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlElement
import com.intellij.psi.xml.XmlTag

object DeclarationResolverUtil {

    fun resolveAttributeDeclaration(attribute: XmlAttribute): JSClass? {
        return CachedValuesManager.getCachedValue(attribute) {
            val resolvedClass =
                resolveClassDeclaration(attribute, AttributeUtil.withoutInjectable(attribute.name), Aurelia.CUSTOM_ATTRIBUTE_DECORATOR)
            CachedValueProvider.Result.create(
                resolvedClass,
                attribute.containingFile,
                PsiModificationTracker.MODIFICATION_COUNT
            )
        }
    }

    fun resolveComponentDeclaration(tag: XmlTag): JSClass? {
        return CachedValuesManager.getCachedValue(tag) {
            val resolvedClass = resolveClassDeclaration(tag, tag.name, Aurelia.CUSTOM_ELEMENT_DECORATOR)
            CachedValueProvider.Result.create(
                resolvedClass,
                tag.containingFile,
                PsiModificationTracker.MODIFICATION_COUNT
            )
        }
    }

    fun resolveBindableAttributes(jsClass: JSClass?): List<PropertySignature> {
        jsClass ?: return emptyList()
        return CachedValuesManager.getCachedValue(jsClass) {
            val resolvedAttributes = resolveBindableAttributesImpl(jsClass)
            CachedValueProvider.Result.create(
                resolvedAttributes,
                jsClass.containingFile,
                PsiModificationTracker.MODIFICATION_COUNT
            )
        }
    }

    private fun resolveBindableAttributesImpl(jsClass: JSClass): List<PropertySignature> {
        val members = arrayListOf<PropertySignature>()
        for (jsMember in jsClass.members) {
            if (AureliaSettings.getInstance().checkPropertyBindableAnnotation && !hasBindableAnnotation(jsMember)) {
                continue
            }
            if (jsMember is PropertySignature) {
                members.add(jsMember)
            }
        }
        return members
    }

    private fun resolveClassDeclaration(element: XmlElement, name: String, decorator: String): JSClass? {
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

    private fun findClassByDecorator(tsFile: PsiFile?, elementName: String, decoratorName: String): JSClass? {
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

    private fun hasBindableAnnotation(member: JSElement): Boolean {
        if (member !is JSAttributeListOwner) return false
        return member.attributeList?.decorators?.any { it.decoratorName == "bindable" } ?: false
    }
}