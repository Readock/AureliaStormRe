package com.github.denofevil.aurelia.component

import com.github.denofevil.aurelia.Aurelia
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

class AureliaComponentElementDescriptor(private val tag: XmlTag) : BaseXmlElementDescriptorImpl(), XmlCustomElementDescriptor {
    private var myElementDecl: XmlElementDecl? = null
    private var declaration: PsiElement?
    private var attributesProvider = AureliaAttributeDescriptorsProvider()
    private var customattributesProvider = AureliaCustomAttributeDescriptorsProvider()

    init {
        this.declaration = DeclarationResolverUtil.resolveComponentDeclaration(tag)
    }

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
        if (attributeName != null && context != null) {
            return customattributesProvider.getAttributeDescriptor(attributeName, context)
                ?: attributesProvider.getAttributeDescriptor(attributeName, context)
                ?: super.getAttributeDescriptor(attributeName, context)
        }
        return super.getAttributeDescriptor(attributeName, context)
    }

    public override fun collectAttributeDescriptors(tag: XmlTag?): Array<XmlAttributeDescriptor> {
        return collectBindableAttributeDescriptors(this.declaration as JSClass?)
    }

    private fun collectBindableAttributeDescriptors(
        jsClass: JSClass?
    ): Array<XmlAttributeDescriptor> {
        val members = ArrayList<XmlAttributeDescriptor>()
        members.addAll(Aurelia.COMPONENT_ATTRIBUTES.map { AureliaAttributeDescriptor(it) })
        DeclarationResolverUtil.resolveBindableAttributes(jsClass).forEach { attr ->
            val descriptor = TypeScriptJSXTagUtil.createAttributeDescriptor(attr, true)
            members.add(AureliaBindingAttributeDescriptor(Aurelia.camelToKebabCase(descriptor.name), descriptor))

            // To support bindings of properties
            Aurelia.INJECTABLE.forEach { suffix ->
                members.add(AureliaBindingAttributeDescriptor("${Aurelia.camelToKebabCase(descriptor.name)}.$suffix", descriptor))
            }
        }
        return members.toTypedArray()
    }

    override fun collectAttributeDescriptorsMap(tag: XmlTag?): HashMap<String, XmlAttributeDescriptor> {
        val attributeMap = HashMap<String, XmlAttributeDescriptor>()
        val descriptors = collectAttributeDescriptors(tag)

        for (descriptor in descriptors) {
            attributeMap[descriptor.name] = descriptor
        }
        return attributeMap
    }

    override fun getQualifiedName(): String {
        return tag.name
    }

    override fun isCustomElement() = true

    override fun getContentType() = CONTENT_TYPE_ANY

    override fun doCollectXmlDescriptors(tag: XmlTag?) = emptyArray<XmlElementDescriptor>()

    override fun collectElementDescriptorsMap(tag: XmlTag?) = hashMapOf<String, XmlElementDescriptor>()
}
