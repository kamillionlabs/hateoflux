# hateoflux (WIP!)

Lightweight HATEOAS library designed to work with Spring Webflux. This readme is currently just a todo list and will be
rewritten accordingly.

## Implemented

* linkTo()
    * Manual (incl. slash())
    * On Controller with method selection
    * Expand templated URIs
    * Build query parameters in URI from Controller
    * Uses custom names for @PathVariable and @RequestParam
    * "templated" attribute is set depending on whether href is a template
    * Link.of(template string with _query_ parameters)
* Representation model equivalents
    * EntityModel
    * CollectionModel
    * PagedModel
* Serialization
* Assemblers

## Backlog

* Javadocs on:
    * assembler
    * Link.expand() (from UriExpander)
* linkTo()
    * Build links with base URL
    * Query parameters (@RequestParam) for collections with distinction between with or without @NonComposite
* Forwarded header handling

## Currently out of scope

* Curie
* Affordance
* EntityLinks
* Representation Model Processors
* LinkRelationProvider
* Preconfigured default Link titles
* (Extended) Support for media types:
    * HAL-FORMS
    * Collection+Json
    * UBER - Uniform Basis for Exchanging Representations
    * ALPS - Application-Level Profile Semantics
* Client-side support (ideally not required as there should be out of the box support from Spring and other Libs)
* HalModelBuilder (Not necessary as Wrappers all come with a builder pattern)