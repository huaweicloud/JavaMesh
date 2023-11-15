/*
 * Copyright (C) 2023-2023 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huaweicloud.sermant.tag.transmission.kafka.interceptors;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.utils.CollectionUtils;
import com.huaweicloud.sermant.core.utils.tag.TrafficUtils;
import com.huaweicloud.sermant.tag.transmission.config.strategy.TagKeyMatcher;
import com.huaweicloud.sermant.tag.transmission.interceptors.AbstractClientInterceptor;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * kafka生产者发送消息的拦截器，支持1.x, 2.x, 3.x
 *
 * @author lilai
 * @since 2023-07-18
 */
public class KafkaProducerInterceptor extends AbstractClientInterceptor<ProducerRecord<?, ?>> {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    @Override
    public ExecuteContext doBefore(ExecuteContext context) {
        Object producerRecordObject = context.getArguments()[0];
        if (!(producerRecordObject instanceof ProducerRecord)) {
            return context;
        }
        injectTrafficTag2Carrier((ProducerRecord<?, ?>) producerRecordObject);
        return context;
    }

    @Override
    public ExecuteContext doAfter(ExecuteContext context) {
        return context;
    }

    /**
     * 向ProducerRecord中添加流量标签
     *
     * @param producerRecord kafka 标签传递载体
     */
    @Override
    protected void injectTrafficTag2Carrier(ProducerRecord<?, ?> producerRecord) {
        Headers headers = producerRecord.headers();
        for (Map.Entry<String, List<String>> entry : TrafficUtils.getTrafficTag().getTag().entrySet()) {
            String key = entry.getKey();
            if (!TagKeyMatcher.isMatch(key)) {
                continue;
            }
            List<String> values = entry.getValue();

            // producer端在标签值不为null的情况下转为list存储，为null时直接put null，因此在consumer端values为空必定是null
            if (CollectionUtils.isEmpty(values)) {
                headers.add(key, null);
                LOGGER.log(Level.FINE, "Traffic tag {0} have been injected to kafka.", entry);
                continue;
            }
            for (String value : values) {
                headers.add(key, value.getBytes(StandardCharsets.UTF_8));
            }
            LOGGER.log(Level.FINE, "Traffic tag {0}={1} have been injected to httpclient.", new Object[]{key,
                    values});
        }
    }
}
