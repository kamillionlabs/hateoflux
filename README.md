<picture>
  <source media="(prefers-color-scheme: dark)" srcset="./readme_resources/hateoflux_white.svg">
  <source media="(prefers-color-scheme: light)" srcset="./readme_resources/hateoflux_black.svg">
  <img alt="hateoflux logo" src="./readme_resources/hateoflux_black.svg">
</picture>

Lightweight HATEOAS library designed for Spring Webflux.

![Version](.github/badges/version.svg)
![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/kamillionlabs/hateoflux/main.yml?branch=master)
![Coverage](.github/badges/jacoco.svg)

README is still WIP!

### Spring HATEOAS vs hateoflux

| Functionalities                                      | Spring HATEOAS                                                                | hateoflux                                                                        |
|------------------------------------------------------|-------------------------------------------------------------------------------|----------------------------------------------------------------------------------|
| Representation Model                                 | ✅ `EntityModel`, `CollectionModel`, `PagedModel`                              | ✅ `HalResourceWrapper`, `HalListWrapper` (includes paging)                       |
| `linkTo()` on controller method                      | ✅  With `WebMvcLinkBuilder` for MVC <br/>and `WebFluxLinkBuilder` for WebFlux | ✅  With `SpringControllerLinkBuilder`                                            |
| URI templates as links (query and path parameters)   | ✅                                                                             | ✅                                                                                |
| Manual expansion of URIs (query and path parameters) | ✅                                                                             | ✅                                                                                |
| Assemblers                                           | ✅                                                                             | ✅                                                                                |
| Serialization                                        | ✅                                                                             | ✅                                                                                |
| Deserialization                                      | ✅                                                                             | ❌ no, only designed for server to client communication (i.e. serialization only) |
| Media Types                                          | ✅ various                                                                     | ⚠️ only `application/hal+json`                                                   |
| Affordance                                           | ✅                                                                             | ❌                                                                                |
| Curie                                                | ✅                                                                             | ❌                                                                                |