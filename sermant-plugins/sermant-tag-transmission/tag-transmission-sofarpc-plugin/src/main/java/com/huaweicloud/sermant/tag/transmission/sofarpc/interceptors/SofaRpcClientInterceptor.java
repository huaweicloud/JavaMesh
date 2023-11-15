/*
 *   Copyright (C) 2023-2023 Huawei Technologies Co., Ltd. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.huaweicloud.sermant.tag.transmission.sofarpc.interceptors;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.utils.CollectionUtils;
import com.huaweicloud.sermant.core.utils.tag.TrafficUtils;
import com.huaweicloud.sermant.tag.transmission.config.strategy.TagKeyMatcher;
import com.huaweicloud.sermant.tag.transmission.interceptors.AbstractClientInterceptor;

import com.alipay.sofa.rpc.core.request.SofaRequest;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * sofarpc client端interceptor，支持5.0+版本
 *
 * @author daizhenyu
 * @since 2023-08-22
 **/
public class SofaRpcClientInterceptor extends AbstractClientInterceptor<SofaRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    @Override
    protected ExecuteContext doBefore(ExecuteContext context) {
        Object[] arguments = context.getArguments();
        if (arguments == null || arguments.length == 0) {
            return context;
        }
        Object argument = arguments[0];
        if (argument instanceof SofaRequest) {
            injectTrafficTag2Carrier((SofaRequest) argument);
        }
        return context;
    }

    @Override
    protected ExecuteContext doAfter(ExecuteContext context) {
        return context;
    }

    /**
     * 向SofaRequest中添加流量标签
     *
     * @param sofaRequest sofarpc客服端 标签传递载体
     */
    @Override
    protected void injectTrafficTag2Carrier(SofaRequest sofaRequest) {
        for (Map.Entry<String, List<String>> entry : TrafficUtils.getTrafficTag().getTag().entrySet()) {
            String key = entry.getKey();
            if (!TagKeyMatcher.isMatch(key)) {
                continue;
            }
            List<String> values = entry.getValue();

            // server端在标签值不为null的情况下转为list存储，为null时直接put null，因此在client端values为空必定是null
            if (CollectionUtils.isEmpty(values)) {
                // sofa 无法添加value为null的键值对
                sofaRequest.addRequestProp(key, "null");
                LOGGER.log(Level.FINE, "Traffic tag {0} have been injected to sofarpc.", entry);
                continue;
            }
            sofaRequest.addRequestProp(key, values.get(0));
            LOGGER.log(Level.FINE, "Traffic tag {0}={1} have been injected to sofarpc.", new Object[]{key,
                    values.get(0)});
        }
    }
}