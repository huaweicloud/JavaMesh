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

package com.huaweicloud.sermant.core.plugin.agent.config;

import com.huaweicloud.sermant.core.config.common.BaseConfig;
import com.huaweicloud.sermant.core.config.common.ConfigTypeKey;

import java.util.Collections;
import java.util.Set;

/**
 * Enhancement configuration
 *
 * @author HapThorin
 * @version 1.0.0
 * @since 2022-01-25
 */
@ConfigTypeKey("agent.config")
public class AgentConfig implements BaseConfig {
    /**
     * Whether to enable bytecode retransform
     */
    private boolean isReTransformEnable = false;

    /**
     * Enhancement ignored set, a set in which the fully qualified name prefix defined is used to exclude classes that
     * are ignored during enhancement, contains {@code com.huawei.sermant} by default, and is not mandatory
     */
    private Set<String> ignoredPrefixes = Collections.singleton("com.huawei.sermant");

    /**
     * Enhancement ignored interface set, defined in the collection interface is used to exclude enhancement is ignored
     * in the process of class, the default contains {@org.Springframework.Additional.Proxy.Factory}, mandatory,
     * otherwise it will cause spring dynamic proxy conflict, throws java.lang.VerifyError
     */
    private Set<String> ignoredInterfaces = Collections.singleton("org.springframework.cglib.proxy.Factory");

    /**
     * Whether to output a search log during the enhancement process
     */
    private boolean isShowEnhanceLog = false;

    /**
     * Whether to output the bytecode file of the enhanced class
     */
    private boolean isOutputEnhancedClasses = false;

    /**
     * The output path of the enhanced class, if empty, is will use agent/enhancedClasses
     */
    private String enhancedClassesOutputPath;

    /**
     * List of inject plugin services
     */
    private Set<String> serviceInjectList = Collections.emptySet();

    /**
     * Allows classes to be loaded from the thread context, mainly used by the PluginClassLoader to load the
     * classes of the host instance through the thread context, if not allowed can be specified during the
     * interceptor call
     */
    private boolean useContextLoader = false;

    public boolean isReTransformEnable() {
        return isReTransformEnable;
    }

    public void setReTransformEnable(boolean reTransformEnable) {
        isReTransformEnable = reTransformEnable;
    }

    public Set<String> getIgnoredPrefixes() {
        return ignoredPrefixes;
    }

    public Set<String> getIgnoredInterfaces() {
        return ignoredInterfaces;
    }

    public void setIgnoredInterfaces(Set<String> ignoredInterfaces) {
        this.ignoredInterfaces = ignoredInterfaces;
    }

    public void setIgnoredPrefixes(Set<String> ignoredPrefixes) {
        this.ignoredPrefixes = ignoredPrefixes;
    }

    public boolean isShowEnhanceLog() {
        return isShowEnhanceLog;
    }

    public void setShowEnhanceLog(boolean showEnhanceLog) {
        isShowEnhanceLog = showEnhanceLog;
    }

    public boolean isOutputEnhancedClasses() {
        return isOutputEnhancedClasses;
    }

    public void setOutputEnhancedClasses(boolean outputEnhancedClasses) {
        isOutputEnhancedClasses = outputEnhancedClasses;
    }

    public String getEnhancedClassesOutputPath() {
        return enhancedClassesOutputPath;
    }

    public void setEnhancedClassesOutputPath(String enhancedClassesOutputPath) {
        this.enhancedClassesOutputPath = enhancedClassesOutputPath;
    }

    public Set<String> getServiceInjectList() {
        return serviceInjectList;
    }

    public void setServiceInjectList(Set<String> serviceInjectList) {
        this.serviceInjectList = serviceInjectList;
    }

    public boolean isUseContextLoader() {
        return useContextLoader;
    }

    public void setUseContextLoader(boolean useContextLoader) {
        this.useContextLoader = useContextLoader;
    }
}
