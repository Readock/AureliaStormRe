package com.github.denofevil.aurelia.attribute

import com.github.denofevil.aurelia.Aurelia
import com.intellij.psi.PsiElement
import com.intellij.psi.meta.PsiPresentableMetaData
import com.intellij.util.ArrayUtil
import com.intellij.xml.impl.BasicXmlAttributeDescriptor

open class AureliaAttributeDescriptor(private val name: String) : BasicXmlAttributeDescriptor(),
    PsiPresentableMetaData {
    override fun getIcon() = Aurelia.ICON

    override fun getTypeName(): String? = null

    override fun init(psiElement: PsiElement) {
    }

    override fun isRequired(): Boolean = false
    override fun hasIdType(): Boolean = false
    override fun hasIdRefType(): Boolean = false
    override fun isEnumerated(): Boolean = false
    override fun getDeclaration(): PsiElement? = null
    override fun getName(): String = name
    override fun isFixed(): Boolean = false
    override fun getDefaultValue(): String? = null
    override fun getEnumeratedValues(): Array<String>? = ArrayUtil.EMPTY_STRING_ARRAY
}