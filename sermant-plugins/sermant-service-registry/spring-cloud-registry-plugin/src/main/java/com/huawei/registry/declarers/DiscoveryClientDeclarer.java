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

package com.huawei.registry.declarers;

import com.huawei.registry.interceptors.DiscoveryClientInterceptor;
import com.huawei.registry.interceptors.DiscoveryClientServiceInterceptor;

import com.huaweicloud.sermant.core.plugin.agent.declarer.InterceptDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.matcher.ClassMatcher;
import com.huaweicloud.sermant.core.plugin.agent.matcher.MethodMatcher;

/**
 * For Eureka, the Consul registry obtains the instance list to intercept the instance
 *
 * @author zhouss
 * @since 2021-12-17
 */
public class DiscoveryClientDeclarer extends AbstractDoubleRegistryDeclarer {
    /**
     * Fully qualified name of the enhanced class This client injection priority is the highest, so just intercept it
     */
    private static final String[] ENHANCE_CLASSES = {
        "org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClient",
        "org.springframework.cloud.client.discovery.composite.reactive.ReactiveCompositeDiscoveryClient"
    };

    /**
     * The fully qualified name of the interception class
     */
    private static final String INTERCEPT_CLASS = DiscoveryClientInterceptor.class.getCanonicalName();

    /**
     * Service name interception
     */
    private static final String SERVICE_INTERCEPT_CLASS = DiscoveryClientServiceInterceptor.class.getCanonicalName();

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameContains(ENHANCE_CLASSES);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
                InterceptDeclarer.build(MethodMatcher.nameEquals("getInstances"), INTERCEPT_CLASS),
                InterceptDeclarer.build(MethodMatcher.nameEquals("getServices"), SERVICE_INTERCEPT_CLASS)
        };
    }
}
