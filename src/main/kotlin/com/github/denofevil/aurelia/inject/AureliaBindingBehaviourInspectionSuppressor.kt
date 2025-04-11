package com.github.denofevil.aurelia.inject

import com.github.denofevil.aurelia.Aurelia
import com.intellij.ide.highlighter.HtmlFileType
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.lang.javascript.JSTokenTypes
import com.intellij.lang.javascript.inspections.JSInspectionSuppressor
import com.intellij.lang.javascript.psi.JSBinaryExpression
import com.intellij.psi.PsiElement

class AureliaBindingBehaviourInspectionSuppressor : JSInspectionSuppressor() {
    override fun isSuppressedFor(child: PsiElement, toolId: String): Boolean {
        // Supress binding behaviours like "& myShit" or "| myShit"
        val element = child.parent?.parent as? JSBinaryExpression ?: return super.isSuppressedFor(child, toolId)
        if (child.parent != element.rOperand) return super.isSuppressedFor(child, toolId)
        val wasInjected = InjectedLanguageManager.getInstance(element.project).isInjectedFragment(element.containingFile)
        // Only injected elements shall be taken into consideration for this
        if (wasInjected) {
            val host = InjectedLanguageManager.getInstance(element.project).getInjectionHost(element)
            if (host?.containingFile?.fileType is HtmlFileType && Aurelia.isPresentFor(host.containingFile)) {
                if (element.operationSign == JSTokenTypes.AND || element.operationSign == JSTokenTypes.OR) {
                    return true
                }
            }
        }
        return super.isSuppressedFor(element, toolId)
    }
}