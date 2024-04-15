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

package com.huaweicloud.sermant.router.dubbo.service;

import com.huaweicloud.sermant.router.common.cache.DubboCache;
import com.huaweicloud.sermant.router.common.constants.RouterConstant;

import com.alibaba.dubbo.common.URL;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test ClusterServiceImpl
 *
 * @author provenceee
 * @since 2022-03-21
 */
public class ClusterUtilsServiceTest {
    private static final URL ALIBABA_URL =
            URL.valueOf("dubbo://localhost:8080/com.huawei.foo.BarTest?bar=foo&application=bar");

    private static final org.apache.dubbo.common.URL APACHE_URL = org.apache.dubbo.common.URL
            .valueOf("dubbo://localhost:8081/com.huawei.foo.FooTest?foo=bar&application=foo");

    private static final int EXPECT_LENGTH = 2;

    private final ClusterUtilsService clusterUtilsService;

    private final Map<String, String> map = new HashMap<>();

    /**
     * Constructor
     */
    public ClusterUtilsServiceTest() {
        clusterUtilsService = new ClusterUtilsServiceImpl();
        map.put(RouterConstant.META_VERSION_KEY, "0.0.1");
        map.put(RouterConstant.META_ZONE_KEY, "foo");
        map.put(RouterConstant.PARAMETERS_KEY_PREFIX + "bar", "bar");
        map.put(null, "foo");
    }

    /**
     * Test Alibaba URL
     *
     * @see com.alibaba.dubbo.common.URL
     */
    @Test
    public void testAlibaba() {
        // the array length does not match
        Object[] arguments = new Object[1];
        arguments[0] = ALIBABA_URL;
        clusterUtilsService.doBefore(arguments);
        Assert.assertNull(DubboCache.INSTANCE.getApplication("com.huawei.foo.BarTest"));

        // normal
        arguments = new Object[EXPECT_LENGTH];
        arguments[0] = ALIBABA_URL;
        arguments[1] = map;
        clusterUtilsService.doBefore(arguments);
        Map<?, ?> resultMap = (Map<?, ?>) arguments[1];
        Assert.assertNull(resultMap.get(RouterConstant.META_VERSION_KEY));
        Assert.assertNull(resultMap.get(RouterConstant.META_ZONE_KEY));
        Assert.assertNull(resultMap.get(RouterConstant.PARAMETERS_KEY_PREFIX + "bar"));
        Assert.assertEquals("foo", resultMap.get(null));
        Assert.assertEquals("bar", DubboCache.INSTANCE.getApplication("com.huawei.foo.BarTest"));

        // arguments[1] is null
        arguments[1] = null;
        clusterUtilsService.doBefore(arguments);
        Assert.assertNull(arguments[1]);
    }

    /**
     * test apache url
     *
     * @see org.apache.dubbo.common.URL
     */
    @Test
    public void testApache() {
        // the array length does not match
        Object[] arguments = new Object[1];
        arguments[0] = APACHE_URL;
        clusterUtilsService.doBefore(arguments);
        Assert.assertNull(DubboCache.INSTANCE.getApplication("com.huawei.foo.FooTest"));

        // normal
        arguments = new Object[EXPECT_LENGTH];
        arguments[0] = APACHE_URL;
        arguments[1] = map;
        clusterUtilsService.doBefore(arguments);
        Map<?, ?> resultMap = (Map<?, ?>) arguments[1];
        Assert.assertNull(resultMap.get(RouterConstant.META_VERSION_KEY));
        Assert.assertNull(resultMap.get(RouterConstant.META_ZONE_KEY));
        Assert.assertNull(resultMap.get(RouterConstant.PARAMETERS_KEY_PREFIX + "bar"));
        Assert.assertEquals("foo", resultMap.get(null));
        Assert.assertEquals("foo", DubboCache.INSTANCE.getApplication("com.huawei.foo.FooTest"));

        // arguments[1] is null
        arguments[1] = null;
        clusterUtilsService.doBefore(arguments);
        Assert.assertNull(arguments[1]);
    }
}