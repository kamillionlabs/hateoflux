# hateoflux

![Version](.github/badges/version.svg)
![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/kamillionlabs/hateoflux/main.yml?branch=master)
![Coverage](.github/badges/jacoco.svg)

Lightweight HATEOAS library designed to work with Spring Webflux.

README is still WIP!

### Spring HATEOAS vs hateoflux

| Functionalities                                      | Spring HATEOAS                                                                | hateoflux                                                  |
|------------------------------------------------------|-------------------------------------------------------------------------------|------------------------------------------------------------|
| Representation Model                                 | ✅ `EntityModel`, `CollectionModel`, `PagedModel`                              | ✅ `HalResourceWrapper`, `HalListWrapper` (includes paging) |
| `linkTo()` on controller method                      | ✅  With `WebMvcLinkBuilder` for MVC <br/>and `WebFluxLinkBuilder` for WebFlux | ✅  With `SpringControllerLinkBuilder`                      |
| URI templates as links (query and path parameters)   | ✅                                                                             | ✅                                                          |
| Manual expansion of URIs (query and path parameters) | ✅                                                                             | ✅                                                          |
| Assemblers                                           | ✅                                                                             | ✅                                                          |
| Serialization                                        | ✅                                                                             | ✅                                                          |
| Deserialization                                      | ✅                                                                             | ❌ only designed for server to client communication         |
| Media Types                                          | ✅ various                                                                     | ❌ only `application/hal+json`                              |
| Affordance                                           | ✅                                                                             | ❌                                                          |
| Curie                                                | ✅                                                                             | ❌                                                          |