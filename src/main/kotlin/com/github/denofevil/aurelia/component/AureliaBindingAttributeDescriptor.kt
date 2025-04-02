package com.github.denofevil.aurelia.component

import com.github.denofevil.aurelia.attribute.AureliaAttributeDescriptor
import com.intellij.psi.PsiElement
import com.intellij.xml.XmlAttributeDescriptor

class AureliaBindingAttributeDescriptor(
    private var attributeName: String,
    private val baseDescriptor: XmlAttributeDescriptor,
) : AureliaAttributeDescriptor(attributeName, false) {


    override fun getDeclaration(): PsiElement? = baseDescriptor.declaration

//    override fun getDefaultValue() = baseDescriptor.defaultValue
//    override fun isEnumerated() = baseDescriptor.isEnumerated
//
//    override fun getEnumeratedValues(): Array<String>? = baseDescriptor.enumeratedValues
//
//    override fun isRequired() = baseDescriptor.isRequired
//    override fun isFixed() = baseDescriptor.isFixed
//    override fun hasIdType() = baseDescriptor.hasIdType()
//    override fun hasIdRefType() = baseDescriptor.hasIdRefType()
}