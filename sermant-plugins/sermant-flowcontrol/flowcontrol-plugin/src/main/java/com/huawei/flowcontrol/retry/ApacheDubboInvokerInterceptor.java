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

package com.huawei.flowcontrol.retry;

import com.huawei.flowcontrol.common.config.CommonConst;
import com.huawei.flowcontrol.common.entity.DubboRequestEntity;
import com.huawei.flowcontrol.common.entity.RequestEntity.RequestType;
import com.huawei.flowcontrol.common.exception.InvokerWrapperException;
import com.huawei.flowcontrol.common.handler.retry.AbstractRetry;
import com.huawei.flowcontrol.common.handler.retry.Retry;
import com.huawei.flowcontrol.common.handler.retry.RetryContext;
import com.huawei.flowcontrol.common.util.ConvertUtils;
import com.huawei.flowcontrol.service.InterceptorSupporter;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker;
import org.apache.dubbo.rpc.service.GenericException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

/**
 * An enhanced class after the apache dubbo intercept, buried to define sentinel resources
 *
 * @author zhouss
 * @since 2022-02-11
 */
public class ApacheDubboInvokerInterceptor extends InterceptorSupporter {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private static final int LOADER_BALANCE_INDEX = 2;

    private final Retry retry = new ApacheDubboRetry();

    /**
     * blacklist, classes in this list are not blocked
     */
    private final List<String> backList = Collections
            .singletonList("org.apache.dubbo.rpc.cluster.support.registry.ZoneAwareClusterInvoker");

    /**
     * Convert apache dubbo. Note that this method is not extractable，Because host dependencies can only be loaded by
     * this interceptor, pulling out results in classes not being found.
     *
     * @param invocation invocation
     * @return DubboRequestEntity
     */
    private DubboRequestEntity convertToApacheDubboEntity(Invocation invocation) {
        String interfaceName = invocation.getInvoker().getInterface().getName();
        String methodName = invocation.getMethodName();
        String version = invocation.getAttachment(ConvertUtils.DUBBO_ATTACHMENT_VERSION);
        if (ConvertUtils.isGenericService(interfaceName, methodName)) {
            // For generalized interfaces, you can obtain the actual interface and version name from the url,
            // The method name is obtained based on parameters and is the first parameter of the requested method
            final URL url = invocation.getInvoker().getUrl();
            interfaceName = url.getParameter(CommonConst.GENERIC_INTERFACE_KEY, interfaceName);
            final Object[] arguments = invocation.getArguments();
            if (arguments != null && arguments.length > 0 && arguments[0] instanceof String) {
                methodName = (String) invocation.getArguments()[0];
            }
            version = url.getParameter(CommonConst.URL_VERSION_KEY, version);
        }

        // High version using API invocation.getTargetServiceUniqueName access path，
        // versions and interfaces are used here to achieve the same end result
        String apiPath = ConvertUtils.buildApiPath(interfaceName, version, methodName);
        return new DubboRequestEntity(apiPath, Collections.unmodifiableMap(invocation.getAttachments()),
                RequestType.CLIENT,
                invocation.getInvoker().getUrl().getParameter(CommonConst.DUBBO_REMOTE_APPLICATION));
    }

    /**
     * The code repeated here is the same as{@link com.huawei.flowcontrol.retry.AlibabaDubboInvokerInterceptor}
     * <H2>Cannot be withdrawn</H2>
     * Because the permissions of the two framework classes are named differently, the host class can be loaded only if
     * the current interceptor
     *
     * @param obj enhanced object
     * @param allArguments method parameter
     * @param ret response result
     * @param isNeedThrow whether to throw an exception
     * @param isRetry need to retry
     * @return methodInvoker
     */
    private Object invokeRetryMethod(Object obj, Object[] allArguments, Object ret, boolean isNeedThrow,
            boolean isRetry) {
        try {
            if (obj instanceof AbstractClusterInvoker) {
                final Invocation invocation = (Invocation) allArguments[0];
                final List<Invoker<?>> invokers = (List<Invoker<?>>) allArguments[1];
                final Optional<Method> checkInvokersOption = getMethodCheckInvokers();
                final Optional<Method> selectOption = getMethodSelect();
                if (!checkInvokersOption.isPresent() || !selectOption.isPresent()) {
                    LOGGER.warning(String.format(Locale.ENGLISH, "It does not support retry for class %s",
                            obj.getClass().getCanonicalName()));
                    return ret;
                }
                if (isRetry) {
                    invocation.getAttachments().put(RETRY_KEY, RETRY_VALUE);
                }

                // check invokers
                checkInvokersOption.get().invoke(obj, invokers, invocation);
                LoadBalance loadBalance = (LoadBalance) allArguments[LOADER_BALANCE_INDEX];

                // select invoker
                final Invoker<?> invoke = (Invoker<?>) selectOption.get()
                        .invoke(obj, loadBalance, invocation, invokers, null);

                // execute call
                final Result result = invoke.invoke(invocation);
                if (result.hasException() && isNeedThrow) {
                    final Throwable exception = result.getException();
                    if (exception instanceof GenericException) {
                        throw (GenericException) exception;
                    } else if (exception instanceof com.alibaba.dubbo.rpc.service.GenericException) {
                        throw (com.alibaba.dubbo.rpc.service.GenericException) exception;
                    } else {
                        throw new InvokerWrapperException(result.getException());
                    }
                }
                return result;
            }
        } catch (IllegalAccessException ex) {
            LOGGER.warning("No such Method ! " + ex.getMessage());
        } catch (InvocationTargetException ex) {
            throw new InvokerWrapperException(ex.getTargetException());
        }
        return ret;
    }

    private Optional<Method> getMethodSelect() {
        return getInvokerMethod("select", func -> {
            try {
                final Method method = AbstractClusterInvoker.class
                        .getDeclaredMethod("select", LoadBalance.class, Invocation.class, List.class, List.class);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException ex) {
                LOGGER.warning("No such Method select! " + ex.getMessage());
            }
            return placeHolderMethod;
        });
    }

    private Optional<Method> getMethodCheckInvokers() {
        return getInvokerMethod("checkInvokers", func -> {
            try {
                final Method method = AbstractClusterInvoker.class
                        .getDeclaredMethod("checkInvokers", List.class, Invocation.class);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException ex) {
                LOGGER.warning("No such Method checkInvokers! " + ex.getMessage());
            }
            return placeHolderMethod;
        });
    }

    @Override
    protected boolean canInvoke(ExecuteContext context) {
        return super.canInvoke(context) && !backList.contains(context.getObject().getClass().getName());
    }

    @Override
    protected final ExecuteContext doBefore(ExecuteContext context) {
        context.skip(null);
        return context;
    }

    @Override
    protected final ExecuteContext doAfter(ExecuteContext context) {
        Object result = context.getResult();
        final Object[] allArguments = context.getArguments();
        final Invocation invocation = (Invocation) allArguments[0];
        try {
            // callHostMethod
            RetryContext.INSTANCE.markRetry(retry);
            result = invokeRetryMethod(context.getObject(), allArguments, result, false, false);
            final List<io.github.resilience4j.retry.Retry> handlers = getRetryHandler()
                    .getHandlers(convertToApacheDubboEntity(invocation));
            if (!handlers.isEmpty() && needRetry(handlers.get(0), result, ((AsyncRpcResult) result).getException())) {
                RetryContext.INSTANCE.markRetry(retry);
                result = handlers.get(0)
                        .executeCheckedSupplier(() -> invokeRetryMethod(context.getObject(), allArguments,
                                context.getResult(), true, true));
                invocation.getAttachments().remove(RETRY_KEY);
            }
        } catch (Throwable throwable) {
            result = buildErrorResponse(throwable, invocation);
        } finally {
            RetryContext.INSTANCE.remove();
        }
        context.changeResult(result);
        return context;
    }

    private Object buildErrorResponse(Throwable throwable, Invocation invocation) {
        Object result;
        Throwable realException = throwable;
        if (throwable instanceof InvokerWrapperException) {
            InvokerWrapperException exception = (InvokerWrapperException) throwable;
            realException = exception.getRealException();
        }
        result = AsyncRpcResult.newDefaultAsyncResult(realException, invocation);
        LOGGER.warning(String.format(Locale.ENGLISH, "Invoking method [%s] failed, reason : %s",
                invocation.getMethodName(), realException.getMessage()));
        return result;
    }

    /**
     * apache retry
     *
     * @since 2022-02-23
     */
    public static class ApacheDubboRetry extends AbstractRetry {
        @Override
        public boolean needRetry(Set<String> statusList, Object result) {
            // dubbo does not support status codes
            return false;
        }

        @Override
        public Class<? extends Throwable>[] retryExceptions() {
            return getRetryExceptions();
        }

        @Override
        public RetryFramework retryType() {
            return RetryFramework.APACHE_DUBBO;
        }
    }
}
