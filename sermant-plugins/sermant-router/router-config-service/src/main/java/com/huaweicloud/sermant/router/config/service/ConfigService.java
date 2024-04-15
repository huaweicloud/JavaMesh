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

package com.huaweicloud.sermant.router.config.service;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;
import com.huaweicloud.sermant.core.plugin.subscribe.ConfigSubscriber;
import com.huaweicloud.sermant.core.plugin.subscribe.CseGroupConfigSubscriber;
import com.huaweicloud.sermant.core.utils.StringUtils;
import com.huaweicloud.sermant.router.common.config.RouterConfig;
import com.huaweicloud.sermant.router.common.constants.RouterConstant;
import com.huaweicloud.sermant.router.config.cache.ConfigCache;
import com.huaweicloud.sermant.router.config.entity.EntireRule;
import com.huaweicloud.sermant.router.config.entity.Route;
import com.huaweicloud.sermant.router.config.entity.RouterConfiguration;
import com.huaweicloud.sermant.router.config.entity.Rule;
import com.huaweicloud.sermant.router.config.listener.RouterConfigListener;
import com.huaweicloud.sermant.router.config.utils.RuleUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * Configuration service
 *
 * @author provenceee
 * @since 2022-07-14
 */
public abstract class ConfigService {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private static final int DEFAULT_TAG_ROUTE_WEIGHT = 100;

    private final AtomicBoolean init = new AtomicBoolean();

    private final RouterConfig routerConfig;

    private final Set<String> requestTags;

    /**
     * Constructor
     */
    public ConfigService() {
        routerConfig = PluginConfigManager.getPluginConfig(RouterConfig.class);
        requestTags = new HashSet<>(routerConfig.getRequestTags());
    }

    /**
     * Initialize the notification
     *
     * @param cacheName Cache name
     * @param serviceName Service name
     */
    public void init(String cacheName, String serviceName) {
        if (StringUtils.isBlank(cacheName) || StringUtils.isBlank(serviceName)) {
            LOGGER.warning(
                    String.format(Locale.ROOT, "CacheName[%s] or serviceName[%s] is empty.", cacheName, serviceName));
            return;
        }
        if (init.compareAndSet(false, true)) {
            initTagRoute(cacheName);
            RouterConfigListener listener = new RouterConfigListener(cacheName);
            ConfigSubscriber subscriber = new CseGroupConfigSubscriber(serviceName, listener, "Sermant-Router");
            subscriber.subscribe();
        }
    }

    /**
     * Obtain the rule key
     *
     * @return Rule key
     */
    public Set<String> getMatchKeys() {
        return routerConfig.isUseRequestRouter() ? requestTags : RuleUtils.getMatchKeys();
    }

    /**
     * Obtain the staining key
     *
     * @return The key of the staining
     */
    public Set<String> getInjectTags() {
        return RuleUtils.getInjectTags();
    }

    private void initTagRoute(String cacheName) {
        Route route = new Route();
        route.setWeight(DEFAULT_TAG_ROUTE_WEIGHT);
        if (RouterConstant.DUBBO_CACHE_NAME.equals(cacheName) && routerConfig.isEnabledDubboZoneRouter()) {
            route.setTags(Collections.singletonMap(RouterConstant.META_ZONE_KEY, routerConfig.getZone()));
        } else if (RouterConstant.SPRING_CACHE_NAME.equals(cacheName) && routerConfig.isEnabledSpringZoneRouter()) {
            route.setTags(Collections.singletonMap(RouterConstant.ZONE, routerConfig.getZone()));
        } else {
            return;
        }
        Rule rule = new Rule();
        rule.setRoute(Collections.singletonList(route));
        EntireRule entireRule = new EntireRule();
        entireRule.setRules(Collections.singletonList(rule));
        entireRule.setKind(RouterConstant.TAG_MATCH_KIND);
        RouterConfiguration configuration = ConfigCache.getLabel(cacheName);
        configuration.resetGlobalRule(entireRule);
    }
}