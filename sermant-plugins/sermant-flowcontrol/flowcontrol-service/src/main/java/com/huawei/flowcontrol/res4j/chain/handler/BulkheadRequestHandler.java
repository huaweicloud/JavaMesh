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

import com.huawei.flowcontrol.res4j.chain.HandlerConstants;
import com.huawei.flowcontrol.res4j.chain.context.ChainContext;
import com.huawei.flowcontrol.res4j.chain.context.RequestContext;
import com.huawei.flowcontrol.res4j.handler.BulkheadHandler;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadFullException;

import java.util.List;
import java.util.Set;

/**
 * isolation bin treatment
 *
 * @author zhouss
 * @since 2022-07-05
 */
public class BulkheadRequestHandler extends FlowControlHandler<Bulkhead> {
    private final BulkheadHandler bulkheadHandler = new BulkheadHandler();

    @Override
    public void onBefore(RequestContext context, Set<String> businessNames) {
        final List<Bulkhead> handlers = bulkheadHandler.createOrGetHandlers(businessNames);
        if (!handlers.isEmpty()) {
            context.save(getContextName(), handlers);
            handlers.forEach(Bulkhead::acquirePermission);
        }
        super.onBefore(context, businessNames);
    }

    @Override
    public void onThrow(RequestContext context, Set<String> businessNames, Throwable throwable) {
        super.onThrow(context, businessNames, throwable);
    }

    @Override
    public void onResult(RequestContext context, Set<String> businessNames, Object result) {
        try {
            final List<Bulkhead> bulkheads = getHandlersFromCache(context.getSourceName(), getContextName());
            if (bulkheads != null && !isOccurBulkheadLimit(context.getSourceName())) {
                bulkheads.forEach(Bulkhead::onComplete);
            }
        } finally {
            context.remove(getContextName());
        }
        super.onResult(context, businessNames, result);
    }

    /**
     * 是否触发隔离仓策略
     *
     * @param sourceName 线程变量的名称
     * @return 若触发隔离仓则无需释放资源
     */
    private boolean isOccurBulkheadLimit(String sourceName) {
        return ChainContext.getThreadLocalContext(sourceName)
                .get(HandlerConstants.OCCURRED_FLOW_EXCEPTION, Exception.class) instanceof BulkheadFullException;
    }

    @Override
    public int getOrder() {
        return HandlerConstants.BULK_HEAD_ORDER;
    }
}
