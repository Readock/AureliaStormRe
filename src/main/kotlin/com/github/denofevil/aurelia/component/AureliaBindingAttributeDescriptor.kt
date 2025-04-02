package com.github.denofevil.aurelia.component

import com.github.denofevil.aurelia.attribute.AureliaAttributeDescriptor
import com.intellij.psi.PsiElement
import com.intellij.xml.XmlAttributeDescriptor

class AureliaBindingAttributeDescriptor(
    private var attributeName: String,
    private val baseDescriptor: XmlAttributeDescriptor,
) : AureliaAttributeDescriptor(attributeName, false) {


    override fun getDeclaration(): PsiElement? = baseDescriptor.declaration
}