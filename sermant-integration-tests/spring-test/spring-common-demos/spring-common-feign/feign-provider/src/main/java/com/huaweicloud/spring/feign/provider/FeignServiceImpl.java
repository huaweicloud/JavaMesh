/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huaweicloud.spring.feign.provider;

import com.huaweicloud.spring.common.flowcontrol.Constants;
import com.huaweicloud.spring.common.flowcontrol.provider.ProviderController;
import com.huaweicloud.spring.feign.api.FeignService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * feign测试
 *
 * @author zhouss
 * @since 2022-07-29
 */
@Controller
@ResponseBody
public class FeignServiceImpl extends ProviderController implements FeignService {
    private final Map<String, Integer> counterMap = new ConcurrentHashMap<>();

    @Value("${service_meta_zone:${SERVICE_META_ZONE:${service.meta.zone:bar}}}")
    private String zone;

    @Value("${spring.application.name}")
    private String name;

    @Value("${service_meta_parameters:${SERVICE_META_PARAMETERS:${service.meta.parameters:}}}")
    private String parameters;

    /**
     * 实例隔离接口测试
     *
     * @return 实例隔离
     * @throws InterruptedException 线程中断抛出
     */
    @Override
    @RequestMapping("instanceIsolation")
    public String instanceIsolation() throws InterruptedException {
        Thread.sleep(Constants.SLEEP_TIME_MS);
        return Constants.HTTP_OK;
    }

    /**
     * 重试测试
     *
     * @param invocationId 每次的调用ID UUID
     * @return retry
     * @throws Exception 模拟抛出异常进行重试
     */
    @Override
    @RequestMapping(value = "retry", method = RequestMethod.GET)
    public String retry(@RequestParam("invocationId") String invocationId) throws Exception {
        counterMap.putIfAbsent(invocationId, 0);
        counterMap.put(invocationId, counterMap.get(invocationId) + 1);

        int retry = counterMap.get(invocationId);

        if (retry >= 1) {
            return String.valueOf(retry);
        }
        throw new Exception("retry!");
    }

    @Override
    @RequestMapping(value = "ping")
    public String ping() {
        return "ok";
    }

    /**
     * 获取区域
     *
     * @param exit 是否退出
     * @return 区域
     */
    @Override
    @GetMapping("/router/metadata")
    public String getMetadata(@RequestParam("exit") boolean exit) {
        if (exit) {
            System.exit(0);
        }
        return "I'm " + name + ", my zone is " + zone + ", my parameters is [" + parameters + "].";
    }
}
