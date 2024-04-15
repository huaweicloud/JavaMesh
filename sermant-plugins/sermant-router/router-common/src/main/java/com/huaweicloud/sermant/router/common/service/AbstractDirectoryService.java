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

package com.huaweicloud.sermant.router.common.service;

import com.huaweicloud.sermant.core.plugin.service.PluginService;

/**
 * AbstractDirectory's service
 *
 * @author provenceee
 * @since 2021-11-24
 */
public interface AbstractDirectoryService extends PluginService {
    /**
     * filter the label invoker
     *
     * @param obj RegistryDirectory
     * @param invocation parameter
     * @param result invokers
     * @return invokers
     * @see com.alibaba.dubbo.registry.integration.RegistryDirectory
     * @see org.apache.dubbo.registry.integration.RegistryDirectory
     * @see com.alibaba.dubbo.rpc.Invoker
     * @see org.apache.dubbo.rpc.Invoker
     */
    Object selectInvokers(Object obj, Object invocation, Object result);
}