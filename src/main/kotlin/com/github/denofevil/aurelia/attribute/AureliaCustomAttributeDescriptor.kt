package com.github.denofevil.aurelia.attribute

import com.intellij.psi.PsiElement

open class AureliaCustomAttributeDescriptor(
    private val name: String,
    private val declaration: PsiElement?,
    private val isFixed: Boolean = false
) : AureliaAttributeDescriptor(name, isFixed) {

    override fun getDeclaration(): PsiElement? = declaration
}