package com.github.denofevil.aurelia

import com.github.denofevil.aurelia.config.AureliaSettings
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.lang.javascript.JSInjectionBracesUtil
import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.psi.ElementManipulators
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.impl.source.xml.XmlAttributeValueImpl
import com.intellij.psi.impl.source.xml.XmlTextImpl
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue

/**
 * @author Dennis.Ushakov
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
                for (attr in Aurelia.INJECTABLE) {
                    if (name.endsWith(".$attr")) {
                        registrar.startInjecting(JavascriptLanguage.INSTANCE)
                            .addPlace(null, null, host as PsiLanguageInjectionHost, range)
                            .doneInjecting()
                        return
                    }
                }
            }
        }
        JSInjectionBracesUtil.injectInXmlTextByDelimiters(registrar, host, JavascriptLanguage.INSTANCE, "\${", "}")
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement>> {
        return listOf(XmlTextImpl::class.java, XmlAttributeValueImpl::class.java)
    }
}
