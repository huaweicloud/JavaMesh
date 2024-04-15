/*
 * Copyright (C) 2022-2024 Sermant Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.registry.auto.sc;

import com.huawei.registry.entity.FixedResult;
import com.huawei.registry.entity.MicroServiceInstance;
import com.huawei.registry.services.RegisterCenterService;

import com.huaweicloud.sermant.core.plugin.service.PluginServiceManager;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;

import java.util.List;

/**
 * HealthIndicator Test
 *
 * @author zhouss
 * @since 2022-09-06
 */
public class ServiceCombHealthIndicatorTest {
    private final String status = "UP";

    @Test
    public void health() {
        try (final MockedStatic<PluginServiceManager> pluginServiceManagerMockedStatic = Mockito.mockStatic(PluginServiceManager.class);){
            pluginServiceManagerMockedStatic.when(() -> PluginServiceManager.getPluginService(RegisterCenterService.class))
                    .thenReturn(new TestRegistryService());
            final ServiceCombHealthIndicator serviceCombHealthIndicator = new ServiceCombHealthIndicator();
            final Health health = serviceCombHealthIndicator.health();
            Assert.assertEquals(health.getStatus().getCode(), status);
        }
    }

    /**
     * 测试用
     *
     * @since 2022-09-06
     */
    class TestRegistryService implements RegisterCenterService {

        @Override
        public void register(FixedResult result) {

        }

        @Override
        public void unRegister() {

        }

        @Override
        public List<MicroServiceInstance> getServerList(String serviceId) {
            return null;
        }

        @Override
        public List<String> getServices() {
            return null;
        }

        @Override
        public String getRegisterCenterStatus() {
            return status;
        }

        @Override
        public String getInstanceStatus() {
            return null;
        }

        @Override
        public void updateInstanceStatus(String status) {

        }
    }
}
