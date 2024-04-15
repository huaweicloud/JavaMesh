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

package com.huaweicloud.sermant.backend.webhook.welink;

import com.huaweicloud.sermant.backend.common.conf.CommonConst;
import com.huaweicloud.sermant.backend.entity.event.QueryResultEventInfoEntity;
import com.huaweicloud.sermant.backend.webhook.WebHookClient;
import com.huaweicloud.sermant.backend.webhook.WebHookConfig;

import java.util.List;

/**
 * Welink webhook client
 *
 * @author xuezechao
 * @since 2023-03-02
 */
public class WelinkHookClient implements WebHookClient {
    private WebHookConfig welinkHookConfig = WelinkHookConfig.getInstance();

    /**
     * Constructor
     */
    public WelinkHookClient() {
        welinkHookConfig.setName(CommonConst.WELINK_WEBHOOK_NAME);
        welinkHookConfig.setId(CommonConst.WELINK_WEBHOOK_ID);
    }

    /**
     * Webhook event notify
     *
     * @param events event information
     * @return notify result
     */
    @Override
    public boolean doNotify(List<QueryResultEventInfoEntity> events) {
        return false;
    }

    /**
     * Get configuration
     *
     * @return configuration
     */
    @Override
    public WebHookConfig getConfig() {
        return welinkHookConfig;
    }
}
