/*
 * Copyright (C) 2021-2021 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.sermant.core.service.dynamicconfig;

import com.huawei.sermant.core.common.LoggerFactory;
import com.huawei.sermant.core.service.dynamicconfig.kie.KieDynamicConfigurationServiceImpl;
import com.huawei.sermant.core.service.dynamicconfig.nop.NopDynamicConfigurationService;
import com.huawei.sermant.core.service.dynamicconfig.service.DynamicConfigType;
import com.huawei.sermant.core.service.dynamicconfig.service.DynamicConfigurationFactoryService;
import com.huawei.sermant.core.service.dynamicconfig.service.DynamicConfigurationService;
import com.huawei.sermant.core.service.dynamicconfig.zookeeper.ZookeeperDynamicConfigurationService;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * The implementation for the DynamicConfigurationFactoryService
 *
 */
public class DynamicConfigurationFactoryServiceImpl implements DynamicConfigurationFactoryService {

    private static final Logger logger = LoggerFactory.getLogger();

    protected DynamicConfigurationService getDynamicConfigurationService(DynamicConfigType dct) {

        if ( dct == DynamicConfigType.ZOO_KEEPER )
            return ZookeeperDynamicConfigurationService.getInstance();

        if ( dct == DynamicConfigType.KIE )
            return KieDynamicConfigurationServiceImpl.getInstance();

        if ( dct == DynamicConfigType.NOP )
            return NopDynamicConfigurationService.getInstance();

        return null;
    }

    @Override
    public DynamicConfigurationService getDynamicConfigurationService() {
        return this.getDynamicConfigurationService(Config.getDynamic_config_type());
    }

    @Override
    public void start() {
        DynamicConfigurationService dcs = this.getDynamicConfigurationService();
        logger.log(Level.INFO, "DynamicConfigurationFactoryServiceImpl start. DynamicConfigurationService inst is " +
                ( dcs == null ? "null" : dcs.toString()) );
    }

    @Override
    public void stop() {
        try {
            this.getDynamicConfigurationService().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
