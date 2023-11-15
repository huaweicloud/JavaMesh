/*
 * Copyright (C) 2023-2023 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huaweicloud.sermant.tag.transmission.sofarpc.interceptors;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.utils.tag.TrafficTag;
import com.huaweicloud.sermant.core.utils.tag.TrafficUtils;

import com.alipay.sofa.rpc.core.request.SofaRequest;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * SofaRpcClientInterceptor类的单元测试
 *
 * @author daizhenyu
 * @since 2023-08-29
 **/
public class SofaRpcClientInterceptorTest extends AbstractRpcInterceptorTest {
    private final SofaRpcClientInterceptor interceptor = new SofaRpcClientInterceptor();

    public SofaRpcClientInterceptorTest() {
    }

    @Override
    public void doBefore(TrafficTag trafficTag) {
        TrafficUtils.setTrafficTag(trafficTag);
    }

    @Test
    public void testSofaRpcClient() {
        // 定义参数
        ExecuteContext context;
        ExecuteContext returnContext;
        Map<String, Object> expectRequestProps;

        // SofaRequest 为null
        context = buildContext(null);
        returnContext = interceptor.before(context);
        Assert.assertNull(returnContext.getArguments()[0]);

        // 流量标签透传开关关闭
        tagTransmissionConfig.setEnabled(false);
        context = buildContext(new SofaRequest());
        returnContext = interceptor.before(context);
        Assert.assertNull(((SofaRequest) returnContext.getArguments()[0]).getRequestProps());
        tagTransmissionConfig.setEnabled(true);

        // SofaRequest不为null,TrafficTag包含完整的流量标签
        context = buildContext(new SofaRequest());
        returnContext = interceptor.before(context);
        expectRequestProps = buildExpectRequestProps("id", "name");
        Assert.assertEquals(((SofaRequest) returnContext.getArguments()[0]).getRequestProps(), expectRequestProps);

        // TrafficTag只有部分流量标签
        context = buildContext(new SofaRequest());
        TrafficUtils.getTrafficTag().getTag().remove("id");
        returnContext = interceptor.before(context);
        expectRequestProps = buildExpectRequestProps("name");
        Assert.assertEquals(((SofaRequest) returnContext.getArguments()[0]).getRequestProps(), expectRequestProps);

        // TrafficTa没有tag信息
        TrafficUtils.removeTrafficTag();
        context = buildContext(new SofaRequest());
        returnContext = interceptor.before(context);
        Assert.assertNull(((SofaRequest) returnContext.getArguments()[0]).getRequestProps());
    }

    private Map<String, Object> buildExpectRequestProps(String... keys) {
        Map<String, Object> expectContext = new HashMap<>();
        for (String key : keys) {
            expectContext.put(key, fullTrafficTag.get(key).get(0));
        }
        return expectContext;
    }

    private ExecuteContext buildContext(SofaRequest sofaRequest) {
        Object[] arguments = new Object[]{sofaRequest};
        return ExecuteContext.forMemberMethod(new Object(), null, arguments, null, null);
    }
}