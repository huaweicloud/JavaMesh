/*
 * Copyright (C) 2021-2024 Sermant Authors. All rights reserved.
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

package com.huawei.flowcontrol.common.core.match;

import com.huawei.flowcontrol.common.entity.RequestEntity;

/**
 * matcher
 *
 * @author zhouss
 * @since 2021-11-16
 */
public interface Matcher {
    /**
     * matching
     *
     * @param requestEntity request body
     * @return if it matches
     */
    boolean match(RequestEntity requestEntity);
}
