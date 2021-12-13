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

package com.huawei.flowcontrol.adapte.cse.test;

import java.util.Collections;

import org.junit.Test;

import com.huawei.flowcontrol.adapte.cse.RuleSyncer;
import com.huawei.flowcontrol.adapte.cse.entity.CseServiceMeta;
import com.huawei.sermant.core.common.CommonConstant;
import com.huawei.sermant.core.common.LoggerFactory;
import com.huawei.sermant.core.service.ServiceManager;

/**
 * 同步测试
 *
 * @author zhouss
 * @since 2021-11-18
 */
public class RuleSyncerTest {
    @Test
    public void testSync() throws InterruptedException {
        LoggerFactory.init(Collections.singletonMap(CommonConstant.LOG_SETTING_FILE_KEY, new Object()));
        ServiceManager.initServices();
        final RuleSyncer ruleSyncer = new RuleSyncer();
        CseServiceMeta.getInstance().setServiceName("discovery");
        CseServiceMeta.getInstance().setApp("sc");
        CseServiceMeta.getInstance().setCustomLabelValue("default");
        CseServiceMeta.getInstance().setEnvironment("producation");
        CseServiceMeta.getInstance().setCustomLabel("public");
        CseServiceMeta.getInstance().setProject("default");
        ruleSyncer.start();
    }
}
