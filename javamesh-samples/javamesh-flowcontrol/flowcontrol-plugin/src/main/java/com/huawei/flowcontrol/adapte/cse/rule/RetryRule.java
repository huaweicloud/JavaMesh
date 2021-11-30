/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 */

package com.huawei.flowcontrol.adapte.cse.rule;

import com.alibaba.csp.sentinel.slots.block.Rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 重试规则
 *
 * @author zhouss
 * @since 2021-11-15
 */
public class RetryRule extends AbstractRule<Rule> {
    /**
     * 默认最大尝试次数
     */
    public static final int DEFAULT_MAX_ATTEMPTS = 3;

    /**
     * 默认等待下次请求时间
     */
    public static final long DEFAULT_WAIT_DURATION_MS = 10L;

    /**
     * 默认重试状态码
     */
    public static final String DEFAULT_RETRY_ON_RESPONSE_STATUS = "502";

    /**
     * 默认初始基数
     */
    private static final long DEFAULT_INITIAL_INTERVAL_MS = 1000L;

    /**
     * 默认指数
     */
    private static final float DEFAULT_MULTIPLIER = 2;

    /**
     * 默认随机因子
     */
    private static final double DEFAULT_RANDOMIZATION_FACTOR = 0.5;

    /**
     * 默认重试策略
     */
    private static final String DEFAULT_RETRY_STRATEGY = "FixedInterval";

    /**
     * 最小基准时间
     */
    private static final long MIN_INITIAL_INTERVAL_MS = 10L;

    /**
     * 最大尝试次数
     */
    private int maxAttempts = DEFAULT_MAX_ATTEMPTS;

    /**
     * 每次重试尝试等待的时间。
     */
    private String waitDuration = String.valueOf(DEFAULT_WAIT_DURATION_MS);

    /**
     * 转换后的尝试等待时间
     */
    private long parsedWaitDuration = DEFAULT_WAIT_DURATION_MS;

    /**
     * 需要重试的http status, 逗号分隔
     */
    private List<String> retryOnResponseStatus = new ArrayList<String>();

    /**
     * 重试策略
     */
    private String retryStrategy = DEFAULT_RETRY_STRATEGY;

    /**
     * 基准时间
     */
    private String initialInterval = String.valueOf(DEFAULT_INITIAL_INTERVAL_MS);

    /**
     * 转换后的基准时间
     */
    private long parsedInitialInterval = DEFAULT_INITIAL_INTERVAL_MS;

    /**
     * 指数基数
     */
    private float multiplier = DEFAULT_MULTIPLIER;

    /**
     * 随机因数
     */
    private double randomizationFactor = DEFAULT_RANDOMIZATION_FACTOR;

    @Override
    public boolean isValid() {
        if (maxAttempts < 1) {
            return true;
        }
        if (parsedWaitDuration < 0) {
            return true;
        }
        if (parsedInitialInterval < MIN_INITIAL_INTERVAL_MS) {
            return true;
        }
        return super.isValid();
    }

    @Override
    public List<Rule> convertToSentinelRule() {
        return Collections.emptyList();
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public String getWaitDuration() {
        return waitDuration;
    }

    public void setWaitDuration(String waitDuration) {
        this.waitDuration = waitDuration;
        this.parsedWaitDuration = parseLongTime(waitDuration, DEFAULT_WAIT_DURATION_MS);
    }

    public List<String> getRetryOnResponseStatus() {
        return retryOnResponseStatus;
    }

    public void setRetryOnResponseStatus(List<String> retryOnResponseStatus) {
        this.retryOnResponseStatus = retryOnResponseStatus;
    }

    public String getRetryStrategy() {
        return retryStrategy;
    }

    public void setRetryStrategy(String retryStrategy) {
        this.retryStrategy = retryStrategy;
    }

    public String getInitialInterval() {
        return initialInterval;
    }

    public void setInitialInterval(String initialInterval) {
        this.initialInterval = initialInterval;
        this.parsedInitialInterval = parseLongTime(initialInterval, DEFAULT_INITIAL_INTERVAL_MS);
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public double getRandomizationFactor() {
        return randomizationFactor;
    }

    public void setRandomizationFactor(double randomizationFactor) {
        this.randomizationFactor = randomizationFactor;
    }

    public long getParsedWaitDuration() {
        return parsedWaitDuration;
    }

    public long getParsedInitialInterval() {
        return parsedInitialInterval;
    }
}
