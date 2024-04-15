/*
 * Copyright (C) 2021-2024 Sermant Authors. All rights reserved.
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

package com.huaweicloud.sermant.router.spring.strategy.rule;

import com.huaweicloud.sermant.router.config.strategy.AbstractRuleStrategy;
import com.huaweicloud.sermant.router.spring.strategy.instance.MatchInstanceStrategy;
import com.huaweicloud.sermant.router.spring.strategy.instance.MismatchInstanceStrategy;
import com.huaweicloud.sermant.router.spring.strategy.mapper.AbstractMetadataMapper;

/**
 * The routing rule matches the policy
 *
 * @param <I> Instance generics
 * @author provenceee
 * @since 2021-10-14
 */
public class InstanceRuleStrategy<I> extends AbstractRuleStrategy<I> {
    /**
     * Constructor
     *
     * @param mapper Metadata fetch method
     */
    public InstanceRuleStrategy(AbstractMetadataMapper<I> mapper) {
        super("spring", new MatchInstanceStrategy<>(), new MismatchInstanceStrategy<>(), mapper);
    }
}