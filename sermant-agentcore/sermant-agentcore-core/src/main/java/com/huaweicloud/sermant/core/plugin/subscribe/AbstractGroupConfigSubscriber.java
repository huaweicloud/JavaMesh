/*
 * Copyright (C) 2022-2024 Sermant Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huaweicloud.sermant.core.plugin.subscribe;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.service.ServiceManager;
import com.huaweicloud.sermant.core.service.dynamicconfig.DynamicConfigService;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigListener;

import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Abstract subscriber for configuration center
 *
 * @author zhouss
 * @since 2022-04-13
 */
public abstract class AbstractGroupConfigSubscriber implements ConfigSubscriber {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private DynamicConfigService dynamicConfigService;

    /**
     * plugin to subscribe
     */
    private final String pluginName;

    /**
     * Custom configuration implementation constructor
     *
     * @param dynamicConfigService Configuration center implementation
     */
    protected AbstractGroupConfigSubscriber(DynamicConfigService dynamicConfigService) {
        this(dynamicConfigService, null);
    }

    /**
     * Custom configuration implementation constructor
     *
     * @param dynamicConfigService Configuration center implementation
     * @param pluginName plugin name
     */
    protected AbstractGroupConfigSubscriber(DynamicConfigService dynamicConfigService, String pluginName) {
        if (dynamicConfigService == null) {
            try {
                this.dynamicConfigService = ServiceManager.getService(DynamicConfigService.class);
            } catch (IllegalArgumentException e) {
                LOGGER.severe("dynamicConfigService is not enabled!");
                this.dynamicConfigService = null;
            }
        } else {
            this.dynamicConfigService = dynamicConfigService;
        }
        this.pluginName = pluginName;
    }

    @Override
    public boolean subscribe() {
        if (dynamicConfigService == null) {
            LOGGER.severe("dynamicConfigService is null, fail to subscribe!");
            return false;
        }
        if (!isReady()) {
            LoggerFactory.getLogger().warning("The group subscriber is not ready, may be service name is null");
            return false;
        }
        final Map<String, DynamicConfigListener> subscribers = buildGroupSubscribers();
        if (subscribers != null && !subscribers.isEmpty()) {
            for (Map.Entry<String, DynamicConfigListener> entry : subscribers.entrySet()) {
                dynamicConfigService.addGroupListener(entry.getKey(), entry.getValue(), true);
                printSubscribeMsg(entry.getKey());
            }
        }
        return true;
    }

    private void printSubscribeMsg(String group) {
        if (pluginName != null) {
            LoggerFactory.getLogger().info(String.format(Locale.ENGLISH,
                    "Plugin [%s] has Success to subscribe group [%s]", pluginName, group));
        } else {
            LoggerFactory.getLogger().info(String.format(Locale.ENGLISH,
                    "Success to subscribe group [%s]", group));
        }
    }

    /**
     * Build group subscribers
     *
     * @return 订阅全集
     */
    protected abstract Map<String, DynamicConfigListener> buildGroupSubscribers();

    /**
     * is ready to subscribe
     *
     * @return result
     */
    protected abstract boolean isReady();
}
