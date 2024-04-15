/*
 * Copyright (C) 2024-2024 Sermant Authors. All rights reserved.
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

package com.huaweicloud.sermant.router.transmit.interceptor;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.router.common.utils.ThreadLocalUtils;
import com.huaweicloud.sermant.router.transmit.BaseTest;
import com.huaweicloud.sermant.router.transmit.RunnableAndCallable;
import com.huaweicloud.sermant.router.transmit.wrapper.CallableWrapper;
import com.huaweicloud.sermant.router.transmit.wrapper.RunnableAndCallableWrapper;
import com.huaweicloud.sermant.router.transmit.wrapper.RunnableWrapper;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.Callable;

/**
 * Test ScheduledExecutorService
 *
 * @author provenceee
 * @since 2024-01-16
 */
public class ScheduledExecutorServiceInterceptorTest extends BaseTest {
    private final ScheduledExecutorServiceInterceptor interceptor;

    private final ExecuteContext context;

    private final Object[] arguments;

    public ScheduledExecutorServiceInterceptorTest() {
        arguments = new Object[1];
        interceptor = new ScheduledExecutorServiceInterceptor();
        context = ExecuteContext.forMemberMethod(new Object(), null, arguments, null, null);
    }

    @Test
    public void testBefore() {
        // Test null
        interceptor.before(context);
        Assert.assertNull(context.getArguments()[0]);

        // The test has no routing data
        Runnable runnable = () -> {
        };
        arguments[0] = runnable;
        interceptor.before(context);
        Assert.assertEquals(runnable, context.getArguments()[0]);

        // The test has no routing data
        ThreadLocalUtils.addRequestTag(Collections.singletonMap("foo", Collections.singletonList("bar")));

        // The test has already been packaged
        RunnableWrapper<?> runnableWrapper = new RunnableWrapper<>(null, null, null, false);
        arguments[0] = runnableWrapper;
        interceptor.before(context);
        Assert.assertEquals(runnableWrapper, context.getArguments()[0]);

        // Wrap RunnableAndCallable
        RunnableAndCallable runnableAndCallable = new RunnableAndCallable();
        arguments[0] = runnableAndCallable;
        interceptor.before(context);
        Assert.assertTrue(context.getArguments()[0] instanceof RunnableAndCallableWrapper);

        // Wrap Runnable
        Runnable runnable1 = () -> {
        };
        arguments[0] = runnable1;
        interceptor.before(context);
        Assert.assertTrue(context.getArguments()[0] instanceof RunnableWrapper);

        // Wrap Callable
        Callable<Object> callable = () -> null;
        arguments[0] = callable;
        interceptor.before(context);
        Assert.assertTrue(context.getArguments()[0] instanceof CallableWrapper);
    }

    @After
    public void clear() {
        ThreadLocalUtils.removeRequestData();
        ThreadLocalUtils.removeRequestTag();
    }
}