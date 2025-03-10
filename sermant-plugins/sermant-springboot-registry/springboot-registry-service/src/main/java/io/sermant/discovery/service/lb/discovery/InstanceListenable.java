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

import io.sermant.discovery.config.RegisterType;
import io.sermant.discovery.service.lb.cache.InstanceCacheManager;

/**
 * Instance listening changes, this feature will be bound to
 * {@link InstanceCacheManager}
 *
 * @author zhouss
 * @since 2022-10-12
 */
public interface InstanceListenable {
    /**
     * Initialize
     */
    void init();

    /**
     * Try to add a listener for a specified service instance, and return it if it has already listened
     *
     * @param serviceName Specify the service name
     * @param listener Listener
     */
    void tryAdd(String serviceName, InstanceChangeListener listener);

    /**
     * Turn off the listener
     */
    void close();

    /**
     * The RegisterType of the listener
     *
     * @return RegisterType
     */
    RegisterType registerType();
}
