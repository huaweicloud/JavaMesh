/*
 * Copyright (C) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.example.demo.interceptor;

import java.lang.reflect.Method;

import com.huawei.apm.core.agent.common.BeforeResult;
import com.huawei.apm.core.agent.interceptor.StaticMethodInterceptor;
import com.huawei.apm.core.service.ServiceManager;
import com.huawei.example.demo.service.DemoComplexService;
import com.huawei.example.demo.service.DemoSimpleService;

/**
 * 插件服务的拦截器示例，在本示例中，将展示如何在拦截器中使用插件服务
 *
 * @author HapThorin
 * @version 1.0.0
 * @since 2021/10/25
 */
public class DemoServiceInterceptor implements StaticMethodInterceptor {
    private DemoSimpleService simpleService;
    private DemoComplexService complexService;

    @Override
    public void before(Class<?> clazz, Method method, Object[] arguments, BeforeResult beforeResult) throws Exception {
        System.out.println(clazz.getSimpleName() + ": [DemoServiceInterceptor]-before");
        simpleService = ServiceManager.getService(DemoSimpleService.class);
        complexService = ServiceManager.getService(DemoComplexService.class);
    }

    @Override
    public Object after(Class<?> clazz, Method method, Object[] arguments, Object result) throws Exception {
        System.out.println(clazz.getSimpleName() + ": [DemoServiceInterceptor]-after");
        simpleService.activeFunc();
        complexService.activeFunc();
        return result;
    }

    @Override
    public void onThrow(Class<?> clazz, Method method, Object[] arguments, Throwable t) {
        System.out.println(clazz.getSimpleName() + ": [DemoServiceInterceptor]-onThrow");
    }
}
