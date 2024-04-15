/*
 * Copyright (C) 2022-2024 Sermant Authors. All rights reserved.
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

package com.huaweicloud.sermant.core.service.send.config;

import com.huaweicloud.sermant.core.config.common.BaseConfig;
import com.huaweicloud.sermant.core.config.common.ConfigTypeKey;

/**
 * Gateway Config
 *
 * @author luanwenfei
 * @since 2022-03-24
 */
@ConfigTypeKey("gateway")
public class GatewayConfig implements BaseConfig {
    private static final int DEFAULT_INIT_RECONNECT_INTERNAL_TIME = 5;

    private static final int DEFAULT_MAX_RECONNECT_INTERNAL_TIME = 180;

    private static final int DEFAULT_NETTY_CONNECT_TIMEOUT = 5000;

    private static final long DEFAULT_NETTY_WRITE_READ_WAIT_TIME = 60000L;

    private static final String DEFAULT_NETTY_IP = "127.0.0.1";

    private static final int DEFAULT_NETTY_PORT = 6888;

    private static final int DEFAULT_SEND_INTERNAL_TIME = 10;

    /**
     * netty ip address
     */
    private String nettyIp = DEFAULT_NETTY_IP;

    /**
     * netty port
     */
    private int nettyPort = DEFAULT_NETTY_PORT;

    /**
     * interval for sending messages, unit: second
     */
    private int sendInternalTime = DEFAULT_SEND_INTERNAL_TIME;

    /**
     * netty initial reconnection time after disconnection, unit: second
     */
    private int initReconnectInternalTime = DEFAULT_INIT_RECONNECT_INTERNAL_TIME;

    /**
     * netty maximum connection reconnection time, unit: second
     */
    private int maxReconnectInternalTime = DEFAULT_MAX_RECONNECT_INTERNAL_TIME;

    /**
     * netty connect timeout, unit: millisecond
     */
    private int nettyConnectTimeout = DEFAULT_NETTY_CONNECT_TIMEOUT;

    private long nettyWriteAndReadWaitTime = DEFAULT_NETTY_WRITE_READ_WAIT_TIME;

    public String getNettyIp() {
        return nettyIp;
    }

    public void setNettyIp(String nettyIp) {
        this.nettyIp = nettyIp;
    }

    public int getNettyPort() {
        return nettyPort;
    }

    public void setNettyPort(int nettyPort) {
        this.nettyPort = nettyPort;
    }

    public int getNettyConnectTimeout() {
        return nettyConnectTimeout;
    }

    public void setNettyConnectTimeout(int nettyConnectTimeout) {
        this.nettyConnectTimeout = nettyConnectTimeout;
    }

    public long getNettyWriteAndReadWaitTime() {
        return nettyWriteAndReadWaitTime;
    }

    public void setNettyWriteAndReadWaitTime(long nettyWriteAndReadWaitTime) {
        this.nettyWriteAndReadWaitTime = nettyWriteAndReadWaitTime;
    }

    public int getSendInternalTime() {
        return sendInternalTime;
    }

    public void setSendInternalTime(int sendInternalTime) {
        this.sendInternalTime = sendInternalTime;
    }

    public int getInitReconnectInternalTime() {
        return initReconnectInternalTime;
    }

    public void setInitReconnectInternalTime(int initReconnectInternalTime) {
        this.initReconnectInternalTime = initReconnectInternalTime;
    }

    public int getMaxReconnectInternalTime() {
        return maxReconnectInternalTime;
    }

    public void setMaxReconnectInternalTime(int maxReconnectInternalTime) {
        this.maxReconnectInternalTime = maxReconnectInternalTime;
    }
}
