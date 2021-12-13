/*
 * Copyright (C) 2021-2021 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.flowcontrol.service;

import com.huawei.flowcontrol.core.FlowControlThreadFactory;
import com.huawei.flowcontrol.core.config.CommonConst;
import com.huawei.flowcontrol.core.init.InitExecutor;
import com.huawei.flowcontrol.core.init.InitRuleRedis;
import com.huawei.flowcontrol.core.util.DataSourceInitUtils;
import com.huawei.sermant.core.common.LoggerFactory;
import com.huawei.sermant.core.plugin.service.PluginService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 流控插件初始化
 *
 * @author zhouss
 * @since 2021-11-20
 */
public class FlowControlServiceImpl implements PluginService {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(
        new FlowControlThreadFactory("FLOW_CONTROL_INIT_THREAD"));

    @Override
    public void start() {
        executorService.execute(new FlowControlInitTask());
    }

    @Override
    public void stop() {
        executorService.shutdown();
        DataSourceInitUtils.stop();
        InitExecutor.stop();
    }

    static class FlowControlInitTask implements Runnable {
        @Override
        public void run() {
            try {
                // 开启定时任务（发送心跳和监控数据）
                InitExecutor.doInit();
                // 获取启动参数，判断是否备份流控规则
                String flag = System.getProperty(CommonConst.REDIS_RULE_STORE);
                if (Boolean.TRUE.toString().equals(flag)) {
                    InitRuleRedis.doInit();
                }
                DataSourceInitUtils.initRules();
            } catch (Throwable e) {
                LoggerFactory.getLogger().warning(String.format("Init Flow control plugin failed, {%s}", e));
            }
        }
    }
}
