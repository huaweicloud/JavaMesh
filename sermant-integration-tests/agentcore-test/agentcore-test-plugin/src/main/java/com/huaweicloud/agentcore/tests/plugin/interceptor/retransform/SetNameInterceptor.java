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

package com.huaweicloud.agentcore.tests.plugin.interceptor.retransform;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.agent.interceptor.AbstractInterceptor;

/**
 * 测试实例方法的拦截
 *
 * @author tangle
 * @since 2023-09-07
 */
public class SetNameInterceptor extends AbstractInterceptor {
    /**
     * 修改Thread.setName()的入参为“modifyName”
     */
    private static final String NAME = "modifyName";

    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        context.getArguments()[0] = NAME;
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) throws Exception {
        return context;
    }
}
