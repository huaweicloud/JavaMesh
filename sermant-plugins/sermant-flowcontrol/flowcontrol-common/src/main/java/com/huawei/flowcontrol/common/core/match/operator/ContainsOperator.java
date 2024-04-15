/*
 * Copyright (C) 2021-2024 Sermant Authors. All rights reserved.
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

/**
 * Based on org/apache/servicecomb/governance/marker/operator/ContainsOperator.java
 * from the Apache ServiceComb Java Chassis project.
 */

package com.huawei.flowcontrol.common.core.match.operator;

import com.huawei.flowcontrol.common.util.StringUtils;

/**
 * include
 *
 * @author zhouss
 * @since 2021-11-22
 */
public class ContainsOperator implements Operator {
    @Override
    public boolean match(String targetValue, String patternValue) {
        return StringUtils.contains(targetValue, patternValue);
    }

    @Override
    public String getId() {
        return "contains";
    }
}
