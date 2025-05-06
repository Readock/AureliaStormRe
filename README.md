# AureliaStorm Re

![Build](https://github.com/Readock/AureliaStormRe/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/27000-aureliastorm-re.svg)](https://plugins.jetbrains.com/plugin/27000-aureliastorm-re)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/27000-aureliastorm-re.svg)](https://plugins.jetbrains.com/plugin/27000-aureliastorm-re)

<!-- Plugin description -->

This plugin brings improved support for [Aurelia Framework](https://aurelia.io) to
the [IntelliJ platform](https://www.jetbrains.com/products.html?fromMenu#lang=js&type=ide) (_fork
of [AureliaStorm Community](https://github.com/CollinHerber/AureliaStorm)_)

### Custom elements & attributes

* Declaration resolving using `@customElement` and `@customAttribute` annotation or class names
* Custom element property recognition (`@bindable` annotation)
* Require and import tag reference detection for typescript files
* Component and property navigation (ctrl+click)
* Component lifecycle hooks (like `attached`, `detached`)
* Detecting bindable HTML attributes and events (such as `class.bind` or `click.delegate`)
* Custom element/attribute and property suggestions (ctrl+space)
* GoTo HTML/ts file action when in same folder (Default Alt+Ctrl+Shift+O)

### Insight for bindings and interpolation

* Has to be enabled in the **plugin settings** (enabling can lead to code warnings)
* Code insight for `${}` and binding attribute values
* Controller properties completion and navigation
* `$this`, `$parent`, `$index`, `$event` support

<br>

Either `aurelia`, `aurelia-cli` or `aurelia-framework` must be present in `package.json`


<!-- Plugin description end -->

## Contributing

You can run the plugin using the [Run Plugin](/.run/Run%20Plugin.run.xml) configuration. This will launch an instance of intellij with
the plugin loaded (for more information
check out the template).

> this project is based on the [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)