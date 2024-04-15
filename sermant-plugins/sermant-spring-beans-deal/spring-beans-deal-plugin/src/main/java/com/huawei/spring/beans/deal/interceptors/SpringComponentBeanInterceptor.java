/*
 * Copyright (C) 2023-2024 Sermant Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huawei.spring.beans.deal.interceptors;

import com.huawei.spring.beans.deal.config.SpringBeansDealConfig;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.agent.interceptor.AbstractInterceptor;
import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;
import com.huaweicloud.sermant.core.utils.StringUtils;

import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.Locale;
import java.util.logging.Logger;

/**
 * Component assembly bean interception specific implementation class
 *
 * @author chengyouling
 * @since 2023-03-27
 */
public class SpringComponentBeanInterceptor extends AbstractInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    @Override
    public ExecuteContext before(ExecuteContext context) {
        ClassPathBeanDefinitionScanner scanner = (ClassPathBeanDefinitionScanner) context.getObject();
        SpringBeansDealConfig config = PluginConfigManager.getPluginConfig(SpringBeansDealConfig.class);
        String componentBeanNames = config.getExcludeBeans();
        if (!StringUtils.isEmpty(componentBeanNames)) {
            String[] componentBean = componentBeanNames.split(",");
            for (String className : componentBean) {
                try {
                    Class<?> clazz = Class.forName(className);
                    scanner.addExcludeFilter(new AssignableTypeFilter(clazz));
                    LOGGER.info(String.format(Locale.ENGLISH, "ComponentScanInterceptor find class: [%s]", className));
                } catch (ClassNotFoundException e) {
                    LOGGER.warning("ComponentScanInterceptor can not find class: " + className);
                }
            }
        }
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) {
        return context;
    }
}
