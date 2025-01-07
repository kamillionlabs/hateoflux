<picture>
  <source media="(prefers-color-scheme: dark)" srcset="./readme_resources/hateoflux_white.svg">
  <source media="(prefers-color-scheme: light)" srcset="./readme_resources/hateoflux_black.svg">
  <img alt="hateoflux logo" src="./readme_resources/hateoflux_black.svg">
</picture>

Lightweight HATEOAS library designed for Spring WebFlux.
___

![Version](.github/badges/development-version.svg)
![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/kamillionlabs/hateoflux/main.yml?branch=master)
![Coverage](.github/badges/jacoco.svg)

![Maven Central](https://img.shields.io/maven-central/v/de.kamillionlabs/hateoflux)
![License](https://img.shields.io/github/license/kamillionlabs/hateoflux)
![Java](https://img.shields.io/badge/Java-17%2B-blue)


hateoflux is a lightweight, reactive-first Java library designed to streamline the creation of hypermedia-driven APIs in Spring WebFlux applications. It addresses the limitations of Spring HATEOAS in reactive environments, offering a more intuitive and maintainable approach to building HAL+JSON compliant APIs.

<br>
<p align=center>
<a href="https://github.com/kamillionlabs/hateoflux#getting-started">Getting Started</a> | 
<a href="https://hateoflux.kamillionlabs.de/docs/cookbook.html">Cookbook</a> | 
<a href="https://hateoflux.kamillionlabs.de">Documentation</a> | 
<a href="https://github.com/kamillionlabs/hateoflux-demos">Demos</a>
</p>


## Table of Contents

- [Why hateoflux?](#why-hateoflux)
- [Features](#features)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Adding to Your Project](#adding-to-your-project)
- [Basic Usage](#basic-usage)
  - [Creating a HalResourceWrapper](#creating-a-halresourcewrapper)
  - [Response Types](#response-types)
- [Advanced Usage](#advanced-usage)
  - [Assemblers](#assemblers)
  - [Link Building](#link-building)
- [Examples & Use Cases](#examples--use-cases)
- [Documentation](#documentation)
- [Comparison with Spring HATEOAS](#comparison-with-spring-hateoas)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## Why hateoflux?

Building hypermedia-driven APIs in reactive Spring applications using WebFlux can be challenging with traditional libraries like Spring HATEOAS, which are primarily designed for Spring MVC. hateoflux offers a reactive-first solution tailored specifically for Spring WebFlux, simplifying hypermedia API development by:

- **Keeping Domain Models Clean:** Uses resource wrappers to decouple domain models from hypermedia concerns.
- **Reducing Boilerplate:** Simplifies assemblers and automates link creation.
- **Enhancing Pagination Handling:** Provides built-in support for pagination with `HalListWrapper`.
- **Focused Documentation:** Offers comprehensive guidance and examples for reactive environments.

## Features

- **Resource Wrappers:** `HalResourceWrapper` and `HalListWrapper` to encapsulate resources and collections.
- **Type-Safe Link Building:** Easily create and manage hypermedia links.
- **Specialized Response Types:** Purpose-built reactive response handling with `HalResourceResponse`, `HalMultiResourceResponse`, and `HalListResponse`.
- **Pagination Support:** Simplified pagination with metadata and navigation links.
- **URI Template Support:** Define dynamic URLs with placeholders.
- **Seamless Spring Integration:** Works effortlessly with existing Spring configurations and annotations.
- **Assembler Interfaces:** Reduce boilerplate with `FlatHalWrapperAssembler` and `EmbeddingHalWrapperAssembler`.

## Getting Started

### Prerequisites

Before integrating hateoflux into your project, ensure that you have the following installed:

- **Java 17 or higher:** Ensure that your development environment is set up with Java 17+.
- **Gradle 8.5 or higher:** Required for building and managing project dependencies.
- **Spring Boot 3.0.0 or higher:** hateoflux is compatible with Spring Boot 3.0.0+ for seamless integration.

### Installation

To include hateoflux in your Spring WebFlux project, add it as a dependency using your preferred build tool.

### Adding to Your Project
> [!TIP]
> Check the latest available version on [Maven Central](https://central.sonatype.com/artifact/de.kamillionlabs/hateoflux).

#### Maven

```xml
<dependency>
    <groupId>de.kamillionlabs</groupId>
    <artifactId>hateoflux</artifactId>
    <version>latest-version</version>
</dependency>
```

#### Gradle
```groovy
dependencies {
    implementation 'de.kamillionlabs:hateoflux:latest-version'
}
```
## Basic Usage
### Creating a HalResourceWrapper
Here's a simple example of how to create a `HalResourceWrapper` for an `OrderDTO` without any embedded resources.
```java
@GetMapping("/order-no-embedded/{orderId}")
public Mono<HalResourceWrapper<OrderDTO, Void>> getOrder(@PathVariable int orderId) {
    
    Mono<OrderDTO> orderMono = orderService.getOrder(orderId);
    return orderMono.map(order -> HalResourceWrapper.wrap(order)
            .withLinks(
                    Link.of("orders/{orderId}/shipment")
                            .expand(orderId)
                            .withRel("shipment"),
                    Link.linkAsSelfOf("orders/" + orderId)
            ));
}
```
**Serialized Output**
```json
{
   "id": 1234,
   "userId": 37,
   "total": 99.99,
   "status": "Processing",
   "_links": {
      "shipment": {
         "href": "orders/1234/shipment"
      },
      "self": {
         "href": "orders/1234"
      }
   }
}
```
### Response Types
hateoflux provides specialized response types (essentially reactive `ResponseEntity`s) to handle different resource scenarios in reactive applications. Here's the previous controller example modified to return a reactive HTTP response while preserving the same body:

```java
@GetMapping("/order-no-embedded/{orderId}")
public HalResourceResponse<OrderDTO, Void> getOrder(@PathVariable String orderId) {
    
        Mono<HalResourceWrapper<OrderDTO, Void>> order = orderService.getOrder(orderId)
            .map(order -> HalResourceWrapper.wrap(order)
                .withLinks(
                        Link.of("orders/{orderId}/shipment")
                                .expand(orderId)
                                .withRel("shipment"),
                        Link.linkAsSelfOf("orders/" + orderId)
                ));
        
    return HalResourceResponse.ok(order)
        .withContentType(MediaType.APPLICATION_JSON)
        .withHeader("Custom-Header", "value");
}
```
The library provides three response types for different scenarios:

* `HalResourceResponse`: For single HAL resources (shown above)
* `HalMultiResourceResponse`: For streaming multiple resources individually
* `HalListResponse`: For collections as a single HAL document, including pagination

## Advanced Usage
### Assemblers
Assemblers in hateoflux reduce boilerplate by handling the wrapping and linking logic. Implement either `FlatHalWrapperAssembler` for resources without embedded entities or `EmbeddingHalWrapperAssembler` for resources with embedded entities.
```java
@Component
public class OrderAssembler implements EmbeddingHalWrapperAssembler<OrderDTO, ShipmentDTO> {

    @Override
    public Class<OrderDTO> getResourceTClass() {
        return OrderDTO.class;
    }

    @Override
    public Class<ShipmentDTO> getEmbeddedTClass() {
        return ShipmentDTO.class;
    }

    @Override
    public Link buildSelfLinkForResource(OrderDTO order, ServerWebExchange exchange) {
        return Link.of("order/" + order.getId())
                   .prependBaseUrl(exchange);
    }

    @Override
    public Link buildSelfLinkForEmbedded(ShipmentDTO shipment, ServerWebExchange exchange) {
        return Link.of("shipment/" + shipment.getId())
                   .prependBaseUrl(exchange)
                   .withHreflang("en-US");
    }

    @Override
    public Link buildSelfLinkForResourceList(ServerWebExchange exchange) {
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        return Link.of("order{?userId,someDifferentFilter}")
                   .expand(queryParams)
                   .prependBaseUrl(exchange);
    }
}
```
### Link Building
Leverage the `SpringControllerLinkBuilder` for type-safe, annotation-aware link creation.

```java
import static de.kamillionlabs.hateoflux.linkbuilder.SpringControllerLinkBuilder.linkTo;

Link userLink = linkTo(UserController.class, controller -> controller.getUser("12345"))
                .withRel(IanaRelation.SELF);
```

## Examples & Use Cases
### Demos
Explore practical examples and debug them in the [hateoflux-demos](https://github.com/kamillionlabs/hateoflux-demos) repository. Fork the repository and run the applications to see hateoflux in action.
### Cookbook
Refer to the [Cookbook: Examples & Use Cases](https://hateoflux.kamillionlabs.de/cookbook/cookbook.html) for detailed and explained scenarios and code snippets demonstrating various functionalities of hateoflux.

## Documentation
Comprehensive documentation is available at [https://hateoflux.kamillionlabs.de (english)](https://hateoflux.kamillionlabs.de), covering:
- [What is hateoflux?](https://hateoflux.kamillionlabs.de/)
- [Representation Model](https://hateoflux.kamillionlabs.de/docs/core-concepts/representation-model.html)
- [Response Types](https://hateoflux.kamillionlabs.de/docs/core-concepts/response-handling.html)
- [Link Building](https://hateoflux.kamillionlabs.de/docs/core-concepts/linkbuilding.html)
- [Assemblers](https://hateoflux.kamillionlabs.de/docs/core-concepts/assemblers.html)
- [Spring HATEOAS vs. hateoflux](https://hateoflux.kamillionlabs.de/docs/spring-vs-hateoflux.html)
- [Cookbook: Examples & Use Cases](https://hateoflux.kamillionlabs.de/docs/cookbook/)

## Comparison with Spring HATEOAS
hateoflux is specifically designed for reactive Spring WebFlux applications, offering a more streamlined and maintainable approach compared to Spring HATEOAS in reactive environments. Key differences include:

| **Aspect**                     | **Spring HATEOAS (WebFlux)**                                                                                             | **hateoflux**                                                                             |
|--------------------------------|--------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------|
| **Representation Models**      | Uses wrappers and inheritance-based models, requiring manual embedding of resources via inheritance or separate classes. | Uses wrappers exclusively to keep domain models clean and decoupled.                      |
| **Response Types**             | Uses standard `ResponseEntity` with manual reactive flow handling                                                        | Dedicated response types optimized for different resource scenarios                       |
| **Assemblers and Boilerplate** | Verbose with manual resource wrapping and link addition.                                                                 | Simplified with built-in methods; only links need to be specified in assemblers.          |
| **Pagination Handling**        | Limited support in reactive environments; requires manual implementation.                                                | Easy pagination with HalListWrapper; handles metadata and navigation links automatically. |
| **Documentation Support**      | Better for Spring MVC; less comprehensive for WebFlux.                                                                   | Tailored for reactive Spring WebFlux with focused documentation and examples.             |
| **Media Types**                | Supports multiple media types (HAL, Collection+JSON, etc.).                                                              | Only supports HAL+JSON for simplicity and performance.                                    |
| **Affordance & CURIE Support** | Supports affordances and CURIEs.                                                                                         | Does not support affordances or CURIEs.                                                   |

For a detailed comparison, refer to the [Spring HATEOAS vs. hateoflux](https://hateoflux.kamillionlabs.de/docs/spring-vs-hateoflux.html) documentation.

## Contributing
Contributions are welcome! Please follow these steps:

1. Fork the repository.
1. Create a new branch for your feature or bugfix.
1. Commit your changes with clear messages.
1. Submit a pull request detailing your changes.
1. For more details, see the [CONTRIBUTING.md](./CONTRIBUTING.md) file.

> [!NOTE]
> All pull requests are subject to code review. Please be patient and responsive to any feedback or requests for changes.

For more details, see the [CONTRIBUTING.md](./CONTRIBUTING.md) file.

## License

This project is licensed under the [Apache License 2.0](/LICENSE). The Apache 2.0 License allows you to freely use, modify, and distribute the software, provided that you include the original license and notices in any copies or substantial portions of the software.

## Contact

If you have any questions, suggestions, or need support, please feel free to open a [discussion](https://github.com/kamillionlabs/hateoflux/discussions), submit an [issue](https://github.com/kamillionlabs/hateoflux/issues), or email us directly at [contact@kamillionlabs.de](mailto:contact@kamillionlabs.de).

