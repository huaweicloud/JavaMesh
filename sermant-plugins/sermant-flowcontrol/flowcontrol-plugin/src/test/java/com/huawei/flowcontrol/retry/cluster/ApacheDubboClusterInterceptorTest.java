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

package com.huawei.flowcontrol.retry.cluster;

import static org.junit.Assert.*;

import com.huawei.flowcontrol.common.config.CommonConst;
import com.huawei.flowcontrol.common.config.FlowControlConfig;
import com.huawei.flowcontrol.common.util.ConvertUtils;
import com.huawei.flowcontrol.retry.AlibabaDubboInvokerInterceptor;
import com.huawei.flowcontrol.retry.ApacheDubboInvokerInterceptor;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;
import com.huaweicloud.sermant.core.service.ServiceManager;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.cluster.Directory;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Collections;

/**
 * Apache dubbo retry invoke test
 *
 * @author zhouss
 * @since 2022-08-31
 */
public class ApacheDubboClusterInterceptorTest {
    private MockedStatic<PluginConfigManager> pluginConfigManagerMockedStatic;

    @After
    public void tearDown() {
        pluginConfigManagerMockedStatic.close();
    }

    /**
     * preinitialization
     *
     * @throws Exception initialization failure thrown
     */
    @Before
    public void before() throws Exception {
        pluginConfigManagerMockedStatic = Mockito
                .mockStatic(PluginConfigManager.class);
        pluginConfigManagerMockedStatic.when(() -> PluginConfigManager.getPluginConfig(FlowControlConfig.class))
                .thenReturn(new FlowControlConfig());
    }

    @Test
    public void test() throws Exception {
        final ApacheDubboInvokerInterceptor interceptor = new ApacheDubboInvokerInterceptor();
        final ExecuteContext executeContext = buildContext();
        interceptor.before(executeContext);
        interceptor.after(executeContext);
        Assert.assertTrue(executeContext.getResult() instanceof AsyncRpcResult);
        Assert.assertEquals(((AsyncRpcResult) executeContext.getResult()).getValue(), getResult());
    }

    private ExecuteContext buildContext() throws NoSuchMethodException {
        final Invocation invocation = Mockito.mock(Invocation.class);
        String interfaceName = this.getClass().getName();
        String version = "1.0.0";
        Mockito.when(invocation.getMethodName()).thenReturn("test");
        Mockito.when(invocation.getAttachment(ConvertUtils.DUBBO_ATTACHMENT_VERSION)).thenReturn(version);
        Mockito.when(invocation.getArguments()).thenReturn(new Object[]{"test"});
        final Invoker invoker = Mockito.mock(Invoker.class);
        Mockito.when(invoker.getInterface()).thenReturn(this.getClass());
        final URL url = Mockito.mock(URL.class);
        Mockito.when(url.getParameter(CommonConst.GENERIC_INTERFACE_KEY, interfaceName)).thenReturn(interfaceName);
        Mockito.when(url.getParameter(CommonConst.URL_VERSION_KEY, version)).thenReturn(version);
        Mockito.when(url.getParameter(CommonConst.DUBBO_REMOTE_APPLICATION)).thenReturn("application");
        Mockito.when(invoker.getUrl()).thenReturn(url);
        Mockito.when(invocation.getInvoker()).thenReturn(invoker);
        final LoadBalance loadBalance = Mockito.mock(LoadBalance.class);
        final Directory directory = Mockito.mock(Directory.class);
        Mockito.when(directory.getUrl()).thenReturn(url);
        Mockito.when(invoker.invoke(invocation)).thenReturn(AsyncRpcResult.newDefaultAsyncResult(getResult(), invocation));
        final ApacheDubboClusterInvoker clusterInvoker = new ApacheDubboClusterInvoker<>(directory);
        return ExecuteContext.forMemberMethod(clusterInvoker,
                this.getClass().getDeclaredMethod("getResult"),
                new Object[] {invocation, Collections.singletonList(invoker), loadBalance},
                Collections.emptyMap(), Collections.emptyMap());
    }

    private String getResult() {
        return "result";
    }
}
