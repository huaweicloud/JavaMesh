/*
 * Copyright (C) 2022-2024 Sermant Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huawei.flowcontrol.retry.handler;

import com.huawei.flowcontrol.common.core.rule.RetryRule;
import com.huawei.flowcontrol.common.exception.InvokerWrapperException;
import com.huawei.flowcontrol.common.handler.retry.Retry;

import com.huaweicloud.sermant.core.utils.ReflectUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * default exception predicate creator
 *
 * @author zhouss
 * @since 2022-04-11
 */
public class DefaultRetryPredicateCreator implements RetryPredicateCreator {
    private static final String ALIBABA_GENERIC_EXCEPTION = "com.alibaba.dubbo.rpc.service.GenericException";

    private static final String APACHE_GENERIC_EXCEPTION = "org.apache.dubbo.rpc.service.GenericException";

    /**
     * default retry status code
     */
    private static final Collection<String> DEFAULT_RETRY_ON_RESPONSE_STATUS = Arrays.asList("502", "503");

    /**
     * default retry exception
     */
    private static final List<Class<? extends Throwable>> STRICT_RETRYABLE = Collections.unmodifiableList(
            Arrays.asList(ConnectException.class, SocketTimeoutException.class, IOException.class,
                    NoRouteToHostException.class)
    );

    @Override
    public Predicate<Throwable> createExceptionPredicate(Class<? extends Throwable>[] retryExceptions) {
        final List<Class<? extends Throwable>> exceptions = new ArrayList<>(Arrays.asList(retryExceptions));
        exceptions.addAll(STRICT_RETRYABLE);
        return exceptions.stream().distinct().map(this::createExceptionPredicate).reduce(Predicate::or)
                .orElseGet(() -> throwable -> true);
    }

    private Predicate<Throwable> createExceptionPredicate(Class<? extends Throwable> retryClass) {
        return (Throwable ex) -> {
            if (retryClass.isAssignableFrom(getRealExceptionClass(ex))) {
                return true;
            }
            final Optional<String> realExceptionClassName = getRealExceptionClassName(ex);
            return realExceptionClassName.isPresent() && retryClass.getName().equals(realExceptionClassName.get());
        };
    }

    /**
     * handling of packaging exceptions
     * <p></p>
     * currently only GenericException is supported
     *
     * @param ex service exception
     * @return exception name
     */
    private Optional<String> getRealExceptionClassName(Throwable ex) {
        String mayBeRealClassName = null;
        if (isGenericException(ex.getClass().getName())) {
            final Optional<Object> getExceptionClass = ReflectUtils
                    .invokeMethod(ex, "getExceptionClass", null, null);
            if (getExceptionClass.isPresent()) {
                mayBeRealClassName = (String) getExceptionClass.get();
            }
        }
        return Optional.ofNullable(mayBeRealClassName);
    }

    private boolean isGenericException(String className) {
        return ALIBABA_GENERIC_EXCEPTION.equals(className) || APACHE_GENERIC_EXCEPTION.equals(className);
    }

    private Class<? extends Throwable> getRealExceptionClass(Throwable ex) {
        if (ex instanceof InvokerWrapperException) {
            // Determine whether it is an exception in the target wrapper
            InvokerWrapperException invokerWrapperException = (InvokerWrapperException) ex;
            if (invokerWrapperException.getRealException() != null) {
                return invokerWrapperException.getRealException().getClass();
            }
        }
        return ex.getClass();
    }

    @Override
    public Predicate<Object> createResultPredicate(Retry retry, RetryRule rule) {
        final List<String> retryOnResponseStatus = rule.getRetryOnResponseStatus();
        if (retryOnResponseStatus.isEmpty()) {
            retryOnResponseStatus.addAll(DEFAULT_RETRY_ON_RESPONSE_STATUS);
        }
        return result -> retry.needRetry(new HashSet<>(retryOnResponseStatus), result);
    }
}
