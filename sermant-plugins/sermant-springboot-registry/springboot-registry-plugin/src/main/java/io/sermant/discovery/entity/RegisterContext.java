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

package io.sermant.discovery.entity;

import java.util.Map;

/**
 * Registration Information Class
 *
 * @author chengyouling
 * @since 2022-10-09
 */
public enum RegisterContext {

    /**
     * Instance
     */
    INSTANCE;

    private final DefaultServiceInstance serviceInstance = new DefaultServiceInstance();

    /**
     * Get a service instance
     *
     * @return Service instances
     */
    public DefaultServiceInstance getServiceInstance() {
        return this.serviceInstance;
    }

    private final ClientInfo clientInfo = new ClientInfo();

    public ClientInfo getClientInfo() {
        return clientInfo;
    }


    /**
     * 客户端信息
     *
     * @since 2022-03-01
     */
    public static class ClientInfo {
        /**
         * 服务名 通过拦截获取
         */
        private String serviceName;

        /**
         * 域名
         */
        private String host;

        /**
         * ip
         */
        private String ip;

        /**
         * 端口
         */
        private int port;

        /**
         * 服务id
         */
        private String serviceId;

        /**
         * 服务元信息
         */
        private Map<String, String> meta;

        /**
         * 区域
         */
        private String zone;

        /**
         * 实例状态 UP DOWN
         */
        private String status = "UN_KNOWN";

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getServiceId() {
            return serviceId;
        }

        public void setServiceId(String serviceId) {
            this.serviceId = serviceId;
        }

        public String getZone() {
            return zone;
        }

        public void setZone(String zone) {
            this.zone = zone;
        }

        public Map<String, String> getMeta() {
            return meta;
        }

        public void setMeta(Map<String, String> meta) {
            this.meta = meta;
        }
    }

}
