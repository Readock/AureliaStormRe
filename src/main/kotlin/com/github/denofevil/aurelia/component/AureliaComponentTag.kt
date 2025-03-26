package com.github.denofevil.aurelia.component

import com.intellij.psi.impl.source.html.HtmlTagImpl

class AureliaComponentTag() : HtmlTagImpl() {
    override fun toString(): String {
        return "AureliaComponentTag(${name})"
    }
}