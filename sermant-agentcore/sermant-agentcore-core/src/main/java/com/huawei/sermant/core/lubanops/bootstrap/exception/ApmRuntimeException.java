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

package com.huawei.sermant.core.lubanops.bootstrap.exception;

/**
 * 全局通用的运行期异常 <br>
 *
 * @author
 * @since 2020年3月9日
 */
public class ApmRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1778208300580921841L;

    public ApmRuntimeException() {
    }

    public ApmRuntimeException(String message) {
        super(message);
    }

    public ApmRuntimeException(String message, Throwable ex) {
        super(message, ex);
    }

    public ApmRuntimeException(Throwable ex) {
        super(ex);
    }

}
