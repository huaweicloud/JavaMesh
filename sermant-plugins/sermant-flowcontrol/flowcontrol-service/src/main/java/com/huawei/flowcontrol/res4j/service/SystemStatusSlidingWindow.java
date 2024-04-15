/*
 * Copyright (C) 2022-2024 Sermant Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package com.huawei.flowcontrol.res4j.service;

import com.huawei.flowcontrol.common.config.CommonConst;
import com.huawei.flowcontrol.res4j.windows.SystemStatusTask;
import com.huawei.flowcontrol.res4j.windows.WindowsArray;

import com.huaweicloud.sermant.core.plugin.service.PluginService;

import java.util.Timer;

/**
 * system status sliding window service
 *
 * @author xuezechao1
 * @since 2022-12-05
 */
public class SystemStatusSlidingWindow implements PluginService {
    private final Timer systemStatus = new Timer();

    private final SystemStatusTask systemStatusTask = new SystemStatusTask();

    @Override
    public void start() {
        /**
         * initialize the sliding window
         */
        WindowsArray.INSTANCE.initWindowsArray();

        /**
         * a scheduled task updates the system status
         */
        systemStatus.scheduleAtFixedRate(systemStatusTask, getDelay(), CommonConst.S_MS_UNIT);
    }

    private long getDelay() {
        return CommonConst.S_MS_UNIT - System.currentTimeMillis() % CommonConst.S_MS_UNIT;
    }

    @Override
    public void stop() {
        systemStatus.cancel();
    }
}
