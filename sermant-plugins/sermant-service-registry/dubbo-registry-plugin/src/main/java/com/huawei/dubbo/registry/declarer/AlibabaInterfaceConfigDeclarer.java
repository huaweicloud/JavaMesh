/*
 * Copyright (C) 2022-2024 Sermant Authors. All rights reserved.
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

package com.huawei.dubbo.registry.declarer;

import com.huaweicloud.sermant.core.plugin.agent.declarer.InterceptDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.matcher.MethodMatcher;

/**
 * AbstractInterfaceConfig Enhancement Classes
 *
 * @author provenceee
 * @since 2022-11-24
 */
public class AlibabaInterfaceConfigDeclarer extends AbstractDeclarer {
    private static final String[] ENHANCE_CLASS = {"com.alibaba.dubbo.config.AbstractInterfaceConfig"};

    private static final String INTERCEPT_CLASS
        = "com.huawei.dubbo.registry.interceptor.AlibabaInterfaceConfigInterceptor";

    private static final String METHOD_NAME = "loadRegistries";

    /**
     * Constructor
     */
    public AlibabaInterfaceConfigDeclarer() {
        super(ENHANCE_CLASS);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
            InterceptDeclarer.build(MethodMatcher.nameEquals(METHOD_NAME), INTERCEPT_CLASS)
        };
    }
}