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

package com.huawei.flowcontrol.res4j.chain.handler;

import com.huawei.flowcontrol.common.entity.RequestEntity.RequestType;
import com.huawei.flowcontrol.res4j.chain.HandlerConstants;
import com.huawei.flowcontrol.res4j.chain.context.RequestContext;
import com.huawei.flowcontrol.res4j.exceptions.CircuitBreakerException;
import com.huawei.flowcontrol.res4j.exceptions.InstanceIsolationException;
import com.huawei.flowcontrol.res4j.handler.CircuitBreakerHandler;
import com.huawei.flowcontrol.res4j.handler.InstanceIsolationHandler;

import java.util.Set;

/**
 * Instance isolation. Instance isolation applies only to clients
 *
 * @author zhouss
 * @since 2022-07-05
 */
public class InstanceIsolationRequestHandler extends CircuitBreakerRequestHandler {
    private static final String CONTEXT_NAME = InstanceIsolationRequestHandler.class.getName();

    private static final String START_TIME = CONTEXT_NAME + "_START_TIME";

    @Override
    public void onBefore(RequestContext context, Set<String> businessNames) {
        try {
            super.onBefore(context, businessNames);
        } catch (CircuitBreakerException ex) {
            throw InstanceIsolationException.createException(ex.getCircuitBreaker());
        }
    }

    @Override
    protected RequestType direct() {
        return RequestType.CLIENT;
    }

    @Override
    protected CircuitBreakerHandler getHandler() {
        return new InstanceIsolationHandler();
    }

    @Override
    public int getOrder() {
        return HandlerConstants.INSTANCE_ISOLATION_ORDER;
    }

    @Override
    protected String getContextName() {
        return CONTEXT_NAME;
    }

    @Override
    protected String getStartTime() {
        return START_TIME;
    }
}
