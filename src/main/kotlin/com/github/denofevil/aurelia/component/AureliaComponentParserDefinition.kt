package com.github.denofevil.aurelia.component

import com.intellij.lang.ASTNode
import com.intellij.lang.html.HTMLParserDefinition
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlElementType

class AureliaComponentParserDefinition : HTMLParserDefinition() {
    override fun createElement(node: ASTNode): PsiElement {
        return when (node.elementType) {
            XmlElementType.XML_TAG -> AureliaComponentTag()
            else -> super.createElement(node)
        }
    }
}