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
 * @since 05.06.2024
 */

package org.kamillion.hateoflux.linkbuilder;

import lombok.Getter;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author Younes El Ouarti
 */
@Getter
public class MethodCaptureInterceptor implements MethodInterceptor {

    private Method capturedMethod = null;
    private Object[] capturedArguments = null;


    @Override
    public Object intercept(final Object o, final Method method, final Object[] args, final MethodProxy methodProxy) {
        capturedMethod = method;
        capturedArguments = args;
        return null; // Interceptor is only used to capture method. Execution is not intended.
    }
}
