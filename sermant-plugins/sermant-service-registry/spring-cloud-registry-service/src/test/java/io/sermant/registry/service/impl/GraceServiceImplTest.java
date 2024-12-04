/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package io.sermant.registry.service.impl;

import io.sermant.core.plugin.config.PluginConfigManager;
import io.sermant.core.utils.ReflectUtils;
import io.sermant.registry.config.ConfigConstants;
import io.sermant.registry.config.GraceConfig;
import io.sermant.registry.config.RegisterConfig;
import io.sermant.registry.config.grace.GraceContext;
import io.sermant.registry.service.cache.AddressCache;

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

    private final GraceConfig graceConfig = new GraceConfig();

    @Before
    public void setUp() {
        pluginConfigManagerMockedStatic = Mockito.mockStatic(PluginConfigManager.class);
        pluginConfigManagerMockedStatic.when(() -> PluginConfigManager.getPluginConfig(GraceConfig.class))
                .thenReturn(graceConfig);
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
        graceConfig.setEnableSpring(true);
        graceConfig.setEnableGraceShutdown(true);
        graceConfig.setShutdownWaitTime(1);
        final long start = System.currentTimeMillis();
        GraceContext.INSTANCE.getGraceShutDownManager().increaseRequestCount();
        spy.addAddress("test");
        spy.shutdown();
        Mockito.doCallRealMethod().when(spy).shutdown();
        Mockito.verify(spy, Mockito.times(1)).shutdown();
        Mockito.verify(spy, Mockito.times(1)).addAddress("test");
        Assert.assertFalse(AddressCache.INSTANCE.getAddressSet().isEmpty());
        final Optional<Object> shutdown = ReflectUtils.getFieldValue(spy, "SHUTDOWN");
        Assert.assertTrue(shutdown.isPresent() && shutdown.get() instanceof AtomicBoolean);
        Assert.assertTrue(((AtomicBoolean) shutdown.get()).get());
        Assert.assertTrue(System.currentTimeMillis() - start
                >= graceConfig.getShutdownWaitTime() * ConfigConstants.SEC_DELTA);
        Assert.assertTrue(GraceContext.INSTANCE.getGraceShutDownManager().isShutDown());
        GraceContext.INSTANCE.getGraceShutDownManager().setShutDown(false);
        GraceContext.INSTANCE.getGraceShutDownManager().decreaseRequestCount();
    }
}
