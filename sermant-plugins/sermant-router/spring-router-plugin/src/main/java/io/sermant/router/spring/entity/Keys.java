/*
 * Copyright (C) 2023-2024 Huawei Technologies Co., Ltd. All rights reserved.
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

package io.sermant.router.spring.entity;

import java.util.Set;

/**
 * Transparent transmission marks the key entity
 *
 * @author provenceee
 * @since 2023-02-21
 */
public class Keys {
    private final Set<String> matchedKeys;

    private final Set<String> injectedTags;

    /**
     * Constructor
     *
     * @param matchedKeys Label routing transparent transmission markers
     * @param injectedTags Swim lane transparent markers
     */
    public Keys(Set<String> matchedKeys, Set<String> injectedTags) {
        this.matchedKeys = matchedKeys;
        this.injectedTags = injectedTags;
    }

    public Set<String> getMatchedKeys() {
        return matchedKeys;
    }

    public Set<String> getInjectedTags() {
        return injectedTags;
    }
}
