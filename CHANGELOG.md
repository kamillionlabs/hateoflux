# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.6.0]

### Added

* Added JavaDocs on numerous classes however, mainly on assemblers and UriExpander

### Changed

* Renamed all assembler methods from "toXyzWrapper" to "wrapInXyzWrapper"

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