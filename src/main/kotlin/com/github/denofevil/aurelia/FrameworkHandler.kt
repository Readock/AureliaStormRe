package com.github.denofevil.aurelia

import com.github.denofevil.aurelia.require.DeclarationResolverUtil
import com.intellij.codeInsight.completion.CompletionUtil
import com.intellij.lang.javascript.index.FrameworkIndexingHandler
import com.intellij.lang.javascript.psi.JSQualifiedNameImpl
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.lang.javascript.psi.ecmal4.JSClass
import com.intellij.lang.javascript.psi.resolve.JSTypeInfo
import com.intellij.lang.javascript.psi.types.JSContext
import com.intellij.lang.javascript.psi.types.JSNamedTypeFactory
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.resolve.FileContextUtil
import com.intellij.psi.util.PsiTreeUtil

/**
 * Provides context (resolved controller classes) to code injections
 */
class FrameworkHandler : FrameworkIndexingHandler() {
    override fun addContextType(info: JSTypeInfo, context: PsiElement) {
        if (DumbService.getInstance(context.project).isDumb) return
        val controller = findController(context) ?: return
        val namespace = JSQualifiedNameImpl.buildProvidedNamespace(controller)
        info.addType(JSNamedTypeFactory.createNamespace(namespace, JSContext.INSTANCE, null, true), false)
    }

    override fun addContextNames(context: PsiElement, names: MutableList<String>) {
        val controller = findController(context) ?: return
        names.add(controller.qualifiedName!!)
    }

    private fun findController(context: PsiElement): JSClass? {
        if (context !is JSReferenceExpression || context.qualifier != null) {
            return null
        }
        if (!Aurelia.isPresentFor(context.getProject())) return null

        val original = CompletionUtil.getOriginalOrSelf<PsiElement>(context)
        val hostFile =
            FileContextUtil.getContextFile(if (original !== context) original else context.getContainingFile().originalFile) ?: return null

        val directory = hostFile.originalFile.parent ?: return null

        val name = FileUtil.getNameWithoutExtension(hostFile.name)
        DeclarationResolverUtil.resolveCustomElementDeclaration(context, name)?.let { return it }

        val controllerFile = directory.findFile("$name.ts") ?: directory.findFile("$name.js")
        return PsiTreeUtil.findChildOfType(controllerFile, JSClass::class.java)
    }
}
