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

package com.huawei.discovery.service.lb.stats;

import com.huawei.discovery.service.lb.rule.BaseTest;
import com.huawei.discovery.service.lb.utils.CommonUtils;

import com.huaweicloud.sermant.core.utils.ReflectUtils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

/**
 * Status statistics test
 *
 * @author zhouss
 * @since 2022-10-09
 */
public class ServiceStatsTest extends BaseTest {
    private final String serviceName = "stats";

    @Test
    public void getStats() {
        final ServiceStats serviceStats = new ServiceStats(serviceName);
        final InstanceStats stats = serviceStats.getStats(CommonUtils.buildInstance(serviceName, 8080));
        Assert.assertNotNull(stats);
        final Optional<Object> createStats = ReflectUtils.invokeMethod(serviceStats, "createStats", null, null);
        Assert.assertTrue(createStats.isPresent());
    }

    @Test
    public void getServiceName() {
        final ServiceStats serviceStats = new ServiceStats(serviceName);
        Assert.assertEquals(serviceStats.getServiceName(), serviceName);
    }
}
