/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package io.sermant.dubbo.registry.declarer;

import io.sermant.core.plugin.agent.declarer.InterceptDeclarer;
import io.sermant.core.plugin.agent.matcher.MethodMatcher;
import io.sermant.dubbo.registry.constants.Constant;

/**
 * RegistryConfig Enhancement Classes
 *
 * @author provenceee
 * @since 2022-04-13
 */
public class RegistryConfigDeclarer extends AbstractDeclarer {
    private static final String[] ENHANCE_CLASS = {"org.apache.dubbo.config.RegistryConfig"};

    private static final String INTERCEPT_CLASS = "io.sermant.dubbo.registry.interceptor.RegistryConfigInterceptor";

    private static final String[] METHOD_NAME = {Constant.SET_PROTOCOL_METHOD_NAME, "setAddress"};

    /**
     * Constructor
     */
    public RegistryConfigDeclarer() {
        super(ENHANCE_CLASS);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
            InterceptDeclarer.build(MethodMatcher.nameContains(METHOD_NAME), INTERCEPT_CLASS)
        };
    }
}
