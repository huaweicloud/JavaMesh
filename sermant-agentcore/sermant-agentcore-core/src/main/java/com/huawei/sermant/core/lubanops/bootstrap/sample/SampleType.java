/*
 * Copyright (C) 2021-2021 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.sermant.core.lubanops.bootstrap.sample;

/**
 * 采样类型
 *
 * @author
 */
public enum SampleType {

    all("1"),
    percentage("2"),
    frequency("3"),
    automatic("4");

    String type;

    SampleType(String t) {
        type = t;
    }

    public String value() {
        return type;
    }

}
