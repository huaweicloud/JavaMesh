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

package com.huaweicloud.sermant.core.service.tracing.common;

/**
 * Key inserted into the Carrier in link trace
 *
 * @author luanwenfei
 * @since 2022-03-18
 */
public enum TracingHeader {
    /**
     * Identifies an entire link
     */
    TRACE_ID("sermant-trace-id"),
    /**
     * Identifies the parent SpanId
     */
    PARENT_SPAN_ID("sermant-parent-span-id"),
    /**
     * Identifies the prefix of SpanId of next process
     */
    SPAN_ID_PREFIX("sermant-span-id-prefix");

    private final String value;

    TracingHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
