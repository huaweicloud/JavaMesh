/*
 * Copyright (C) 2021-2024 Sermant Authors. All rights reserved.
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

package com.huaweicloud.sermant.core.config.strategy;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.config.common.BaseConfig;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Strategy for loading configuration object
 * <p>Loading configuration information consists of two steps:
 * <pre>
 *     1.Load the configuration file as configuration information{@link #getConfigHolder(File, Map)}
 *     2.Load the configuration information into the configuration object{@link #loadConfig(Object, BaseConfig)}
 * </pre>
 *
 * @param <T> configuration type
 * @author HapThorin
 * @version 1.0.0
 * @since 2021-08-18
 */
public interface LoadConfigStrategy<T> {
    /**
     * whether it can load
     *
     * @param file configuration file
     * @return boolean result
     */
    boolean canLoad(File file);

    /**
     * The host object for obtaining configuration information
     * <p>The host object is related to the configuration file format, such as the properties file for the {@code
     * Properties} object and the xml for the {@code Document} object
     * <p>You need to customize the class to obtain configuration information by configuring the file name. There are
     * generally the following methods for carrying objects:
     * <pre>
     *     1.Get an external configuration file through a specific configuration file directory or relative directory
     *     2.Obtain the configuration file in the internal resource directory
     *     3.Get the configuration file through the network socket stream
     * </pre>
     * {@code argsMap} is the parameter set at startup. It has the highest priority and needs to be customized
     *
     * @param config configuration file
     * @param argsMap argsMap
     * @return host object
     */
    T getConfigHolder(File config, Map<String, Object> argsMap);

    /**
     * 加载配置，将配置信息主要承载对象中的配置信息加载到配置对象中
     *
     * @param holder 配置信息主要承载对象
     * @param config 配置对象
     * @param <R>    配置对象类型
     * @return 配置对象
     */
    <R extends BaseConfig> R loadConfig(T holder, R config);

    /**
     * default {@link LoadConfigStrategy}, do not perform any logical operations
     *
     * @since 2021-08-18
     */
    class DefaultLoadConfigStrategy implements LoadConfigStrategy<Object> {
        /**
         * 日志
         */
        private static final Logger LOGGER = LoggerFactory.getLogger();

        @Override
        public boolean canLoad(File file) {
            return false;
        }

        @Override
        public Object getConfigHolder(File config, Map<String, Object> argsMap) {
            LOGGER.log(Level.WARNING, String.format(Locale.ROOT, "[%s] will do nothing when reading config file. ",
                    DefaultLoadConfigStrategy.class.getName()));
            return argsMap;
        }

        @Override
        public <R extends BaseConfig> R loadConfig(Object holder, R config) {
            LOGGER.log(Level.WARNING, String.format(Locale.ROOT, "[%s] will do nothing when loading config. ",
                    DefaultLoadConfigStrategy.class.getName()));
            return config;
        }
    }
}
