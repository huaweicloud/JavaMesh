/*
 * Copyright (C) 2023-2023 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huaweicloud.sermant.core.plugin.classloader;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.Plugin;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 用于适配从多个PluginClassLoader中寻找对应类和资源
 *
 * @author luanwenfei
 * @since 2023-05-30
 */
public class PluginClassFinder {
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private final Map<String, PluginClassLoader> pluginClassLoaderMap = new HashMap<>();

    /**
     * 缓存插件类加载器
     *
     * @param plugin 插件
     */
    public void addPluginClassLoader(Plugin plugin) {
        pluginClassLoaderMap.put(plugin.getName(), plugin.getPluginClassLoader());
    }

    /**
     * 移除插件类加载器
     *
     * @param plugin 插件
     */
    public void removePluginClassLoader(Plugin plugin) {
        pluginClassLoaderMap.remove(plugin.getName());
    }

    /**
     * 在Sermant搜索路径下加载对应类名的类
     *
     * @param name 需要查找的类名
     * @return Class<?>
     * @throws ClassNotFoundException 如果在缓存的插件类加载器中都找不到该类则抛出类找不到的异常
     */
    public Class<?> loadSermantClass(String name) throws ClassNotFoundException {
        for (PluginClassLoader pluginClassLoader : pluginClassLoaderMap.values()) {
            try {
                Class<?> clazz = pluginClassLoader.loadSermantClass(name);
                if (clazz != null) {
                    return clazz;
                }
            } catch (ClassNotFoundException e) {
                LOGGER.log(Level.WARNING, "load sermant class failed, msg is {0}", e.getMessage());
            }
        }
        throw new ClassNotFoundException("Can not load class in pluginClassLoaders: " + name);
    }

    /**
     * 在Sermant搜索路径下查找对应资源路径的资源
     *
     * @param path 资源路径
     * @return URL 资源对应的URL
     */
    public Optional<URL> findSermantResource(String path) {
        for (PluginClassLoader pluginClassLoader : pluginClassLoaderMap.values()) {
            URL url = pluginClassLoader.findResource(path);
            if (url != null) {
                return Optional.of(url);
            }
        }
        return Optional.empty();
    }
}
