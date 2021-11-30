/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 */

package com.huawei.apm.core.service.dynamicconfig;

import com.huawei.apm.core.common.LoggerFactory;
import com.huawei.apm.core.service.BaseService;
import com.huawei.apm.core.service.dynamicconfig.service.ConfigChangedEvent;
import com.huawei.apm.core.service.dynamicconfig.service.ConfigurationListener;
import com.huawei.apm.core.service.dynamicconfig.service.DynamicConfigType;
import com.huawei.apm.core.service.dynamicconfig.service.DynamicConfigurationFactoryService;
import com.huawei.apm.core.service.dynamicconfig.service.DynamicConfigurationService;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.ServiceLoader;

/**
 * kie与zookeeper切换测试
 *
 * @author zhouss
 * @since 2021-11-29
 */
public class DynamicConfigurationSwitchTest {
    private DynamicConfigurationFactoryService service;

    @Before
    public void init() {
        LoggerFactory.init(Collections.singletonMap(LoggerFactory.JAVAMESH_LOGBACK_SETTING_FILE, "log"));
    }

    @Test
    public void test() throws InterruptedException {
        // zk
        loadDynamicConfiguration(DynamicConfigType.ZOO_KEEPER, null);
        final DynamicConfigurationService zkService = service.getDynamicConfigurationService();
        addListener(zkService);

        // kie
        loadDynamicConfiguration(DynamicConfigType.KIE, null);
        final DynamicConfigurationService kieService = service.getDynamicConfigurationService();
        addListener(kieService);

        // Thread.sleep(1000000);

    }

    private void loadDynamicConfiguration(DynamicConfigType type, String url) {
        ServiceLoader<BaseService> sl = ServiceLoader.load(BaseService.class);
        Config.getInstance().dynamic_config_type = type;
        if (url != null) {
            Config.getInstance().kie_url = url;
        }
        for (BaseService cs : sl) {
            if (cs.getClass().toString().contains("DynamicConfigurationFactoryServiceImpl")) {
                service = (DynamicConfigurationFactoryService) cs;
                break;
            }
        }
    }

    private void addListener(DynamicConfigurationService dynamicConfigurationService) {
        final ConfigurationListener configurationListener = new ConfigurationListener() {
            @Override
            public void process(ConfigChangedEvent event) {
                System.out.println(event);
            }
        };
        dynamicConfigurationService.addConfigListener("/zk", "test", configurationListener);
    }

}
