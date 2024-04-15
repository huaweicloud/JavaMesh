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

package com.huawei.flowcontrol.res4j.chain;

import com.huawei.flowcontrol.common.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * ChainBuilder, String all spi loaded handlers in order of priority
 *
 * @author zhouss
 * @since 2022-07-05
 */
public enum HandlerChainBuilder {
    /**
     * singleton
     */
    INSTANCE;

    private static final int HANDLER_SIZE = 4;

    private static final List<AbstractChainHandler> HANDLERS = new ArrayList<>(HANDLER_SIZE);

    static {
        for (AbstractChainHandler handler : ServiceLoader.load(AbstractChainHandler.class, HandlerChainBuilder.class
                .getClassLoader())) {
            HANDLERS.add(handler);
        }
    }

    /**
     * build chain
     *
     * @return ProcessorChain execution chain
     */
    public HandlerChain build() {
        final HandlerChain processorChain = new HandlerChain();
        Collections.sort(HANDLERS);
        HANDLERS.forEach(processorChain::addLastHandler);
        return processorChain;
    }

    /**
     * get handler chain
     *
     * @param name name
     * @return ChainHandler
     */
    public static Optional<AbstractChainHandler> getHandler(String name) {
        for (AbstractChainHandler abstractChainHandler : HANDLERS) {
            if (StringUtils.equal(abstractChainHandler.getClass().getName(), name)) {
                return Optional.of(abstractChainHandler);
            }
        }
        return Optional.empty();
    }
}
