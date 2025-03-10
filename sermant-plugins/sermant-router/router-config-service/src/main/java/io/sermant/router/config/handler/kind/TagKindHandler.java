/*
 *
 *  * Copyright (C) 2024-2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.sermant.router.config.handler.kind;

import io.sermant.router.common.constants.RouterConstant;

import java.util.HashSet;
import java.util.Set;

/**
 * Tag Configuration handler (Compatible with 1.0.x Edition)
 *
 * @author provenceee
 * @since 2024-01-11
 */
public class TagKindHandler extends AbstractKindHandler {
    private final Set<String> keys;

    /**
     * Constructor
     */
    public TagKindHandler() {
        super(RouterConstant.TAG_KEY_PREFIX, RouterConstant.TAG_MATCH_KIND);
        this.keys = new HashSet<>();
        this.keys.add(RouterConstant.GLOBAL_TAG_KEY);
        this.keys.add(RouterConstant.TAG_KEY_PREFIX);
    }

    @Override
    public boolean shouldHandle(String key) {
        return super.shouldHandle(key)
                && (keys.contains(key) || key.startsWith(RouterConstant.TAG_KEY_PREFIX + RouterConstant.POINT));
    }
}
