package com.github.denofevil.aurelia.hook

import com.github.denofevil.aurelia.Aurelia
import com.github.denofevil.aurelia.config.AureliaBundle
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.lang.javascript.psi.JSFunction
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElement


class AureliaHookLineMarkerProvider : LineMarkerProvider {

    override fun collectSlowLineMarkers(elements: MutableList<out PsiElement>, result: MutableCollection<in LineMarkerInfo<*>>) {
        super.collectSlowLineMarkers(elements, result)
        for (element in elements) {
            if (DumbService.getInstance(element.project).isDumb) return
            collectLineMarkerInfo(element)?.let { result.add(it) }
        }
    }

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        return null
    }

    private fun collectLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        val method = element as? JSFunction ?: return null
//        if (method.nameIdentifier != element) return null
        if (!Aurelia.isFrameworkCandidate(element)) return null

        checkLifecycleMethod(method, element)?.let { return it }
        checkChangeHandler(method, element)?.let { return it }
        return null
    }

    private fun checkLifecycleMethod(method: JSFunction, element: PsiElement): LineMarkerInfo<*>? {
        if (!HookUtil.isLifecycleMethod(method)) return null

        return NavigationGutterIconBuilder.create(Aurelia.ICON)
            .setTooltipText(AureliaBundle.get("hint.lifecycleMethod"))
            .setTarget(method.parent)
            .createLineMarkerInfo(element)
    }

    private fun checkChangeHandler(method: JSFunction, element: PsiElement): LineMarkerInfo<*>? {
        val observable = HookUtil.findChangeCallbackTarget(method) ?: return null

        return NavigationGutterIconBuilder.create(Aurelia.ICON)
            .setTooltipText(AureliaBundle.get("hint.observableCallbackMethod"))
            .setTarget(observable)
            .createLineMarkerInfo(element)
    }
}
