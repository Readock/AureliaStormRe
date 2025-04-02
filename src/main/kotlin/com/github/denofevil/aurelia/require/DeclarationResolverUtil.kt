package com.github.denofevil.aurelia.require

import com.github.denofevil.aurelia.config.AureliaSettings
import com.github.denofevil.aurelia.index.AureliaIndexUtil
import com.intellij.lang.javascript.psi.JSElement
import com.intellij.lang.javascript.psi.JSRecordType.PropertySignature
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeListOwner
import com.intellij.lang.javascript.psi.ecmal4.JSClass
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag

object DeclarationResolverUtil {

    fun resolveAttributeDeclaration(attribute: XmlAttribute): JSClass? {
        return CachedValuesManager.getCachedValue(attribute) {
            val candidates = AureliaIndexUtil.resolveCustomAttributeClasses(attribute.name, attribute.project)
            val resolvedClass = resolveClassDeclaration(attribute, attribute.name, candidates)
            CachedValueProvider.Result.create(
                resolvedClass,
                attribute.containingFile,
                PsiModificationTracker.MODIFICATION_COUNT
            )
        }
    }

    fun resolveComponentDeclaration(tag: XmlTag): JSClass? {
        return resolveComponentDeclaration(tag, tag.name)
    }

    fun resolveComponentDeclaration(element: PsiElement, name: String): JSClass? {
        return CachedValuesManager.getCachedValue(element) {
            val candidates = AureliaIndexUtil.resolveCustomElementClasses(name, element.project)
            val resolvedClass = resolveClassDeclaration(element, name, candidates)
            CachedValueProvider.Result.create(
                resolvedClass,
                element.containingFile,
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

    private fun resolveClassDeclaration(element: PsiElement, name: String, annotatedClassCandidates: List<JSClass>): JSClass? {
        val importedClasses = RequireImportUtil.resolveImportByName(element, name).map { findClassesOf(it) }.flatten()
        importedClasses.firstOrNull { annotatedClassCandidates.contains(it) }?.let { return it }

        val module = ModuleUtil.findModuleForPsiElement(element)
        annotatedClassCandidates.firstOrNull { ModuleUtil.findModuleForPsiElement(it) == module }?.let { return it }
        return annotatedClassCandidates.firstOrNull()
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

    private fun findClassesOf(psiClass: PsiFile): Collection<JSClass> {
        return PsiTreeUtil.findChildrenOfType(psiClass, JSClass::class.java)
    }

    private fun hasBindableAnnotation(member: JSElement): Boolean {
        if (member !is JSAttributeListOwner) return false
        return member.attributeList?.decorators?.any { it.decoratorName == "bindable" } ?: false
    }
}