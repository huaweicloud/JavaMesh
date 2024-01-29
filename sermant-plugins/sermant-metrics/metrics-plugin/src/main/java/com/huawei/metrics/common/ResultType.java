/*
 * Copyright (C) 2023-2023 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.metrics.common;

/**
 * 结果类型
 *
 * @author zhp
 * @since 2023-12-15
 */
public enum ResultType {
    /**
     * 请求成功
     */
    SUCCESS(0),

    /**
     * 客户端错误
     */
    CLIENT_ERROR(1),

    /**
     * 服务端错误
     */
    SERVER_ERROR(2),

    /**
     * 请求失败
     */
    ERROR(3);

    private final int value;

    ResultType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
