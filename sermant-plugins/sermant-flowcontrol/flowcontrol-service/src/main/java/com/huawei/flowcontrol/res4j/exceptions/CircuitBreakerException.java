/*
 * Copyright (C) 2022-2024 Sermant Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huawei.flowcontrol.res4j.exceptions;

import com.huawei.flowcontrol.res4j.adaptor.CircuitBreakerAdaptor;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import java.util.Locale;

/**
 * CircuitBreaker exception
 *
 * @author zhouss
 * @since 2022-08-09
 */
public class CircuitBreakerException extends RuntimeException {
    private final CircuitBreaker circuitBreaker;

    /**
     * CircuitBreaker exception
     *
     * @param circuitBreaker CircuitBreaker
     * @param message circuit breaker information
     * @param writableStackTrace stackOrNot
     */
    public CircuitBreakerException(CircuitBreaker circuitBreaker, String message, boolean writableStackTrace) {
        super(message, null, false, writableStackTrace);
        this.circuitBreaker = circuitBreaker;
    }

    /**
     * construct exception message
     *
     * @param circuitBreaker CircuitBreaker configuration
     * @return exception
     */
    public static CircuitBreakerException createException(CircuitBreaker circuitBreaker) {
        return new CircuitBreakerException(circuitBreaker, createMsg(circuitBreaker),
                circuitBreaker.getCircuitBreakerConfig().isWritableStackTraceEnabled());
    }

    /**
     * create prompt
     *
     * @param circuitBreaker CircuitBreaker
     * @return promptMessage
     */
    protected static String createMsg(CircuitBreaker circuitBreaker) {
        String msg;
        if (circuitBreaker instanceof CircuitBreakerAdaptor && ((CircuitBreakerAdaptor) circuitBreaker).isForceOpen()) {
            msg = String.format(Locale.ENGLISH, "CircuitBreaker '%s' has forced open and deny any requests",
                    circuitBreaker.getName());
        } else {
            msg = String.format(Locale.ENGLISH, "CircuitBreaker '%s' is %s and does not permit further calls",
                    circuitBreaker.getName(), circuitBreaker.getState());
        }
        return msg;
    }

    public CircuitBreaker getCircuitBreaker() {
        return circuitBreaker;
    }
}
