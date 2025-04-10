package com.github.denofevil.aurelia

import com.github.denofevil.aurelia.config.AureliaSettings
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.openapi.util.TextRange
import com.intellij.psi.ElementManipulators
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.xml.XmlAttributeValueImpl
import com.intellij.psi.impl.source.xml.XmlTextImpl
import com.intellij.psi.templateLanguages.OuterLanguageElement
import com.intellij.psi.xml.*

/**
 * Injects js/ts code into HTML files
 */
class Injector : MultiHostInjector {
    override fun getLanguagesToInject(registrar: MultiHostRegistrar, host: PsiElement) {
        if (!AureliaSettings.getInstance().jsInjectionEnabled) return
        if (!Aurelia.isPresentFor(host.project)) return

        val range = ElementManipulators.getValueTextRange(host)
        if (host is XmlAttributeValue) {
            val parent = host.getParent()
            if (parent is XmlAttribute) {
                val name = parent.name
                if (Aurelia.REPEAT_FOR.contains(name)) {
                    registrar.startInjecting(JavascriptLanguage.INSTANCE)
                        .addPlace("for(var ", ")", host as PsiLanguageInjectionHost, range)
                        .doneInjecting()
                    return
                }
                for (attr in Aurelia.INJECTABLE) {
                    if (name.endsWith(".$attr")) {
                        val variables = collectRepeatForVariables(host)
                        registrar.startInjecting(JavascriptLanguage.INSTANCE)
                            .addPlace(variables, null, host as PsiLanguageInjectionHost, range)
                            .doneInjecting()
                        return
                    }
                }
            }
        }
        val text = ElementManipulators.getValueText(host)
        var start = text.indexOf("\${")
        while (start >= 0) {
            var end = range.length
            var nested = 0
            for (i in start + 2 until range.length) {
                if (text[i] == '{') nested++
                if (nested == 0 && text[i] == '}') {
                    end = i
                    break
                }
                if (text[i] == '}') nested--
            }
            var injectionCandidate = host.findElementAt(start)
            while (injectionCandidate is PsiWhiteSpace) injectionCandidate = injectionCandidate.getNextSibling()

            if (injectionCandidate != null &&
                injectionCandidate.startOffsetInParent <= end &&
                !XmlTokenType.COMMENTS.contains(injectionCandidate.node.elementType) &&
                injectionCandidate.node.elementType !== XmlElementType.XML_COMMENT &&
                injectionCandidate !is OuterLanguageElement
            ) {
                val variables = collectRepeatForVariables(host)
                registrar.startInjecting(JavascriptLanguage.INSTANCE)
                    .addPlace(
                        variables, null, host as PsiLanguageInjectionHost,
                        TextRange(range.startOffset + start + 2, range.startOffset + end)
                    )
                    .doneInjecting()
            }
            start = text.indexOf("\${", end)
        }
    }


    private fun collectRepeatForVariables(host: PsiElement): String? {
        val variables = mutableListOf<String>()
        var current: PsiElement? = host
        while (current != null) {
            if (current is XmlTag) {
                current.attributes.filter { Aurelia.REPEAT_FOR.contains(it.name) }.forEach { attribute ->
                    if (!attribute.value.isNullOrBlank()) {
                        val variable = extractRepeatForVariable(attribute.value!!)
                        if (variable != null) {
                            variables.add(variable)
                        }
                    }
                }
            }
            current = current.parent
        }
        if (variables.isNotEmpty()) {
            return "var ${variables.joinToString(";var ")};"
        }
        return null
    }

    private fun extractRepeatForVariable(expression: String): String? {
        val parts = expression.split(Aurelia.REPEAT_FOR_OF_KEYWORD)
        if (parts.isNotEmpty()) {
            return parts[0]
        }
        return null
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement>> {
        return listOf(XmlTextImpl::class.java, XmlAttributeValueImpl::class.java)
    }
}
