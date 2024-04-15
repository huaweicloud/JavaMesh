/*
 * Copyright (C) 2022-2024 Sermant Authors. All rights reserved.
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

package com.huaweicloud.loadbalancer.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * dubbo service name cache
 *
 * @author zhouss
 * @since 2022-09-13
 */
public enum DubboApplicationCache {
    /**
     * instance
     */
    INSTANCE;

    /**
     * serviceInterfaceCache key:interfaceName value:downstreamServiceName
     */
    private final Map<String, String> applicationCache = new ConcurrentHashMap<>();

    /**
     * cache
     *
     * @param interfaceName interface name
     * @param application service name
     */
    public void cache(String interfaceName, String application) {
        applicationCache.put(interfaceName, application);
    }

    public Map<String, String> getApplicationCache() {
        return applicationCache;
    }
}
