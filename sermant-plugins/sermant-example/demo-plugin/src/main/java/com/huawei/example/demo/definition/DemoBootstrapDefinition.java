/*
 * Copyright (C) 2021-2021 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.example.demo.definition;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatchers;

import com.huawei.sermant.core.agent.definition.EnhanceDefinition;
import com.huawei.sermant.core.agent.definition.MethodInterceptPoint;
import com.huawei.sermant.core.agent.matcher.ClassMatcher;
import com.huawei.sermant.core.agent.matcher.ClassMatchers;

/**
 * 以Thread为例测试启动类加载器加载的类的增强情况
 *
 * @author HapThorin
 * @version 1.0.0
 * @since 2021/10/27
 */
public class DemoBootstrapDefinition implements EnhanceDefinition {
    @Override
    public ClassMatcher enhanceClass() {
        return ClassMatchers.named("java.lang.Thread");
    }

    @Override
    public MethodInterceptPoint[] getMethodInterceptPoints() {
        return new MethodInterceptPoint[]{
                MethodInterceptPoint.newStaticMethodInterceptPoint(
                        "com.huawei.example.demo.interceptor.DemoStaticInterceptor",
                        ElementMatchers.<MethodDescription>named("getAllStackTraces") // 测试静态方法
                ),
                MethodInterceptPoint.newConstructorInterceptPoint(
                        "com.huawei.example.demo.interceptor.DemoConstInterceptor",
                        ElementMatchers.takesNoArguments() // 测试无参构造函数
                ),
                MethodInterceptPoint.newInstMethodInterceptPoint(
                        "com.huawei.example.demo.interceptor.DemoInstInterceptor",
                        ElementMatchers.<MethodDescription>named("setName") // 测试实例方法
                )
        };
    }
}
