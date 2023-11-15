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

package com.huaweicloud.sermant.tag.transmission.httpclientv4.interceptors;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.utils.CollectionUtils;
import com.huaweicloud.sermant.core.utils.tag.TrafficUtils;
import com.huaweicloud.sermant.tag.transmission.config.strategy.TagKeyMatcher;
import com.huaweicloud.sermant.tag.transmission.interceptors.AbstractClientInterceptor;

import org.apache.http.HttpRequest;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HttpClient 流量标签透传的拦截器, 仅针对4.x版本
 *
 * @author lilai
 * @since 2023-07-17
 */
public class HttpClient4xInterceptor extends AbstractClientInterceptor<HttpRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    @Override
    public ExecuteContext doBefore(ExecuteContext context) {
        Object httpRequestObject = context.getArguments()[1];
        if (!(httpRequestObject instanceof HttpRequest)) {
            return context;
        }
        injectTrafficTag2Carrier((HttpRequest) httpRequestObject);
        return context;
    }

    @Override
    public ExecuteContext doAfter(ExecuteContext context) {
        return context;
    }

    /**
     * 向HttpRequest中添加流量标签
     *
     * @param httpRequest httpclient 4.x 标签传递载体
     */
    @Override
    protected void injectTrafficTag2Carrier(HttpRequest httpRequest) {
        for (Map.Entry<String, List<String>> entry : TrafficUtils.getTrafficTag().getTag().entrySet()) {
            String key = entry.getKey();
            if (!TagKeyMatcher.isMatch(key)) {
                continue;
            }
            List<String> values = entry.getValue();

            // server端在标签值不为null的情况下转为list存储，为null时直接put null，因此在client端values为空必定是null
            if (CollectionUtils.isEmpty(values)) {
                httpRequest.addHeader(key, null);
                LOGGER.log(Level.FINE, "Traffic tag {0} have been injected to httpclient.", entry);
                continue;
            }
            for (String value : values) {
                httpRequest.addHeader(key, value);
            }
            LOGGER.log(Level.FINE, "Traffic tag {0}={1} have been injected to httpclient.", new Object[]{key,
                    values});
        }
    }
}
