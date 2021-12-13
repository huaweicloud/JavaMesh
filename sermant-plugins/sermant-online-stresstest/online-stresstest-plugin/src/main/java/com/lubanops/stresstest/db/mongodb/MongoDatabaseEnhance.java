/*
 * Copyright (C) 2021-2021 Huawei Technologies Co., Ltd. All rights reserved.
 */

package com.lubanops.stresstest.db.mongodb;

import com.huawei.sermant.core.agent.definition.EnhanceDefinition;
import com.huawei.sermant.core.agent.definition.MethodInterceptPoint;
import com.huawei.sermant.core.agent.matcher.ClassMatcher;
import com.huawei.sermant.core.agent.matcher.ClassMatchers;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * Mybatis 增强
 *
 * @author yiwei
 * @since 2021/10/21
 */
public class MongoDatabaseEnhance implements EnhanceDefinition {
    private static final String OLD_ENHANCE_CLASS = "com.mongodb.MongoClient";
    private static final String NEW_ENHANCE_CLASS = "com.mongodb.client.internal.MongoClientImpl";
    private static final String INTERCEPT_CLASS = "com.lubanops.stresstest.db.mongodb.MongoDatabaseInterceptor";

    @Override
    public ClassMatcher enhanceClass() {
        return ClassMatchers.multiClass(OLD_ENHANCE_CLASS, NEW_ENHANCE_CLASS);
    }

    @Override
    public MethodInterceptPoint[] getMethodInterceptPoints() {
        return new MethodInterceptPoint[]{MethodInterceptPoint.newInstMethodInterceptPoint(INTERCEPT_CLASS,
                ElementMatchers.named("getDatabase"))
        };
    }
}
