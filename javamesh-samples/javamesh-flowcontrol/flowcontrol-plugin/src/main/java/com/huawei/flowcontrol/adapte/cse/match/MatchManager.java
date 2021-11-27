/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 */

package com.huawei.flowcontrol.adapte.cse.match;

import com.huawei.flowcontrol.adapte.cse.ResolverManager;
import com.huawei.flowcontrol.adapte.cse.entity.CseMatchRequest;
import com.huawei.flowcontrol.util.FilterUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 匹配管理器
 *
 * @author zhouss
 * @since 2021-11-24
 */
public enum MatchManager {
    /**
     * 单例
     */
    INSTANCE;

    /**
     * 从dubbo attachment获取version版本
     */
    public static final String DUBBO_ATTACHMENT_VERSION = "version";

    /**
     * 匹配所有的业务场景
     *
     * @param request http请求
     * @return 匹配的所有业务场景
     */
    public Set<String> matchHttp(HttpServletRequest request) {
        return match(buildHttpRequest(request));
    }

    /**
     * 匹配所有的业务场景
     * 针对alibaba dubbo
     *
     * @param invocation 请求信息
     * @return 匹配的所有业务场景
     */
    public Set<String> matchAlibabaDubbo(com.alibaba.dubbo.rpc.Invocation invocation) {
        return match(buildAlibabaDubboRequest(invocation));
    }

    /**
     * 匹配所有的业务场景
     * 针对apache dubbo
     *
     * @param invocation 请求信息
     * @return 匹配的所有业务场景
     */
    public Set<String> matchApacheDubbo(org.apache.dubbo.rpc.Invocation invocation) {
        return match(buildApacheDubboRequest(invocation));
    }

    private Set<String> match(CseMatchRequest cseRequest) {
        // 匹配规则
        final MatchGroupResolver resolver = ResolverManager.INSTANCE.getResolver(MatchGroupResolver.CONFIG_KEY);
        final Map<String, BusinessMatcher> matchGroups = resolver.getRules();
        final Set<String> result = new HashSet<String>();
        for (Map.Entry<String, BusinessMatcher> entry : matchGroups.entrySet()) {
            if (entry.getValue().match(cseRequest.getApiPath(), cseRequest.getHeaders(), cseRequest.getHttpMethod())) {
                if (!ResolverManager.INSTANCE.hasMatchedRule(entry.getKey())) {
                    continue;
                }
                // 资源名（业务场景名）
                result.add(entry.getKey());
            }
        }
        return result;
    }

    private CseMatchRequest buildHttpRequest(HttpServletRequest request) {
        // 获取路径
        String apiPath = FilterUtil.filterTarget(request);
        // 获取请求头
        final Enumeration<String> headerNames = request.getHeaderNames();
        final Map<String, String> headers = new HashMap<String, String>();
        while (headerNames.hasMoreElements()) {
            final String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        // 方法类型
        final String method = request.getMethod();
        return new CseMatchRequest(apiPath, headers, method);
    }

    public CseMatchRequest buildAlibabaDubboRequest(com.alibaba.dubbo.rpc.Invocation invocation) {
        // invocation.getTargetServiceUniqueName 该方法高版本有，返回的接口信息会携带版本号例如 com.huawei.dubbotest.service.CTest:0.0.0
        // 接口信息则没有版本com.huawei.dubbotest.service.CTest, 但低版本getAttachment会携带版本字段，因此可直接拼凑
        String apiPath = invocation.getInvoker().getInterface().getName()
                + ":" + invocation.getAttachment(DUBBO_ATTACHMENT_VERSION)
                + "." + invocation.getMethodName();
        return new CseMatchRequest(apiPath, invocation.getAttachments(), "POST");
    }

    public CseMatchRequest buildApacheDubboRequest(org.apache.dubbo.rpc.Invocation invocation) {
        // invocation.getTargetServiceUniqueName
        String apiPath = invocation.getInvoker().getInterface().getName()
                + ":" + invocation.getAttachment(DUBBO_ATTACHMENT_VERSION)
                + "." + invocation.getMethodName();
        return new CseMatchRequest(apiPath, invocation.getAttachments(), "POST");
    }

}
