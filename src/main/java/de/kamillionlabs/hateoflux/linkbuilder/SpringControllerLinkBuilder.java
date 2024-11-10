/*
 * Copyright (c)  2024 kamillionlabs contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @since 03.06.2024
 */

package de.kamillionlabs.hateoflux.linkbuilder;

import de.kamillionlabs.hateoflux.model.hal.Composite;
import de.kamillionlabs.hateoflux.model.link.Link;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.Factory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.objenesis.SpringObjenesis;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Provides functionality to build URI links pointing to Spring controllers. It is designed to work seamlessly with
 * various Spring annotations such as {@link RestController}, {@link GetMapping}, {@link PathVariable},
 * {@link RequestParam}, etc. This class aims to be a direct replacement for the {@code WebMvcLinkBuilder} i.e.
 * {@code WebFluxLinkBuilder} in Spring HATEOAS, offering a similar interface and usage.
 *
 * @author Younes El Ouarti
 */
public class SpringControllerLinkBuilder {

    private SpringControllerLinkBuilder() {
    }

    /**
     * Creates a {@link Link} object that represents a link to the resource(s) addressed by calling the API of
     * the specified controller class. This method uses the {@link ControllerMethodReference} to ensure type-safe
     * referencing of controller methods. The link is expanded using the parameters in the template and the parameters
     * with which the method reference was called.
     * <p>
     * The method distinguishes between {@link PathVariable} and {@link RequestParam} (i.e., query parameters). When
     * parameters of the latter type are used, collections are allowed. By default, collections are expanded in a
     * non-composite way (i.e., {@code var=1,2} as opposed to {@code var=1&var=2}). If the parameter in the controller
     * class is annotated with {@link Composite}, then {@code linkTo()} will adhere to it, i.e., render it in the
     * composite way.
     * <p>
     * <b>Example usage:</b><br>
     * <i>@ signs are prepended with "_" because of a javadocs bug where the @ the beginning of a
     * line is interpreted and thus messes up the code example</i>
     * <p>
     * Given the following class:
     * <blockquote><pre>
     * _@Controller;
     * _@RequestMapping("/user")
     * public class UserController {
     *
     *    _@GetMapping("/{userId}")
     *    public User getUser(@PathVariable userId){
     *    ...
     *    }
     *  }
     * </pre></blockquote>
     * <p>
     * When the {@code linkTo()} method is called as follows:
     * <blockquote><pre>
     * Link link = linkTo(UserController.class, c -> c.getUser("12345"));
     * </pre></blockquote>
     * <p>
     * The resulting link has then the href: {@code /user/12345}
     * <br>
     *
     * @param <ControllerT>
     *         the type of the controller
     * @param controllerClass
     *         the class of the controller containing the target method. This class must be
     *         annotated with {@link RestController} or {@link Controller} to be valid.
     * @param methodRef
     *         a functional interface implementation that references the controller method
     *         for which the link is to be generated.
     * @return an expanded {@link Link} object that encapsulates the URI pointing to the resource as exposed by the
     * controller method referenced
     *
     * @throws IllegalArgumentException
     *         if the controller class is not correctly annotated as a {@link Controller}
     *         or {@link RestController}, which is necessary for the correct functioning of the method reference.
     */
    public static <ControllerT> Link linkTo(Class<ControllerT> controllerClass,
                                            ControllerMethodReference<ControllerT> methodRef) {

        assertClassIsCorrectlyAnnotated(controllerClass);
        String basePath = extractControllerBasePath(controllerClass);

        if (methodRef == null) {
            return Link.linkAsSelfOf(basePath);
        }

        final MethodCaptureInterceptor interceptor = new MethodCaptureInterceptor();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(controllerClass);
        // Set the callback types instead of actual callbacks
        enhancer.setCallbackType(MethodInterceptor.class);

        // Create the proxy class without instantiating it
        Class<?> proxyClass = enhancer.createClass();

        SpringObjenesis objenesis = new SpringObjenesis();
        ControllerT proxy = (ControllerT) objenesis.newInstance(proxyClass);

        // Set the callback on the proxy instance
        ((Factory) proxy).setCallback(0, interceptor);

        // Invoke the method reference, which will capture the method details
        // "unused" var is necessary to mitigate IDE warning of "Value is never used as Publisher"
        Object unused = methodRef.invoke(proxy);
        final Method capturedMethod = interceptor.getCapturedMethod();

        if (capturedMethod == null) {
            throw new IllegalStateException("No method reference captured");
        }

        String methodPath = extractMethodPath(capturedMethod);
        String fullPath = basePath + methodPath;
        fullPath = expandTemplatedPath(fullPath, interceptor);

        return Link.linkAsSelfOf(fullPath);
    }

    /**
     * Variation of the {@link #linkTo(Class, ControllerMethodReference)} method. Please refer to the mentioned method
     * for full documentation and usage examples. This method calls the aforementioned method with
     * {@code methodRef=null}.
     *
     * @param <ControllerT>
     *         the type of the controller
     * @param controllerClass
     *         the class of the controller containing the target method. This class must be
     *         annotated with {@link RestController} or {@link Controller} to be valid.
     * @return a {@link Link} object that encapsulates the URI pointing to the resource as exposed by the
     * controller method referenced
     */
    public static <ControllerT> Link linkTo(Class<ControllerT> controllerClass) {
        return linkTo(controllerClass, null);
    }

    private static String expandTemplatedPath(String fullPath, MethodCaptureInterceptor interceptor) {
        Map<String, Object> variables = new HashMap<>();
        List<QueryParameter> queryParameters = new ArrayList<>();
        Parameter[] parameters = interceptor.getCapturedMethod().getParameters();
        Object[] parameterValues = interceptor.getCapturedArguments();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object parameterValue = parameterValues[i];
            if (parameterValue == null
                    || (parameterValue instanceof Collection
                    && ((Collection<?>) parameterValue).isEmpty())) {
                continue;
            }
            if (parameter.isAnnotationPresent(PathVariable.class)) {
                String parameterName = Optional.ofNullable(parameter.getAnnotation(PathVariable.class)) //
                        .map(PathVariable::value) //
                        .filter(n -> !n.isEmpty()) //
                        .orElse(parameter.getName());
                variables.put(parameterName, parameterValue);
            }
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                String parameterName = Optional.ofNullable(parameter.getAnnotation(RequestParam.class)) //
                        .map(RequestParam::value) //
                        .filter(n -> !n.isEmpty()) //
                        .orElse(parameter.getName());

                var builder = QueryParameter.builder()
                        .name(parameterName);

                if (parameterValue instanceof Collection<?> collectionParameterValue) {
                    builder.listOfValues(collectionParameterValue, hasToBeExpandedAsComposite(parameter));
                } else {
                    builder.value(parameterValue);
                }
                queryParameters.add(builder.build());
            }
        }
        fullPath = UriExpander.expand(fullPath, variables);
        fullPath = appendQueryParams(fullPath, queryParameters);

        return fullPath;
    }

    private static boolean hasToBeExpandedAsComposite(Parameter parameter) {
        return Optional.ofNullable(parameter.getAnnotation(Composite.class))
                .isPresent(); //NonComposite is default
    }

    /**
     * Appends query parameters to a given URI based on a map of arguments.
     *
     * @param uriToAppendTo
     *         base URI to which query parameters will be appended.
     * @param queryParameters
     *         list containing query parameters and their values.
     * @return A URI string with appended query parameters.
     */
    private static String appendQueryParams(String uriToAppendTo, List<QueryParameter> queryParameters) {
        if (queryParameters == null || queryParameters.isEmpty()) {
            return uriToAppendTo;
        }
        String expandedQueryParameterUriPart = UriExpander.constructExpandedQueryParameterUriPart(queryParameters);
        return uriToAppendTo + expandedQueryParameterUriPart;
    }

    private static <ControllerT> void assertClassIsCorrectlyAnnotated(final Class<ControllerT> controllerClass) {

        Assert.notNull(controllerClass, "Controller class must not be null!");
        final boolean isControllerClass = controllerClass.isAnnotationPresent(Controller.class) //
                || controllerClass.isAnnotationPresent(RestController.class);

        Assert.isTrue(isControllerClass, "Controller must be annotated as such, either with @Controller or " +
                "@RestController!");
    }

    private static String extractControllerBasePath(Class<?> controllerClass) {
        return Optional.ofNullable(controllerClass.getAnnotation(RequestMapping.class)) //
                .flatMap(a -> Arrays.stream(a.value()).findFirst())
                .orElse("");
    }

    private static String extractMethodPath(Method method) {
        return Optional.ofNullable(method.getAnnotation(RequestMapping.class)) //
                .flatMap(a -> Arrays.stream(a.value()).findFirst()) //
                .or(() -> Optional.ofNullable(method.getAnnotation(GetMapping.class))//
                        .flatMap(a -> Arrays.stream(a.value()).findFirst())) //
                .or(() -> Optional.ofNullable(method.getAnnotation(PostMapping.class))//
                        .flatMap(a -> Arrays.stream(a.value()).findFirst())) //
                .or(() -> Optional.ofNullable(method.getAnnotation(PutMapping.class))//
                        .flatMap(a -> Arrays.stream(a.value()).findFirst())) //
                .or(() -> Optional.ofNullable(method.getAnnotation(DeleteMapping.class))//
                        .flatMap(a -> Arrays.stream(a.value()).findFirst())) //
                .or(() -> Optional.ofNullable(method.getAnnotation(PatchMapping.class))//
                        .flatMap(a -> Arrays.stream(a.value()).findFirst())) //
                .orElse("");
    }
}
