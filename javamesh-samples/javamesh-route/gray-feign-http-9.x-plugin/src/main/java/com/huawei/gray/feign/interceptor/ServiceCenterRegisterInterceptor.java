/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 */

package com.huawei.gray.feign.interceptor;

import com.huawei.apm.core.agent.common.BeforeResult;
import com.huawei.apm.core.agent.interceptor.InstanceMethodInterceptor;
import com.huawei.apm.core.service.ServiceManager;
import com.huawei.gray.feign.service.RegisterService;

import java.lang.reflect.Method;

/**
 * 拦截Service Center创建微服务的方法
 *
 * @author lilai
 * @since 2021-11-03
 */
public class ServiceCenterRegisterInterceptor implements InstanceMethodInterceptor {
    private RegisterService registerService;

    @Override
    public void before(Object obj, Method method, Object[] arguments, BeforeResult beforeResult) throws Exception {
        registerService = ServiceManager.getService(RegisterService.class);
    }

    /**
     * 拦截MicroserviceFactory.createMicroserviceFromConfiguration的返回参数
     *
     * @param obj       拦截对象
     * @param method    拦截方法
     * @param arguments 方法参数
     * @param result    返回结果
     */
    @Override
    public Object after(Object obj, Method method, Object[] arguments, Object result) throws Exception {
        registerService.after(obj, method, arguments, result);
        return result;
    }

    @Override
    public void onThrow(Object obj, Method method, Object[] arguments, Throwable t) {
    }
}
