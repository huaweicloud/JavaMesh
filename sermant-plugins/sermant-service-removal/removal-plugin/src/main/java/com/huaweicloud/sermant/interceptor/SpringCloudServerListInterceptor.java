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

package com.huaweicloud.sermant.interceptor;

import com.huaweicloud.sermant.common.RemovalConstants;
import com.huaweicloud.sermant.core.utils.StringUtils;

import com.netflix.loadbalancer.Server;

/**
 * SpringCloud service invoking enhancement class
 *
 * @author zhp
 * @since 2023-02-17
 */
public class SpringCloudServerListInterceptor extends AbstractRemovalInterceptor<Server> {
    @Override
    protected String createKey(Server server) {
        return server.getHost() + RemovalConstants.CONNECTOR + server.getPort();
    }

    @Override
    protected String getServiceKey(Server server) {
        if (server.getMetaInfo() == null) {
            return StringUtils.EMPTY;
        }
        return StringUtils.getString(server.getMetaInfo().getAppName());
    }
}
