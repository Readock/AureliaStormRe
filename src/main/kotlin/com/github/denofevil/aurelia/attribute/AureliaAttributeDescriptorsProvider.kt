package com.github.denofevil.aurelia.attribute

import com.github.denofevil.aurelia.Aurelia
import com.github.denofevil.aurelia.component.AureliaBindingAttributeDescriptor
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlAttributeDescriptor
import com.intellij.xml.XmlAttributeDescriptorsProvider
import com.intellij.xml.XmlElementDescriptor

/**
 * @author Dennis.Ushakov
 * @author felix
 */
class AureliaAttributeDescriptorsProvider : XmlAttributeDescriptorsProvider {
    
    override fun getAttributeDescriptors(xmlTag: XmlTag): Array<XmlAttributeDescriptor> {
        return emptyArray()
    }

    override fun getAttributeDescriptor(name: String, xmlTag: XmlTag): XmlAttributeDescriptor? {
        return getHtmlAttributeWithoutInjectableDescriptor(name, xmlTag)
            ?: Aurelia.WHITE_LIST_ATTRIBUTES.firstOrNull { it == name }?.let { AureliaAttributeDescriptor(name) }
    }

    private fun getHtmlAttributeWithoutInjectableDescriptor(name: String, xmlTag: XmlTag): XmlAttributeDescriptor? {
        val tagDescriptor: XmlElementDescriptor? = xmlTag.descriptor
        if (name != AttributeUtil.withoutInjectable(name)) {
            // adds support for events like "click.bind" and uses the "onclick" descriptor for that
            // make sure to add "on" only when there also is a ".bind"
            val descriptor = tagDescriptor?.getAttributeDescriptor(AttributeUtil.withoutInjectable(name), xmlTag)
                ?: tagDescriptor?.getAttributeDescriptor("on${AttributeUtil.withoutInjectable(name)}", xmlTag)
            descriptor?.let { return AureliaBindingAttributeDescriptor(name, descriptor) }
        }
        return null
    }
}
