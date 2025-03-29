package com.github.denofevil.aurelia.attribute

import com.github.denofevil.aurelia.Aurelia
import com.github.denofevil.aurelia.require.DeclarationResolverUtil
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlAttributeDescriptor
import com.intellij.xml.XmlAttributeDescriptorsProvider
import javax.swing.text.html.HTML

class AureliaAttributeDescriptorProvider : XmlAttributeDescriptorsProvider {
    private val htmlAttributes = HTML.getAllAttributeKeys().map { it.toString().lowercase() }

    override fun getAttributeDescriptors(element: XmlTag?) =
        element?.attributes?.mapNotNull { getAttributeDescriptor(it.value, element) }?.toTypedArray()
            ?: emptyArray<XmlAttributeDescriptor>()


    override fun getAttributeDescriptor(attributeName: String?, tag: XmlTag?): XmlAttributeDescriptor? {
        val attribute = tag?.getAttribute(attributeName)
        val isExcludedAttribute = htmlAttributes.stream().anyMatch { it.equals(attributeName) }
                || Aurelia.COMPONENT_ATTRIBUTES.contains(attributeName)
                || attributeName == "from"
        if (!isExcludedAttribute && tag != null && attribute != null) {
            val ref = DeclarationResolverUtil.resolveAttributeDeclaration(attribute)
            if (ref != null) {
                return AureliaCustomAttributeDescriptor(attribute.name, ref)
            }
        }
        return null
    }
}