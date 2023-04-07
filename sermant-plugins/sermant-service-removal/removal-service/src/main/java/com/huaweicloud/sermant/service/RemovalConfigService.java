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

package com.huaweicloud.sermant.service;

import com.huaweicloud.sermant.common.RemovalConstants;
import com.huaweicloud.sermant.config.RemovalDynamicConfigListener;
import com.huaweicloud.sermant.core.plugin.service.PluginService;
import com.huaweicloud.sermant.core.plugin.subscribe.ConfigSubscriber;
import com.huaweicloud.sermant.core.plugin.subscribe.CseGroupConfigSubscriber;

/**
 * 配置监听服务
 *
 * @author zhp
 * @since 2023-04-04
 */
public class RemovalConfigService implements PluginService {
    private static final String DEFAULT_SERVICE_NAME = "default";

    @Override
    public void start() {
        ConfigSubscriber configSubscriber = new CseGroupConfigSubscriber(
                DEFAULT_SERVICE_NAME, new RemovalDynamicConfigListener(), RemovalConstants.PLUGIN_NAME);
        configSubscriber.subscribe();
    }
}
