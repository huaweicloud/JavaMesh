/*
 * Copyright (C) 2021-2021 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.flowcontrol.adapte.cse;

import com.huawei.sermant.core.common.LoggerFactory;
import com.huawei.sermant.core.service.ServiceManager;
import com.huawei.sermant.core.service.dynamicconfig.DynamicConfigurationFactoryServiceImpl;
import com.huawei.sermant.core.service.dynamicconfig.utils.LabelGroupUtils;
import com.huawei.sermant.core.service.dynamicconfig.service.ConfigChangeType;
import com.huawei.sermant.core.service.dynamicconfig.service.ConfigChangedEvent;
import com.huawei.sermant.core.service.dynamicconfig.service.ConfigurationListener;
import com.huawei.flowcontrol.adapte.cse.entity.CseServiceMeta;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * kie同步
 *
 * @author zhouss
 * @since 2021-11-18
 */
public class RuleSyncer {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private static final int WAIT_INTERVAL_MS = 10 * 1000;

    /**
     * 最大等待5分钟
     */
    private static final int MAX_WAIT_INTERVAL_MS = 60 * 5 * 1000;

    private final Map<String, RuleConfigListener> listenerCache = new LinkedHashMap<String, RuleConfigListener>();

    private final DynamicConfigurationFactoryServiceImpl dynamicConfigurationFactoryService =
            ServiceManager.getService(DynamicConfigurationFactoryServiceImpl.class);

    /**
     * 初始化通知
     */
    public void start() {
        // 此块只会适配KIE配置中心
        long waitTimeMs = 0L;
        while (!CseServiceMeta.getInstance().isReady()) {
            try {
                Thread.sleep(WAIT_INTERVAL_MS);
                waitTimeMs += WAIT_INTERVAL_MS;
                if (waitTimeMs >= MAX_WAIT_INTERVAL_MS) {
                    // 大于最大等待时间，放弃同步
                    return;
                }
            } catch (InterruptedException ignored) {
                // ignored
            }
        }
        initRequests();
    }

    public void initRequests() {
        buildAppRequest();
        buildServiceRequest();
        buildCustomRequest();
        for (Map.Entry<String, RuleConfigListener> entry : listenerCache.entrySet()) {
            dynamicConfigurationFactoryService.getDynamicConfigurationService()
                    .addGroupListener(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 停止方法
     */
    public void stop() {
        for (Map.Entry<String, RuleConfigListener> entry : listenerCache.entrySet()) {
            dynamicConfigurationFactoryService.getDynamicConfigurationService()
                    .removeGroupListener(entry.getKey(), entry.getKey(), entry.getValue());
        }
    }

    private void buildServiceRequest() {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("app", CseServiceMeta.getInstance().getApp());
        map.put("service", CseServiceMeta.getInstance().getServiceName());
        map.put("environment", CseServiceMeta.getInstance().getEnvironment());
        listenerCache.put(LabelGroupUtils.createLabelGroup(map), new RuleConfigListener());
    }

    private void buildAppRequest() {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("app", CseServiceMeta.getInstance().getApp());
        map.put("environment", CseServiceMeta.getInstance().getEnvironment());
        listenerCache.put(LabelGroupUtils.createLabelGroup(map), new RuleConfigListener());
    }

    private void buildCustomRequest() {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put(CseServiceMeta.getInstance().getCustomLabel(), CseServiceMeta.getInstance().getCustomLabelValue());
        listenerCache.put(LabelGroupUtils.createLabelGroup(map), new RuleConfigListener());
    }

    static class RuleConfigListener implements ConfigurationListener {

        @Override
        public void process(ConfigChangedEvent event) {
            ResolverManager.INSTANCE.resolve(event.getKey(), event.getContent(),
                    event.getChangeType() == ConfigChangeType.DELETED);
            LOGGER.log(Level.INFO, String.format("Config [%s] has been %s ", event.getKey(), event.getChangeType()));
        }
    }
}
