/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huaweicloud.sermant.core.plugin.subscribe.processor;

import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigEvent;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigEventType;

import java.util.Map;

/**
 * 优先级排序事件, 附带全量数据
 *
 * @author zhouss
 * @since 2022-04-21
 */
public class OrderConfigEvent extends DynamicConfigEvent {
    private static final long serialVersionUID = 4990176887738080367L;

    private final Map<String, Object> allData;

    /**
     * 构造器
     *
     * @param key 配置键
     * @param group 组
     * @param content 配置内容
     * @param eventType 事件类型
     * @param allData 所有数据
     */
    public OrderConfigEvent(String key, String group, String content, DynamicConfigEventType eventType, Map<String,
            Object> allData) {
        super(key, group, content, eventType);
        this.allData = allData;
    }

    /**
     * 全量数据
     *
     * @return 全量接收的数据, 已按照优先级进行数据覆盖
     */
    public Map<String, Object> getAllData() {
        return this.allData;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object target) {
        return super.equals(target);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
