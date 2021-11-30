/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 */

package com.huawei.flowcontrol.adapte.cse.rule;


import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;

import java.util.Collections;
import java.util.List;

/**
 * 限流规则
 *
 * @author zhouss
 * @since 2021-11-15
 */
public class RateLimitingRule extends AbstractRule<FlowRule> {
    /**
     * 默认超时时间
     */
    public static final long DEFAULT_TIMEOUT_DURATION_MS = 0L;

    /**
     * 默认单位时间
     */
    public static final long DEFAULT_LIMIT_REFRESH_PERIOD_MS = 1000L;

    /**
     * 默认许可数
     */
    public static final int DEFAULT_RATE = 1000;

    /**
     * 超时时间
     */
    private String timeoutDuration = "0";

    /**
     * 转换后的超时时间
     */
    private long parsedTimeoutDuration = DEFAULT_TIMEOUT_DURATION_MS;

    /**
     * 单位时间
     */
    private String limitRefreshPeriod = "1000";

    /**
     * 转换后的单位时间
     */
    private long parsedLimitRefreshPeriod = DEFAULT_LIMIT_REFRESH_PERIOD_MS;

    /**
     * 默认许可数
     * 单位时间内超过该许可数便会触发限流
     */
    private int rate = DEFAULT_RATE;

    @Override
    public boolean isValid() {
        return parsedTimeoutDuration < 0 || parsedLimitRefreshPeriod <= 0 || rate <= 0 || super.isValid();
    }

    @Override
    public List<FlowRule> convertToSentinelRule() {
        final FlowRule flowRule = new FlowRule();
        // 转换为rate/s, sentinel当前只能以1S为单位进行统计, 因此此处做一定请求比例转换
        flowRule.setCount(this.rate * 1000.0 / this.parsedLimitRefreshPeriod);
        flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        flowRule.setResource(getName());
        return Collections.singletonList(flowRule);
    }

    public long getParsedTimeoutDuration() {
        return parsedTimeoutDuration;
    }

    public long getParsedLimitRefreshPeriod() {
        return parsedLimitRefreshPeriod;
    }

    public String getTimeoutDuration() {
        return timeoutDuration;
    }

    public void setTimeoutDuration(String timeoutDuration) {
        this.timeoutDuration = timeoutDuration;
        this.parsedTimeoutDuration = parseLongTime(timeoutDuration, DEFAULT_TIMEOUT_DURATION_MS);
    }

    public String getLimitRefreshPeriod() {
        return limitRefreshPeriod;
    }

    public void setLimitRefreshPeriod(String limitRefreshPeriod) {
        this.limitRefreshPeriod = limitRefreshPeriod;
        this.parsedLimitRefreshPeriod = parseLongTime(limitRefreshPeriod, DEFAULT_LIMIT_REFRESH_PERIOD_MS);
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
