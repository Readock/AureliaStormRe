package com.github.denofevil.aurelia.completion

import com.github.denofevil.aurelia.Aurelia
import com.github.denofevil.aurelia.index.AureliaIndexUtil
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.xml.XmlElement
import com.intellij.util.ProcessingContext

class AureliaElementCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val element = parameters.position.parent as? XmlElement ?: return
        AureliaIndexUtil.getAllCustomElementNames(element.project).forEach {
            result.addElement(LookupElementBuilder.create(it).withIcon(Aurelia.CLASS_ICON))
        }
    }
}