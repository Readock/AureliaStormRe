package com.github.denofevil.aurelia.component

import com.github.denofevil.aurelia.AttributesProvider
import com.github.denofevil.aurelia.Aurelia
import com.github.denofevil.aurelia.AureliaAttributeDescriptor
import com.intellij.lang.javascript.frameworks.commonjs.CommonJSUtil
import com.intellij.lang.javascript.frameworks.jsx.tsx.TypeScriptJSXTagUtil
import com.intellij.lang.javascript.psi.JSElement
import com.intellij.lang.javascript.psi.JSRecordType.PropertySignature
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeListOwner
import com.intellij.lang.javascript.psi.ecmal4.JSClass
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.html.HtmlTag
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlElementDecl
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlAttributeDescriptor
import com.intellij.xml.XmlCustomElementDescriptor
import com.intellij.xml.XmlElementDescriptor
import com.intellij.xml.XmlNSDescriptor
import com.intellij.xml.impl.dtd.BaseXmlElementDescriptorImpl
import com.intellij.xml.util.XmlUtil
import kotlin.jvm.optionals.getOrElse

class AureliaComponentElementDescriptor(private val tag: HtmlTag) : BaseXmlElementDescriptorImpl(), XmlCustomElementDescriptor {
    private var myElementDecl: XmlElementDecl? = null
    private var myName: String = tag.name
    private var declaration: PsiElement?
    private var attributesProvider = AttributesProvider()

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

        val containingFile = tag.containingFile as? XmlFile ?: return null

        val imports: List<String> = findRequireImports(containingFile, tag.name).map { it.replace(".", "") }
        val jsImportFiles = imports.map { CommonJSUtil.resolveReferencedElements(tag, it) }.flatten()
        if (jsImportFiles.isNotEmpty()) {
            // if possible we take declarations from a <require from=""> tag
            return jsImportFiles.map { findComponentClassByDecorator(it, tag.name) }.firstOrNull();
        }
        // no matching require tag so we will search for a fitting ts file
        val tsFilesWithComponentName = FilenameIndex.getVirtualFilesByName(
            "${tag.name}.ts", scope
        )
        if (!tsFilesWithComponentName.isEmpty()) {
            val files = tsFilesWithComponentName.stream().map { f -> f.findPsiFile(project) }
                .map { f -> findComponentClassByDecorator(f, tag.name) }
                .filter { it != null }.map { it!! }.toList()
            return files.firstOrNull { imports.any { i -> it.containingFile.virtualFile.path.contains(i) } } ?: files.firstOrNull()
        }
        return null
    }

    private fun findRequireImports(xmlFile: XmlFile, componentName: String): List<String> {
        val rootTag = xmlFile.rootTag ?: return emptyList()
        // Find <require> elements with a "from" attribute
        val requireTags = rootTag.findSubTags("require")
        return requireTags.filter { it.getAttributeValue("from") != null }.map { it.getAttributeValue("from")!! }.filter {
            return@filter it.endsWith("/$componentName")
        }
    }

    private fun findComponentClassByDecorator(tsFile: PsiFile?, tagName: String): PsiElement? {
        val jsClasses = PsiTreeUtil.findChildrenOfType(tsFile, JSClass::class.java) as Collection<JSClass>
        return jsClasses.stream().filter { matchesWithCustomComponent(it, tagName) }.findFirst().getOrElse { jsClasses.firstOrNull() }
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

    override fun getAttributeDescriptor(attributeName: String?, context: XmlTag?): XmlAttributeDescriptor? {
        if (attributeName != null && context != null) {
            return attributesProvider.getAttributeDescriptor(attributeName, context)
                ?: super.getAttributeDescriptor(attributeName, context)
        }
        return super.getAttributeDescriptor(attributeName, context)
    }

    public override fun collectAttributeDescriptors(tag: XmlTag?): Array<XmlAttributeDescriptor> {
        val bindables = collectBindableAttributeDescriptors(this.declaration as JSClass?)
        val globals = emptyArray<XmlAttributeDescriptor>()
        val componentAttributes = Aurelia.COMPONENT_ATTRIBUTES.map { AureliaAttributeDescriptor(it) }.toTypedArray()
        return bindables + globals + componentAttributes
    }

    private fun collectBindableAttributeDescriptors(
        jsClass: JSClass?
    ): Array<XmlAttributeDescriptor> {
        val members: ArrayList<XmlAttributeDescriptor?> = ArrayList()
        findBindableAttributes(jsClass).forEach { attr ->
            val descriptor = TypeScriptJSXTagUtil.createAttributeDescriptor(attr, true)
            members.add(AureliaBindingAttributeDescriptor(Aurelia.camelToKebabCase(descriptor.name), descriptor))

            // To support bindings of properties
            Aurelia.INJECTABLE.forEach { suffix ->
                members.add(AureliaBindingAttributeDescriptor("${Aurelia.camelToKebabCase(descriptor.name)}.$suffix", descriptor))
            }
        }
        return members.toArray(XmlAttributeDescriptor.EMPTY) as Array<XmlAttributeDescriptor>
    }

    fun findBindableAttributes(jsClass: JSClass?): ArrayList<PropertySignature> {
        val members = arrayListOf<PropertySignature>()
        jsClass ?: return members;
        for (jsMember in jsClass.members) {
            if (hasBindableAnnotation(jsMember) && jsMember is PropertySignature) {
                members.add(jsMember)
            }
        }
        return members
    }

    private fun hasBindableAnnotation(member: JSElement): Boolean {
        if (member !is JSAttributeListOwner) return false
        return member.attributeList?.decorators?.any { it.decoratorName == "bindable" } ?: false
    }

    private fun matchesWithCustomComponent(jsClass: JSClass, componentName: String): Boolean {
        val matchingClassName = jsClass.name != null && (Aurelia.camelToKebabCase(jsClass.name!!)) == componentName
        if (matchingClassName) {
            return true;
        }
        return jsClass.attributeList?.decorators?.any {
            it.decoratorName == "customElement" && it.expression?.text?.contains(componentName) == true
        } ?: false
    }

    override fun collectAttributeDescriptorsMap(tag: XmlTag?): HashMap<String, XmlAttributeDescriptor> {
        val attributeMap = HashMap<String, XmlAttributeDescriptor>()
        val descriptors = collectAttributeDescriptors(tag)

        for (descriptor in descriptors) {
            attributeMap[descriptor.name] = descriptor
        }
        return attributeMap
    }

    override fun getQualifiedName(): String {
        return tag.name
    }

    override fun isCustomElement() = true

    override fun getContentType() = CONTENT_TYPE_ANY

    override fun doCollectXmlDescriptors(tag: XmlTag?) = emptyArray<XmlElementDescriptor>()

    override fun collectElementDescriptorsMap(tag: XmlTag?) = hashMapOf<String, XmlElementDescriptor>()
}
