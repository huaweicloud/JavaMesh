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
 */

package com.huawei.registry.support;

import com.huawei.registry.config.ConfigConstants;

import com.huaweicloud.sermant.core.common.LoggerFactory;

import java.util.function.Consumer;

/**
 * Register for Extended Time
 *
 * @author zhouss
 * @since 2022-05-17
 */
public class RegistryDelayConsumer implements Consumer<Long> {
    /**
     * Delayed sleep
     *
     * @param sleepTime Delay time in seconds
     */
    @Override
    public void accept(Long sleepTime) {
        if (sleepTime == null) {
            return;
        }
        try {
            Thread.sleep(sleepTime * ConfigConstants.SEC_DELTA);
        } catch (InterruptedException ex) {
            LoggerFactory.getLogger().severe("[GracePlugin] Delay registry failed!");
        }
    }
}
