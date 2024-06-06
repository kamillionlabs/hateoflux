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

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Younes El Ouarti
 */
public class SpringControllerLinkBuilder {

    public static <T> String linkTo(Class<T> controllerClass, ControllerMethodReference<T> methodRef) {

        assertClassIsCorrectlyAnnotated(controllerClass);

        final MethodCaptureInterceptor interceptor = new MethodCaptureInterceptor();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(controllerClass);
        enhancer.setCallback(interceptor);
        T proxy = (T) enhancer.create();

        // Invoke the method reference which will capture the method details
        methodRef.apply(proxy);
        final Method capturedMethod = interceptor.getCapturedMethod();

        if (capturedMethod == null) {
            throw new IllegalArgumentException("No method reference captured");
        }

        String basePath = getControllerBasePath(controllerClass);
        String methodPath = getMethodPath(capturedMethod);
        return basePath + methodPath;
    }

    private static <T> void assertClassIsCorrectlyAnnotated(final Class<T> controllerClass) {

        Assert.notNull(controllerClass, "Controller class must not be null!");
        final boolean isControllerClass = controllerClass.isAnnotationPresent(Controller.class) //
                || controllerClass.isAnnotationPresent(RestController.class);

        Assert.isTrue(isControllerClass, "Controller must be annotated as such, either with @Controller or @RestController!");
    }

    private static String getControllerBasePath(Class<?> controllerClass) {
        return Optional.ofNullable(controllerClass.getAnnotation(RequestMapping.class)) //
                .flatMap(a -> Arrays.stream(a.value()).findFirst())
                .orElse("");
    }

    private static String getMethodPath(Method method) {
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
