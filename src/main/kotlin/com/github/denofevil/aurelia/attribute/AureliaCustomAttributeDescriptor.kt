package com.github.denofevil.aurelia.attribute

import com.intellij.psi.PsiElement

open class AureliaCustomAttributeDescriptor(private val name: String, private val declaration: PsiElement?) :
    AureliaAttributeDescriptor(name) {
        
    override fun getDeclaration(): PsiElement? = declaration
}