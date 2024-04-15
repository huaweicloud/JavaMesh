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

package com.huaweicloud.sermant.mq.prohibition.rocketmq.declarer;

import com.huaweicloud.sermant.core.plugin.agent.declarer.AbstractPluginDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.declarer.InterceptDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.matcher.ClassMatcher;
import com.huaweicloud.sermant.mq.prohibition.rocketmq.utils.RocketMqEnhancementHelper;

/**
 * PullConsumer interceptor declarer, supports RocketMQ 4.8+version
 *
 * @author daizhenyu
 * @since 2023-12-04
 **/
public class RocketMqPullConsumerDeclarer extends AbstractPluginDeclarer {
    @Override
    public ClassMatcher getClassMatcher() {
        return RocketMqEnhancementHelper.getPullConsumerClassMatcher();
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
                RocketMqEnhancementHelper.getPullConsumerStartInterceptDeclarers(),
                RocketMqEnhancementHelper.getPullConsumerSubscribeInterceptDeclarers(),
                RocketMqEnhancementHelper.getPullConsumerUnsubscribeInterceptDeclarers(),
                RocketMqEnhancementHelper.getPullConsumerAssignInterceptDeclarers(),
                RocketMqEnhancementHelper.getPullConsumerShutdownInterceptDeclarers()
        };
    }
}
