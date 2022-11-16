/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
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
import com.huawei.discovery.entity.Recorder;
import com.huawei.discovery.entity.ServiceInstance;
import com.huawei.discovery.retry.InvokerContext;
import com.huawei.discovery.retry.Retry;
import com.huawei.discovery.retry.Retry.RetryContext;
import com.huawei.discovery.retry.RetryException;
import com.huawei.discovery.retry.config.DefaultRetryConfig;
import com.huawei.discovery.retry.config.RetryConfig;
import com.huawei.discovery.service.InvokerService;
import com.huawei.discovery.service.lb.DiscoveryManager;
import com.huawei.discovery.service.lb.LbConstants;
import com.huawei.discovery.service.lb.stats.InstanceStats;
import com.huawei.discovery.service.lb.stats.ServiceStatsManager;
import com.huawei.discovery.service.retry.policy.PolicyContext;
import com.huawei.discovery.service.retry.policy.RetryPolicy;
import com.huawei.discovery.service.retry.policy.RoundRobinRetryPolicy;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 重试调用
 *
 * @author zhouss
 * @since 2022-09-28
 */
public class RetryServiceImpl implements InvokerService {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private final Map<String, Retry> retryCache = new ConcurrentHashMap<>();

    private RetryPolicy retryPolicy;

    /**
     * 最大配置数量
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
            // 重试最终失败抛出异常, 需对异常进行封装, 返回给上游, 或者作为当前调用返回调用方
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
        final RetryContext<Recorder> context = retry.context();
        final InvokerContext invokerContext = new InvokerContext();
        final PolicyContext policyContext = new PolicyContext();
        boolean isInRetry = false;
        do {
            final long start = System.currentTimeMillis();
            policyContext.setServiceInstance(invokerContext.getServiceInstance());
            final Optional<ServiceInstance> instance = choose(serviceName, isInRetry, policyContext);
            if (!instance.isPresent()) {
                LOGGER.warning("Can not find provider service named : " + serviceName);
                return Optional.empty();
            }
            invokerContext.setServiceInstance(instance.get());
            final InstanceStats stats = ServiceStatsManager.INSTANCE.getInstanceStats(instance.get());
            context.onBefore(stats);
            long consumeTimeMs;
            try {
                final Object result = invokeFunc.apply(invokerContext);
                consumeTimeMs = System.currentTimeMillis() - start;
                isInRetry = true;
                if (invokerContext.getEx() != null) {
                    // 此处调用器, 若调用出现异常, 则以异常结果返回
                    context.onError(stats, invokerContext.getEx(), consumeTimeMs);
                    invokerContext.setEx(null);
                    continue;
                }
                final boolean isNeedRetry = context.onResult(stats, result, consumeTimeMs);
                if (!isNeedRetry) {
                    context.onComplete(stats);
                    return Optional.ofNullable(result);
                }
            } catch (Exception ex) {
                handleEx(ex, context, stats, System.currentTimeMillis() - start);
            }
        } while (true);
    }

    private void handleEx(Exception ex, RetryContext<Recorder> context, InstanceStats stats, long consumeTimeMs)
            throws Exception {
        if (ex instanceof RetryException) {
            throw ex;
        }
        context.onError(stats, ex, consumeTimeMs);
    }

    private Optional<ServiceInstance> choose(String serviceName, boolean isInRetry, PolicyContext policyContext) {
        if (isInRetry) {
            final Optional<ServiceInstance> select = retryPolicy.select(serviceName, policyContext);
            select.ifPresent(instance -> LOGGER.info(String.format(Locale.ENGLISH,
                    "Start retry for invoking instance [id: %s] of service [%s] at time %s",
                    instance.getMetadata().get(LbConstants.SERMANT_DISCOVERY), serviceName, LocalDateTime.now())));
            return select;
        }
        return DiscoveryManager.INSTANCE.choose(serviceName);
    }
}
