package com.github.denofevil.aurelia.attribute

import com.github.denofevil.aurelia.Aurelia
import com.github.denofevil.aurelia.config.AureliaSettings
import com.github.denofevil.aurelia.require.DeclarationResolverUtil
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlAttributeDescriptor
import com.intellij.xml.XmlAttributeDescriptorsProvider

class AureliaCustomAttributeDescriptorsProvider : XmlAttributeDescriptorsProvider {

    override fun getAttributeDescriptors(element: XmlTag?): Array<XmlAttributeDescriptor> {
        element ?: return emptyArray()
        val existing = element.attributes.mapNotNull { getAttributeDescriptor(it.value, element) }.toTypedArray()
        return existing
    }

    override fun getAttributeDescriptor(attributeName: String?, tag: XmlTag?): XmlAttributeDescriptor? {
        if (!AureliaSettings.getInstance().isCustomAttributesEnabled) return null

        val attribute = tag?.getAttribute(attributeName) ?: return null
        if (Aurelia.isFrameworkCandidate(attribute)) {
            val ref = DeclarationResolverUtil.resolveAttributeDeclaration(attribute)
            if (ref != null) {
                return AureliaCustomAttributeDescriptor(attribute.name, ref)
            }
        }
        return null
    }
}