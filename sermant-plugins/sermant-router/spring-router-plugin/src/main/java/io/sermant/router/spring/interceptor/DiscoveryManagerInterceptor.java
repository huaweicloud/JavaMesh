/*
 * Copyright (C) 2022-2024 Huawei Technologies Co., Ltd. All rights reserved.
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

package io.sermant.router.spring.interceptor;

import io.sermant.core.common.LoggerFactory;
import io.sermant.core.plugin.agent.entity.ExecuteContext;
import io.sermant.core.plugin.agent.interceptor.AbstractInterceptor;
import io.sermant.core.plugin.config.PluginConfigManager;
import io.sermant.core.plugin.service.PluginServiceManager;
import io.sermant.core.utils.ReflectUtils;
import io.sermant.router.common.config.RouterConfig;
import io.sermant.router.common.constants.RouterConstant;
import io.sermant.router.spring.cache.AppCache;
import io.sermant.router.spring.service.SpringConfigService;
import io.sermant.router.spring.utils.SpringRouterUtils;

import java.util.logging.Logger;

/**
 * Register plugin intercept point
 *
 * @author provenceee
 * @since 2022-10-13
 */
public class DiscoveryManagerInterceptor extends AbstractInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private final SpringConfigService configService;

    private final RouterConfig routerConfig;

    /**
     * Constructor
     */
    public DiscoveryManagerInterceptor() {
        configService = PluginServiceManager.getPluginService(SpringConfigService.class);
        routerConfig = PluginConfigManager.getPluginConfig(RouterConfig.class);
    }

    @Override
    public ExecuteContext before(ExecuteContext context) {
        Object[] arguments = context.getArguments();
        if (arguments != null && arguments.length > 0) {
            Object obj = arguments[0];
            Object serviceName = ReflectUtils.getFieldValue(obj, "serviceName").orElse(null);
            if (serviceName instanceof String) {
                AppCache.INSTANCE.setAppName((String) serviceName);
                configService.init(RouterConstant.SPRING_CACHE_NAME, (String) serviceName);
            } else {
                LOGGER.warning("Service name is null or not instanceof string.");
            }
            SpringRouterUtils.putMetaData(SpringRouterUtils.getMetadata(obj), routerConfig);
        }
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) {
        return context;
    }
}