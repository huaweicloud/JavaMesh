/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.lubanops.apm.plugin.servermonitor.jvm;


import com.lubanops.apm.plugin.servermonitor.entity.IBMMemoryPool;

import java.util.LinkedList;
import java.util.List;

/**
 * 未知ibm内存类
 *
 * @author zhengbin zhao
 * @since 2021-03-16
 */
public class UnknownMemoryPool implements MemoryPoolMetricsAccessor {
    @Override
    public List<IBMMemoryPool> getMemoryPoolMetricsList() {
        return new LinkedList<IBMMemoryPool>();
    }
}
