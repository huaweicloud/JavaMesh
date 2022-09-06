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

package com.huawei.dynamic.config.init;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程工厂测试
 *
 * @author zhouss
 * @since 2022-09-05
 */
public class DynamicConfigThreadFactoryTest {
    /**
     * 测试线程工厂是否生效
     */
    @Test
    public void test() {
        String threadName = "thread-1";
        final DynamicConfigThreadFactory dynamicConfigThreadFactory = new DynamicConfigThreadFactory(threadName);
        final ExecutorService executorService = Executors.newFixedThreadPool(1, dynamicConfigThreadFactory);
        executorService.execute(() -> {
            Assert.assertEquals(Thread.currentThread().getName(), threadName);
        });
        executorService.shutdown();
    }
}
