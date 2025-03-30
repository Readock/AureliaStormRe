package com.github.denofevil.aurelia.config

import com.intellij.openapi.options.Configurable
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel
import kotlin.reflect.KMutableProperty0


class AureliaSettingsConfigurable : Configurable {
    private lateinit var jsInjectionEnabledCheckbox: JCheckBox
    private lateinit var customAttributesEnabledCheckbox: JCheckBox
    private lateinit var customComponentsEnabledCheckbox: JCheckBox
    private lateinit var checkPropertyBindableAnnotationCheckbox: JCheckBox
    private lateinit var settings: AureliaSettings


    private val settingsMap: Map<JCheckBox, KMutableProperty0<Boolean>> by lazy {
        mapOf(
            jsInjectionEnabledCheckbox to settings::jsInjectionEnabled,
            customAttributesEnabledCheckbox to settings::isCustomAttributesEnabled,
            customComponentsEnabledCheckbox to settings::isCustomComponentEnabled,
            checkPropertyBindableAnnotationCheckbox to settings::checkPropertyBindableAnnotation
        )
    }

    override fun createComponent(): JComponent {
        settings = AureliaSettings.getInstance()
        jsInjectionEnabledCheckbox = JCheckBox(AureliaStormBundle.get("settings.enableJsInjection"))
        customAttributesEnabledCheckbox = JCheckBox(AureliaStormBundle.get("settings.enableCustomAttributes"))
        customComponentsEnabledCheckbox = JCheckBox(AureliaStormBundle.get("settings.enableCustomComponents"))
        checkPropertyBindableAnnotationCheckbox = JCheckBox(AureliaStormBundle.get("settings.checkPropertyBindableAnnotation"))
        val builder: FormBuilder = FormBuilder.createFormBuilder()
        settingsMap.keys.forEach { builder.addComponent(it) }
        val panel = builder.panel
        val wrapper = JPanel(BorderLayout())
        wrapper.add(panel, BorderLayout.NORTH)
        return wrapper
    }

    override fun isModified(): Boolean {
        return settingsMap.any { (checkbox, property) -> checkbox.isSelected != property.get() }
    }

    override fun apply() {
        settingsMap.forEach { (checkbox, property) -> property.set(checkbox.isSelected) }
    }

    override fun reset() {
        settingsMap.forEach { (checkbox, property) -> checkbox.isSelected = property.get() }
    }

    override fun getDisplayName(): String {
        return "Aurelia Webstorm Plugin"
    }
}