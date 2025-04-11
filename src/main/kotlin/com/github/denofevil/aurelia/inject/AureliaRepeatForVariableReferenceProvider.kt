package com.github.denofevil.aurelia.inject

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.lang.javascript.psi.JSForInStatement
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag

/**
 * Provides references for variables that are coming from "repeat.for" attributes
 */
class AureliaRepeatForVariableReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: com.intellij.util.ProcessingContext): Array<PsiReference> {
        val refExpr = element as? JSReferenceExpression ?: return PsiReference.EMPTY_ARRAY
        val name = refExpr.referenceName ?: return PsiReference.EMPTY_ARRAY

        // Get the original HTML host of this injected JS expression
        val injectionHost = InjectedLanguageManager.getInstance(element.project).getInjectionHost(element)
            ?: return PsiReference.EMPTY_ARRAY

        // TODO: clean this code up and support variants of repeat.for as well
        val sourceTag: XmlTag? = PsiTreeUtil.findFirstParent(injectionHost) {
            (it as? XmlTag)?.getAttribute("repeat.for")?.value?.contains("$name ") == true
        } as? XmlTag?
        if (sourceTag == null) return PsiReference.EMPTY_ARRAY

        val repeatAttrValue = sourceTag.getAttribute("repeat.for")?.valueElement ?: return PsiReference.EMPTY_ARRAY
        val injectedPsi = InjectedLanguageManager.getInstance(repeatAttrValue.project)
            .getInjectedPsiFiles(repeatAttrValue)
            ?.firstOrNull()
            ?.first as? PsiFile
        val forStatement = PsiTreeUtil.findChildOfType(injectedPsi, JSForInStatement::class.java)
        val loopVariable = forStatement?.varDeclaration?.declarations?.firstOrNull() ?: return PsiReference.EMPTY_ARRAY

        return arrayOf(object : PsiReferenceBase<JSReferenceExpression>(refExpr) {
            override fun resolve(): PsiElement = loopVariable
            override fun getVariants(): Array<Any> = emptyArray()
        })
    }
}
