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

package com.huawei.discovery.retry.config;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Retry the configuration
 *
 * @author zhouss
 * @since 2022-10-18
 */
public interface RetryConfig {
    /**
     * Retry the configuration name
     *
     * @return name
     */
    String getName();

    /**
     * Get the retry wait time
     *
     * @return Retry wait time
     */
    long getRetryRetryWaitMs();

    /**
     * Obtain the retry time based on the number of retries
     *
     * @param retryCount Number of retries
     * @return Predicate
     */
    Function<Integer, Long> getRetryWaitMs(int retryCount);

    /**
     * Returns the maximum number of retries
     *
     * @return Maximum number of retries
     */
    int getMaxRetry();

    /**
     * Retry exception judgment
     *
     * @return Predicate
     */
    Predicate<Throwable> getThrowablePredicate();

    /**
     * The result retries check
     *
     * @return Predicate
     */
    Predicate<Object> getResultPredicate();
}
