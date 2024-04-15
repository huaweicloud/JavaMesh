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

package com.huawei.registry.interceptors.health;

import com.huawei.registry.config.RegisterConfig;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Intercept requests on a regular basis
 *
 * @author zhouss
 * @since 2022-09-07
 */
public class ConsulWatchRequestInterceptorTest {
    private final RegisterConfig registerConfig = new RegisterConfig();

    private MockedStatic<PluginConfigManager> pluginConfigManagerMockedStatic;

    private ConsulWatchRequestInterceptor interceptor;

    @Before
    public void setUp() throws Exception {
        pluginConfigManagerMockedStatic = Mockito.mockStatic(PluginConfigManager.class);
        pluginConfigManagerMockedStatic.when(() -> PluginConfigManager.getPluginConfig(RegisterConfig.class))
                .thenReturn(registerConfig);
        interceptor = new ConsulWatchRequestInterceptor();
    }

    @After
    public void tearDown() throws Exception {
        pluginConfigManagerMockedStatic.close();
    }

    @Test
    public void doBefore() throws NoSuchMethodException {
        registerConfig.setOpenMigration(true);
        final ExecuteContext context = interceptor
                .doBefore(ExecuteContext.forMemberMethod(this, String.class.getDeclaredMethod("trim"),
                        null, null, null));
        Assert.assertFalse(context.isSkip());
        registerConfig.setOpenMigration(false);
        final ExecuteContext openContext = interceptor.doBefore(context);
        Assert.assertTrue(openContext.isSkip());
    }
}
