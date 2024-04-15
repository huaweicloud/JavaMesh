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

import com.huawei.flowcontrol.common.entity.FlowControlResult;
import com.huawei.flowcontrol.common.entity.RequestEntity;
import com.huawei.flowcontrol.res4j.chain.context.ChainContext;
import com.huawei.flowcontrol.res4j.chain.context.RequestContext;
import com.huawei.flowcontrol.res4j.util.FlowControlExceptionUtils;

import com.huaweicloud.sermant.core.common.LoggerFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * request chain entry class
 *
 * @author zhouss
 * @since 2022-07-11
 */
public enum HandlerChainEntry {
    /**
     * singleton
     */
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger();

    /**
     * HandlerChain
     */
    private final HandlerChain chain = HandlerChainBuilder.INSTANCE.build();

    /**
     * pre-method
     *
     * @param sourceName source name
     * @param requestEntity request body
     * @param flowControlResult flow control result
     */
    public void onBefore(String sourceName, RequestEntity requestEntity, FlowControlResult flowControlResult) {
        try {
            final RequestContext threadLocalContext = ChainContext.getThreadLocalContext(sourceName);
            threadLocalContext.setRequestEntity(requestEntity);
            chain.onBefore(threadLocalContext, null);
        } catch (Exception ex) {
            flowControlResult.setRequestType(requestEntity.getRequestType());
            FlowControlExceptionUtils.handleException(ex, flowControlResult);
            ChainContext.getThreadLocalContext(sourceName).save(HandlerConstants.OCCURRED_FLOW_EXCEPTION, ex);
            LOGGER.log(Level.FINE, ex, ex::getMessage);
        }
    }

    /**
     * dubbo pre-method, This distinguishes between the production end and the consumption end
     *
     * @param sourceName source name
     * @param requestEntity request body
     * @param flowControlResult flow control result
     * @param isProvider whether it is the production end
     */
    public void onDubboBefore(String sourceName, RequestEntity requestEntity, FlowControlResult flowControlResult,
            boolean isProvider) {
        String formatSourceName = formatSourceName(sourceName, isProvider);
        configPrefix(formatSourceName, isProvider);
        onBefore(formatSourceName, requestEntity, flowControlResult);
    }

    private String formatSourceName(String sourceName, boolean isProvider) {
        String prefix = isProvider ? HandlerConstants.THREAD_LOCAL_DUBBO_PROVIDER_PREFIX
                : HandlerConstants.THREAD_LOCAL_DUBBO_CONSUMER_PREFIX;

        // The length of the String Builder is initialized for performance
        StringBuilder sb = new StringBuilder(prefix.length() + sourceName.length());
        sb.append(prefix).append(sourceName);
        return sb.toString();
    }

    /**
     * postset method
     *
     * @param sourceName source name
     * @param result execution result
     */
    public void onResult(String sourceName, Object result) {
        try {
            chain.onResult(ChainContext.getThreadLocalContext(sourceName), null, result);
        } finally {
            ChainContext.remove(sourceName);
        }
    }

    private void configPrefix(String sourceName, boolean isProvider) {
        if (isProvider) {
            ChainContext.setKeyPrefix(sourceName, HandlerConstants.THREAD_LOCAL_DUBBO_PROVIDER_PREFIX);
        } else {
            ChainContext.setKeyPrefix(sourceName, HandlerConstants.THREAD_LOCAL_DUBBO_CONSUMER_PREFIX);
        }
    }

    /**
     * postset method
     *
     * @param sourceName source name
     * @param result execution result
     * @param isProvider whether it is the production end
     */
    public void onDubboResult(String sourceName, Object result, boolean isProvider) {
        final String formatSourceName = formatSourceName(sourceName, isProvider);
        configPrefix(formatSourceName, isProvider);
        onResult(formatSourceName, result);
    }

    /**
     * exception method
     *
     * @param sourceName source name
     * @param throwable exception message
     */
    public void onThrow(String sourceName, Throwable throwable) {
        final RequestContext context = ChainContext.getThreadLocalContext(sourceName);
        context.save(HandlerConstants.OCCURRED_REQUEST_EXCEPTION, throwable);
        chain.onThrow(context, null, throwable);
    }

    /**
     * exceptionMethod
     *
     * @param sourceName source name
     * @param throwable exception message
     * @param isProvider is provider
     */
    public void onDubboThrow(String sourceName, Throwable throwable, boolean isProvider) {
        final String formatSourceName = formatSourceName(sourceName, isProvider);
        configPrefix(formatSourceName, isProvider);
        onThrow(formatSourceName, throwable);
    }
}
