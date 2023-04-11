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

package com.huaweicloud.integration.removal;

import com.huaweicloud.integration.utils.RequestUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.HashMap;

/**
 * 离群实例插件场景测试
 *
 * @author zhp
 * @since 2023-03-15
 */
@EnabledIfEnvironmentVariable(named = "TEST_TYPE", matches = "removal")
public class RemovalTest {
    private static final String REQ_URL = "http://127.0.0.1:28020/removal/testReq";

    private static final int TIMES = 10000;

    @Test
    public void testRemoval() {
        int reqFailNum = getReqFailNum();
        Assertions.assertTrue(reqFailNum != 0, "No request failures have occurred");
        reqFailNum = getReqFailNum();
        Assertions.assertEquals(0, reqFailNum, "fail still occur after the outlier instance is removed");
    }

    /**
     * 测试离群实例摘除功能
     *
     * @return 失败数量
     */
    private static int getReqFailNum() {
        long currentTimes = System.currentTimeMillis();
        int reqFailNum = 0;
        while (System.currentTimeMillis() - currentTimes <= TIMES) {
            try {
                RequestUtils.get(REQ_URL, new HashMap<>(), String.class);
            } catch (Exception e) {
                reqFailNum++;
            }
        }
        return reqFailNum;
    }
}
