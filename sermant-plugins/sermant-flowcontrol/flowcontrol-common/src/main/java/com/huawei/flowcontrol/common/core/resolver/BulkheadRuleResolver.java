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

package com.huawei.flowcontrol.common.core.resolver;

import com.huawei.flowcontrol.common.core.rule.BulkheadRule;

/**
 * isolation bin configuration analysis
 *
 * @author zhouss
 * @since 2021-11-16
 */
public class BulkheadRuleResolver extends AbstractResolver<BulkheadRule> {
    /**
     * isolation bin configuration key
     */
    public static final String CONFIG_KEY = "servicecomb.bulkhead";

    /**
     * isolation bin constructor
     */
    public BulkheadRuleResolver() {
        super(CONFIG_KEY);
    }

    @Override
    protected Class<BulkheadRule> getRuleClass() {
        return BulkheadRule.class;
    }
}
