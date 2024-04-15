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

package com.huawei.flowcontrol.res4j.handler.exception;

import com.huawei.flowcontrol.common.entity.FlowControlResult;

import java.util.function.BiConsumer;

/**
 * flow control exception handler
 *
 * @param <E> exception type
 * @author zhouss
 * @since 2022-08-05
 */
public interface ExceptionHandler<E extends Throwable> extends BiConsumer<E, FlowControlResult> {
    /**
     * object exception handling
     *
     * @return exception type
     */
    Class<E> targetException();
}
