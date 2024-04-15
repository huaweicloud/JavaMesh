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

package com.huaweicloud.sermant.core.common;

import com.huaweicloud.sermant.core.exception.SchemaException;
import com.huaweicloud.sermant.core.utils.FileUtils;
import com.huaweicloud.sermant.core.utils.JarFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * BootArgsIndexer
 *
 * @author HapThorin
 * @version 1.0.0
 * @since 2021-11-03
 */
public class BootArgsIndexer {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger();

    /**
     * Version of java agent
     */
    private static final String CORE_VERSION;

    /**
     * Directory where the core function implementation package resides
     */
    private static File implementDir;

    /**
     * Config File
     */
    private static File configFile;

    /**
     * Plugin settings file
     */
    private static File pluginSettingFile;

    /**
     * Log Setting File
     */
    private static File logSettingFile;

    /**
     * PluginPackage directory
     */
    private static File pluginPackageDir;

    private static String appName;

    private static String appType;

    private static String serviceName;

    private static String instanceId;

    private BootArgsIndexer() {
    }

    public static String getCoreVersion() {
        return CORE_VERSION;
    }

    public static File getImplementDir() {
        return implementDir;
    }

    public static File getConfigFile() {
        return configFile;
    }

    public static File getPluginSettingFile() {
        return pluginSettingFile;
    }

    public static File getPluginPackageDir() {
        return pluginPackageDir;
    }

    public static File getLogSettingFile() {
        return logSettingFile;
    }

    public static String getAppName() {
        return appName;
    }

    public static String getServiceName() {
        return serviceName;
    }

    public static String getAppType() {
        return appType;
    }

    public static String getInstanceId() {
        return instanceId;
    }

    /**
     * 构建路径索引器
     *
     * @param argsMap 启动参数
     */
    public static void build(Map<String, Object> argsMap) {
        implementDir = new File(FileUtils.validatePath(argsMap.get(CommonConstant.CORE_IMPLEMENT_DIR_KEY).toString()));
        if (!implementDir.exists() || !implementDir.isDirectory()) {
            LOGGER.warning("Implement directory not found! ");
        }
        configFile = new File(FileUtils.validatePath(argsMap.get(CommonConstant.CORE_CONFIG_FILE_KEY).toString()));
        if (!configFile.exists() || !configFile.isFile()) {
            LOGGER.warning("Config file is not found! ");
        }
        pluginSettingFile = new File(FileUtils.validatePath(argsMap.get(CommonConstant.PLUGIN_SETTING_FILE_KEY)
                .toString()));
        if (!pluginSettingFile.exists() || !pluginSettingFile.isFile()) {
            LOGGER.warning("Plugin setting file is not found! ");
        }
        logSettingFile = new File(FileUtils.validatePath(argsMap.get(CommonConstant.LOG_SETTING_FILE_KEY)
                .toString()));
        if (!logSettingFile.exists() || !logSettingFile.isFile()) {
            LOGGER.warning("Log setting file is not found! Using default log setting file in resources.");
        }
        pluginPackageDir = new File(FileUtils.validatePath(argsMap.get(CommonConstant.PLUGIN_PACKAGE_DIR_KEY)
                .toString()));
        if (!pluginPackageDir.exists() || !pluginPackageDir.isDirectory()) {
            LOGGER.warning("Plugin package directory is not found! ");
        }

        appName = argsMap.get(CommonConstant.APP_NAME_KEY).toString();

        appType = argsMap.get(CommonConstant.APP_TYPE_KEY).toString();

        serviceName = argsMap.get(CommonConstant.SERVICE_NAME_KEY).toString();

        instanceId = UUID.randomUUID().toString();
    }

    static {
        final String currentFile = BootArgsIndexer.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(currentFile);
            CORE_VERSION = JarFileUtils.getManifestAttr(jarFile, CommonConstant.CORE_VERSION_KEY).toString();
        } catch (IOException e) {
            throw new SchemaException(SchemaException.MISSING_VERSION, currentFile);
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException ignored) {
                    LOGGER.warning("Unexpected exception occurs. ");
                }
            }
        }
    }
}
