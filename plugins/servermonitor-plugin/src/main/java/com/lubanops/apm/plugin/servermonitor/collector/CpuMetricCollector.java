/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.lubanops.apm.plugin.servermonitor.collector;


import com.lubanops.apm.plugin.servermonitor.command.Command;
import com.lubanops.apm.plugin.servermonitor.command.CommandExecutor;
import com.lubanops.apm.plugin.servermonitor.command.CpuCommand;
import com.lubanops.apm.plugin.servermonitor.entity.CpuMetric;

import static com.lubanops.apm.plugin.servermonitor.common.CalculateUtil.getPercentage;

/**
 * Linux CPU指标{@link CpuMetric}采集器，通过执行两次{@link CpuCommand}命令
 * 获取两次{@link CpuCommand.CpuStat}结果，来计算调用间隔时间内各种CPU时间占比。
 *
 * <p>每调用一次{@link #getCpuMetric()}方法会触发一次{@link CpuCommand}命令
 * 的执行，然后将本次执行的{@link CpuCommand.CpuStat}结果与上次执行的结果进行计
 * 算得到{@link CpuMetric}，并缓存本次执行结果用于下次计算</p>
 *
 * <p>重构泛PaaS：com.huawei.apm.plugin.collection.cpu.ServerCPUProvider
 * </p>
 */
public class CpuMetricCollector {

    private static final int SCALE = 0;

    private final CpuMetric emptyResult = new CpuMetric();

    private CpuCommand.CpuStat lastState;

    /**
     * 获取cpu指标{@link CpuMetric}
     *
     * @return {@link CpuMetric}
     */
    public CpuMetric getCpuMetric() {
        final CpuCommand.CpuStat currentState = CommandExecutor.execute(Command.CPU);
        return currentState == null ? emptyResult : buildResult(currentState);
    }

    private CpuMetric buildResult(CpuCommand.CpuStat currentState) {
        CpuMetric cpuMetric;
        if (lastState == null) {
            cpuMetric = emptyResult;
        } else {
            cpuMetric = CpuMetric.newBuilder()
                .withIdlePercentage(calcIdlePercentage(currentState))
                .withIoWaitPercentage(calcIoWaitPercentage(currentState))
                .withSysPercentage(calcSysPercentage(currentState))
                .withUserPercentage(calcUserPercentage(currentState))
                .build();
        }
        lastState = currentState;
        return cpuMetric;
    }

    private int calcIdlePercentage(CpuCommand.CpuStat currentState) {
        return getPercentage(
            currentState.getIdle() - lastState.getIdle(),
            currentState.getTotal() - lastState.getTotal(), SCALE).intValue();
    }

    private int calcIoWaitPercentage(CpuCommand.CpuStat currentState) {
        return getPercentage(
            currentState.getIoWait() - lastState.getIoWait(),
            currentState.getTotal() - lastState.getTotal(), SCALE).intValue();
    }

    private int calcSysPercentage(CpuCommand.CpuStat currentState) {
        return getPercentage(
            currentState.getSystem() - lastState.getSystem(),
            currentState.getTotal() - lastState.getTotal(), SCALE).intValue();
    }

    /**
     * user = user + nice 参照
     * <a href=https://github.com/i4oolish/nmon/blob/master/lnmon.c>nmon</a>
     */
    private int calcUserPercentage(CpuCommand.CpuStat currentState) {
        return getPercentage(
            (currentState.getUser() + currentState.getNice()) - (lastState.getUser() + lastState.getNice()),
            currentState.getTotal() - lastState.getTotal(), SCALE).intValue();
    }
}
