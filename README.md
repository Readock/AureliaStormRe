<!-- Plugin description -->

# AureliaStorm

![Build](https://github.com/%REPOSITORY%/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)

**This plugin brings support for [Aurelia](http://aurelia.io) framework to
the [IntelliJ platform](https://www.jetbrains.com/products.html?fromMenu#lang=js&type=ide).**

### Features:

* Custom element recognition (resolved using `@customElement` annotation)
    * Custom element property recognition (`@bindable` annotation)
* require and import tag reference detection and navigation (using ctrl + click)
* Detecting bindable HTML attributes and events (such as `class.bind` or `click.delegate`)
* Code insight for `${}` and binding attributes
    * Controller properties completion and navigation
    * Has to be enabled in the plugin settings
    * Does not support `repeat.for` and other aurelia features (enabling can lead to code warnings)
* New project generation via aurelia-cli
* Supports Aurelia 1 and 2

Either `aurelia` (v2), or `aurelia-cli` (v1) must be present in the project npm dependencies

### Limitations:

*

<!-- Plugin description end -->

## Contributing

### Prerequisites

* JDK version 17 or later suggested

### Running the plugin

In order to test your plugin use the [runIde](https://plugins.jetbrains.com/docs/intellij/configuring-plugin-project.html#run-ide-task)
Gradle. This will launch an instance of intellij with the plugin loaded.

> this project is based on the [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)