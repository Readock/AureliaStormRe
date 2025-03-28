# Changelog

## [Unreleased]

### 1.4.0

#### Features

* Custom component recognition
    * Gets resolved over <require from=""> tags
    * Without require a ts with the same names component gets used
    * Checks for matching @customElement and class names
* Custom component bindable property recognition (by @bindable)
* Suppress <template> and <require> element warnings

#### Improved

* Aurelia detection with large projects

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
