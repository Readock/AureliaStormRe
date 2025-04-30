package com.github.denofevil.aurelia.hook

import com.github.denofevil.aurelia.Aurelia
import com.github.denofevil.aurelia.index.AureliaIndexUtil
import com.intellij.lang.javascript.psi.JSFunction
import com.intellij.lang.javascript.psi.ecmal4.JSClass
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

object HookUtil {
    fun isLifecycleMethod(method: JSFunction): Boolean {
        val methodClass = PsiTreeUtil.findFirstParent(method) { it is JSClass } as JSClass
        if (!AureliaIndexUtil.isCustomElementClass(methodClass)) return false
        return Aurelia.LIFECYCLE_METHODS.contains(method.name)
    }

    fun isChangeCallback(method: JSFunction): Boolean = findChangeCallbackTarget(method) != null

    fun findChangeCallbackTarget(method: JSFunction): PsiElement? {
        if (method.name?.endsWith("Changed") != true) return null
        val methodClass = method.parent as? JSClass ?: return null
        return methodClass.members.firstOrNull { it.name == method.name?.substringBeforeLast("Changed") }
    }

}