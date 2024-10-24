# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.14.0]

### Added

* Removed the TODO list in readme and added an actual one

### Changed

* Better javadoc for Link attribute setters.

## [0.13.0]

### Changed

* Changed wording from "entity" to "resource" when addressing objects that are part of/make out the hal-document

## [0.12.0]

### Changed

* hateoflux uses primarily Spring Boot version of consumer/client

### Fixed

* Fix warning in SpringControllerLinkBuilder ("Value is never used as Publisher")

## [0.11.0]

### Added

* Added utilities for base url
* Added local publishing to build.gradle

### Changed

* In assemblers all buildSelfLink skeleton methods assign the SELF relation per default

### Fixed

* Fix exception in SpringControllerLinkBuilder when controller had no default constructor

## [0.10.0]

### Added

* Added badges for master branch for:
    * build status
    * version

## [0.9.0]

### Added

* Added code coverage badge

## [0.8.0]

### Added

* Added CI pipeline for building, testing and local releasing (publishing is still done manually by maintainer)

## [0.7.0]

### Added

* Bundling lib logic for maven central
* Added missing javadocs

### Changed

* Organization renamed from "kamillion" to "kamillionlabs"

## [0.6.0]

### Added

* Added JavaDocs on numerous classes, notably on Assemblers, Wrappers and the UriExpander

### Changed

* Renamed all assembler methods from "toXyzWrapper" to "wrapInXyzWrapper"

### Fixed

* Some getters in the wrappers were giving the entities away as they are, instead of creating new lists (immutability)

## [0.5.0]

### Added

* Query parameters can now be collections
* @Composite can be used on Controller classes to render query parameter lists as a composite
  (e.g. ?var=1&var=2 as opposed to ?var=1,2)

### Changed

* Renamed Pairs to PairList

### Fixed

* Link#templated is now only read dynamically and not directly settable

## [0.4.0]

### Added

* Link#of now accepts URIs with templated query parameters

### Removed

* Removed Spring Boot org.springframework.boot:spring-boot-starter-webflux. Only actually used libraries are
  incorporated:
    * org.springframework:spring-webflux
    * org.springframework.boot:spring-boot-starter-json

## [0.3.0]

### Added

* Templating of error messages

### Changed

* Used consistent error message formats for null, empty or type assertions
* Consistent behaviour of getRequiredXyz() methods (all throw exception when value not found)

## [0.2.0]

### Added

* Introduction of Assemblers and utility classes Pair and Pairs

### Changed

* No dedicated "Paged" Wrapper. ListWrapper is now pageable
* Collections were removed in favor of Lists

## [0.1.0]

### Added

* Initial creation of the project
* Implementation of linkTo()
    * Manual (incl. slash())
    * On Controller with method selection
    * Expand templated URIs
    * Build query parameters in URI from Controller
    * Uses custom names for @PathVariable and @RequestParam
* Definition/Implementation of (Spring's) Representation model equivalents
    * EntityModel
    * CollectionModel
    * PagedModel
* Serialization