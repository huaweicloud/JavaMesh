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
import io.sermant.router.common.config.RouterConfig;
import io.sermant.router.common.constants.RouterConstant;
import io.sermant.router.common.utils.ReflectUtils;
import io.sermant.router.spring.cache.AppCache;
import io.sermant.router.spring.service.SpringConfigService;
import io.sermant.router.spring.utils.SpringRouterUtils;

import org.springframework.cloud.client.serviceregistry.AbstractAutoServiceRegistration;
import org.springframework.cloud.client.serviceregistry.Registration;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AbstractAutoServiceRegistration enhancement class, spring cloud registration method
 *
 * @author provenceee
 * @since 2022-07-12
 */
public class ServiceRegistryInterceptor extends AbstractInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private final SpringConfigService configService;

    private final RouterConfig routerConfig;

    /**
     * Constructor
     */
    public ServiceRegistryInterceptor() {
        configService = PluginServiceManager.getPluginService(SpringConfigService.class);
        routerConfig = PluginConfigManager.getPluginConfig(RouterConfig.class);
    }

    @Override
    public ExecuteContext before(ExecuteContext context) {
        Object object = context.getObject();
        if (object instanceof AbstractAutoServiceRegistration) {
            AbstractAutoServiceRegistration<?> serviceRegistration = (AbstractAutoServiceRegistration<?>) object;
            try {
                Registration registration = (Registration) ReflectUtils.getAccessibleObject(
                        serviceRegistration.getClass().getDeclaredMethod("getRegistration"))
                        .invoke(serviceRegistration);
                AppCache.INSTANCE.setAppName(registration.getServiceId());
                configService.init(RouterConstant.SPRING_CACHE_NAME, registration.getServiceId());
                SpringRouterUtils.putMetaData(registration.getMetadata(), routerConfig);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException ex) {
                LOGGER.log(Level.WARNING, "Can not get the registration.", ex);
            }
        }
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) {
        return context;
    }
}