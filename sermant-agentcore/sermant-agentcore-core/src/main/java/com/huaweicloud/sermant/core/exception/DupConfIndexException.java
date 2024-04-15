/*
 * Copyright (C) 2021-2024 Sermant Authors. All rights reserved.
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

package com.huaweicloud.sermant.core.exception;

import java.util.Locale;

/**
 * DupConfIndexException，throws When using the same key to interpret its value，like
 * {@code config.key=prefix.${config.key}.suffix}
 *
 * @author HapThorin
 * @version 1.0.0
 * @since 2021-11-04
 */
public class DupConfIndexException extends RuntimeException {
    private static final long serialVersionUID = -5266141076199244336L;

    /**
     * constructor
     *
     * @param key key
     */
    public DupConfIndexException(String key) {
        super(String.format(Locale.ROOT, "Unable to use [%s] to explain [%s]. ", key, key));
    }
}
