# AureliaStorm Re Changelog

## [Unreleased]

## [2.0.1]

### Fixed

* Reference detection getting called for files outside of aurelia

## [2.0.0]

### Added

* Custom component / attribute recognition
* References gets resolved over <require from=""> tags
* Without require a class with matching @customElement will be taken (also works with name suffix)
* Developer Note: Having custom elements with the same name might not recognize correctly in some instances
* Require and import tag reference detection and navigation
* Detecting bindable HTML attributes and events
* Require and import tag support
* Custom component bindable property recognition (by @bindable)
* Suppress <template> and <require> element warnings

## [1.2.1]

### Changed

* Support older versions

## [1.2.0]

### Added

* Support <let> element recognition
* Support the `else` attribute
* Adds support for `promise.bind` recognition

### Fixed

* Remove deprecated getDependencies Call
* Remove deprecated getBaseDir call
