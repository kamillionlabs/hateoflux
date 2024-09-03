/*
 * Copyright (c)  2024 kamillion-suite contributors
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

package org.kamillion.hateoflux.linkbuilder;

import org.kamillion.hateoflux.model.link.Link;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author Younes El Ouarti
 */
public class SpringControllerLinkBuilder {

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
        enhancer.setCallback(interceptor);
        ControllerT proxy = (ControllerT) enhancer.create();

        // Invoke the method reference, which will capture the method details
        methodRef.invoke(proxy);
        final Method capturedMethod = interceptor.getCapturedMethod();

        if (capturedMethod == null) {
            throw new IllegalStateException("No method reference captured");
        }

        String methodPath = extractMethodPath(capturedMethod);
        String fullPath = basePath + methodPath;
        fullPath = expandTemplatedPath(fullPath, interceptor);

        return Link.linkAsSelfOf(fullPath);
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
                    builder.isCollection(true)
                            .values(collectionParameterValue.stream().map(String::valueOf).toList())
                            .isSpecifiedAsComposite(hasToBeExpandedAsComposite(parameter));
                } else {
                    builder.values(List.of(String.valueOf(parameterValue)))
                            .isCollection(false)
                            .isSpecifiedAsComposite(false);
                }
                queryParameters.add(builder.build());
            }
        }
        fullPath = UriExpander.expand(fullPath, variables);
        fullPath = appendQueryParams(fullPath, queryParameters);

        return fullPath;
    }

    private static boolean hasToBeExpandedAsComposite(Parameter parameter) {
        return false;
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

    public static <ControllerT> Link linkTo(Class<ControllerT> controllerClass) {
        return linkTo(controllerClass, null);
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
