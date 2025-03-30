package com.github.denofevil.aurelia.attribute

import com.github.denofevil.aurelia.Aurelia
import com.github.denofevil.aurelia.component.AureliaBindingAttributeDescriptor
import com.github.denofevil.aurelia.require.DeclarationResolverUtil
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlAttributeDescriptor
import com.intellij.xml.XmlAttributeDescriptorsProvider
import com.intellij.xml.XmlElementDescriptor

/**
 * @author Dennis.Ushakov
 * @author felix
 */
class AureliaAttributeDesciptorsProvider : XmlAttributeDescriptorsProvider {
    override fun getAttributeDescriptors(xmlTag: XmlTag): Array<XmlAttributeDescriptor> {
        val declaration = DeclarationResolverUtil.resolveComponentDeclaration(xmlTag);
        val declarationMembers = arrayListOf<XmlAttributeDescriptor>()
        if (declaration != null) {
            declarationMembers.addAll(DeclarationResolverUtil.resolveBindableAttributes(declaration).map {
                AureliaAttributeDescriptor(Aurelia.camelToKebabCase("${it.memberName}.bind"))
            })
        }
        declarationMembers.addAll(Aurelia.AUTOCOMPLETE_ATTRIBUTES.map { AureliaAttributeDescriptor(it) })
        return declarationMembers.toTypedArray()
    }

    override fun getAttributeDescriptor(name: String, xmlTag: XmlTag): XmlAttributeDescriptor? {
        if (Aurelia.WHITE_LIST_ATTRIBUTES.any { AttributeUtil.withoutInjectable(it) == AttributeUtil.withoutInjectable(name) }) {
            return AureliaAttributeDescriptor(name)
        }
        // attributes like name, value etc. should also have the same descriptor when using them with .bind
        return getHtmlAttributeWithoutInjectableDescriptor(name, xmlTag)
    }

    private fun getHtmlAttributeWithoutInjectableDescriptor(name: String, xmlTag: XmlTag): XmlAttributeDescriptor? {
        val tagDescriptor: XmlElementDescriptor? = xmlTag.descriptor
        if (name != AttributeUtil.withoutInjectable(name)) {
            // adds support for events like "click.bind" and uses the "onclick" descriptor for that
            // make sure to add "on" only when there also is a ".bind"
            val descriptor = tagDescriptor?.getAttributeDescriptor(AttributeUtil.withoutInjectable(name), xmlTag)
                ?: tagDescriptor?.getAttributeDescriptor("on${AttributeUtil.withoutInjectable(name).substringAfter("on")}", xmlTag)
            descriptor?.let { return AureliaBindingAttributeDescriptor(name, descriptor) }
        }
        return null
    }
}
