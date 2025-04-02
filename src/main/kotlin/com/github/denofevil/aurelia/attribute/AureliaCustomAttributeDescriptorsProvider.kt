package com.github.denofevil.aurelia.attribute

import com.github.denofevil.aurelia.index.AureliaIndexUtil
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlAttributeDescriptor
import com.intellij.xml.XmlAttributeDescriptorsProvider

/**
 * Provides descriptors with custom attributes
 */
class AureliaCustomAttributeDescriptorsProvider : XmlAttributeDescriptorsProvider {

    override fun getAttributeDescriptors(element: XmlTag?): Array<XmlAttributeDescriptor> {
        element ?: return emptyArray()
        return customAttributes(element).toTypedArray()
    }

    override fun getAttributeDescriptor(attributeName: String?, tag: XmlTag?): XmlAttributeDescriptor? {
        if (attributeName == null || tag == null) return null
        AureliaIndexUtil.resolveCustomAttributeClasses(attributeName, tag.project).firstOrNull()?.let {
            return AureliaCustomAttributeDescriptor(attributeName, it, true)
        }
        return null
    }

    private fun customAttributes(xmlTag: XmlTag): List<XmlAttributeDescriptor> {
        return AureliaIndexUtil.getAllCustomAttributeNames(xmlTag.project).map {
            val decl = AureliaIndexUtil.resolveCustomAttributeClasses(it, xmlTag.project).firstOrNull()
            return@map AureliaCustomAttributeDescriptor(it, decl, true)
        }
    }
}