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

package com.huawei.discovery.declarers;

import com.huawei.discovery.interceptors.HttpComponentsClientHttpConnectorInterceptor;

import com.huaweicloud.sermant.core.plugin.agent.declarer.InterceptDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.matcher.ClassMatcher;
import com.huaweicloud.sermant.core.plugin.agent.matcher.MethodMatcher;

/**
 * Webclient httpclient5 interception point, springboot 2.4.0+
 *
 * @author provenceee
 * @since 2023-04-25
 */
public class HttpComponentsClientHttpConnectorDeclarer extends BaseDeclarer {
    private static final String ENHANCE_CLASS
            = "org.springframework.http.client.reactive.HttpComponentsClientHttpConnector";

    private static final String INTERCEPT_CLASS = HttpComponentsClientHttpConnectorInterceptor.class.getCanonicalName();

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameEquals(ENHANCE_CLASS);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
                InterceptDeclarer.build(MethodMatcher.nameEquals("execute").and(MethodMatcher.paramTypesEqual(
                                "org.springframework.http.client.reactive.HttpComponentsClientHttpRequest",
                                "org.apache.hc.client5.http.protocol.HttpClientContext")),
                        INTERCEPT_CLASS)
        };
    }
}