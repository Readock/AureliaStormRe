# AureliaStorm Re

![Build](https://github.com/%REPOSITORY%/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)

<!-- Plugin description -->

> _This plugin brings improved support for [Aurelia Framework](https://aurelia.io) to
the [IntelliJ platform](https://www.jetbrains.com/products.html?fromMenu#lang=js&type=ide)_

## Features:

#### Custom elements & attributes

* Declaration resolving using `@customElement` and `@customAttribute` annotation or class names
* Custom element property recognition (`@bindable` annotation)
* Require and import tag reference detection for typescript files
* Component and property navigation
* Detecting bindable HTML attributes and events (such as `class.bind` or `click.delegate`)

#### Code injection (experimental)

* Has to be enabled in the plugin settings (enabling can lead to code warnings)
* Code insight for `${}` and binding attributes
* Controller properties completion and navigation

Either `aurelia` (v2), or `aurelia-cli` (v1) must be present in the project npm dependencies

---

_originally a fork of [Aurelia Storm Community](https://github.com/CollinHerber/AureliaStorm)_

<!-- Plugin description end -->

## Contributing

### Prerequisites

* JDK version 17 or later suggested

### Running the plugin

In order to test your plugin use the [runIde](https://plugins.jetbrains.com/docs/intellij/configuring-plugin-project.html#run-ide-task)
Gradle. This will launch an instance of intellij with the plugin loaded.

> this project is based on the [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)