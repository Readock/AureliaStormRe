package com.github.denofevil.aurelia.component

import com.github.denofevil.aurelia.Aurelia
import com.github.denofevil.aurelia.config.AureliaSettings
import com.intellij.psi.impl.source.xml.XmlElementDescriptorProvider
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlElementDescriptor
import org.jetbrains.annotations.Nullable


class AureliaCustomElementDescriptorProvider : XmlElementDescriptorProvider {

    @Nullable
    override fun getDescriptor(tag: XmlTag): XmlElementDescriptor? {
        if (!AureliaSettings.getInstance().isCustomComponentEnabled) return null

        if (Aurelia.isFrameworkCandidate(tag)) {
            val descriptor = AureliaCustomElementDescriptor(tag)
            if (descriptor.declaration != null) {
                return descriptor
            }
        }
        return null
    }
}