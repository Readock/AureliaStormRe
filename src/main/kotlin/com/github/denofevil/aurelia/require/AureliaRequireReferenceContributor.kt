package com.github.denofevil.aurelia.require

import com.github.denofevil.aurelia.Aurelia
import com.intellij.patterns.XmlPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar


class AureliaRequireReferenceContributor : PsiReferenceContributor() {

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        Aurelia.IMPORT_ELEMENTS.forEach { element ->
            registrar.registerReferenceProvider(
                XmlPatterns.xmlAttributeValue("from").inside(XmlPatterns.xmlTag().withName(element)),
                AureliaRequireReferenceProvider()
            )
        }
    }
}