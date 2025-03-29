package com.github.denofevil.aurelia.component

import com.github.denofevil.aurelia.Aurelia
import com.github.denofevil.aurelia.config.AureliaSettings
import com.intellij.psi.html.HtmlTag
import com.intellij.psi.impl.source.xml.XmlElementDescriptorProvider
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlElementDescriptor
import org.jetbrains.annotations.Nullable
import javax.swing.text.html.HTML


class AureliaComponentTagDescriptorProvider : XmlElementDescriptorProvider {
    private val htmlTags = HTML.getAllTags().map { it.toString().lowercase() }


    @Nullable
    override fun getDescriptor(tag: XmlTag): XmlElementDescriptor? {
        if (!AureliaSettings.getInstance().isCustomComponentEnabled) return null
        
        val tagName = tag.name.lowercase()
        val isExcludedTag = htmlTags.stream().anyMatch { it.equals(tagName) }
                || Aurelia.CUSTOM_ELEMENTS.contains(tagName) || tagName == "require"
        if (tag is HtmlTag && !isExcludedTag && Aurelia.isPresentFor(tag.containingFile)) {
            val descriptor = AureliaComponentElementDescriptor(tag)
            if (descriptor.declaration != null) {
                return descriptor
            }
        }
        return null
    }
}