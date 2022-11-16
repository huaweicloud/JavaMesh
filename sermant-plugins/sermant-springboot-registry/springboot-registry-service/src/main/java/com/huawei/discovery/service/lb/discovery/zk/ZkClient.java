/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.discovery.service.lb.discovery.zk;

import com.huawei.discovery.config.DiscoveryPluginConfig;
import com.huawei.discovery.config.LbConfig;

import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;
import com.huaweicloud.sermant.core.plugin.service.PluginService;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryForever;

import java.util.concurrent.atomic.AtomicReference;

/**
 * zookeeper连接客户端
 *
 * @author zhouss
 * @since 2022-10-25
 */
public class ZkClient implements PluginService {
    private final AtomicReference<ConnectionState> zkState = new AtomicReference<>();

    private final LbConfig lbConfig;

    private CuratorFramework client;

    /**
     * 构造
     */
    public ZkClient() {
        this.lbConfig = PluginConfigManager.getPluginConfig(LbConfig.class);
    }

    @Override
    public void start() {
        if (!PluginConfigManager.getPluginConfig(DiscoveryPluginConfig.class).isEnableRegistry()) {
            return;
        }
        this.client = this.buildClient();
        this.client.getConnectionStateListenable().addListener(new ConnectStateListener());
        this.client.start();
    }

    private CuratorFramework buildClient() {
        return CuratorFrameworkFactory.newClient(lbConfig.getRegistryAddress(), lbConfig.getReadTimeoutMs(),
                lbConfig.getConnectionTimeoutMs(), new RetryForever(lbConfig.getRetryIntervalMs()));
    }

    @Override
    public void stop() {
        if (this.client != null) {
            this.client.close();
        }
    }

    public CuratorFramework getClient() {
        return client;
    }

    /**
     * zk连接状态是否ok
     *
     * @return true: ok
     */
    public boolean isStateOk() {
        final ConnectionState connectionState = zkState.get();
        return connectionState != null && connectionState.isConnected();
    }

    /**
     * zookeeper状态监听器
     *
     * @since 2022-10-13
     */
    class ConnectStateListener implements ConnectionStateListener {
        private ConnectionState oldState;

        @Override
        public void stateChanged(CuratorFramework curatorFramework, ConnectionState newState) {
            if (zkState.compareAndSet(oldState, newState)) {
                this.oldState = newState;
            }
        }
    }
}
