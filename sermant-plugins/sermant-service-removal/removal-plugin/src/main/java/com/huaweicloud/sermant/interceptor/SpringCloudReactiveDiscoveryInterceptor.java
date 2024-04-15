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

import com.huaweicloud.sermant.common.RemovalConstants;
import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;

import reactor.core.publisher.Flux;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * SpringCloud service invoking enhancement class
 *
 * @author zhp
 * @since 2023-02-17
 */
public class SpringCloudReactiveDiscoveryInterceptor extends AbstractRemovalInterceptor<ServiceInstance> {
    @Override
    public ExecuteContext doAfter(ExecuteContext context) {
        if (context.getResult() == null || !(context.getResult() instanceof Flux)) {
            return context;
        }
        Flux<ServiceInstance> serviceInstanceFlux = (Flux<ServiceInstance>) context.getResult();
        List<ServiceInstance> serverList = serviceInstanceFlux.collectList().block();
        if (serverList == null || serverList.size() == 0) {
            return context;
        }
        return context.changeResult(Flux.fromIterable(removeInstance(serverList)));
    }

    @Override
    protected String createKey(ServiceInstance serviceInstance) {
        return serviceInstance.getHost() + RemovalConstants.CONNECTOR + serviceInstance.getPort();
    }

    @Override
    protected String getServiceKey(ServiceInstance instance) {
        return instance.getServiceId();
    }
}
