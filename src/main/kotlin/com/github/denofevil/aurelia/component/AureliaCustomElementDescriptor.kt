package com.github.denofevil.aurelia.component

import com.github.denofevil.aurelia.Aurelia
import com.github.denofevil.aurelia.attribute.AttributeUtil
import com.github.denofevil.aurelia.attribute.AureliaAttributeDescriptor
import com.github.denofevil.aurelia.attribute.AureliaAttributeDescriptorsProvider
import com.github.denofevil.aurelia.attribute.AureliaCustomAttributeDescriptorsProvider
import com.github.denofevil.aurelia.require.DeclarationResolverUtil
import com.intellij.lang.javascript.frameworks.jsx.tsx.TypeScriptJSXTagUtil
import com.intellij.lang.javascript.psi.ecmal4.JSClass
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlElementDecl
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlAttributeDescriptor
import com.intellij.xml.XmlCustomElementDescriptor
import com.intellij.xml.XmlElementDescriptor
import com.intellij.xml.XmlNSDescriptor
import com.intellij.xml.impl.dtd.BaseXmlElementDescriptorImpl
import com.intellij.xml.util.XmlUtil

/**
 * Describes custom elements and provides correct declaration of the component
 * - Make sure to call all AttributeDescriptors in this::getAttributeDescriptor
 */
class AureliaCustomElementDescriptor(private val tag: XmlTag) : BaseXmlElementDescriptorImpl(), XmlCustomElementDescriptor {
    private var myElementDecl: XmlElementDecl? = null
    private var declaration: JSClass? = DeclarationResolverUtil.resolveCustomElementDeclaration(tag)
    private var attributeDescriptorsProvider = AureliaAttributeDescriptorsProvider()
    private var customAttributeDescriptorsProvider = AureliaCustomAttributeDescriptorsProvider()

    override fun getDeclaration(): PsiElement? {
        return this.declaration
    }

    override fun getName(context: PsiElement?): String {
        return this.name
    }

    override fun getName(): String {
        return tag.name
    }

    override fun init(element: PsiElement) {
        this.myElementDecl = element as XmlElementDecl
    }

    override fun getDefaultName(): String {
        return tag.name
    }

    override fun getNSDescriptor(): XmlNSDescriptor? {
        return getNsDescriptorFrom(this.myElementDecl)
    }

    private fun getNsDescriptorFrom(elementDecl: PsiElement?): XmlNSDescriptor? {
        val file = XmlUtil.getContainingFile(elementDecl)
        if (file == null) {
            return null
        } else {
            val document = checkNotNull(file.document)

            val descriptor = document.metaData as XmlNSDescriptor?
            return descriptor ?: document.getDefaultNSDescriptor("", false)
        }
    }

    override fun getAttributeDescriptor(attributeName: String?, context: XmlTag?): XmlAttributeDescriptor? {
        if (attributeName == null || context == null) return null

        // resolve descriptor for binding properties
        DeclarationResolverUtil.resolveBindableAttributes(this.declaration).firstOrNull {
            Aurelia.camelToKebabCase(it.memberName).lowercase() == AttributeUtil.withoutInjectable(attributeName).lowercase()
        }?.let {
            val descriptor = TypeScriptJSXTagUtil.createAttributeDescriptor(it, true)
            return AureliaBindingAttributeDescriptor(attributeName, descriptor)
        }
        // try to resolve using other descriptors
        customAttributeDescriptorsProvider.getAttributeDescriptor(attributeName, context)?.let { return it }
        attributeDescriptorsProvider.getAttributeDescriptor(attributeName, context)?.let { return it }

        // try to resolve checking additional attributes
        Aurelia.COMPONENT_ATTRIBUTES.firstOrNull { it == attributeName }?.let { return AureliaAttributeDescriptor(attributeName) }
        return null
    }

    public override fun collectAttributeDescriptors(tag: XmlTag?): Array<XmlAttributeDescriptor> {
        return emptyArray() // handled by other descriptors
    }

    override fun collectAttributeDescriptorsMap(tag: XmlTag?): HashMap<String, XmlAttributeDescriptor> {
        return HashMap() // handled by other descriptors
    }

    override fun getQualifiedName(): String {
        return tag.name
    }

    override fun isCustomElement() = true

    override fun getContentType() = CONTENT_TYPE_ANY

    override fun doCollectXmlDescriptors(tag: XmlTag?) = emptyArray<XmlElementDescriptor>()

    override fun collectElementDescriptorsMap(tag: XmlTag?) = hashMapOf<String, XmlElementDescriptor>()
}
