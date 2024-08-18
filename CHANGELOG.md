# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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