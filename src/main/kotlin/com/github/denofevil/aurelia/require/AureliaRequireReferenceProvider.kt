package com.github.denofevil.aurelia.require

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.xml.XmlAttributeValue

class AureliaRequireReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: com.intellij.util.ProcessingContext): Array<PsiReference> {
        if (element is XmlAttributeValue) {
            return arrayOf(AureliaRequireReference(element))
        }
        return PsiReference.EMPTY_ARRAY
    }
}
