/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.javamesh.sample.servermonitor.service;

import com.huawei.apm.core.config.ConfigManager;
import com.huawei.apm.core.lubanops.bootstrap.log.LogFactory;
import com.huawei.apm.core.plugin.config.PluginConfigManager;
import com.huawei.apm.core.plugin.service.PluginService;
import com.huawei.apm.core.service.ServiceManager;
import com.huawei.apm.core.service.send.GatewayClient;
import com.huawei.javamesh.sample.servermonitor.collecttask.CollectTask;
import com.huawei.javamesh.sample.servermonitor.collecttask.IbmJvmMetricProvider;
import com.huawei.javamesh.sample.servermonitor.collecttask.OpenJvmMetricProvider;
import com.huawei.javamesh.sample.servermonitor.collecttask.ServerMonitorMetricProvider;
import com.huawei.javamesh.sample.servermonitor.config.ServerMonitorConfig;
import com.huawei.javamesh.sample.servermonitor.config.ServiceConfig;
import com.huawei.javamesh.sample.servermonitor.entity.AgentRegistration;
import com.huawei.javamesh.sample.servermonitor.entity.NetworkAddress;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * 监控采集服务
 */
public class MonitorService implements PluginService {

    private static final int AGENT_REG_DATA_TYPE = 7;

    private static final Logger LOGGER = LogFactory.getLogger();

    private final List<CollectTask<?>> collectTasks = new LinkedList<CollectTask<?>>();

    @Override
    public void start() {
        // 这个最好放在核心模块主流程
        register();

        startCollectTasks();
    }

    private void register() {
        ServiceConfig config = PluginConfigManager.getPluginConfig(ServiceConfig.class);
        AgentRegistration.Builder srBuilder = AgentRegistration.newBuilder()
            .setService(config.getService())
            .setServiceInstance(config.getServiceInstance())
            .setJvmVersion(System.getProperty("java.vm.version"))
            .setJvmVendor(System.getProperty("java.vm.vendor"))
            .setRuntimeVersion(System.getProperty("java.runtime.version"));

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress.isLoopbackAddress()) {
                        continue;
                    }
                    srBuilder.addNetworkAddresses(NetworkAddress.newBuilder()
                        .setAddress(inetAddress.getHostAddress())
                        .setHostname(inetAddress.getHostName()));
                }
            }
        } catch (SocketException e) {
            LOGGER.severe("Failed to get network interfaces.");
            srBuilder.addAllNetworkAddresses(Collections.singletonList(
                NetworkAddress.newBuilder()
                    .setAddress("unknown")
                    .setHostname("unknown").build()));
        }
        ServiceManager.getService(GatewayClient.class)
            .send(srBuilder.build().toByteArray(), AGENT_REG_DATA_TYPE);
    }

    private void startCollectTasks() {
        final ServerMonitorConfig config = ConfigManager.getConfig(ServerMonitorConfig.class);
        final long collectInterval = config.getCollectInterval();
        final long consumeInterval = config.getConsumeInterval();
        final TimeUnit timeUnit = TimeUnit.valueOf(config.getTimeunit());
        if (System.getProperty("java.vm.vendor").contains("IBM")) {
            collectTasks.add(CollectTask.create(IbmJvmMetricProvider.getInstance(),
                collectInterval, consumeInterval, timeUnit));
        } else {
            collectTasks.add(CollectTask.create(OpenJvmMetricProvider.getInstance(),
                collectInterval, consumeInterval, timeUnit));
        }
        // 此处用白名单比较合适，比如除了Windows外的其他非Linux也是不采集的
        if(!System.getProperty("os.name").contains("Windows")) {
            collectTasks.add(CollectTask.create(new ServerMonitorMetricProvider(collectInterval),
                collectInterval, consumeInterval, timeUnit));
        }

        for (CollectTask<?> collectTask : collectTasks) {
            collectTask.start();
        }
    }

    @Override
    public void stop() {
        for (CollectTask<?> collectTask : collectTasks) {
            collectTask.stop();
        }
    }
}
