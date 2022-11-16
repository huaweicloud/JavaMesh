/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package com.huawei.monitor.common;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 指标计算实体类
 *
 * @author zhp
 * @since 2022-09-15
 */
public class MetricCalEntity {
    /**
     * 服务端累计请求数
     */
    private AtomicLong reqNum;

    /**
     * 服务端累计成功请求数
     */
    private AtomicLong successFulReqNum;

    /**
     * 服务端累计请求时间
     */
    private AtomicLong consumeReqTimeNum;

    /**
     * 服务端累计失败请求数
     */
    private AtomicLong failedReqNum;

    /**
     * 初始化
     */
    public MetricCalEntity() {
        this.reqNum = new AtomicLong();
        this.successFulReqNum = new AtomicLong();
        this.consumeReqTimeNum = new AtomicLong();
        this.failedReqNum = new AtomicLong();
    }

    public AtomicLong getReqNum() {
        return reqNum;
    }

    public void setReqNum(AtomicLong reqNum) {
        this.reqNum = reqNum;
    }

    public AtomicLong getSuccessFulReqNum() {
        return successFulReqNum;
    }

    public void setSuccessFulReqNum(AtomicLong successFulReqNum) {
        this.successFulReqNum = successFulReqNum;
    }

    public AtomicLong getConsumeReqTimeNum() {
        return consumeReqTimeNum;
    }

    public void setConsumeReqTimeNum(AtomicLong consumeReqTimeNum) {
        this.consumeReqTimeNum = consumeReqTimeNum;
    }

    public AtomicLong getFailedReqNum() {
        return failedReqNum;
    }

    public void setFailedReqNum(AtomicLong failedReqNum) {
        this.failedReqNum = failedReqNum;
    }
}
