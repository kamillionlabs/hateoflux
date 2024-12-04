# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.1]

### Changed
* HalResourecWrapper#isEmbeddedOriginallyAList is now private

### Fixed
* Single scalars (e.g. String, Integers, Boolean, etc.) are now not allowed in Wrappers and will result in an exception

## [1.0.0]

### Changed

* Final adjustments to building the library and README
* Change now to SNAPSHOT development versioning

### Fixed

* Revert to max 10 individual dependabot PRs

## [0.24.0]

### Changed

* Badges divided now into dev and release groups (just some shuffling)
* version now is development-version
* dependabot now only creates at most a single PR and groups all changes together

## [0.23.0]

### Added

* Configured dependabot
* Upgrade default Spring Boot version to 3.4.0

## [0.22.0]

### Added
* Added a comprehensive README.md
* Added a CONTRIBUTING.md
* Added a CODE_OF_CONDUCT.md
* Exported the code style and made available in the project

## [0.21.0]

### Added

* Utility functions to fluently create PairFlux and MultiRightPairFlux

### Fixed

* Fixed NPE when creating a HalResourceWrapper with a `null` embedded (which is allowed)

## [0.20.0]

### Added

* EmbeddingHalWrapperAssembler now also accepts a list of embedded when creating HalListWrappers
* New MultiRightPair utility class

### Changed

* Assemblers also accept MultiRightPairs instead
* Embedded now allowed to be empty without taking the type Void
* Consistent serialization of embedded resources as lists, empty lists or their removal from the json

## [0.19.0]

### Changed

* Refactored Assemblers. Only Flat- and EmbeddingHalWrapperAssembler remain. Both contain imperative and reactive
  functions

## [0.18.0]

### Changed

* Allow expansion of URL even though values for paging are missing (are handled automatically by assemblers)

### Fixed

* Correctly process MultiValueMaps in expand() methods
* Assemblers correctly build empty list wrappers if no list was provided

## [0.17.0]

### Added

* Add collector `PairListCollector` to collect a stream of `Pair`s

## [0.16.0]

### Changed

* Minor Readme changes

## [0.15.0]

### Added

* Link contains now a method to automatically generate navigation links from HalPageInfo

### Changed

* Upgraded default Spring Boot version
* Assemblers now also build navigation links when a paged list wrapper are created

### Fixed

* Fixed interpretation of HalPageInfo field "size"

## [0.14.0]

### Added

* Removed the TODO list in readme and added a comparison table

### Changed

* Better javadoc for Link attribute setters.

### Fixed

* Added fallback for relation resolving for when spring context isn't loaded

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