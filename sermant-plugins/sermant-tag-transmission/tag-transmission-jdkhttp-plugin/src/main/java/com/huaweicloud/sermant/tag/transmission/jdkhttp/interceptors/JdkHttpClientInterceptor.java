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

package com.huaweicloud.sermant.tag.transmission.jdkhttp.interceptors;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.utils.CollectionUtils;
import com.huaweicloud.sermant.core.utils.tag.TrafficUtils;
import com.huaweicloud.sermant.tag.transmission.config.strategy.TagKeyMatcher;
import com.huaweicloud.sermant.tag.transmission.interceptors.AbstractClientInterceptor;

import sun.net.www.MessageHeader;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDK HttpClient 流量标签透传的拦截器
 *
 * @author lilai
 * @since 2023-08-08
 */
public class JdkHttpClientInterceptor extends AbstractClientInterceptor<MessageHeader> {
    /**
     * 过滤一次处理过程中拦截器的多次调用
     */
    protected static final ThreadLocal<Boolean> LOCK_MARK = new ThreadLocal<>();

    private static final Logger LOGGER = LoggerFactory.getLogger();

    @Override
    public ExecuteContext doBefore(ExecuteContext context) {
        if (LOCK_MARK.get() != null) {
            return context;
        }
        LOCK_MARK.set(Boolean.TRUE);

        Object messageHeaderObject = context.getArguments()[0];
        if (!(messageHeaderObject instanceof MessageHeader)) {
            return context;
        }

        injectTrafficTag2Carrier((MessageHeader) messageHeaderObject);
        return context;
    }

    @Override
    public ExecuteContext doAfter(ExecuteContext context) {
        LOCK_MARK.remove();
        return context;
    }

    @Override
    public ExecuteContext onThrow(ExecuteContext context) {
        LOCK_MARK.remove();
        return context;
    }

    /**
     * 向MessageHeader中添加流量标签
     *
     * @param messageHeader Jdk HttpClient 标签传递载体
     */
    @Override
    protected void injectTrafficTag2Carrier(MessageHeader messageHeader) {
        for (Map.Entry<String, List<String>> entry : TrafficUtils.getTrafficTag().getTag().entrySet()) {
            String key = entry.getKey();
            if (!TagKeyMatcher.isMatch(key)) {
                continue;
            }
            List<String> values = entry.getValue();

            // server端在标签值不为null的情况下转为list存储，为null时直接put null，因此在client端values为空必定是null
            if (CollectionUtils.isEmpty(values)) {
                messageHeader.add(key, null);
                LOGGER.log(Level.FINE, "Traffic tag {0} have been injected to jdkhttp.", entry);
                continue;
            }
            for (String value : values) {
                messageHeader.add(key, value);
            }
            LOGGER.log(Level.FINE, "Traffic tag {0}={1} have been injected to httpclient.", new Object[]{key,
                    values});
        }
    }
}
