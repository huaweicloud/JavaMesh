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

package com.huawei.flowcontrol.res4j.handler.exception;

import com.huawei.flowcontrol.common.config.CommonConst;
import com.huawei.flowcontrol.common.core.constants.RuleConstants;
import com.huawei.flowcontrol.common.core.rule.fault.FaultException;
import com.huawei.flowcontrol.common.core.rule.fault.FaultRule;
import com.huawei.flowcontrol.common.entity.FlowControlResponse;
import com.huawei.flowcontrol.common.entity.FlowControlResult;
import com.huawei.flowcontrol.common.event.FlowControlEventEntity;
import com.huawei.flowcontrol.res4j.util.FlowControlEventUtils;

/**
 * 错误注入异常处理
 *
 * @author zhouss
 * @since 2022-08-05
 */
public class FaultExceptionHandler extends AbstractExceptionHandler<FaultException> {
    @Override
    protected FlowControlResponse getFlowControlResponse(FaultException ex,
            FlowControlResult flowControlResult) {
        FlowControlEventUtils.notifySameRuleMatchedEvent(
                FlowControlEventEntity.FLOW_CONTROL_FAULTINJECTION_ENABLE,
                "Fault");
        final FaultRule rule = ex.getRule();
        if (RuleConstants.FAULT_RULE_FALLBACK_NULL_TYPE.equals(rule.getFallbackType())) {
            return new FlowControlResponse(ex.getMsg(), CommonConst.HTTP_OK, null);
        }

        // 抛异常
        return new FlowControlResponse(ex.getMsg(), rule.getErrorCode());
    }

    @Override
    public Class<FaultException> targetException() {
        return FaultException.class;
    }
}
