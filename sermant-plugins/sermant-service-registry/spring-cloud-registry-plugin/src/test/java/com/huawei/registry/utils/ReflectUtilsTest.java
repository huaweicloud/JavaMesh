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

package com.huawei.registry.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.huaweicloud.sermant.core.utils.ReflectUtils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

/**
 * Test the ReflectUtils utility class
 *
 * @author zhouss
 * @since 2022-06-29
 */
public class ReflectUtilsTest {
    private static final String FINAL_FIELD_NAME = "finalValue";

    /**
     * Test method calls
     */
    @Test
    public void testInvokeMethod() {
        final TestReflectObj testReflectObj = new TestReflectObj();
        final Optional<Object> son = ReflectUtils.invokeMethod(testReflectObj, "son", null, null);
        assertTrue(son.isPresent());
        assertEquals(son.get(), "the son");
        final Optional<Object> test = ReflectUtils.invokeMethod(testReflectObj, "test", null, null);
        assertTrue(test.isPresent());
        assertEquals(test.get(), "parent");
        final Optional<Object> hello = ReflectUtils
                .invokeMethod(testReflectObj, "hello", new Class[]{String.class}, new Object[]{"Bob"});
        assertTrue(hello.isPresent());
        assertEquals(hello.get(), "hello Bob");
    }

    /**
     * Test the setting field value
     */
    @Test
    public void setFieldValue() {
        final TestReflectObj testReflectObj = new TestReflectObj();
        ReflectUtils.setFieldValue(testReflectObj, FINAL_FIELD_NAME, "setFieldValue");
        final Optional<Object> fieldValue = ReflectUtils.getFieldValue(testReflectObj, FINAL_FIELD_NAME);
        Assert.assertTrue(fieldValue.isPresent());
        assertEquals("setFieldValue", fieldValue.get());
    }

    /**
     * Test to get field values
     */
    @Test
    public void getFieldValue() {
        final TestReflectObj testReflectObj = new TestReflectObj();
        final Optional<Object> finalValue = ReflectUtils.getFieldValue(testReflectObj, FINAL_FIELD_NAME);
        Assert.assertTrue(finalValue.isPresent());
        Assert.assertEquals(finalValue.get(), "final");
        final Optional<Object> staticFinalValue = ReflectUtils.getFieldValue(testReflectObj, "STATIC_FINAL_VALUE");
        Assert.assertTrue(staticFinalValue.isPresent());
        Assert.assertEquals(staticFinalValue.get(), "staticFinal");
    }

    /**
     * Parent class
     *
     * @since 2022-06-29
     */
    static class Parent {
        private String test() {
            return "parent";
        }
    }

    /**
     * Subclasses
     *
     * @since 2022-06-29
     */
    static class TestReflectObj extends Parent {
        private final String finalValue = "final";
        private static final String STATIC_FINAL_VALUE = "staticFinal";

        private String son() {
            return "the son";
        }

        private String hello(String name) {
            return "hello " + name;
        }
    }
}
