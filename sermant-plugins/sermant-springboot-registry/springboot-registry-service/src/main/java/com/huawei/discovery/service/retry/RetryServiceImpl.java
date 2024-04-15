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

package com.huawei.discovery.service.retry;

import com.huawei.discovery.config.DiscoveryPluginConfig;
import com.huawei.discovery.config.LbConfig;
import com.huawei.discovery.retry.InvokerContext;
import com.huawei.discovery.retry.Retry;
import com.huawei.discovery.retry.RetryException;
import com.huawei.discovery.retry.config.DefaultRetryConfig;
import com.huawei.discovery.retry.config.RetryConfig;
import com.huawei.discovery.service.InvokerService;
import com.huawei.discovery.service.retry.policy.RetryPolicy;
import com.huawei.discovery.service.retry.policy.RoundRobinRetryPolicy;
import com.huawei.discovery.service.util.ApplyUtil;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Retry the call
 *
 * @author zhouss
 * @since 2022-09-28
 */
public class RetryServiceImpl implements InvokerService {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private final Map<String, Retry> retryCache = new ConcurrentHashMap<>();

    private RetryPolicy retryPolicy;

    /**
     * Maximum number of configurations
     */
    private int maxSize;

    private Retry defaultRetry;

    @Override
    public void start() {
        if (!PluginConfigManager.getPluginConfig(DiscoveryPluginConfig.class).isEnableRegistry()) {
            return;
        }
        final LbConfig lbConfig = PluginConfigManager.getPluginConfig(LbConfig.class);
        maxSize = lbConfig.getMaxRetryConfigCache();
        defaultRetry = Retry.create(DefaultRetryConfig.create());
        initRetryPolicy(lbConfig);
    }

    private void initRetryPolicy(LbConfig lbConfig) {
        for (RetryPolicy policy : ServiceLoader.load(RetryPolicy.class, this.getClass().getClassLoader())) {
            if (policy.name().equalsIgnoreCase(lbConfig.getRetryPolicy())) {
                LOGGER.warning(String.format(Locale.ENGLISH, "Use retry policy %s!", lbConfig.getRetryPolicy()));
                retryPolicy = policy;
                break;
            }
        }
        if (retryPolicy == null) {
            LOGGER.warning(String.format(Locale.ENGLISH, "Can not match retry policy %s, replace by RoundRobin",
                    lbConfig.getRetryPolicy()));
            retryPolicy = new RoundRobinRetryPolicy();
        }
    }

    @Override
    public Optional<Object> invoke(Function<InvokerContext, Object> invokeFunc, Function<Throwable, Object> exFunc,
            String serviceName) {
        return invoke(invokeFunc, exFunc, serviceName, getRetry(null));
    }

    @Override
    public Optional<Object> invoke(Function<InvokerContext, Object> invokeFunc, Function<Throwable, Object> exFunc,
            String serviceName, RetryConfig retryConfig) {
        return invoke(invokeFunc, exFunc, serviceName, getRetry(retryConfig));
    }

    private Optional<Object> invoke(Function<InvokerContext, Object> invokeFunc, Function<Throwable, Object> exFunc,
            String serviceName, Retry retry) {
        try {
            return invokeWithEx(invokeFunc, serviceName, retry);
        } catch (RetryException ex) {
            return Optional.ofNullable(exFunc.apply(ex.getRealEx()));
        } catch (Exception ex) {
            // If the retry finally fails to throw an exception, the exception needs to be encapsulated and returned to
            // the upstream or returned to the caller as the current call
            LOGGER.log(Level.WARNING, String.format(Locale.ENGLISH, "Request failed for service [%s]", serviceName),
                    ex);
            return Optional.ofNullable(exFunc.apply(ex));
        }
    }

    private Retry getRetry(RetryConfig retryConfig) {
        if (retryConfig == null) {
            return defaultRetry;
        }
        if (retryCache.size() >= maxSize) {
            LOGGER.warning(String.format(Locale.ENGLISH,
                    "Retry Config [%s] exceed max size [%s], it will replace it with default retry!",
                    retryConfig.getName(), maxSize));
            return defaultRetry;
        }
        return retryCache.computeIfAbsent(retryConfig.getName(), name -> Retry.create(retryConfig));
    }

    private Optional<Object> invokeWithEx(Function<InvokerContext, Object> invokeFunc, String serviceName, Retry retry)
            throws Exception {
        final InvokerContext invokerContext = new InvokerContext();
        return ApplyUtil.invokeWithEx(invokeFunc, serviceName, retry, invokerContext, retryPolicy);
    }
}
