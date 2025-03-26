package com.github.denofevil.aurelia

import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlAttributeDescriptor
import com.intellij.xml.XmlAttributeDescriptorsProvider

/**
 * @author Dennis.Ushakov
 */
class AttributesProvider : XmlAttributeDescriptorsProvider {
    override fun getAttributeDescriptors(xmlTag: XmlTag): Array<XmlAttributeDescriptor> = arrayOf(
        AureliaAttributeDescriptor(Aurelia.REPEAT_FOR),
        AureliaAttributeDescriptor(Aurelia.VIRTUAL_REPEAT_FOR),
        AureliaAttributeDescriptor(Aurelia.AURELIA_APP)
    )

    override fun getAttributeDescriptor(name: String, xmlTag: XmlTag): XmlAttributeDescriptor? {
        for (attr in Aurelia.INJECTABLE) {
            if (name.endsWith(".$attr")) {
                val attrName = name.substring(0, name.length - attr.length - 1)
                if ("if" == attrName || "show" == attrName || "switch" == attrName || Aurelia.ELSE == attrName) {
                    return AureliaAttributeDescriptor(name)
                }
                val descriptor = xmlTag.descriptor
                if (descriptor != null) {
                    val attributeDescriptor = descriptor.getAttributeDescriptor(attrName, xmlTag)
                    return attributeDescriptor ?: descriptor.getAttributeDescriptor("on$attrName", xmlTag)
                }
            }
        }
        return if (containsAureliaAttribute(name)) {
            AureliaAttributeDescriptor(name)
        } else null
    }

    private fun containsAureliaAttribute(name: String): Boolean {
        return Aurelia.REPEAT_FOR == name
                || Aurelia.VIRTUAL_REPEAT_FOR == name
                || Aurelia.AURELIA_APP == name
                || Aurelia.CASE == name
                || Aurelia.REF == name
                || Aurelia.PROMISE == name
                || Aurelia.THEN == name
    }
}
