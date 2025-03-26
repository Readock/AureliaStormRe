package com.github.denofevil.aurelia

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoFilter
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.html.HtmlTag
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable


class AureliaAttributesFilter : HighlightInfoFilter {
    override fun accept(@NotNull highlightInfo: HighlightInfo, @Nullable file: PsiFile?): Boolean {
        if (null == file) {
            return true
        }

        val project: Project = file.project
        if (!Aurelia.isPresentFor(project)) {
            return true
        }

        val element = file.findElementAt(highlightInfo.getStartOffset()) ?: return true

        // handle rest cases
        if (HighlightSeverity.WARNING == highlightInfo.severity) {
            if (element.text == "load" && isAnchorTag(element.parent)) {
                return false
            }
        }

        return true
    }
}

fun isAnchorTag(element: PsiElement?): Boolean {
    // Check if the element itself is an HtmlTag and a <a> tag
    if (element is HtmlTag) {
        return "a".equals(element.name, ignoreCase = true)
    }
    // Check if the parent of the element is an HtmlTag and a <a> tag
    else if (element?.parent is HtmlTag) {
        return "a".equals((element.parent as HtmlTag).name, ignoreCase = true)
    }
    return false
}
