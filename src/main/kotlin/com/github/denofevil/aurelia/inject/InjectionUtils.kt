package com.github.denofevil.aurelia.inject

import com.github.denofevil.aurelia.Aurelia
import com.intellij.ide.highlighter.HtmlFileType
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag

object InjectionUtils {

    fun isAureliaInjected(element: PsiElement): Boolean {
        if (element.language !is JavascriptLanguage) return false // only js can be injected
        val wasInjected = InjectedLanguageManager.getInstance(element.project).isInjectedFragment(element.containingFile)
        // Only injected elements shall be taken into consideration for this
        if (wasInjected) {
            val host = InjectedLanguageManager.getInstance(element.project).getInjectionHost(element)
            if (!Aurelia.isPresentFor(host?.containingFile)) return false
            return host?.containingFile?.fileType is HtmlFileType
        }
        return false
    }

    fun findParentRepeatForTag(element: PsiElement, variableName: String? = null): XmlTag? {
        return PsiTreeUtil.findFirstParent(element) {
            val tag = (it as? XmlTag)?.getAttribute("repeat.for") ?: return@findFirstParent false
            if (variableName == null) return@findFirstParent true
            return@findFirstParent tag.value?.contains("$variableName ") == true
        } as? XmlTag?
    }
}