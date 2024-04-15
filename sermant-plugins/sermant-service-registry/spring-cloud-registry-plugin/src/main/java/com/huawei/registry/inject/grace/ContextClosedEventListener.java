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

package com.huawei.registry.inject.grace;

import com.huawei.registry.config.GraceConfig;
import com.huawei.registry.services.GraceService;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;
import com.huaweicloud.sermant.core.plugin.service.PluginServiceManager;

import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Spring closes the time listener
 *
 * @author provenceee
 * @since 2022-05-25
 */
@Component
public class ContextClosedEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private GraceService graceService;

    /**
     * Constructor
     */
    public ContextClosedEventListener() {
        try {
            graceService = PluginServiceManager.getPluginService(GraceService.class);
        } catch (IllegalArgumentException exception) {
            LOGGER.severe("graceService is not enabled");
            graceService = null;
        }
    }

    /**
     * ContextClosedEvent Event listeners
     */
    @EventListener(value = ContextClosedEvent.class)
    public void listener() {
        if (!isEnableGraceDown() || graceService == null) {
            return;
        }
        graceService.shutdown();
    }

    private boolean isEnableGraceDown() {
        GraceConfig graceConfig = PluginConfigManager.getPluginConfig(GraceConfig.class);
        return graceConfig.isEnableSpring() && graceConfig.isEnableGraceShutdown() && graceConfig
                .isEnableOfflineNotify();
    }
}
