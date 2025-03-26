package com.github.denofevil.aurelia.component

import com.github.denofevil.aurelia.Aurelia
import com.intellij.lang.javascript.frameworks.jsx.tsx.TypeScriptJSXTagUtil
import com.intellij.lang.javascript.psi.JSElement
import com.intellij.lang.javascript.psi.JSRecordType.PropertySignature
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeListOwner
import com.intellij.lang.javascript.psi.ecmal4.JSClass
import com.intellij.openapi.util.Condition
import com.intellij.openapi.util.Conditions
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.html.HtmlTag
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlElementDecl
import com.intellij.psi.xml.XmlTag
import com.intellij.util.containers.orNull
import com.intellij.xml.XmlAttributeDescriptor
import com.intellij.xml.XmlElementDescriptor
import com.intellij.xml.XmlNSDescriptor
import com.intellij.xml.impl.dtd.BaseXmlElementDescriptorImpl
import com.intellij.xml.util.XmlUtil
import kotlin.jvm.optionals.getOrNull

class AureliaComponentElementDescriptor(private val tag: HtmlTag) : BaseXmlElementDescriptorImpl() {
    private var myElementDecl: XmlElementDecl? = null
    private var myName: String = tag.name
    private var declaration: PsiElement?

    init {
        this.declaration = resolveDeclarationClass()
    }

    override fun getDeclaration(): PsiElement? {
        return this.declaration
    }

    override fun getName(context: PsiElement?): String {
        return this.name
    }

    private fun resolveDeclarationClass(): PsiElement? {
        // Find file in project structure (assuming it's in the same folder)
        val project = tag.project
        val scope = GlobalSearchScope.allScope(project)

        val tsFiles = FilenameIndex.getVirtualFilesByName(
            "${tag.name}.ts", scope
        )

        if (!tsFiles.isEmpty()) {
            val files = tsFiles.stream().map { f -> f.findPsiFile(project) }
                .map { f -> findComponentClassByDecorator(f, tag.name) }
                .filter { it != null }
            return files.findFirst().orNull()
        }
        return null
    }

    private fun findComponentClassByDecorator(tsFile: PsiFile?, tagName: String): PsiElement? {
        val jsClasses = PsiTreeUtil.findChildrenOfType(tsFile, JSClass::class.java) as Collection<JSClass>
        return jsClasses.stream().filter { hasCustomComponentAnnotation(it, tagName) }.findFirst().getOrNull()
    }

    override fun getName(): String {
        return tag.name
    }

    override fun init(element: PsiElement) {
        this.myElementDecl = element as XmlElementDecl
    }

    override fun getDefaultName(): String {
        return tag.name
    }

    override fun getNSDescriptor(): XmlNSDescriptor? {
        return getNsDescriptorFrom(this.myElementDecl)
    }

    private fun getNsDescriptorFrom(elementDecl: PsiElement?): XmlNSDescriptor? {
        val file = XmlUtil.getContainingFile(elementDecl)
        if (file == null) {
            return null
        } else {
            val document = checkNotNull(file.document)

            val descriptor = document.metaData as XmlNSDescriptor?
            return descriptor ?: document.getDefaultNSDescriptor("", false)
        }
    }

    override fun getContentType(): Int {
        return if (myElementDecl!!.contentSpecElement.isAny) {
            1
        } else if (myElementDecl!!.contentSpecElement.hasChildren()) {
            2
        } else if (myElementDecl!!.contentSpecElement.isEmpty) {
            0
        } else {
            if (myElementDecl!!.contentSpecElement.isMixed) 3 else 1
        }
    }

    override fun doCollectXmlDescriptors(tag: XmlTag?): Array<XmlElementDescriptor> {
        return emptyArray() // No predefined child elements
    }

    override fun collectAttributeDescriptors(tag: XmlTag?): Array<XmlAttributeDescriptor> {
        return getXmlAttributeDescriptors(this.declaration as JSClass?, Conditions.alwaysTrue())
    }

    private fun getXmlAttributeDescriptors(jsClass: JSClass?, filter: Condition<in PropertySignature?>): Array<XmlAttributeDescriptor> {
        val members: ArrayList<XmlAttributeDescriptor?> = ArrayList()
        if (jsClass?.members == null) {
            return emptyArray()
        }
        for (jsMember in jsClass.members) {
            if (hasBindableAnnotation(jsMember) && jsMember is PropertySignature) {
                val descriptor = TypeScriptJSXTagUtil.createAttributeDescriptor(jsMember, true)
                members.add(AureliaBindingAttributeDescriptor(camelToKebabCase(descriptor.name), descriptor))

                // To support bindings of properties
                Aurelia.INJECTABLE.forEach { suffix ->
                    members.add(AureliaBindingAttributeDescriptor("${camelToKebabCase(descriptor.name)}.$suffix", descriptor))
                }
            }
        }
        return members.toArray(XmlAttributeDescriptor.EMPTY) as Array<XmlAttributeDescriptor>
    }

    private fun hasCustomComponentAnnotation(jsClass: JSClass, componentName: String): Boolean {
        val matchingClassName = jsClass.name != null && (camelToKebabCase(jsClass.name!!)) == componentName
        return jsClass.attributeList?.decorators?.any {
            (it.decoratorName == "customElement" && it.expression?.text?.contains(componentName) == true)
                    || (it.decoratorName == "containerless" && matchingClassName)
        } ?: false
    }

    private fun hasBindableAnnotation(member: JSElement): Boolean {
        if (member !is JSAttributeListOwner) return false
        return member.attributeList?.decorators?.any { it.decoratorName == "bindable" } ?: false
    }

    override fun collectAttributeDescriptorsMap(tag: XmlTag?): HashMap<String, XmlAttributeDescriptor> {
        val attributeMap = HashMap<String, XmlAttributeDescriptor>()
        val descriptors = collectAttributeDescriptors(tag)

        for (descriptor in descriptors) {
            attributeMap[descriptor.name] = descriptor
        }
        return attributeMap
    }

    private fun camelToKebabCase(camel: String): String {
        return camel.replace(Regex("([a-z])([A-Z])"), "$1-$2").lowercase()
    }

    override fun collectElementDescriptorsMap(tag: XmlTag?): HashMap<String, XmlElementDescriptor> {
        return hashMapOf() // No nested elements
    }

    override fun getQualifiedName(): String {
        return tag.name
    }
}
