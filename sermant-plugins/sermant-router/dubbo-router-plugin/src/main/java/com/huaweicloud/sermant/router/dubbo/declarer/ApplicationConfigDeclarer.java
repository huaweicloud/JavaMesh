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

package com.huaweicloud.sermant.router.dubbo.declarer;

import com.huaweicloud.sermant.core.plugin.agent.matcher.MethodMatcher;

/**
 * Enhance the setName method of the ApplicationConfig class to obtain the application name
 *
 * @author provenceee
 * @since 2021-11-08
 */
public class ApplicationConfigDeclarer extends AbstractDeclarer {
    private static final String[] ENHANCE_CLASS = {"org.apache.dubbo.config.ApplicationConfig",
            "com.alibaba.dubbo.config.ApplicationConfig"};

    private static final String INTERCEPT_CLASS
            = "com.huaweicloud.sermant.router.dubbo.interceptor.ApplicationConfigInterceptor";

    private static final String[] METHOD_NAME = {"setName", "setParameters"};

    /**
     * Constructor
     */
    public ApplicationConfigDeclarer() {
        super(ENHANCE_CLASS, INTERCEPT_CLASS, null);
    }

    /**
     * get the method matcher
     *
     * @return method matcher
     */
    @Override
    public MethodMatcher getMethodMatcher() {
        return MethodMatcher.nameContains(METHOD_NAME);
    }
}