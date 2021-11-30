/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 */

package com.huawei.flowcontrol.adapte.cse;


import com.huawei.flowcontrol.adapte.cse.match.MatchGroupResolver;
import com.huawei.flowcontrol.adapte.cse.resolver.AbstractResolver;
import com.huawei.flowcontrol.adapte.cse.resolver.listener.ConfigUpdateListener;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * 解析器管理
 *
 * @author zhouss
 * @since 2021-11-16
 */
public enum ResolverManager {
    /**
     * 单例
     */
    INSTANCE;

    /**
     * 基于SPI加载所有Resolver
     */
    private final Map<String, AbstractResolver<?>> resolversMap = new HashMap<String, AbstractResolver<?>>();

    ResolverManager() {
        loadSpiResolvers();
    }

    /**
     * 解析配置
     *
     * @param rulesMap 配置中心获取的规则数据
     * @param forDelete 是否是为了移除场景
     */
    public void resolve(Map<String, String> rulesMap, boolean forDelete) {
        for (Map.Entry<String, String> ruleEntity : rulesMap.entrySet()) {
            final String key = ruleEntity.getKey();
            resolve(key, ruleEntity.getValue(), forDelete);
        }
    }

    /**
     * 判断是否由对应的业务场景规则
     *
     * @param businessKey 业务场景名
     * @return boolean
     */
    public boolean hasMatchedRule(String businessKey) {
        final String matchGroupKey = AbstractResolver.getConfigKeyPrefix(MatchGroupResolver.CONFIG_KEY);
        for (Map.Entry<String, AbstractResolver<?>> entry : resolversMap.entrySet()) {
            if (entry.getKey().equals(matchGroupKey)) {
                continue;
            }
            if (entry.getValue().getRules().containsKey(businessKey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 单个kv解析
     *
     * @param key 键
     * @param value 值
     * @param forDelete 是否是删除键
     */
    public void resolve(String key, String value, boolean forDelete) {
        final Set<Map.Entry<String, AbstractResolver<?>>> resolvers = resolversMap.entrySet();
        for (Map.Entry<String, AbstractResolver<?>> resolverEntry : resolvers) {
            if (!key.startsWith(resolverEntry.getKey())) {
                continue;
            }
            String businessKey = key.substring(resolverEntry.getKey().length());
            // 匹配以该配置打头的解析器，更新解析器内容
            resolverEntry.getValue().parseRule(businessKey, value, true, forDelete);
            resolverEntry.getValue().notifyListeners();
        }
    }

    /**
     * 注册监听器
     *
     * @param configKey 监听的规则类型
     * @param listener 监听器
     */
    public void registerListener(String configKey, ConfigUpdateListener listener) {
        String configKeyPrefix = AbstractResolver.getConfigKeyPrefix(configKey);
        final AbstractResolver<?> abstractResolver = resolversMap.get(configKeyPrefix);
        if (abstractResolver != null) {
            abstractResolver.registerListener(listener);
        }
    }

    /**
     * 解析配置
     *
     * @param rulesMap 配置中心获取的规则数据
     */
    public void resolve(Map<String, String> rulesMap) {
        resolve(rulesMap, false);
    }

    public Map<String, AbstractResolver<?>> getResolversMap() {
        return resolversMap;
    }

    public <R extends AbstractResolver<?>> R getResolver(String configKey) {
        return (R) resolversMap.get(AbstractResolver.getConfigKeyPrefix(configKey));
    }

    private void loadSpiResolvers() {
        for (AbstractResolver<?> resolver : ServiceLoader.load(AbstractResolver.class)) {
            final String configKeyPrefix = AbstractResolver.getConfigKeyPrefix(resolver.getConfigKey());
            if (".".equals(configKeyPrefix)) {
                // 空配置跳过
                continue;
            }
            resolversMap.put(configKeyPrefix, resolver);
        }
    }

}
