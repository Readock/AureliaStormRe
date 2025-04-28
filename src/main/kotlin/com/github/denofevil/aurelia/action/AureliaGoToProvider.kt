package com.github.denofevil.aurelia.action

import com.github.denofevil.aurelia.Aurelia
import com.github.denofevil.aurelia.FileUtils
import com.github.denofevil.aurelia.config.AureliaBundle
import com.intellij.ide.highlighter.HtmlFileType
import com.intellij.lang.javascript.JavaScriptFileType
import com.intellij.lang.javascript.TypeScriptFileType
import com.intellij.navigation.GotoRelatedItem
import com.intellij.navigation.GotoRelatedProvider
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.psi.PsiElement
import com.intellij.util.SmartList
import javax.swing.Icon

/**
 * Adds support for the "Navigate > Related Symbol..." action
 */
class AureliaGoToProvider : GotoRelatedProvider() {

    override fun getItems(context: DataContext): List<GotoRelatedItem> {
        val file = CommonDataKeys.PSI_FILE.getData(context)
        if (file != null) {
            return getItems(file)
        }
        return emptyList()
    }

    override fun getItems(psiElement: PsiElement): List<GotoRelatedItem> {
        val file = psiElement.containingFile
        if (file == null || !Aurelia.isPresentFor(file)) {
            return emptyList()
        }
        val results: MutableList<GotoRelatedItem> = SmartList()
        if (file.fileType is HtmlFileType) {
            FileUtils.findControllerClassOfHtmlFile(file)?.let {
                results.add(AureliaGotoRelatedItem(it))
            }
        }
        if (file.fileType is JavaScriptFileType || file.fileType is TypeScriptFileType) {
            FileUtils.findViewOfControllerFile(file)?.let {
                results.add(AureliaGotoRelatedItem(it))
            }
        }
        return results
    }
}

class AureliaGotoRelatedItem(element: PsiElement) : GotoRelatedItem(element, AureliaBundle.get("action.goto.groupName")) {
    override fun getCustomIcon(): Icon = Aurelia.ICON
}