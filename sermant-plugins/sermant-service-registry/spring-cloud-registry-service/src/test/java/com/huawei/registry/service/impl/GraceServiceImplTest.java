/*
 * Copyright (C) 2022-2024 Sermant Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huawei.registry.service.impl;

import com.huawei.registry.config.GraceConfig;
import com.huawei.registry.config.RegisterConfig;
import com.huawei.registry.service.cache.AddressCache;

import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;
import com.huaweicloud.sermant.core.utils.ReflectUtils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Test the offline notification logic
 *
 * @author zhouss
 * @since 2022-06-30
 */
public class GraceServiceImplTest {
    /**
     * PluginConfigManager mock object
     */
    public MockedStatic<PluginConfigManager> pluginConfigManagerMockedStatic;

    @Before
    public void setUp() {
        pluginConfigManagerMockedStatic = Mockito.mockStatic(PluginConfigManager.class);
        pluginConfigManagerMockedStatic.when(() -> PluginConfigManager.getPluginConfig(GraceConfig.class))
                .thenReturn(new GraceConfig());
        pluginConfigManagerMockedStatic.when(() -> PluginConfigManager.getPluginConfig(RegisterConfig.class))
                .thenReturn(new RegisterConfig());
    }

    @After
    public void tearDown() {
        pluginConfigManagerMockedStatic.close();
        AddressCache.INSTANCE.cleanCache();
    }

    /**
     * Test the shutdown logic as well as the loading handler logic
     */
    @Test
    public void testShutDown() {
        final GraceServiceImpl spy = Mockito.spy(new GraceServiceImpl());
        spy.shutdown();
        spy.addAddress("test");
        Mockito.doCallRealMethod().when(spy).shutdown();
        Mockito.verify(spy, Mockito.times(1)).shutdown();
        Mockito.verify(spy, Mockito.times(1)).addAddress("test");
        Assert.assertFalse(AddressCache.INSTANCE.getAddressSet().isEmpty());
        final Optional<Object> shutdown = ReflectUtils.getFieldValue(spy, "SHUTDOWN");
        Assert.assertTrue(shutdown.isPresent() && shutdown.get() instanceof AtomicBoolean);
        Assert.assertTrue(((AtomicBoolean) shutdown.get()).get());
    }
}
