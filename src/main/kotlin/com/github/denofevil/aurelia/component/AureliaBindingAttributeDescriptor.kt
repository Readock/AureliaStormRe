package com.github.denofevil.aurelia.component

import com.intellij.psi.PsiElement
import com.intellij.xml.XmlAttributeDescriptor
import com.intellij.xml.impl.BasicXmlAttributeDescriptor

class AureliaBindingAttributeDescriptor(
    private val attributeName: String,
    private val baseDescriptor: XmlAttributeDescriptor,
) : BasicXmlAttributeDescriptor() {


    override fun getDeclaration(): PsiElement = baseDescriptor.declaration
    override fun getName() = attributeName
    override fun init(element: PsiElement?) {
        throw UnsupportedOperationException()
    }

    override fun getDefaultValue() = baseDescriptor.defaultValue
    override fun isEnumerated() = baseDescriptor.isEnumerated

    override fun getEnumeratedValues(): Array<String>? = baseDescriptor.enumeratedValues

    override fun isRequired() = baseDescriptor.isRequired
    override fun isFixed() = baseDescriptor.isFixed
    override fun hasIdType() = baseDescriptor.hasIdType()
    override fun hasIdRefType() = baseDescriptor.hasIdRefType()
}