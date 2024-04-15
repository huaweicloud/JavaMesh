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

package com.huaweicloud.loadbalancer.constants;

/**
 * dubbo url parameter constant, see
 * <p/>
 * org.apache.dubbo.common.URL#getParameter
 * <p/>
 * org.apache.dubbo.common.URL#getParameter
 *
 * @author zhouss
 * @since 2022-09-13
 */
public class DubboUrlParamsConstants {
    /**
     * interface
     */
    public static final String DUBBO_INTERFACE = "interface";

    /**
     * service name
     */
    public static final String DUBBO_APPLICATION = "application";

    /**
     * remote service name
     */
    public static final String DUBBO_REMOTE_APPLICATION = "remote.application";

    /**
     * loadbalancing
     */
    public static final String DUBBO_LOAD_BALANCER_KEY = "loadbalance";

    private DubboUrlParamsConstants() {
    }
}
