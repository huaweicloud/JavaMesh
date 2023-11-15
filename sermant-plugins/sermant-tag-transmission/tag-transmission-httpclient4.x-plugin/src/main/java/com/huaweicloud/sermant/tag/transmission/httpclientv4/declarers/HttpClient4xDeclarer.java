/*
 *   Copyright (C) 2023-2023 Huawei Technologies Co., Ltd. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.huaweicloud.sermant.tag.transmission.httpclientv4.declarers;

import com.huaweicloud.sermant.core.plugin.agent.declarer.AbstractPluginDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.declarer.InterceptDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.matcher.ClassMatcher;
import com.huaweicloud.sermant.core.plugin.agent.matcher.MethodMatcher;
import com.huaweicloud.sermant.tag.transmission.httpclientv4.interceptors.HttpClient4xInterceptor;

/**
 * HttpClient 流量标签透传的增强声明, 仅针对4.x版本
 *
 * @author lilai
 * @since 2023-07-17
 */
public class HttpClient4xDeclarer extends AbstractPluginDeclarer {
    /**
     * 增强类的全限定名 http请求
     */
    private static final String[] ENHANCE_CLASSES = {
            "org.apache.http.impl.client.AbstractHttpClient",
            "org.apache.http.impl.client.DefaultRequestDirector",
            "org.apache.http.impl.client.InternalHttpClient",
            "org.apache.http.impl.client.MinimalHttpClient"
    };

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameContains(ENHANCE_CLASSES);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
                InterceptDeclarer.build(MethodMatcher.nameContains("doExecute", "execute")
                                .and(MethodMatcher.paramTypesEqual(
                                        "org.apache.http.HttpHost",
                                        "org.apache.http.HttpRequest",
                                        "org.apache.http.protocol.HttpContext")),
                        new HttpClient4xInterceptor())
        };
    }
}
