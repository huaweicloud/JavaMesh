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

package com.huawei.sermant.core.lubanops.bootstrap.api;

import java.util.List;

public interface JSONAPI {

    String toJSONString(Object object);

    String toJSONString(Object obj, List<String> excludeKeys);

    <T> T parseObject(String text, Class<T> type);

    int[] parseIntArray(String s);

    <T> List<T> parseList(String s, Class<T> type);

}
