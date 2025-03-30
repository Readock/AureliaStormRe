package com.github.denofevil.aurelia.require

import com.intellij.lang.ecmascript6.resolve.JSFileReferencesUtil
import com.intellij.psi.ElementManipulators
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.xml.XmlAttributeValue

class AureliaRequireReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: com.intellij.util.ProcessingContext): Array<PsiReference> {
        if (element is XmlAttributeValue) {
            val text = ElementManipulators.getValueText(element)
            val range = ElementManipulators.getValueTextRange(element)
            val reference = AureliaRequireReference(element)
            if (reference.resolve() != null) {
                return arrayOf(reference)
            }
            return JSFileReferencesUtil.createImportExportFromClauseReferences(element, range.startOffset, text, this)
        }
        return PsiReference.EMPTY_ARRAY
    }
}
