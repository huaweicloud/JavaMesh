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

package com.huaweicloud.sermant.tag.transmission.grpc.interceptors;

import com.huaweicloud.sermant.core.utils.tag.TrafficTag;
import com.huaweicloud.sermant.core.utils.tag.TrafficUtils;

import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ServerHeaderInterceptorTest
 *
 * @author daizhenyu
 * @since 2023-08-31
 **/
public class ServerHeaderInterceptorTest extends AbstractRpcInterceptorTest {
    private final ServerHeaderInterceptor interceptor = new ServerHeaderInterceptor();

    public ServerHeaderInterceptorTest() {
    }

    @Override
    public void doBefore(TrafficTag trafficTag) {
    }

    @Test
    public void testInterceptCall() {
        // Configure parameters required by the grpc interceptor
        ServerCall call = Mockito.mock(ServerCall.class);
        ServerCallHandler handler = Mockito.mock(ServerCallHandler.class);
        Metadata metadata;
        Map<String, List<String>> expectTag;
        Key<String> name = Key.of("name", Metadata.ASCII_STRING_MARSHALLER);
        Key<String> id = Key.of("id", Metadata.ASCII_STRING_MARSHALLER);

        // metadata is null
        interceptor.interceptCall(call, null, handler);
        Assert.assertNull(TrafficUtils.getTrafficTag());

        // The metadata contains traffic labels that all match the matching rules, and the value is not null.
        metadata = new Metadata();
        metadata.put(id, "001");
        metadata.put(name, "test001");
        expectTag = new HashMap<>();
        expectTag.put("id", Collections.singletonList("001"));
        expectTag.put("name", Collections.singletonList("test001"));
        interceptor.interceptCall(call, metadata, handler);
        Assert.assertEquals(TrafficUtils.getTrafficTag().getTag(), expectTag);

        // metadata contains null traffic tag
        metadata = new Metadata();
        metadata.put(name, "null");
        metadata.put(id, "001");
        expectTag = new HashMap<>();
        expectTag.put("id", Collections.singletonList("001"));
        expectTag.put("name", null);
        interceptor.interceptCall(call, metadata, handler);
        Assert.assertEquals(TrafficUtils.getTrafficTag().getTag(), expectTag);
    }

    @After
    public void afterTest() {
        Mockito.clearAllCaches();
    }
}