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

package com.huaweicloud.sermant.tag.transmission.utils;

/**
 * RocketMQ's producer thread marking tool class
 *
 * @author lilai
 * @since 2023-09-16
 */
public class RocketmqProducerMarkUtils {
    /**
     * producer thread mark
     */
    private static final ThreadLocal<Boolean> PRODUCER_MARK = new ThreadLocal<>();

    private RocketmqProducerMarkUtils() {
    }

    /**
     * mark the current thread as a producer thread
     */
    public static void setProducerMark() {
        PRODUCER_MARK.set(true);
    }

    /**
     * Determines whether the current thread is a producer thread
     *
     * @return whether the current thread is a producer thread
     */
    public static boolean isProducer() {
        return PRODUCER_MARK.get() != null;
    }
}
