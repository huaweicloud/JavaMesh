/*
 * Copyright (C) 2023-2023 Huawei Technologies Co., Ltd. All rights reserved.
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

package io.sermant.removal.declarer;

import io.sermant.core.plugin.agent.declarer.AbstractPluginDeclarer;
import io.sermant.core.plugin.agent.declarer.InterceptDeclarer;
import io.sermant.core.plugin.agent.matcher.ClassMatcher;
import io.sermant.core.plugin.agent.matcher.MethodMatcher;
import io.sermant.removal.interceptor.AlibabaDubboClusterUtilsInterceptor;

/**
 * Enhance the mergeUrl method of the ClusterUtils class
 *
 * @author zhp
 * @since 2023-03-16
 */
public class AlibabaDubboClusterUtilsDeclarer extends AbstractPluginDeclarer {
    private static final String ENHANCE_CLASS = "com.alibaba.dubbo.rpc.cluster.support.ClusterUtils";

    private static final String INTERCEPT_CLASS = AlibabaDubboClusterUtilsInterceptor.class.getCanonicalName();

    private static final String METHOD_NAME = "mergeUrl";

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameEquals(ENHANCE_CLASS);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{InterceptDeclarer.build(MethodMatcher.nameEquals(METHOD_NAME), INTERCEPT_CLASS)};
    }
}
