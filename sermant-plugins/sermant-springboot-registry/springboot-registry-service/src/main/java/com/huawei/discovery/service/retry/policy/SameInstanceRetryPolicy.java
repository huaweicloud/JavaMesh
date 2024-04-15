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

package com.huawei.discovery.service.retry.policy;

import com.huawei.discovery.config.LbConfig;
import com.huawei.discovery.entity.ServiceInstance;

import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;

import java.util.Optional;

/**
 * If the maximum number of retries exceeds the same instance, Server Load Balancer selects other instances
 *
 * @author zhouss
 * @since 2022-10-21
 */
public class SameInstanceRetryPolicy extends RoundRobinRetryPolicy {
    private final int maxSameRetry;

    /**
     * Constructor
     */
    public SameInstanceRetryPolicy() {
        this.maxSameRetry = PluginConfigManager.getPluginConfig(LbConfig.class).getMaxSameRetry();
    }

    @Override
    public Optional<ServiceInstance> select(String serviceName, PolicyContext policyContext) {
        if (policyContext.getServiceInstance() != null && policyContext.isContinue(this.maxSameRetry)) {
            return Optional.of(policyContext.getServiceInstance());
        }
        return super.select(serviceName, policyContext);
    }

    @Override
    public String name() {
        return "SameInstance";
    }
}
