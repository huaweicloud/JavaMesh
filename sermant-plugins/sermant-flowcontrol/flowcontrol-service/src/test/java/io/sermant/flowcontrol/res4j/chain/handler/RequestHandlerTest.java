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

package io.sermant.flowcontrol.res4j.chain.handler;

import io.sermant.core.operation.OperationManager;
import io.sermant.core.operation.converter.api.YamlConverter;
import io.sermant.core.plugin.config.PluginConfigManager;
import io.sermant.flowcontrol.common.config.FlowControlConfig;
import io.sermant.flowcontrol.common.config.XdsFlowControlConfig;
import io.sermant.flowcontrol.common.core.ResolverManager;
import io.sermant.flowcontrol.common.core.match.MatchGroupResolver;
import io.sermant.flowcontrol.res4j.chain.HandlerChainEntry;
import io.sermant.flowcontrol.res4j.chain.context.ChainContext;
import io.sermant.flowcontrol.res4j.windows.WindowsArray;
import io.sermant.implement.operation.converter.YamlConverterImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * test all request handlers
 *
 * @author zhouss
 * @since 2022-08-30
 */
public class RequestHandlerTest {
    private HandlerChainEntry entry;

    private MockedStatic<PluginConfigManager> pluginConfigManagerMockedStatic;

    private WindowsArray windowsArray;

    private final String sourceName = this.getClass().getName();

    private final List<RequestTest> testList = new ArrayList<>();

    private MockedStatic<OperationManager> operationManagerMockedStatic;

    /**
     * preprocessing
     */
    @Before
    public void setUp() {
        operationManagerMockedStatic = Mockito.mockStatic(OperationManager.class);
        operationManagerMockedStatic.when(() -> OperationManager.getOperation(YamlConverter.class))
                .thenReturn(new YamlConverterImpl());
        pluginConfigManagerMockedStatic = Mockito.mockStatic(PluginConfigManager.class);
        FlowControlConfig flowControlConfig = new FlowControlConfig();
        flowControlConfig.setEnableStartMonitor(true);
        flowControlConfig.setEnableSystemAdaptive(true);
        flowControlConfig.setEnableSystemRule(true);
        pluginConfigManagerMockedStatic
                .when(() -> PluginConfigManager.getPluginConfig(FlowControlConfig.class))
                .thenReturn(flowControlConfig);
        pluginConfigManagerMockedStatic.when(() -> PluginConfigManager.getPluginConfig(XdsFlowControlConfig.class))
                .thenReturn(new XdsFlowControlConfig());
        publishMatchGroup();
        loadTests();
        entry = HandlerChainEntry.INSTANCE;
        setUpSystemRule();
    }

    private void setUpSystemRule() {
        WindowsArray.INSTANCE.initWindowsArray();
    }

    private void loadTests() {
        for (RequestTest requestTest : ServiceLoader.load(RequestTest.class)) {
            testList.add(requestTest);
        }
    }

    private void publishMatchGroup() {
        ResolverManager.INSTANCE.resolve(buildKey(MatchGroupResolver.CONFIG_KEY), getMatchGroupRule(), false);
    }

    private String buildKey(String prefix) {
        return prefix + "." + RequestTest.BUSINESS_NAME;
    }

    @After
    public void close() {
        pluginConfigManagerMockedStatic.close();
        operationManagerMockedStatic.close();
        ChainContext.remove();
    }

    /**
     * test rate limiting
     */
    @Test
    public void test() {
        for(RequestTest requestTest : testList) {
            requestTest.publishRule();
            requestTest.test(entry, sourceName);
            requestTest.clear();
        }
    }

    private String getMatchGroupRule() {
        return "alias: test\n"
                + "matches:\n"
                + "- apiPath:\n"
                + "    exact: " + RequestTest.API_PATH + "\n"
                + "  headers: {}\n"
                + "  method:\n"
                + "  - POST\n"
                + "  name: degrade\n"
                + "  showAlert: false\n"
                + "  uniqIndex: c3w7x";
    }
}
