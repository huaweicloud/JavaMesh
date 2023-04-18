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

package com.huawei.discovery.interceptors;

import com.huawei.discovery.entity.DefaultServiceInstance;
import com.huawei.discovery.entity.RegisterContext;
import com.huawei.discovery.entity.ServiceInstance.Status;
import com.huawei.discovery.utils.HostIpAddressUtils;

import com.huaweicloud.sermant.core.config.ConfigManager;
import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.agent.interceptor.Interceptor;
import com.huaweicloud.sermant.core.plugin.config.ServiceMeta;
import com.huaweicloud.sermant.core.utils.StringUtils;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 结束阶段设置服务相关信息
 *
 * @author chengyouling
 * @since 2022-10-09
 */
public class SpringEnvironmentInfoInterceptor implements Interceptor {
    private static final int DEFAULT_PORT = 8080;

    private static final String DEFAULT_SERVICE_NAME = "default-service";

    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) throws Exception {
        Object[] arguments = context.getArguments();
        if (arguments != null && arguments.length > 0) {
            Object argument = arguments[0];
            if (argument instanceof ConfigurableApplicationContext) {
                ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) argument;

                // 这里有可能会进入多次，多次进入时，后面的优先级高于前面，所以直接覆盖更新就行
                this.setClientInfo(applicationContext.getEnvironment(), HostIpAddressUtils.getHostAddress());
            }
        }
        return context;
    }

    @Override
    public ExecuteContext onThrow(ExecuteContext context) throws Exception {
        return context;
    }

    private void setClientInfo(ConfigurableEnvironment environment, String ipAddress) {
        String address = environment.getProperty("server.address");
        String port = environment.getProperty("server.port");
        String serviceName = environment.getProperty("spring.application.name");
        DefaultServiceInstance instance = RegisterContext.INSTANCE.getServiceInstance();
        instance.setHost(StringUtils.isEmpty(address) ? ipAddress : address);
        instance.setIp(StringUtils.isEmpty(address) ? ipAddress : address);
        instance.setPort(getProperty(instance.getPort(), port, Integer::parseInt, DEFAULT_PORT));
        instance.setServiceName(
                getProperty(instance.getServiceName(), serviceName, value -> value, DEFAULT_SERVICE_NAME));
        instance.setId(instance.getIp() + ":" + instance.getPort());
        instance.setStatus(Status.UP.name());
        if (instance.getMetadata() == null) {
            Map<String, String> metadata = new HashMap<String, String>();
            ServiceMeta serviceMeta = ConfigManager.getConfig(ServiceMeta.class);
            if (StringUtils.isExist(serviceMeta.getZone())) {
                metadata.put("zone", serviceMeta.getZone());
            }
            instance.setMetadata(metadata);
        }
    }

    private <T> T getProperty(T currentProperty, String env, Function<String, T> envMapper, T defaultValue) {
        // environment.getProperty不为空，覆盖
        if (!StringUtils.isBlank(env)) {
            return envMapper.apply(env);
        }

        // environment.getProperty为空且当前值为null，存入默认值
        if (currentProperty == null) {
            return defaultValue;
        }

        // environment.getProperty为空且当前存在值，返回当前值
        return currentProperty;
    }
}
