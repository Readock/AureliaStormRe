package com.github.denofevil.aurelia.index

import com.intellij.lang.javascript.JavaScriptFileType
import com.intellij.lang.javascript.TypeScriptFileType
import com.intellij.lang.javascript.psi.ecmal4.JSClass
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.*
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor

val AureliaCustomElementIndexKey: ID<String, Void> = ID.create("aurelia.customElement.index")

class AureliaCustomElementIndex : ScalarIndexExtension<String>() {

    override fun getName(): ID<String, Void> = AureliaCustomElementIndexKey

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