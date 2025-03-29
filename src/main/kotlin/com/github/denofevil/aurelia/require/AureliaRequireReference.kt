package com.github.denofevil.aurelia.require

import com.intellij.lang.javascript.frameworks.commonjs.CommonJSUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag

class AureliaRequireReference(private val attribute: XmlAttributeValue) : PsiReferenceBase<XmlAttributeValue>(attribute) {

    override fun resolve(): PsiElement? {
        val tag = element.parent?.parent as? XmlTag ?: return null
        return CommonJSUtil.resolveReferencedElements(tag, element.value).firstOrNull()
            ?: CommonJSUtil.resolveReferencedElements(tag, "src/${element.value}").firstOrNull()
    }

    override fun getVariants(): Array<Any> = emptyArray()
}
