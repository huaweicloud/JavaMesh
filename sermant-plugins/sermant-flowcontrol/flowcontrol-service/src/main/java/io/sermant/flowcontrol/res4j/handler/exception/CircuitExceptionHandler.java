/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.sermant.flowcontrol.res4j.handler.exception;

import io.sermant.flowcontrol.common.config.CommonConst;
import io.sermant.flowcontrol.common.entity.FlowControlResponse;
import io.sermant.flowcontrol.common.entity.FlowControlResult;
import io.sermant.flowcontrol.res4j.exceptions.CircuitBreakerException;

/**
 * circuit breaker exception handling
 *
 * @author zhouss
 * @since 2022-08-05
 */
public class CircuitExceptionHandler extends AbstractExceptionHandler<CircuitBreakerException> {
    @Override
    protected FlowControlResponse getFlowControlResponse(CircuitBreakerException ex,
            FlowControlResult flowControlResult) {
        return new FlowControlResponse(ex.getMessage(), getCode());
    }

    @Override
    public Class<CircuitBreakerException> targetException() {
        return CircuitBreakerException.class;
    }

    /**
     * get response code
     *
     * @return code
     */
    protected int getCode() {
        return CommonConst.TOO_MANY_REQUEST_CODE;
    }
}
