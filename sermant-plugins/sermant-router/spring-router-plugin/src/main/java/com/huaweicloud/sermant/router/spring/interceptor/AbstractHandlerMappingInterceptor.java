/*
 * Copyright (C) 2022-2024 Sermant Authors. All rights reserved.
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
 */

package com.huaweicloud.sermant.router.spring.interceptor;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.agent.interceptor.AbstractInterceptor;
import com.huaweicloud.sermant.router.common.handler.Handler;
import com.huaweicloud.sermant.router.common.utils.ThreadLocalUtils;
import com.huaweicloud.sermant.router.spring.handler.AbstractMappingHandler;
import com.huaweicloud.sermant.router.spring.handler.LaneMappingHandler;
import com.huaweicloud.sermant.router.spring.handler.RouteMappingHandler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Webflux obtains the header interception point
 *
 * @author provenceee
 * @since 2022-10-10
 */
public class AbstractHandlerMappingInterceptor extends AbstractInterceptor {
    private static final String EXCEPT_CLASS_NAME
            = "org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping";

    private final List<AbstractMappingHandler> handlers;

    /**
     * Constructor
     */
    public AbstractHandlerMappingInterceptor() {
        handlers = new ArrayList<>();
        handlers.add(new LaneMappingHandler());
        handlers.add(new RouteMappingHandler());
        handlers.sort(Comparator.comparingInt(Handler::getOrder));
    }

    @Override
    public ExecuteContext before(ExecuteContext context) {
        if (shouldHandle(context)) {
            ThreadLocalUtils.removeRequestTag();
            ServerWebExchange exchange = (ServerWebExchange) context.getArguments()[0];
            ServerHttpRequest request = exchange.getRequest();
            HttpHeaders headers = request.getHeaders();
            String path = request.getURI().getPath();
            String methodName = request.getMethod().name();
            Map<String, List<String>> queryParams = request.getQueryParams();
            handlers.forEach(handler -> ThreadLocalUtils
                    .addRequestTag(handler.getRequestTag(path, methodName, headers, queryParams)));
        }
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) {
        // Reactive programming cannot be deleted in the after method, otherwise thread variables cannot be
        // transparently transmitted to the load balancer thread
        // Will be deleted in HttpServerHandleInterceptor, ReactiveTypeHandlerInterceptor
        return context;
    }

    @Override
    public ExecuteContext onThrow(ExecuteContext context) {
        if (shouldHandle(context)) {
            ThreadLocalUtils.removeRequestTag();
        }
        return context;
    }

    private boolean shouldHandle(ExecuteContext context) {
        Object[] arguments = context.getArguments();
        return arguments.length > 0 && arguments[0] instanceof ServerWebExchange
                && EXCEPT_CLASS_NAME.equals(context.getObject().getClass().getName());
    }
}