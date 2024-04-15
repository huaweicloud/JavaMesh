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

import org.junit.Assert;
import org.junit.Test;

/**
 * State Manager Test
 *
 * @author zhouss
 * @since 2022-10-09
 */
public class ServiceStatsManagerTest extends BaseTest {
    @Test
    public void getServiceStats() {
        final ServiceStats test = ServiceStatsManager.INSTANCE.getServiceStats("test");
        Assert.assertNotNull(test);
    }

    @Test
    public void getInstanceStats() {
        final InstanceStats instanceStats = ServiceStatsManager.INSTANCE
                .getInstanceStats(CommonUtils.buildInstance("test", 8989));
        Assert.assertNotNull(instanceStats);
    }

}
