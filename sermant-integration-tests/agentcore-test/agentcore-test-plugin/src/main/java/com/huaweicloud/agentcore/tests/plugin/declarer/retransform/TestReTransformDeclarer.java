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

package com.huaweicloud.agentcore.tests.plugin.declarer.retransform;

import com.huaweicloud.agentcore.tests.plugin.interceptor.retransform.ActiveCountInterceptor;
import com.huaweicloud.agentcore.tests.plugin.interceptor.retransform.GetAllStackTracesInterceptor;
import com.huaweicloud.agentcore.tests.plugin.interceptor.retransform.NoParamsConstructorInterceptor;
import com.huaweicloud.agentcore.tests.plugin.interceptor.retransform.SetNameInterceptor;
import com.huaweicloud.sermant.core.plugin.agent.declarer.AbstractPluginDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.declarer.InterceptDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.matcher.ClassMatcher;
import com.huaweicloud.sermant.core.plugin.agent.matcher.MethodMatcher;

/**
 * 测试对类的重转换能力
 *
 * @author luanwenfei
 * @since 2023-09-07
 */
public class TestReTransformDeclarer extends AbstractPluginDeclarer {
    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameEquals("java.lang.Thread");
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
                // 测试静态方法
                InterceptDeclarer.build(MethodMatcher.nameEquals("getAllStackTraces"),
                        new GetAllStackTracesInterceptor()),
                // 测试无参构造方法
                InterceptDeclarer.build(MethodMatcher.isConstructor().and(MethodMatcher.paramCountEquals(0)),
                        new NoParamsConstructorInterceptor()),
                // 测试实例方法
                InterceptDeclarer.build(MethodMatcher.nameEquals("setName"), new SetNameInterceptor()),
                // 测试静态方法skip
                InterceptDeclarer.build(MethodMatcher.nameEquals("activeCount"), new ActiveCountInterceptor()),
        };
    }
}
