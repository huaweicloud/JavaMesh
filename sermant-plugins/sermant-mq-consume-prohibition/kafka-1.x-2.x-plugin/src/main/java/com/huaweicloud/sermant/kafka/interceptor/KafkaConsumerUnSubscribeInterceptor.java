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

package com.huaweicloud.sermant.kafka.interceptor;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.agent.interceptor.AbstractInterceptor;
import com.huaweicloud.sermant.kafka.cache.KafkaConsumerWrapper;
import com.huaweicloud.sermant.kafka.controller.KafkaConsumerController;
import com.huaweicloud.sermant.kafka.extension.KafkaConsumerHandler;
import com.huaweicloud.sermant.utils.InvokeUtils;

import java.util.Collections;

/**
 * Interceptor for KafkaConsumer unsubscribe method
 * {@link org.apache.kafka.clients.consumer.KafkaConsumer#unsubscribe()}
 *
 * @author lilai
 * @since 2023-12-05
 */
public class KafkaConsumerUnSubscribeInterceptor extends AbstractInterceptor {
    private KafkaConsumerHandler handler;

    /**
     * Construction method with KafkaConsumerHandler
     *
     * @param handler unsubscribe方法拦截点处理器
     */
    public KafkaConsumerUnSubscribeInterceptor(KafkaConsumerHandler handler) {
        this.handler = handler;
    }

    /**
     * Non parametric construction method
     */
    public KafkaConsumerUnSubscribeInterceptor() {
    }

    @Override
    public ExecuteContext before(ExecuteContext context) {
        if (InvokeUtils.isKafkaInvokeBySermant(Thread.currentThread().getStackTrace())) {
            return context;
        }
        if (handler != null) {
            handler.doBefore(context);
        }
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) {
        if (InvokeUtils.isKafkaInvokeBySermant(Thread.currentThread().getStackTrace())) {
            return context;
        }
        KafkaConsumerWrapper kafkaConsumerWrapper = KafkaConsumerController.getKafkaConsumerCache()
                .get(context.getObject().hashCode());
        if (kafkaConsumerWrapper == null) {
            return context;
        }
        updateKafkaConsumerWrapper(kafkaConsumerWrapper);
        if (handler != null) {
            handler.doAfter(context);
        }
        return context;
    }

    @Override
    public ExecuteContext onThrow(ExecuteContext context) {
        if (InvokeUtils.isKafkaInvokeBySermant(Thread.currentThread().getStackTrace())) {
            return context;
        }
        if (handler != null) {
            handler.doOnThrow(context);
        }
        return context;
    }

    private void updateKafkaConsumerWrapper(KafkaConsumerWrapper kafkaConsumerWrapper) {
        kafkaConsumerWrapper.setOriginalTopics(Collections.emptySet());
        kafkaConsumerWrapper.setOriginalPartitions(Collections.emptySet());
        kafkaConsumerWrapper.setAssign(false);
    }
}
