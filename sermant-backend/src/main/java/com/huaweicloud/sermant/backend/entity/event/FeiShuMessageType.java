/*
 * Copyright (C) 2023-2024 Sermant Authors. All rights reserved.
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

package com.huaweicloud.sermant.backend.entity.event;

/**
 * Message types supported by FeiShu
 *
 * @author xuezechao
 * @since 2023-03-02
 */
public enum FeiShuMessageType {
    /**
     * Text
     */
    TEXT("text"),

    /**
     * Rich text
     */
    POST("post"),

    /**
     * Message card
     */
    INTERACTIVE("interactive");

    private String type;

    FeiShuMessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
