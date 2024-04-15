/*
 * Copyright (C) 2021-2024 Sermant Authors. All rights reserved.
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

package com.huawei.registry.grace.declarers;

import com.huawei.registry.config.grace.GraceContext;
import com.huawei.registry.grace.interceptors.RegistryDelayInterceptor;

import com.huaweicloud.sermant.core.plugin.agent.declarer.AbstractPluginDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.declarer.InterceptDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.matcher.ClassMatcher;
import com.huaweicloud.sermant.core.plugin.agent.matcher.MethodMatcher;

/**
 * Register a delay blocker
 *
 * @author zhouss
 * @since 2022-05-17
 */
public class RegistryDelayDeclarer extends AbstractPluginDeclarer {
    /**
     * Older versions 1.5.x(springboot)
     */
    public static final String OLD_VERSION_ENHANCE_CLASS =
            "org.springframework.cloud.client.discovery.AbstractDiscoveryLifecycle";

    /**
     * EUREKA Automatic registration class
     */
    private static final String EUREKA_ENHANCE_CLASS =
            "org.springframework.cloud.netflix.eureka.serviceregistry.EurekaAutoServiceRegistration";

    /**
     * Universal automatic registration class
     */
    private static final String ENHANCE_CLASS =
            "org.springframework.cloud.client.serviceregistry.AbstractAutoServiceRegistration";

    /**
     * The fully qualified name of the interception class
     */
    private static final String INTERCEPT_CLASS = RegistryDelayInterceptor.class.getCanonicalName();

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameContains(ENHANCE_CLASS, EUREKA_ENHANCE_CLASS,
                OLD_VERSION_ENHANCE_CLASS);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        GraceContext.INSTANCE.setStartTime(System.currentTimeMillis());
        return new InterceptDeclarer[]{
            InterceptDeclarer.build(MethodMatcher.nameEquals("start"), INTERCEPT_CLASS)
        };
    }
}
