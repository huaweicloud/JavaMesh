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

package com.huaweicloud.sermant.tag.transmission.rocketmqv4.interceptors;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.utils.CollectionUtils;
import com.huaweicloud.sermant.core.utils.tag.TrafficUtils;
import com.huaweicloud.sermant.tag.transmission.config.strategy.TagKeyMatcher;
import com.huaweicloud.sermant.tag.transmission.interceptors.AbstractClientInterceptor;

import org.apache.rocketmq.common.protocol.header.SendMessageRequestHeader;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * RocketMQ流量标签透传的生产者拦截器，支持RocketMQ4.x
 *
 * @author tangle
 * @since 2023-07-20
 */
public class RocketmqProducerSendInterceptor extends AbstractClientInterceptor<SendMessageRequestHeader> {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    /**
     * SendMessageRequestHeader在sendMessage方法中的参数下标
     */
    private static final int ARGUMENT_INDEX = 3;

    /**
     * SendMessageRequestHeader的Properties中键值对的连接符（ASCII值为1）
     */
    private static final char LINK_MARK = 1;

    /**
     * SendMessageRequestHeader的Properties中键值对的分隔符（ASCII值为2）
     */
    private static final char SPLIT_MARK = 2;

    @Override
    public ExecuteContext doBefore(ExecuteContext context) {
        if (context.getArguments()[ARGUMENT_INDEX] instanceof SendMessageRequestHeader) {
            injectTrafficTag2Carrier((SendMessageRequestHeader) context.getArguments()[ARGUMENT_INDEX]);
        }
        return context;
    }

    /**
     * 向SendMessageRequestHeader中添加流量标签
     *
     * @param header RocketMQ 标签传递载体
     */
    @Override
    protected void injectTrafficTag2Carrier(SendMessageRequestHeader header) {
        String oldProperties = header.getProperties();
        String newProperties = this.insertTags2Properties(oldProperties);
        header.setProperties(newProperties);
    }

    /**
     * 将流量标签插入到properties字符串中 原始properties的格式形如：key1[SOH]value1[STX]key2[SOH]value2，其中[SOH]为ASCII=1的符号，[STX]为ASCII=2的符号
     *
     * @param oldProperties 原始properties
     * @return String
     */
    private String insertTags2Properties(String oldProperties) {
        StringBuilder newProperties = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : TrafficUtils.getTrafficTag().getTag().entrySet()) {
            String key = entry.getKey();
            if (!TagKeyMatcher.isMatch(key)) {
                continue;
            }
            List<String> values = entry.getValue();
            newProperties.append(key);
            newProperties.append(LINK_MARK);
            newProperties.append(CollectionUtils.isEmpty(values) ? null : values.get(0));
            newProperties.append(SPLIT_MARK);
            LOGGER.log(Level.FINE, "Traffic tag {0} have been injected to rocketmq.", entry);
        }
        if (newProperties.length() == 0) {
            return oldProperties;
        }
        if (oldProperties == null || oldProperties.length() == 0) {
            // rocketmq的header为空，需要去除新header最后的分隔符
            newProperties.deleteCharAt(newProperties.length() - 1);
            return newProperties.toString();
        }
        return newProperties.append(oldProperties).toString();
    }

    @Override
    public ExecuteContext doAfter(ExecuteContext context) {
        return context;
    }
}
