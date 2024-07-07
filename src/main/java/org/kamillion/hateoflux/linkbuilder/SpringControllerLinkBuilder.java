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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Younes El Ouarti
 */
public class SpringControllerLinkBuilder {

    public static <T> Link linkTo(Class<T> controllerClass, ControllerMethodReference<T> methodRef) {

        assertClassIsCorrectlyAnnotated(controllerClass);
        String basePath = extractControllerBasePath(controllerClass);

        if (methodRef == null) {
            return Link.linkAsSelfOf(basePath);
        }

        final MethodCaptureInterceptor interceptor = new MethodCaptureInterceptor();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(controllerClass);
        enhancer.setCallback(interceptor);
        T proxy = (T) enhancer.create();

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
        Map<String, Object> pathVariables = new HashMap<>();
        Map<String, Object> queryParameters = new HashMap<>();
        Parameter[] parameters = interceptor.getCapturedMethod().getParameters();
        Object[] parameterValues = interceptor.getCapturedArguments();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object parameterValue = parameterValues[i];
            if (parameterValue == null) {
                continue;
            }
            if (parameter.isAnnotationPresent(PathVariable.class)) {
                String parameterName = Optional.ofNullable(parameter.getAnnotation(PathVariable.class)) //
                        .map(PathVariable::value) //
                        .filter(n -> !n.isEmpty()) //
                        .orElse(parameter.getName());
                pathVariables.put(parameterName, parameterValue);
            }
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                String parameterName = Optional.ofNullable(parameter.getAnnotation(RequestParam.class)) //
                        .map(RequestParam::value) //
                        .filter(n -> !n.isEmpty()) //
                        .orElse(parameter.getName());
                queryParameters.put(parameterName, parameterValue);
            }

        }
        fullPath = UriExpander.expand(fullPath, pathVariables);
        fullPath = appendQueryParams(fullPath, queryParameters);

        return fullPath;
    }

    private static String getNameFromAnnotation(Parameter parameter, Annotation annotation) {
        return Optional.ofNullable(parameter.getAnnotation(PathVariable.class))//
                .map(PathVariable::value)//
                .orElse(parameter.getName());
    }

    /**
     * Appends query parameters to a given URI based on a map of arguments.
     *
     * @param uriToAppendTo
     *         The base URI to which query parameters will be appended.
     * @param args
     *         The map containing query parameters and their values.
     * @return A URI string with appended query parameters.
     */
    public static String appendQueryParams(String uriToAppendTo, Map<String, Object> args) {
        if (args == null || args.isEmpty()) {
            return uriToAppendTo;
        }

        boolean hasParams = uriToAppendTo.contains("?");
        StringJoiner joiner = new StringJoiner("&", hasParams ? "&" : "?", "");

        for (var entry : args.entrySet()) {
            // Encode keys and values to ensure they are URL safe
            String key = encode(entry.getKey(), UTF_8);
            String value = encode(entry.getValue().toString(), UTF_8);
            joiner.add(key + "=" + value);
        }
        return uriToAppendTo + joiner;
    }

    public static <T> Link linkTo(Class<T> controllerClass) {
        return linkTo(controllerClass, null);
    }

    private static <T> void assertClassIsCorrectlyAnnotated(final Class<T> controllerClass) {

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
