/*
 *   Copyright (C) 2023-2023 Huawei Technologies Co., Ltd. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.huaweicloud.sermant.tag.transmission.crossthread.pojo;

import com.huaweicloud.sermant.core.utils.tag.TrafficData;
import com.huaweicloud.sermant.core.utils.tag.TrafficTag;

/**
 * TrafficMessage，包含TrafficTag和TrafficData
 *
 * @author daizhenyu
 * @since 2023-09-05
 **/
public class TrafficMessage {
    private TrafficTag trafficTag;

    private TrafficData trafficData;

    /**
     * 无参构造方法
     */
    public TrafficMessage() {
    }

    /**
     * 有参构造方法
     *
     * @param trafficTag 流量标签
     * @param trafficData 流量数据
     */
    public TrafficMessage(TrafficTag trafficTag, TrafficData trafficData) {
        this.trafficTag = trafficTag;
        this.trafficData = trafficData;
    }

    public void setTrafficData(TrafficData trafficData) {
        this.trafficData = trafficData;
    }

    public void setTrafficTag(TrafficTag trafficTag) {
        this.trafficTag = trafficTag;
    }

    public TrafficTag getTrafficTag() {
        return trafficTag;
    }

    public TrafficData getTrafficData() {
        return trafficData;
    }
}
