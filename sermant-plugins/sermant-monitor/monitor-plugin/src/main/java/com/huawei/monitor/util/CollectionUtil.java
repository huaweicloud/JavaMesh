/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.monitor.util;

import java.util.List;

/**
 * 集合工具类
 *
 * @author zhp
 * @version 1.0.0
 * @since 2022-08-02
 */
public class CollectionUtil {
    private CollectionUtil() {
    }

    /**
     * 是否为空
     *
     * @param list 集合
     * @return 判断结果
     */
    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }
}
