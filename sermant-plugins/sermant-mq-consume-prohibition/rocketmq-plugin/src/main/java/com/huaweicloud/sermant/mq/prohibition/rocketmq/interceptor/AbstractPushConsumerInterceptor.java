/*
 *  Copyright (C) 2023-2024 Sermant Authors. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.huaweicloud.sermant.mq.prohibition.rocketmq.interceptor;

import com.huaweicloud.sermant.config.ProhibitionConfigManager;
import com.huaweicloud.sermant.core.plugin.agent.interceptor.AbstractInterceptor;
import com.huaweicloud.sermant.rocketmq.controller.RocketMqPushConsumerController;
import com.huaweicloud.sermant.rocketmq.extension.RocketMqConsumerHandler;
import com.huaweicloud.sermant.rocketmq.wrapper.DefaultMqPushConsumerWrapper;

/**
 * Abstract interceptor
 *
 * @author daizhenyu
 * @since 2023-12-04
 **/
public abstract class AbstractPushConsumerInterceptor extends AbstractInterceptor {
    /**
     * external extension handler
     */
    protected RocketMqConsumerHandler handler;

    /**
     * Non-parametric construction method
     */
    public AbstractPushConsumerInterceptor() {
    }

    /**
     * Parameterized construction method
     *
     * @param handler external extension handler
     */
    public AbstractPushConsumerInterceptor(RocketMqConsumerHandler handler) {
        this.handler = handler;
    }

    /**
     * pushConsumer perform the consumption prohibition operation
     *
     * @param pushConsumerWrapper pushConsumer packaging class instance
     */
    protected void disablePushConsumption(DefaultMqPushConsumerWrapper pushConsumerWrapper) {
        if (pushConsumerWrapper != null) {
            RocketMqPushConsumerController.disablePushConsumption(pushConsumerWrapper,
                    ProhibitionConfigManager.getRocketMqProhibitionTopics());
        }
    }
}
