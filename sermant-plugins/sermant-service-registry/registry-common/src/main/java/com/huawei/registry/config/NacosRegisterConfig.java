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

package com.huawei.registry.config;

import com.huaweicloud.sermant.core.config.ConfigManager;
import com.huaweicloud.sermant.core.config.common.ConfigTypeKey;
import com.huaweicloud.sermant.core.plugin.config.PluginConfig;
import com.huaweicloud.sermant.core.plugin.config.ServiceMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * nacos注册插件配置
 *
 * @author chengyouling
 * @since 2022-10-20
 */
@ConfigTypeKey(value = "nacos.service")
public class NacosRegisterConfig implements PluginConfig {
    /**
     * spring cloud zone
     * 若未配置默认使用系统环境变量的zone, 即spring.cloud.loadbalancer.zone
     */
    private String zone;

    /**
     * 是否加密
     */
    private boolean secure = false;

    /**
     * nacos服务发现地址
     */
    private String address = "127.0.0.1:8848";

    /**
     * nacos认证账户
     */
    private String username;

    /**
     * nacos认证密码
     */
    private String password;

    /**
     * 节点地址
     */
    private String endpoint = "";

    /**
     * 命名空间
     */
    private String namespace;

    /**
     * nacos日志文件名
     */
    private String logName;

    /**
     * 服务实例权重
     */
    private float weight = 1;

    /**
     * 集群名称
     */
    private String clusterName = "DEFAULT";

    /**
     * 组
     */
    private String group = "DEFAULT_GROUP";

    /**
     * 启动时是否加载缓存
     */
    private String namingLoadCacheAtStart = "false";

    /**
     * 命名空间AK
     */
    private String accessKey;

    /**
     * 命名空间SK
     */
    private String secretKey;

    /**
     * 实例是否可用
     */
    private boolean instanceEnabled = true;

    /**
     * 是否临时节点
     */
    private boolean ephemeral = true;

    /**
     * 实例元数据
     */
    private Map<String, String> metadata = new HashMap<>();

    /**
     * 是否快速失败取缓存数据，false为不取，直接失败
     */
    private boolean failureToleranceEnabled = false;

    /**
     * 是否开启watch
     */
    private boolean watchEnabled = false;

    /**
     * 服务名分隔符
     */
    private String serviceNameSeparator = ":";

    /**
     * 构造方法
     */
    public NacosRegisterConfig() {
        final ServiceMeta serviceMeta = ConfigManager.getConfig(ServiceMeta.class);
        if (serviceMeta == null) {
            return;
        }
        zone = serviceMeta.getZone();
        group = serviceMeta.getApplication();
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getNamingLoadCacheAtStart() {
        return namingLoadCacheAtStart;
    }

    public void setNamingLoadCacheAtStart(String namingLoadCacheAtStart) {
        this.namingLoadCacheAtStart = namingLoadCacheAtStart;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public boolean isInstanceEnabled() {
        return instanceEnabled;
    }

    public void setInstanceEnabled(boolean instanceEnabled) {
        this.instanceEnabled = instanceEnabled;
    }

    public boolean isEphemeral() {
        return ephemeral;
    }

    public void setEphemeral(boolean ephemeral) {
        this.ephemeral = ephemeral;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public boolean isFailureToleranceEnabled() {
        return failureToleranceEnabled;
    }

    public void setFailureToleranceEnabled(boolean failureToleranceEnabled) {
        this.failureToleranceEnabled = failureToleranceEnabled;
    }

    public boolean isWatchEnabled() {
        return watchEnabled;
    }

    public void setWatchEnabled(boolean watchEnabled) {
        this.watchEnabled = watchEnabled;
    }

    public String getServiceNameSeparator() {
        return serviceNameSeparator;
    }

    public void setServiceNameSeparator(String serviceNameSeparator) {
        this.serviceNameSeparator = serviceNameSeparator;
    }

    /**
     * 获取配置参数
     *
     * @return 配置
     */
    public Properties getNacosProperties() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, address);
        properties.put(PropertyKeyConst.USERNAME, Objects.toString(username, ""));
        properties.put(PropertyKeyConst.PASSWORD, Objects.toString(password, ""));
        properties.put(PropertyKeyConst.NAMESPACE, Objects.toString(namespace, ""));
        properties.put(PropertyKeyConst.NACOS_NAMING_LOG_NAME, Objects.toString(logName, ""));
        if (endpoint.contains(PropertyKeyConst.HTTP_URL_COLON)) {
            int index = endpoint.indexOf(PropertyKeyConst.HTTP_URL_COLON);
            properties.put(PropertyKeyConst.ENDPOINT, endpoint.substring(0, index));
            properties.put(PropertyKeyConst.ENDPOINT_PORT, endpoint.substring(index + 1));
        } else {
            properties.put(PropertyKeyConst.ENDPOINT, endpoint);
        }
        properties.put(PropertyKeyConst.ACCESS_KEY, Objects.toString(accessKey, ""));
        properties.put(PropertyKeyConst.SECRET_KEY, Objects.toString(secretKey, ""));
        properties.put(PropertyKeyConst.CLUSTER_NAME, clusterName);
        properties.put(PropertyKeyConst.NAMING_LOAD_CACHE_AT_START, namingLoadCacheAtStart);
        return properties;
    }
}
