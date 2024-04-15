/*
 * Copyright (C) 2022-2024 Sermant Authors. All rights reserved.
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

package com.huawei.flowcontrol.common.core.rule.fault;

import com.huawei.flowcontrol.common.core.constants.RuleConstants;

/**
 * request to abandon error injection
 *
 * @author zhouss
 * @since 2022-08-05
 */
public class AbortFault extends AbstractFault {
    /**
     * fault injection
     *
     * @param rule error injection rule
     * @throws IllegalArgumentException thrown if rule is null
     */
    public AbortFault(FaultRule rule) {
        super(rule);
    }

    @Override
    protected void exeFault(FaultRule faultRule) {
        throw new FaultException(faultRule.getErrorCode(), formatMsg(faultRule), faultRule);
    }

    private String formatMsg(FaultRule faultRule) {
        if (RuleConstants.FAULT_RULE_FALLBACK_NULL_TYPE.equals(faultRule.getFallbackType())) {
            return "Request has been aborted by fault-ReturnNull";
        }
        return "Request has been aborted by fault-ThrowException";
    }
}
