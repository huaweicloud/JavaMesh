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

package com.huaweicloud.spring.feign.consumer.controller;

import com.huaweicloud.spring.feign.api.Feign15xService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.UUID;

/**
 * 流控测试
 *
 * @author zhouss
 * @since 2022-07-29
 */
@Controller
@ResponseBody
@RequestMapping("flowcontrol")
public class FlowController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlowController.class);

    @Autowired
    private Feign15xService feign15xService;

    /**
     * 实例隔离接口测试
     *
     * @return 实例隔离
     */
    @RequestMapping("instanceIsolation")
    public String instanceIsolation() {
        try {
            return feign15xService.instanceIsolation();
        } catch (Exception ex) {
            return convertMsg(ex);
        }
    }

    /**
     * 实例隔离接口测试
     *
     * @return 实例隔离
     */
    @RequestMapping("retry")
    public int retry() {
        Integer tryCount = null;
        try {
            tryCount = feign15xService.retry(UUID.randomUUID().toString());
        } catch (Exception ex) {
            LOGGER.error("Retry {} times", tryCount);
            LOGGER.error(ex.getMessage(), ex);
        }
        return tryCount == null ? 0 : tryCount;
    }

    /**
     * 限流测试
     *
     * @return ok
     */
    @RequestMapping("rateLimiting")
    public String rateLimiting() {
        return feign15xService.rateLimiting();
    }

    /**
     * 限流测试
     *
     * @return ok
     */
    @RequestMapping("prefixRateLimiting")
    public String rateLimitingPrefix() {
        return rateLimiting();
    }

    /**
     * 限流测试
     *
     * @return ok
     */
    @RequestMapping("rateLimitingContains")
    public String rateLimitingContains() {
        return rateLimiting();
    }

    /**
     * 限流测试
     *
     * @return ok
     */
    @RequestMapping("rateLimitingSuffix")
    public String rateLimitingSuffix() {
        return rateLimiting();
    }

    /**
     * 慢调用熔断测试
     *
     * @return ok
     */
    @RequestMapping("timedBreaker")
    public String timedBreaker() {
        try {
            return feign15xService.timedBreaker();
        } catch (Exception ex) {
            return convertMsg(ex);
        }
    }

    /**
     * 异常熔断测试
     *
     * @return ok
     */
    @RequestMapping("exceptionBreaker")
    public String exceptionBreaker() {
        try {
            return feign15xService.exceptionBreaker();
        } catch (Exception ex) {
            return convertMsg(ex);
        }
    }

    /**
     * 隔离仓测试
     *
     * @return ok
     */
    @RequestMapping("bulkhead")
    public String bulkhead() {
        return feign15xService.bulkhead();
    }

    /**
     * 请求头匹配测试
     *
     * @return ok
     */
    @RequestMapping("header")
    public String header() {
        try {
            return feign15xService.headerExact();
        } catch (Exception ex) {
            return convertMsg(ex);
        }
    }

    /**
     * 请求头匹配测试
     *
     * @return ok
     */
    @RequestMapping("headerPrefix")
    public String headerPrefix() {
        try {
            return feign15xService.headerPrefix();
        } catch (Exception ex) {
            return convertMsg(ex);
        }
    }

    /**
     * 请求头匹配测试
     *
     * @return ok
     */
    @RequestMapping("headerSuffix")
    public String headerSuffix() {
        try {
            return feign15xService.headerSuffix();
        } catch (Exception ex) {
            return convertMsg(ex);
        }
    }

    /**
     * 请求头匹配测试
     *
     * @return ok
     */
    @RequestMapping("headerContains")
    public String headerContains() {
        try {
            return feign15xService.headerContains();
        } catch (Exception ex) {
            return convertMsg(ex);
        }
    }

    /**
     * 请求头匹配测试
     *
     * @return ok
     */
    @RequestMapping("headerCompareMatch")
    public String headerCompareMatch() {
        try {
            return feign15xService.headerCompareMatch();
        } catch (Exception ex) {
            return convertMsg(ex);
        }
    }

    /**
     * 请求头匹配测试
     *
     * @return ok
     */
    @RequestMapping("headerCompareNotMatch")
    public String headerCompareNotMatch() {
        try {
            return feign15xService.headerCompareNotMatch();
        } catch (Exception ex) {
            return convertMsg(ex);
        }
    }

    /**
     * 匹配服务名测试-匹配前提, 触发流控
     *
     * @return ok
     */
    @RequestMapping("serviceNameMatch")
    public String serviceNameMatch() {
        try {
            return feign15xService.serviceNameMatch();
        } catch (Exception ex) {
            return convertMsg(ex);
        }
    }

    /**
     * 匹配服务名测试-不匹配前提, 不触发流控
     *
     * @return ok
     */
    @RequestMapping("serviceNameNoMatch")
    public String serviceNameNoMatch() {
        return feign15xService.serviceNameNoMatch();
    }

    /**
     * 错误注入测试-返回空
     *
     * @return 返回空-由agent实现
     */
    @RequestMapping("faultNull")
    public String faultNull() {
        return feign15xService.faultNull();
    }

    /**
     * 错误注入测试-抛异常
     *
     * @return 抛异常-由agent实现
     */
    @RequestMapping("faultThrow")
    public String faultThrow() {
        try {
            feign15xService.faultThrow();
        } catch (Exception ex) {
            return convertMsg(ex);
        }
        return "";
    }

    /**
     * 错误注入测试-请求延迟
     *
     * @return 请求延迟-由agent实现
     */
    @RequestMapping("faultDelay")
    public String faultDelay() {
        return feign15xService.faultDelay();
    }

    private String convertMsg(Exception ex) {
        if (ex instanceof UndeclaredThrowableException) {
            return ((UndeclaredThrowableException) ex).getUndeclaredThrowable().getMessage();
        }
        return ex.getMessage();
    }
}
