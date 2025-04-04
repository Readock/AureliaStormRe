package com.github.denofevil.aurelia.index

import com.intellij.lang.javascript.JavaScriptFileType
import com.intellij.lang.javascript.TypeScriptFileType
import com.intellij.lang.javascript.psi.ecmal4.JSClass
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.*
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor

/**
 * File based index for fast custom element resolving
 */
class AureliaCustomElementIndex : ScalarIndexExtension<String>() {
    companion object {
        val KEY: ID<String, Void> = ID.create("aurelia.customElement.index")
    }

    override fun getName(): ID<String, Void> = KEY

    override fun getVersion(): Int = 1

    override fun dependsOnFileContent(): Boolean = true

    override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor()

    override fun getInputFilter(): FileBasedIndex.InputFilter =
        DefaultFileTypeSpecificInputFilter(JavaScriptFileType.INSTANCE, TypeScriptFileType.INSTANCE)

    override fun getIndexer(): DataIndexer<String, Void, FileContent> {
        return DataIndexer { fileContent ->
            val result = mutableMapOf<String, Void?>()
            val psiFile = fileContent.psiFile
            PsiTreeUtil.findChildrenOfType(psiFile, JSClass::class.java).forEach { jsClass ->
                AureliaIndexUtil.resolveClassCustomElementNameByAnnotation(jsClass)?.lowercase()?.let {
                    result[it] = null
                }
            }
            result
        }
    }

}