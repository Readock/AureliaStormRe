package com.github.denofevil.aurelia.component

import com.github.denofevil.aurelia.Aurelia
import com.intellij.psi.html.HtmlTag
import com.intellij.psi.impl.source.xml.XmlElementDescriptorProvider
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlElementDescriptor
import org.jetbrains.annotations.Nullable
import javax.swing.text.html.HTML


class ComponentTagDescriptorProvider : XmlElementDescriptorProvider {
    private val htmlTags = HTML.getAllTags().map { it.toString().lowercase() }

    @Nullable
    override fun getDescriptor(tag: XmlTag): XmlElementDescriptor? {
        val isCommonHtmlTag = htmlTags.stream().anyMatch { it.equals(tag.name.lowercase()) };
        if (tag is HtmlTag && !isCommonHtmlTag && Aurelia.isPresentFor(tag.containingFile)) {
            val descriptor = AureliaComponentElementDescriptor(tag)
            if (descriptor.declaration != null) {
                return descriptor // only apply when there was a declaration
            }
        }
        return null
    }
}