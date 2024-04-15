/*
 * Copyright (C) 2022-2024 Sermant Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huawei.flowcontrol.common.core.rule;

import com.huawei.flowcontrol.common.core.ResolverManager;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigEvent;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigEventType;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigListener;

import java.util.Collections;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * rule synchronization listener
 *
 * @author zhouss
 * @since 2022-01-25
 */
public class RuleDynamicConfigListener implements DynamicConfigListener {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    @Override
    public void process(DynamicConfigEvent event) {
        LOGGER.log(Level.INFO, String.format(Locale.ENGLISH, "Config [%s] has been received, operator type: [%s] ",
                event.getKey(), event.getEventType()));
        ResolverManager.INSTANCE.resolve(Collections.singletonMap(event.getKey(), event.getContent()),
                event.getEventType() == DynamicConfigEventType.DELETE);
    }
}
