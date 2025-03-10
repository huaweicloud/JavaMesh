/*
 * Copyright (C) 2023-2023 Huawei Technologies Co., Ltd. All rights reserved.
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

package io.sermant.discovery.entity;

import io.sermant.core.common.LoggerFactory;
import io.sermant.core.plugin.config.PluginConfigManager;
import io.sermant.discovery.config.LbConfig;
import io.sermant.discovery.retry.config.DefaultRetryConfig;
import io.sermant.discovery.retry.config.RetryConfig;
import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.client.ClientResponse;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * webclient Retry
 *
 * @author provenceee
 * @since 2023-05-05
 */
public class LowerVersionRetryExchangeFilterFunction extends AbstractRetryExchangeFilterFunction {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private static final RetryConfig RETRY_CONFIG = DefaultRetryConfig.create();

    private static final LbConfig LB_CONFIG = PluginConfigManager.getPluginConfig(LbConfig.class);

    @Override
    public Mono<ClientResponse> retry(Mono<ClientResponse> mono) {
        try {
            return mono.retry(LB_CONFIG.getMaxRetry(),
                    this::retryMatcher);
        } catch (NoSuchMethodError error) {
            LOGGER.log(Level.SEVERE, "Cannot not retry, please check webflux's version, error is: ", error);
            return mono;
        }
    }

    private boolean retryMatcher(Throwable throwable) {
        boolean shouldRetry = RETRY_CONFIG.getThrowablePredicate().test(throwable);
        if (shouldRetry) {
            LOGGER.log(Level.WARNING, "Start retry, throwable is: ", throwable);
            try {
                Thread.sleep(LB_CONFIG.getRetryWaitMs());
            } catch (InterruptedException ignored) {
                // ignored
            }
        }
        return shouldRetry;
    }
}
