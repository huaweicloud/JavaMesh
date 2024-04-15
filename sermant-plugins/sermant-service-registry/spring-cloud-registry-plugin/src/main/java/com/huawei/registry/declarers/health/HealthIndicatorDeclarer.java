/*
 * Copyright (C) 2022-2024 Sermant Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huawei.registry.declarers.health;

import com.huawei.registry.declarers.AbstractDoubleRegistryDeclarer;
import com.huawei.registry.interceptors.health.HealthIndicatorInterceptor;

import com.huaweicloud.sermant.core.plugin.agent.declarer.InterceptDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.matcher.ClassMatcher;
import com.huaweicloud.sermant.core.plugin.agent.matcher.MethodMatcher;

/**
 * Health checks
 *
 * @author zhouss
 * @since 2022-06-13
 */
public class HealthIndicatorDeclarer extends AbstractDoubleRegistryDeclarer {
    /**
     * Health Check Enhancement Class
     */
    private static final String[] ENHANCE_CLASSES = {
        "org.springframework.boot.actuate.health.AbstractHealthIndicator",
        "org.springframework.cloud.netflix.eureka.EurekaHealthIndicator",
        "org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryHealthIndicator"
    };

    /**
     * The fully qualified name of the interception class
     */
    private static final String INTERCEPT_CLASS = HealthIndicatorInterceptor.class.getCanonicalName();

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameContains(ENHANCE_CLASSES);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
                InterceptDeclarer.build(MethodMatcher.nameEquals("health"), INTERCEPT_CLASS)
        };
    }
}
