/*
 * Copyright (C) 2022-2024 Huawei Technologies Co., Ltd. All rights reserved.
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

package io.sermant.backend.entity.heartbeat;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Heartbeat message
 *
 * @author luanwenfei
 * @since 2022-03-19
 */
public class HeartbeatMessage {
    private String service;

    private String appType;

    private String hostName;

    @JSONField(serialize = false)
    private long receiveTime;

    private AtomicBoolean health = new AtomicBoolean(false);

    private List<String> ip;

    private long heartbeatTime;

    private long lastHeartbeatTime;

    private String version;

    private String instanceId;

    private Map<String, PluginInfo> pluginInfoMap = new HashMap<>();

    private Map<String, ExternalAgentInfo> externalAgentInfoMap = new HashMap<>();

    private String appName;

    private String artifact;

    private String processId;

    private boolean dynamicInstall;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public List<String> getIp() {
        return ip;
    }

    public void setIp(List<String> ip) {
        this.ip = ip;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public long getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(long heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }

    public long getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }

    public void setLastHeartbeatTime(long lastHeartbeatTime) {
        this.lastHeartbeatTime = lastHeartbeatTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public List<PluginInfo> getPluginInfoMap() {
        return new ArrayList<>(pluginInfoMap.values());
    }

    public void setPluginInfoMap(Map<String, PluginInfo> pluginInfoMap) {
        this.pluginInfoMap = pluginInfoMap;
    }

    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    public boolean isHealth() {
        return this.health.get();
    }

    /**
     * Set health status
     *
     * @param health health
     */
    public void setHealth(boolean health) {
        this.health.compareAndSet(!health, health);
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getArtifact() {
        return artifact;
    }

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    public String getProcessId() {
        return processId;
    }

    public boolean isDynamicInstall() {
        return dynamicInstall;
    }

    public void setDynamicInstall(boolean dynamicInstall) {
        this.dynamicInstall = dynamicInstall;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Map<String, ExternalAgentInfo> getExternalAgentInfoMap() {
        return externalAgentInfoMap;
    }

    public void setExternalAgentInfoMap(Map<String, ExternalAgentInfo> externalAgentInfoMap) {
        this.externalAgentInfoMap = externalAgentInfoMap;
    }
}
