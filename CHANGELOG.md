# Changelog

## [Unreleased]

### 2.0.0

#### Features

* Custom component / attribute recognition
    * Gets resolved over <require from=""> tags
    * Without require a class with matching @customElement will be taken (also works with name suffix)
    * Having custom elements with the same name might not recognize correctly
* require and import tag reference detection and navigation
* Detecting bindable HTML attributes and events
* require and import tag support
* Custom component bindable property recognition (by @bindable)
* Suppress <template> and <require> element warnings

#### Improved

* Aurelia detection with larger projects

### 1.2.1

* Updated since build to support older versions

### 1.2.0

#### Features

* Support <let> element recognition
* Support the `else` attribute
* Adds support for `promise.bind` recognition

#### Fixes

* Remove deprecated getDependencies Call
* Remove deprecated getBaseDir call
