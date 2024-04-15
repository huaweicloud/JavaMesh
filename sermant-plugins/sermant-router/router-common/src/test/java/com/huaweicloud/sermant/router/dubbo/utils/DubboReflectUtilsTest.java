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

package com.huaweicloud.sermant.router.dubbo.utils;

import com.huaweicloud.sermant.core.utils.StringUtils;
import com.huaweicloud.sermant.router.common.utils.DubboReflectUtils;
import com.huaweicloud.sermant.router.common.utils.ReflectUtils;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcInvocation;

import org.apache.dubbo.common.utils.MapUtils;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Reflex test class
 *
 * @author provenceee
 * @since 2022-03-18
 */
public class DubboReflectUtilsTest {
    private static final URL ALIBABA_URL = URL
            .valueOf("dubbo://localhost:8080/com.huaweicloud.foo.BarTest?bar=foo&version=0.0.1");

    private static final org.apache.dubbo.common.URL APACHE_URL = org.apache.dubbo.common.URL
            .valueOf("dubbo://localhost:8081/com.huaweicloud.foo.FooTest?foo=bar&version=0.0.1");

    private static final String BAR = "bar";

    private static final String FOO = "foo";

    private final ApplicationConfig alibabaConfig;

    private final org.apache.dubbo.config.ApplicationConfig apacheConfig;

    /**
     * Constructor
     */
    public DubboReflectUtilsTest() {
        alibabaConfig = new ApplicationConfig(BAR);
        DubboReflectUtils.setParameters(alibabaConfig, Collections.singletonMap(BAR, FOO));
        apacheConfig = new org.apache.dubbo.config.ApplicationConfig(FOO);
        DubboReflectUtils.setParameters(apacheConfig, Collections.singletonMap(FOO, BAR));
    }

    /**
     * Test obtaining queryMap
     */
    @Test
    public void testGetQueryMap() {
        Map<String, String> queryMap = DubboReflectUtils.getQueryMap(new Entity());
        Assert.assertNotNull(queryMap);
        Assert.assertEquals(BAR, queryMap.get(FOO));
    }

    /**
     * Test to get private fields
     */
    @Test
    public void testGetFieldValue() {
        Map<String, String> queryMap = (Map<String, String>) ReflectUtils.getFieldValue(new Entity(), "queryMap")
                .orElse(null);
        Assert.assertNotNull(queryMap);
        Assert.assertEquals(BAR, queryMap.get(FOO));

        Assert.assertFalse(ReflectUtils.getFieldValue(new Entity(), "test").isPresent());
    }

    /**
     * Get the parameters
     *
     * @see com.alibaba.dubbo.common.URL
     * @see org.apache.dubbo.common.URL
     */
    @Test
    public void testGetParameter() {
        Assert.assertEquals(FOO, DubboReflectUtils.getParameter(ALIBABA_URL, BAR));
        Assert.assertEquals(BAR, DubboReflectUtils.getParameter(APACHE_URL, FOO));
    }

    /**
     * Get the application parameters
     *
     * @see com.alibaba.dubbo.config.ApplicationConfig
     * @see org.apache.dubbo.config.ApplicationConfig
     */
    @Test
    public void testGetParameters() {
        Assert.assertEquals(FOO, DubboReflectUtils.getParameters(alibabaConfig).get(BAR));
        Assert.assertEquals(BAR, DubboReflectUtils.getParameters(APACHE_URL).get(FOO));
    }

    /**
     * Get the URL
     *
     * @see com.alibaba.dubbo.rpc.Invoker
     * @see org.apache.dubbo.rpc.Invoker
     */
    @Test
    public void testGetUrl() {
        Assert.assertEquals(ALIBABA_URL, DubboReflectUtils.getUrl(new AlibabaInvoker<>()));
        Assert.assertEquals(APACHE_URL, DubboReflectUtils.getUrl(new ApacheInvoker<>()));
    }

    /**
     * Obtain the name of the service interface
     *
     * @see com.alibaba.dubbo.common.URL
     * @see org.apache.dubbo.common.URL
     */
    @Test
    public void testGetServiceInterface() {
        Assert.assertEquals("com.huaweicloud.foo.BarTest", DubboReflectUtils.getServiceInterface(ALIBABA_URL));
        Assert.assertEquals("com.huaweicloud.foo.FooTest", DubboReflectUtils.getServiceInterface(APACHE_URL));
    }

    /**
     * Obtain the name of the service interface
     *
     * @see com.alibaba.dubbo.common.URL
     * @see org.apache.dubbo.common.URL
     */
    @Test
    public void testGetServiceKey() {
        Assert.assertEquals("com.huaweicloud.foo.BarTest:0.0.1", DubboReflectUtils.getServiceKey(ALIBABA_URL));
        Assert.assertEquals("com.huaweicloud.foo.FooTest:0.0.1", DubboReflectUtils.getServiceKey(APACHE_URL));
    }

    /**
     * Obtain the name of the dubbo request method
     *
     * @see com.alibaba.dubbo.rpc.Invocation
     * @see org.apache.dubbo.rpc.Invocation
     */
    @Test
    public void testGetMethodName() {
        Assert.assertEquals("BarTest", DubboReflectUtils.getMethodName(new AlibabaInvocation()));
        Assert.assertEquals("FooTest", DubboReflectUtils.getMethodName(new ApacheInvocation()));
    }

    /**
     * Obtain the parameters of the dubbo request
     *
     * @see com.alibaba.dubbo.rpc.Invocation
     * @see org.apache.dubbo.rpc.Invocation
     */
    @Test
    public void testGetArguments() {
        Object[] alibabaArguments = DubboReflectUtils.getArguments(new AlibabaInvocation());
        Assert.assertEquals(2, alibabaArguments.length);
        Assert.assertEquals(BAR, alibabaArguments[0]);
        Assert.assertEquals(FOO, alibabaArguments[1]);

        Object[] apacheArguments = DubboReflectUtils.getArguments(new ApacheInvocation());
        Assert.assertEquals(2, alibabaArguments.length);
        Assert.assertEquals(FOO, apacheArguments[0]);
        Assert.assertEquals(BAR, apacheArguments[1]);
    }

    /**
     * Set the parameters at the time of registration
     *
     * @see com.alibaba.dubbo.config.ApplicationConfig
     * @see org.apache.dubbo.config.ApplicationConfig
     */
    @Test
    public void testSetParameters() {
        // 由于注册时已经调用过ReflectUtils.setParameters方法，所以这里只要验证值就行
        Assert.assertEquals(FOO, alibabaConfig.getParameters().get(BAR));
        Assert.assertEquals(BAR, apacheConfig.getParameters().get(FOO));
    }

    /**
     * Obtain attachments from null
     *
     * @see com.alibaba.dubbo.rpc.Invocation
     * @see org.apache.dubbo.rpc.Invocation
     */
    @Test
    public void testGetAttachmentsWithNull() {
        Assert.assertEquals(0, DubboReflectUtils.getAttachments(null).size());
    }

    /**
     * Obtain attachments from Invocation
     *
     * @see com.alibaba.dubbo.rpc.Invocation
     * @see org.apache.dubbo.rpc.Invocation
     */
    @Test
    public void testGetAttachmentsWithInvocation() {
        AlibabaInvocation alibabaInvocation = new AlibabaInvocation();
        alibabaInvocation.getAttachments().put(BAR, FOO);
        Assert.assertEquals(FOO, DubboReflectUtils.getAttachments(alibabaInvocation).get(BAR));

        ApacheInvocation apacheInvocation = new ApacheInvocation();
        apacheInvocation.getObjectAttachments().put(FOO, BAR);
        Assert.assertEquals(BAR, DubboReflectUtils.getAttachments(apacheInvocation).get(FOO));
    }

    /**
     * Get attachments from RpcContext
     *
     * @see com.alibaba.dubbo.rpc.RpcContext
     */
    @Test
    public void testGetAttachmentsWithRpcContext() {
        RpcContext.getContext().getAttachments().put(BAR, FOO);
        Assert.assertEquals(FOO, DubboReflectUtils.getAttachments(new RpcInvocation()).get(BAR));
    }

    /**
     * Get the permission check class
     */
    @Test
    public void testGetAccessibleObject() throws NoSuchMethodException {
        Method method = Entity.class.getDeclaredMethod("getQueryMap");
        Assert.assertFalse(method.isAccessible());
        Assert.assertTrue(ReflectUtils.getAccessibleObject(method).isAccessible());
    }

    /**
     * Test class
     *
     * @since 2022-03-18
     */
    public static class Entity {
        private final Map<String, String> queryMap = new HashMap<>();

        /**
         * Constructor
         */
        public Entity() {
            queryMap.put(FOO, BAR);
        }

        private Map<String, String> getQueryMap() {
            return queryMap;
        }
    }

    /**
     * Test class
     *
     * @since 2022-03-18
     */
    public static class AlibabaInvoker<T> implements Invoker<T> {
        @Override
        public Class<T> getInterface() {
            return null;
        }

        @Override
        public Result invoke(Invocation invocation) throws RpcException {
            return null;
        }

        @Override
        public URL getUrl() {
            return ALIBABA_URL;
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public void destroy() {
        }
    }

    /**
     * Test class
     *
     * @since 2022-03-18
     */
    public static class ApacheInvoker<T> implements org.apache.dubbo.rpc.Invoker<T> {
        @Override
        public Class<T> getInterface() {
            return null;
        }

        @Override
        public org.apache.dubbo.rpc.Result invoke(org.apache.dubbo.rpc.Invocation invocation)
                throws org.apache.dubbo.rpc.RpcException {
            return null;
        }

        @Override
        public org.apache.dubbo.common.URL getUrl() {
            return APACHE_URL;
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public void destroy() {
        }
    }

    /**
     * Test class
     *
     * @since 2022-03-18
     */
    public static class AlibabaInvocation implements Invocation {
        private final Map<String, String> attachments = new HashMap<>();

        @Override
        public String getMethodName() {
            return "BarTest";
        }

        @Override
        public Class<?>[] getParameterTypes() {
            return new Class[]{String.class, String.class};
        }

        @Override
        public Object[] getArguments() {
            return new Object[]{BAR, FOO};
        }

        @Override
        public Map<String, String> getAttachments() {
            return attachments;
        }

        @Override
        public String getAttachment(String key) {
            return attachments.get(key);
        }

        @Override
        public String getAttachment(String key, String defaultValue) {
            return attachments.getOrDefault(key, defaultValue);
        }

        @Override
        public Invoker<?> getInvoker() {
            return new AlibabaInvoker<>();
        }

        @Override
        public Object put(Object o1, Object o2) {
            return new Object();
        }

        @Override
        public Object get(Object obj) {
            return new Object();
        }

        @Override
        public Map<Object, Object> getAttributes() {
            return Collections.emptyMap();
        }
    }

    /**
     * Test class
     *
     * @since 2022-03-18
     */
    public static class ApacheInvocation implements org.apache.dubbo.rpc.Invocation {
        private final Map<String, Object> attachments = new HashMap<>();

        @Override
        public String getTargetServiceUniqueName() {
            return "";
        }

        @Override
        public String getProtocolServiceKey() {
            return "";
        }

        @Override
        public String getMethodName() {
            return "FooTest";
        }

        @Override
        public String getServiceName() {
            return "";
        }

        @Override
        public Class<?>[] getParameterTypes() {
            return new Class[]{String.class, String.class};
        }

        @Override
        public Object[] getArguments() {
            return new Object[]{FOO, BAR};
        }

        @Override
        public Map<String, String> getAttachments() {
            return MapUtils.objectToStringMap(attachments);
        }

        @Override
        public Map<String, Object> getObjectAttachments() {
            return attachments;
        }

        @Override
        public void setAttachment(String key, String value) {
            attachments.put(key, value);
        }

        @Override
        public void setAttachment(String key, Object value) {
            attachments.put(key, value);
        }

        @Override
        public void setObjectAttachment(String key, Object value) {
            attachments.put(key, value);
        }

        @Override
        public void setAttachmentIfAbsent(String key, String value) {
            attachments.putIfAbsent(key, value);
        }

        @Override
        public void setAttachmentIfAbsent(String key, Object value) {
            attachments.putIfAbsent(key, value);
        }

        @Override
        public void setObjectAttachmentIfAbsent(String key, Object value) {
            attachments.putIfAbsent(key, value);
        }

        @Override
        public String getAttachment(String key) {
            Object value = attachments.get(key);
            return value instanceof String ? (String) value : null;
        }

        @Override
        public String getAttachment(String key, String defaultValue) {
            Object value = attachments.get(key);
            if (value instanceof String) {
                String strValue = (String) value;
                return StringUtils.isBlank(strValue) ? defaultValue : strValue;
            }
            return defaultValue;
        }

        @Override
        public Object getObjectAttachment(String key) {
            return attachments.get(key);
        }

        @Override
        public Object getObjectAttachment(String key, Object defaultValue) {
            return attachments.getOrDefault(key, defaultValue);
        }

        @Override
        public org.apache.dubbo.rpc.Invoker<?> getInvoker() {
            return new ApacheInvoker<>();
        }

        @Override
        public Object put(Object key, Object value) {
            return new Object();
        }

        @Override
        public Object get(Object key) {
            return new Object();
        }

        @Override
        public Map<Object, Object> getAttributes() {
            return Collections.emptyMap();
        }
    }
}