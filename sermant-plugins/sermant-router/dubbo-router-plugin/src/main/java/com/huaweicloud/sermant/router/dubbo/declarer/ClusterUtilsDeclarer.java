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
 * Enhance the mergeUrl method of the ClusterUtils class
 *
 * @author provenceee
 * @since 2021-06-28
 */
public class ClusterUtilsDeclarer extends AbstractDeclarer {
    private static final String[] ENHANCE_CLASS = {"org.apache.dubbo.rpc.cluster.support.ClusterUtils",
            "com.alibaba.dubbo.rpc.cluster.support.ClusterUtils"};

    private static final String INTERCEPT_CLASS
            = "com.huaweicloud.sermant.router.dubbo.interceptor.ClusterUtilsInterceptor";

    private static final String METHOD_NAME = "mergeUrl";

    /**
     * Constructor
     */
    public ClusterUtilsDeclarer() {
        super(ENHANCE_CLASS, INTERCEPT_CLASS, METHOD_NAME);
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return super.getMethodMatcher().and(MethodMatcher.isStaticMethod());
    }
}