package com.github.denofevil.aurelia.inject

import com.github.denofevil.aurelia.Aurelia
import com.intellij.ide.highlighter.HtmlFileType
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.lang.javascript.patterns.JSPatterns
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.patterns.PatternCondition
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.util.ProcessingContext

/**
 * Registers the ReferenceProviders for references that are coming from "repeat.for" attributes
 */
class AureliaRepeatForVariableReferenceContributor : PsiReferenceContributor() {

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            JSPatterns.jsReferenceExpression().with(object : PatternCondition<JSReferenceExpression>("wasInjected") {
                override fun accepts(expression: JSReferenceExpression, context: ProcessingContext?): Boolean {
                    val wasInjected = InjectedLanguageManager.getInstance(expression.project).isInjectedFragment(expression.containingFile)
                    // Only injected elements shall be taken into consideration for thi
                    if (wasInjected) {
                        val host = InjectedLanguageManager.getInstance(expression.project).getInjectionHost(expression)
                        if (!Aurelia.isPresentFor(host?.containingFile)) return false
                        return host?.containingFile?.fileType is HtmlFileType
                    }
                    return false
                }
            }),
            AureliaRepeatForVariableReferenceProvider()
        )
    }
}