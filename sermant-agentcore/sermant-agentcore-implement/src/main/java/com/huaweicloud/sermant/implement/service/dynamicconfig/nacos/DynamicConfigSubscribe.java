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

package com.huaweicloud.sermant.implement.service.dynamicconfig.nacos;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.config.ConfigManager;
import com.huaweicloud.sermant.core.plugin.config.ServiceMeta;
import com.huaweicloud.sermant.core.plugin.subscribe.ConfigSubscriber;
import com.huaweicloud.sermant.core.service.ServiceManager;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigListener;
import com.huaweicloud.sermant.core.utils.MapUtils;
import com.huaweicloud.sermant.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Nacos dynamic configuration subscription
 *
 * @author tangle
 * @since 2023-08-22
 */
public class DynamicConfigSubscribe implements ConfigSubscriber {
    private static final ServiceMeta SERVICE_META = ConfigManager.getConfig(ServiceMeta.class);

    private static final Logger LOGGER = LoggerFactory.getLogger();

    private final List<String> listenerCache = new ArrayList<>();

    private String serviceName;

    private DynamicConfigListener listener;

    private String key;

    private NacosDynamicConfigService nacosDynamicConfigService;

    /**
     * Constructor, initialize information and compile regular expressions
     *
     * @param serviceName service name
     * @param listener dynamic configuration listener
     * @param key configuration key
     */
    public DynamicConfigSubscribe(String serviceName, DynamicConfigListener listener, String key) {
        this.serviceName = serviceName;
        this.listener = listener;
        this.key = key;
        try {
            this.nacosDynamicConfigService = ServiceManager.getService(NacosDynamicConfigService.class);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "nacosDynamicConfigService is not enabled!");
        }
    }

    /**
     * Subscribe to multiple group
     *
     * @return subscribe result
     */
    @Override
    public boolean subscribe() {
        buildGroupSubscribers();
        boolean result = true;
        for (String group : listenerCache) {
            result &= nacosDynamicConfigService.doAddConfigListener(key, group, listener);
        }
        return result;
    }

    /**
     * Unsubscribe to multiple group
     *
     * @return unsubscribe result
     */
    public boolean unSubscribe() {
        buildGroupSubscribers();
        boolean result = true;
        for (String group : listenerCache) {
            result &= nacosDynamicConfigService.doRemoveConfigListener(key, group);
        }
        return result;
    }

    private void buildGroupSubscribers() {
        buildAppRequest();
        buildServiceRequest();
        buildCustomRequest();
    }

    private void buildAppRequest() {
        final HashMap<String, String> map = new HashMap<>();
        map.put("app", SERVICE_META.getApplication());
        map.put("environment", SERVICE_META.getEnvironment());
        final String labelGroup = createLabelGroup(map);
        listenerCache.add(labelGroup);
    }

    private void buildServiceRequest() {
        final HashMap<String, String> map = new HashMap<>();
        map.put("app", SERVICE_META.getApplication());
        map.put("service", serviceName);
        map.put("environment", SERVICE_META.getEnvironment());
        final String labelGroup = createLabelGroup(map);
        listenerCache.add(labelGroup);
    }

    private void buildCustomRequest() {
        if (StringUtils.isBlank(SERVICE_META.getCustomLabel()) || StringUtils.isBlank(
                SERVICE_META.getCustomLabelValue())) {
            return;
        }
        final HashMap<String, String> map = new HashMap<>();
        map.put(SERVICE_META.getCustomLabel(), SERVICE_META.getCustomLabelValue());
        final String labelGroup = createLabelGroup(map);
        listenerCache.add(labelGroup);
    }

    /**
     * Create label group
     *
     * @param labels label group
     * @return labelGroup, for example: app:sc_service:helloService
     */
    public static String createLabelGroup(Map<String, String> labels) {
        if (MapUtils.isEmpty(labels)) {
            return StringUtils.EMPTY;
        }
        final StringBuilder group = new StringBuilder();
        final List<String> keys = new ArrayList<>(labels.keySet());

        // Prevent the same map from having different labels
        Collections.sort(keys);
        for (String key : keys) {
            String value = labels.get(key);
            if (key == null || value == null) {
                LOGGER.log(Level.SEVERE, "Invalid group label, key: {0}, value: {1}", new String[]{key, value});
                continue;
            }
            group.append(key).append(":").append(value).append("_");
        }
        if (group.length() == 0) {
            return StringUtils.EMPTY;
        }
        if (!NacosUtils.isValidGroupName(group.toString())) {
            LOGGER.log(Level.SEVERE, "Invalid group name. group: {0}", group);
            return StringUtils.EMPTY;
        }
        return NacosUtils.reBuildGroup(group.deleteCharAt(group.length() - 1).toString());
    }
}
