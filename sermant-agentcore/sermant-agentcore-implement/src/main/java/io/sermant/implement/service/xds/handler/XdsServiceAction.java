/*
 * Copyright (C) 2024-2024 Sermant Authors. All rights reserved.
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

package io.sermant.implement.service.xds.handler;

import java.util.concurrent.CountDownLatch;

/**
 * Xds handler interface to subscribe resource from istiod
 *
 * @author daizhenyu
 * @since 2024-05-14
 **/
public interface XdsServiceAction {
    /**
     * when countDownLatch is null, it is an asynchronous subscription; otherwise, it is a synchronous subscription
     *
     * @param requestKey request key to get the xds data from cache
     * @param countDownLatch Used to notify the xds requesting thread to obtain data
     */
    void subscribe(String requestKey, CountDownLatch countDownLatch);

    /**
     * async subscribe
     *
     * @param requestKey request key to get the xds data from cache
     */
    void subscribe(String requestKey);
}
