package com.github.denofevil.aurelia.config

import com.intellij.AbstractBundle

object AureliaStormBundle : AbstractBundle("messages.AureliaStormBundle") {
    fun get(key: String, vararg params: Any): String {
        return getMessage(key, *params)
    }
}