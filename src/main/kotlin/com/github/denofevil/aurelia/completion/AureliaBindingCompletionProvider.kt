package com.github.denofevil.aurelia.completion

import com.github.denofevil.aurelia.Aurelia
import com.github.denofevil.aurelia.index.AureliaIndexUtil
import com.github.denofevil.aurelia.require.DeclarationResolverUtil
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.util.parentOfType
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag
import com.intellij.util.ProcessingContext

class AureliaBindingCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val attribute = parameters.position.parentOfType<XmlAttribute>(true) ?: return
        val tag = parameters.position.parentOfType<XmlTag>(true) ?: return
        if (!Aurelia.isFrameworkCandidate(tag)) return
        if (!attribute.name.contains(".")) return

        val name = attribute.name.substringBeforeLast(".")
        val declaration = DeclarationResolverUtil.resolveCustomElementDeclaration(tag)
        val signatures = DeclarationResolverUtil.resolveBindableAttributes(declaration)

        val isCustomAttribute = AureliaIndexUtil.resolveCustomAttributeClasses(name, tag.project).isNotEmpty()
        val isPropertyBinding = signatures.any { Aurelia.camelToKebabCase(it.memberName) == name }
        if (!isPropertyBinding && !isCustomAttribute) return // skip because we cant bind it

        Aurelia.ATTRIBUTE_BINDING_SUGGESTIONS.forEach {
            result.addElement(
                LookupElementBuilder.create("$name.$it").withIcon(Aurelia.ICON).withInsertHandler { context, _ ->
                    context.editor.document.replaceString(context.startOffset, context.tailOffset, "$name.$it=\"\"")
                    context.editor.caretModel.moveToOffset(context.startOffset + name.length + it.length + 3)
                })
        }
    }
}