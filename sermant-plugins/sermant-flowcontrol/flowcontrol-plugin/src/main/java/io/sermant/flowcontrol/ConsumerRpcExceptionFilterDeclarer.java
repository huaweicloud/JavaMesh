/*
 * Copyright (C) 2025-2025 Sermant Authors. All rights reserved.
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

package io.sermant.flowcontrol;

/**
 * Intercept the invoke method of org.apache.dubbo.rpc.filter.RpcExceptionFilter
 * for dubbo3.3.x consumer filter
 *
 * @author chengyouling
 * @since 2025-02-27
 */
public class ConsumerRpcExceptionFilterDeclarer extends DubboDeclarer {
    private static final String ENHANCE_CLASS = "org.apache.dubbo.rpc.filter.RpcExceptionFilter";

    private static final String INTERCEPT_CLASS = ApacheDubboInterceptor.class.getCanonicalName();

    /**
     * apache dubbo Declarer
     */
    public ConsumerRpcExceptionFilterDeclarer() {
        super(ENHANCE_CLASS, INTERCEPT_CLASS);
    }
}
