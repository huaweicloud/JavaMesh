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

package io.sermant.discovery.service.lb.discovery;

import io.sermant.discovery.entity.ServiceInstance;

import java.util.List;

/**
 * Listeners for instance changes, and notifications are sent when the instance changes
 *
 * @author zhouss
 * @since 2022-10-12
 */
public interface InstanceChangeListener {
    /**
     * Notice
     *
     * @param eventType The type of event
     * @param serviceInstance Change the instance
     */
    void notify(EventType eventType, ServiceInstance serviceInstance);


    /**
     * Notice
     *
     * @param serviceInstances All instances
     */
    void notify(List<ServiceInstance> serviceInstances);

    /**
     * The type of event
     *
     * @since 2022-10-12
     */
    enum EventType {
        /**
         * Add
         */
        ADDED,

        /**
         * Update
         */
        UPDATED,

        /**
         * Delete
         */
        DELETED
    }
}
