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

package com.huaweicloud.sermant.interceptor;

import com.huaweicloud.sermant.core.utils.StringUtils;

import org.apache.dubbo.rpc.Invocation;

/**
 * Apache Dubbo service calls the enhanced class
 *
 * @author zhp
 * @since 2023-02-17
 */
public class ApacheDubboInvokeInterceptor extends AbstractCallInterceptor<Invocation> {
    @Override
    protected String getHost(Invocation invocation) {
        if (invocation == null || invocation.getInvoker() == null || invocation.getInvoker().getUrl() == null) {
            return StringUtils.EMPTY;
        }
        return StringUtils.getString(invocation.getInvoker().getUrl().getHost());
    }

    @Override
    protected String getPort(Invocation invocation) {
        if (invocation == null || invocation.getInvoker() == null || invocation.getInvoker().getUrl() == null) {
            return StringUtils.EMPTY;
        }
        return StringUtils.getString(invocation.getInvoker().getUrl().getPort());
    }

    /**
     * Obtain the parameter subscript of instance information
     *
     * @return Parameter subscript for example information
     */
    @Override
    protected int getIndex() {
        return 1;
    }
}
