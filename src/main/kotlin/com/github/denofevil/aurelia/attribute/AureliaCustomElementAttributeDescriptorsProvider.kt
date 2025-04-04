package com.github.denofevil.aurelia.attribute

import com.github.denofevil.aurelia.Aurelia
import com.github.denofevil.aurelia.component.AureliaBindingAttributeDescriptor
import com.github.denofevil.aurelia.require.DeclarationResolverUtil
import com.intellij.lang.javascript.frameworks.jsx.tsx.TypeScriptJSXTagUtil
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlAttributeDescriptor
import com.intellij.xml.XmlAttributeDescriptorsProvider

/**
 * Provides descriptors for custom elements (only used for suggestions with ctrl + space)
 */
class AureliaCustomElementAttributeDescriptorsProvider : XmlAttributeDescriptorsProvider {

    override fun getAttributeDescriptors(xmlTag: XmlTag): Array<XmlAttributeDescriptor> {
        if (!Aurelia.isFrameworkCandidate(xmlTag)) return emptyArray()
        return (declarationPropertyBindings(xmlTag)).toTypedArray()
    }

    override fun getAttributeDescriptor(name: String, xmlTag: XmlTag): XmlAttributeDescriptor? {
        return null // handled by custom element descriptor
    }

    private fun declarationPropertyBindings(xmlTag: XmlTag): List<AureliaAttributeDescriptor> {
        val declaration = DeclarationResolverUtil.resolveCustomElementDeclaration(xmlTag)
        if (declaration != null) {
            return DeclarationResolverUtil.resolveBindableAttributesOnlyWithAnnotation(declaration).map {
                val descriptor = TypeScriptJSXTagUtil.createAttributeDescriptor(it, true)
                AureliaBindingAttributeDescriptor(Aurelia.camelToKebabCase("${it.memberName}.bind"), descriptor)
            }
        }
        return emptyList()
    }
}
