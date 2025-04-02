package com.github.denofevil.aurelia.config

import com.intellij.AbstractBundle

object AureliaBundle : AbstractBundle("messages.AureliaBundle") {
    fun get(key: String, vararg params: Any): String {
        return getMessage(key, *params)
    }
}