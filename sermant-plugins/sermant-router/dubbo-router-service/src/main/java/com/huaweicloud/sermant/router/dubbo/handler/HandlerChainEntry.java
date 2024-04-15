/*
 * Copyright (C) 2023-2024 Sermant Authors. All rights reserved.
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

package com.huaweicloud.sermant.router.dubbo.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * route handler chain entry
 *
 * @author lilai
 * @since 2023-02-24
 */
public enum HandlerChainEntry {
    /**
     * singleton
     */
    INSTANCE;

    private static final int HANDLER_SIZE = 4;

    private static final HandlerChain HANDLER_CHAIN = new HandlerChain();

    static {
        final List<AbstractRouteHandler> handlers = new ArrayList<>(HANDLER_SIZE);
        for (AbstractRouteHandler handler : ServiceLoader.load(AbstractRouteHandler.class,
                HandlerChainEntry.class.getClassLoader())) {
            handlers.add(handler);
        }
        Collections.sort(handlers);
        handlers.forEach(HANDLER_CHAIN::addLastHandler);
    }

    /**
     * invoke the route handler chain
     *
     * @param targetService target service
     * @param invokers invokers
     * @param invocation invocation
     * @param queryMap RegistryDirectory's queryMap
     * @param serviceInterface the name of the interface
     * @return invokers
     * @see org.apache.dubbo.registry.integration.RegistryDirectory
     * @see com.alibaba.dubbo.rpc.Invoker
     * @see org.apache.dubbo.rpc.Invocation
     */
    public Object process(String targetService, List<Object> invokers, Object invocation,
            Map<String, String> queryMap, String serviceInterface) {
        return HANDLER_CHAIN.handle(targetService, invokers, invocation, queryMap, serviceInterface);
    }
}
