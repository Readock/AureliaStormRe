# Changelog

## [Unreleased]

### [2.0.1] - 2025-04-04

#### Fix

* Reference detection getting called outside of aurelia projects

### [2.0.0] - 2025-04-04

#### Features

* Custom component / attribute recognition
* References gets resolved over <require from=""> tags
* Without require a class with matching @customElement will be taken (also works with name suffix)
* Developer Note: Having custom elements with the same name might not recognize correctly in some instances
* Require and import tag reference detection and navigation
* Detecting bindable HTML attributes and events
* Require and import tag support
* Custom component bindable property recognition (by @bindable)
* Suppress <template> and <require> element warnings

### [1.2.1]

* Updated since build to support older versions

### [1.2.0]

#### Features

* Support <let> element recognition
* Support the `else` attribute
* Adds support for `promise.bind` recognition

#### Fixes

* Remove deprecated getDependencies Call
* Remove deprecated getBaseDir call
