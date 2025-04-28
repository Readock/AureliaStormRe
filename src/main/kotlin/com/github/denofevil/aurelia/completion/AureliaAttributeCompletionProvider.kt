package com.github.denofevil.aurelia.completion

import com.github.denofevil.aurelia.Aurelia
import com.github.denofevil.aurelia.index.AureliaIndexUtil
import com.github.denofevil.aurelia.require.DeclarationResolverUtil
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.xml.XmlAttribute
import com.intellij.util.ProcessingContext

class AureliaAttributeCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val element = parameters.position.parent as? XmlAttribute ?: return
        val tag = element.parent ?: return

        AureliaIndexUtil.getAllCustomAttributeNames(tag.project).forEach {
            result.addElement(LookupElementBuilder.create(it).withIcon(Aurelia.ATTRIBUTE_ICON))
        }
        val jsClass = DeclarationResolverUtil.resolveCustomElementDeclaration(tag)
        DeclarationResolverUtil.resolveBindableAttributesOnlyWithAnnotation(jsClass).forEach {
            val text = Aurelia.camelToKebabCase(it.memberName).lowercase()
            result.addElement(LookupElementBuilder.create(text).withIcon(Aurelia.PROPERTY_ICON))
        }
    }
}