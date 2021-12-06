/*
 * Copyright (C) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved
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

package com.huawei.javamesh.metricserver.dto.openjdk;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
public class OpenJdkMemoryPoolDTO {
    private final String service;
    private final String serviceInstance;
    private final Instant time;

    private final OraclePoolType type;
    private final Long init;
    private final Long max;
    private final Long used;
    private final Long committed;

    public enum OraclePoolType {
        CODE_CACHE,
        NEW_GEN,
        OLD_GEN,
        PERM_GEN,
        SURVIVOR,
        METASPACE
    }
}
