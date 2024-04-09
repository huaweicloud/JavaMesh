/*
 *   Copyright (C) 2023-2024 Sermant Authors. All rights reserved.
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

package com.huaweicloud.sermant.tag.transmission.grpc.interceptors;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.utils.CollectionUtils;
import com.huaweicloud.sermant.core.utils.tag.TrafficUtils;
import com.huaweicloud.sermant.tag.transmission.config.strategy.TagKeyMatcher;
import com.huaweicloud.sermant.tag.transmission.interceptors.AbstractClientInterceptor;

import io.grpc.Metadata;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * grpc invokes the server using dynamic message, intercepts the header parameter injected traffic tag
 *
 * @author daizhenyu
 * @since 2023-08-21
 **/
public class ClientCallImplInterceptor extends AbstractClientInterceptor<Metadata> {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    @Override
    protected ExecuteContext doBefore(ExecuteContext context) {
        Object[] arguments = context.getArguments();

        // the number of entries for the blocked method is 2
        if (arguments == null || arguments.length <= 1) {
            return context;
        }
        Object metadataObject = arguments[1];
        if (metadataObject instanceof Metadata) {
            injectTrafficTag2Carrier((Metadata) metadataObject);
        }
        return context;
    }

    @Override
    protected ExecuteContext doAfter(ExecuteContext context) {
        return context;
    }

    @Override
    protected void injectTrafficTag2Carrier(Metadata header) {
        for (Map.Entry<String, List<String>> entry : TrafficUtils.getTrafficTag().getTag().entrySet()) {
            String key = entry.getKey();
            if (!TagKeyMatcher.isMatch(key)) {
                continue;
            }
            List<String> values = entry.getValue();

            // The server side converts the label value to list storage when it is not null. If it is null, it directly
            // puts null. Therefore, if the client side values are empty, they must be null.
            if (CollectionUtils.isEmpty(values)) {
                // grpc will check for null, pass the "null" string here
                header.put(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER), "null");
                LOGGER.log(Level.FINE, "Traffic tag {0}=null have been injected to grpc.", key);
                continue;
            }
            header.put(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER), values.get(0));
            LOGGER.log(Level.FINE, "Traffic tag {0}={1} have been injected to grpc.", new Object[]{key,
                    values.get(0)});
        }
    }
}
