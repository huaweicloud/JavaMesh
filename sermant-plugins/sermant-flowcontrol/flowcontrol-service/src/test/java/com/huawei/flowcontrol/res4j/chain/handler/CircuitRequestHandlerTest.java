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

package com.huawei.flowcontrol.res4j.chain.handler;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import com.huawei.flowcontrol.common.config.FlowControlConfig;
import com.huawei.flowcontrol.common.core.ResolverManager;
import com.huawei.flowcontrol.common.core.resolver.CircuitBreakerRuleResolver;
import com.huawei.flowcontrol.common.core.rule.CircuitBreakerRule;
import com.huawei.flowcontrol.common.entity.FlowControlResult;
import com.huawei.flowcontrol.common.entity.MetricEntity;
import com.huawei.flowcontrol.common.entity.RequestEntity;
import com.huawei.flowcontrol.res4j.chain.HandlerChainEntry;
import com.huawei.flowcontrol.res4j.handler.InstanceIsolationHandler;
import com.huawei.flowcontrol.res4j.service.ServiceCollectorService;

import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;
import com.huaweicloud.sermant.core.utils.ReflectUtils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Optional;

/**
 * circuit breaker test
 *
 * @author zhouss
 * @since 2022-08-30
 */
public class CircuitRequestHandlerTest extends BaseEntityTest implements RequestTest {
    private static final int MIN_CALL = 2;
    protected HandlerChainEntry entry;
    protected String sourceName;

    private static final String METHOD_NAME = "createProcessor";

    private static final long NANO_TIMES = 1000000000;

    private static final String BUSINESS_NAME = "testPublishEvent";

    private MockedStatic<PluginConfigManager> pluginConfigManagerMockedStatic;

    @Before
    public void setUp() {
        FlowControlConfig flowControlConfig = new FlowControlConfig();
        flowControlConfig.setEnableStartMonitor(true);
        pluginConfigManagerMockedStatic = Mockito.mockStatic(PluginConfigManager.class);
        pluginConfigManagerMockedStatic.when(()->PluginConfigManager.getPluginConfig(FlowControlConfig.class))
                .thenReturn(flowControlConfig);
    }

    // The mock static method needs to be closed when it is finished
    @After
    public void tearDown() throws Exception {
        pluginConfigManagerMockedStatic.close();
    }

    @Override
    public void test(HandlerChainEntry entry, String sourceName) {
        this.entry = entry;
        this.sourceName = sourceName;
        String cirMsg = getMsg();
        Assert.assertTrue(checkHttp(httpClientEntity).buildResponseMsg().contains(cirMsg));
        Assert.assertTrue(checkHttp(httpServerEntity).buildResponseMsg().contains(cirMsg));
        Assert.assertTrue(checkDubbo(dubboClientEntity, false).buildResponseMsg().contains(cirMsg));
        Assert.assertTrue(checkDubbo(dubboServerEntity, false).buildResponseMsg().contains(cirMsg));
        Assert.assertTrue(checkDubbo(dubboClientEntity, true).buildResponseMsg().contains(cirMsg));
        Assert.assertTrue(checkDubbo(dubboServerEntity, true).buildResponseMsg().contains(cirMsg));
    }

    /**
     * get result information
     *
     * @return msg
     */
    protected String getMsg() {
        return "and does not permit further calls";
    }

    /**
     * test http
     *
     * @param requestEntity request body
     * @return responseResult
     */
    protected FlowControlResult checkHttp(RequestEntity requestEntity) {
        final FlowControlResult flowControlResult = new FlowControlResult();
        for (int i = 0; i < MIN_CALL + 1; i++) {
            entry.onBefore(sourceName, requestEntity, flowControlResult);
            entry.onThrow(sourceName, new RuntimeException("error"));
            entry.onResult(sourceName, new Object());
        }
        return flowControlResult;
    }

    /**
     * test dubbo
     *
     * @param requestEntity request body
     * @param isProvider    is provider
     * @return responseResult
     */
    protected FlowControlResult checkDubbo(RequestEntity requestEntity, boolean isProvider) {
        final FlowControlResult flowControlResult = new FlowControlResult();
        for (int i = 0; i < MIN_CALL + 1; i++) {
            entry.onDubboBefore(sourceName, requestEntity, flowControlResult, isProvider);
            entry.onDubboThrow(sourceName, new RuntimeException("error"), isProvider);
            entry.onDubboResult(sourceName, new Object(), isProvider);
        }
        return flowControlResult;
    }

    @Override
    public void publishRule() {
        ResolverManager.INSTANCE.resolve(buildKey(CircuitBreakerRuleResolver.CONFIG_KEY), getRule(), false);
    }

    @Override
    public void clear() {
        ResolverManager.INSTANCE.resolve(buildKey(CircuitBreakerRuleResolver.CONFIG_KEY), getRule(), true);
    }

    /**
     * rule
     *
     * @return rule
     */
    protected String getRule() {
        return "failureRateThreshold: 80\n"
                + "minimumNumberOfCalls: 2\n"
                + "name: circuit breaker\n"
                + "slidingWindowSize: 10000\n"
                + "slidingWindowType: time\n"
                + "slowCallDurationThreshold: \"100\"\n"
                + "slowCallRateThreshold: 60\n"
                + "waitDurationInOpenState: 10s";
    }

    /**
     * test event processing
     */
    @Test
    public void testAddPublishEvent() {
        testEventConsume();
    }

    /**
     * test event consumption
     */
    private static void testEventConsume() {
        CircuitBreakerRule rule = new CircuitBreakerRule();
        rule.setFailureRateThreshold(80);
        rule.setMinimumNumberOfCalls(2);
        rule.setName("fuse");
        rule.setSlidingWindowSize("10000");
        rule.setSlidingWindowType("time");
        rule.setSlowCallDurationThreshold("100");
        rule.setSlowCallRateThreshold(60);
        rule.setWaitDurationInOpenState("10s");
        Optional<Object> optional = ReflectUtils.invokeMethod(new InstanceIsolationHandler(), METHOD_NAME,
                new Class[]{String.class, CircuitBreakerRule.class}, new Object[]{BUSINESS_NAME, rule});
        if (optional.isPresent() && optional.get() instanceof Optional) {
            Optional<?> circuitBreakerOptional = (Optional<?>) optional.get();
            if (circuitBreakerOptional.isPresent() && circuitBreakerOptional.get() instanceof CircuitBreaker) {
                CircuitBreaker circuitBreaker = (CircuitBreaker) circuitBreakerOptional.get();
                circuitBreaker.onSuccess(NANO_TIMES, circuitBreaker.getTimestampUnit());
            }
        }
        Map<String, MetricEntity> monitors = ServiceCollectorService.MONITORS;
        Assert.assertNotNull(monitors);
        MetricEntity metricEntity = monitors.get(BUSINESS_NAME);
        Assert.assertNotNull(metricEntity);
        Assert.assertNotNull(metricEntity.getFuseRequest());
        Assert.assertTrue(metricEntity.getFuseRequest().longValue() > 0);
        long total = metricEntity.getFailedFuseRequest().get() + metricEntity.getIgnoreFulFuseRequest().get()
                + metricEntity.getSuccessFulFuseRequest().get();
        Assert.assertEquals(total, metricEntity.getFuseRequest().longValue());
    }
}
