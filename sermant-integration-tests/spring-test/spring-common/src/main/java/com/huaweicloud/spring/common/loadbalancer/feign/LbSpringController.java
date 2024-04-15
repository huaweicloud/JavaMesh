/*
 * Copyright (C) 2022-2024 Sermant Authors. All rights reserved.
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

package com.huaweicloud.spring.common.loadbalancer.feign;

import com.huaweicloud.spring.common.FeignConstants;
import com.huaweicloud.spring.common.loadbalancer.consumer.SpringLbController;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * spring lb
 *
 * @author zhouss
 * @since 2022-08-17
 */
@Controller
@ResponseBody
@RequestMapping("/lb")
public class LbSpringController extends SpringLbController implements BeanFactoryAware {
    private BeanFactory beanFactory;

    /**
     * 获取负载均衡
     *
     * @param serviceName 目标服务
     * @return 负载均衡类型
     */
    @Override
    @RequestMapping("/getSpringLb")
    public String getSpringLb(@RequestParam("serviceName") String serviceName) {
        return super.getSpringLb(serviceName);
    }

    @Override
    protected void ping() {
        final Object flowControlService = beanFactory.getBean(FeignConstants.FEIGN_SERVICE_BEAN_NAME);
        ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(flowControlService.getClass(), "ping"),
                flowControlService);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public BeanFactory getBeanFactory() {
        return beanFactory;
    }
}
