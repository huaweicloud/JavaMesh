/*
 * Copyright (C) 2021-2024 Sermant Authors. All rights reserved.
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

package com.huaweicloud.sermant.router.config.strategy;

import java.util.Map;
import java.util.function.Function;

/**
 * This is the label routing policy used to determine whether the instance matches
 *
 * @param <I> Instance generics
 * @param <T> Label generics
 * @author provenceee
 * @since 2021-12-08
 */
public interface InstanceStrategy<I, T> {
    /**
     * Check whether the instances match
     *
     * @param instance Instance
     * @param tags Label
     * @param mapper Methods to obtain metadata
     * @return Whether it matches or not
     */
    boolean isMatch(I instance, T tags, Function<I, Map<String, String>> mapper);
}