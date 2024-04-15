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

package com.huawei.discovery.entity;

import com.huawei.discovery.config.DiscoveryPluginConfig;
import com.huawei.discovery.utils.HttpConstants;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * A simple request recorder that currently only logs pre-requests
 *
 * @author zhouss
 * @since 2022-10-12
 */
public class SimpleRequestRecorder implements Recorder {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private final AtomicLong allRequestCount = new AtomicLong();

    private final DiscoveryPluginConfig discoveryPluginConfig;

    /**
     * Constructor
     */
    public SimpleRequestRecorder() {
        this.discoveryPluginConfig = PluginConfigManager.getPluginConfig(DiscoveryPluginConfig.class);
    }

    @Override
    public void beforeRequest() {
        if (!isEnable()) {
            return;
        }
        final long allRequest = allRequestCount.incrementAndGet();
        if (allRequest <= 0) {
            allRequestCount.set(0);
            LOGGER.info("SimpleRequestRecorder has over the max num of long, it has been reset to 0!");
        }
        LOGGER.info(String.format(Locale.ENGLISH,
                "currentTime: %s request count handle by plugin is: %s",
                HttpConstants.currentTime(), allRequest));
    }

    /**
     * Whether to turn on recording
     *
     * @return Whether to turn on recording
     */
    public boolean isEnable() {
        return this.discoveryPluginConfig.isEnableRequestCount();
    }

    @Override
    public void errorRequest(Throwable ex, long consumeTimeMs) {
    }

    @Override
    public void afterRequest(long consumeTimeMs) {
    }

    @Override
    public void completeRequest() {
    }
}
