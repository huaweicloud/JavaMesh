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

package com.huawei.sermant.core.exception;

import java.util.Locale;

import com.huawei.sermant.core.config.common.BaseConfig;

/**
 * 非法配置异常，配置对象缺少默认的构造函数
 *
 * @author HapThorin
 * @version 1.0.0
 * @since 2021/11/18
 */
public class IllegalConfigException extends RuntimeException {
    public IllegalConfigException(Class<? extends BaseConfig> cls) {
        super(String.format(Locale.ROOT, "Unable to create default instance of %s, please check. ", cls.getName()));
    }
}
