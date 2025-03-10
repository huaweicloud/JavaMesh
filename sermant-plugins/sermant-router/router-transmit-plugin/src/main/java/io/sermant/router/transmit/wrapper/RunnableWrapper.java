/*
 * Copyright (C) 2024-2024 Huawei Technologies Co., Ltd. All rights reserved.
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

package io.sermant.router.transmit.wrapper;

import io.sermant.router.common.request.RequestData;
import io.sermant.router.common.request.RequestTag;

/**
 * Runnable packaging class
 *
 * @param <T> Generics
 * @author provenceee
 * @since 2024-01-16
 */
public class RunnableWrapper<T> extends AbstractThreadWrapper<T> implements Runnable {
    /**
     * Constructor
     *
     * @param runnable runnable
     * @param requestTag Request tags
     * @param requestData Request data
     * @param cannotTransmit Whether the thread variable needs to be deleted before the method can be executed
     */
    public RunnableWrapper(Runnable runnable, RequestTag requestTag, RequestData requestData, boolean cannotTransmit) {
        super(runnable, null, requestTag, requestData, cannotTransmit);
    }
}
