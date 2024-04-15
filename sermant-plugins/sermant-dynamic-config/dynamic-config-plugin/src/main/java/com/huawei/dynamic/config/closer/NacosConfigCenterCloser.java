/*
 * Copyright (C) 2022-2024 Sermant Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huawei.dynamic.config.closer;

import com.huaweicloud.sermant.core.utils.ClassUtils;
import com.huaweicloud.sermant.core.utils.ReflectUtils;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * nacos closer
 *
 * @author zhouss
 * @since 2022-07-12
 */
public class NacosConfigCenterCloser implements ConfigCenterCloser {
    private static final String REFRESHER_NAME = "nacosContextRefresher";

    private static final String CONFIG_CENTER_PROPERTY_SOURCE_TYPE =
            "com.alibaba.cloud.nacos.client.NacosPropertySource";

    private Object nacosContextRefresher;

    private Boolean isSupported;

    @Override
    public boolean close(BeanFactory beanFactory, Environment environment) {
        shutdownConfigService();
        return removeNacosPropertySource(environment);
    }

    @Override
    public boolean isSupport(BeanFactory beanFactory) {
        if (isSupported == null && nacosContextRefresher == null) {
            final Optional<Class<?>> refresherClass = ClassUtils
                    .loadClass("com.alibaba.cloud.nacos.refresh.NacosContextRefresher", Thread.currentThread()
                            .getContextClassLoader(), false);
            final Map<String, ?> beanMap = getBeans(Collections.singletonList(REFRESHER_NAME),
                    refresherClass.orElse(null), beanFactory);
            this.nacosContextRefresher = beanMap.get(REFRESHER_NAME);
        }
        isSupported = this.nacosContextRefresher != null;
        return isSupported;
    }

    @Override
    public ConfigCenterType type() {
        return ConfigCenterType.NACOS;
    }

    @Override
    public boolean isCurConfigCenterSource(PropertySource<?> propertySource) {
        return CONFIG_CENTER_PROPERTY_SOURCE_TYPE.equals(propertySource.getClass().getName());
    }

    private boolean removeNacosPropertySource(Environment environment) {
        return removeBootstrapPropertySource(environment, "NACOS");
    }

    private void shutdownConfigService() {
        if (nacosContextRefresher == null) {
            return;
        }
        final Optional<Object> configService = ReflectUtils.getFieldValue(nacosContextRefresher, "configService");
        configService.ifPresent(object -> ReflectUtils.invokeMethod(object, "shutDown", null, null));
    }
}
