/*
 * Copyright (C) 2023-2024 Sermant Authors. All rights reserved.
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

package com.huaweicloud.sermant.router.dubbo.handler;

import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;
import com.huaweicloud.sermant.core.utils.StringUtils;
import com.huaweicloud.sermant.router.common.cache.DubboCache;
import com.huaweicloud.sermant.router.common.config.RouterConfig;
import com.huaweicloud.sermant.router.common.constants.RouterConstant;
import com.huaweicloud.sermant.router.common.utils.CollectionUtils;
import com.huaweicloud.sermant.router.common.utils.DubboReflectUtils;
import com.huaweicloud.sermant.router.common.utils.FlowContextUtils;
import com.huaweicloud.sermant.router.config.cache.ConfigCache;
import com.huaweicloud.sermant.router.config.entity.Match;
import com.huaweicloud.sermant.router.config.entity.MatchRule;
import com.huaweicloud.sermant.router.config.entity.MatchStrategy;
import com.huaweicloud.sermant.router.config.entity.Route;
import com.huaweicloud.sermant.router.config.entity.RouterConfiguration;
import com.huaweicloud.sermant.router.config.entity.Rule;
import com.huaweicloud.sermant.router.config.entity.ValueMatch;
import com.huaweicloud.sermant.router.config.utils.FlowRuleUtils;
import com.huaweicloud.sermant.router.config.utils.RuleUtils;
import com.huaweicloud.sermant.router.dubbo.strategy.RuleStrategyHandler;
import com.huaweicloud.sermant.router.dubbo.strategy.TypeStrategyChooser;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Routing handler for traffic matching method
 *
 * @author lilai
 * @since 2023-02-24
 */
public class FlowRouteHandler extends AbstractRouteHandler {
    private final RouterConfig routerConfig;

    // A set of tags used to filter instances, where value is null, indicating that all instances containing the tag
    // are filtered without determining the value value
    private final Map<String, String> allMismatchTags;

    /**
     * Construction method
     */
    public FlowRouteHandler() {
        routerConfig = PluginConfigManager.getPluginConfig(RouterConfig.class);
        allMismatchTags = new HashMap<>();
        for (String requestTag : routerConfig.getRequestTags()) {
            // Dubbo will replace the "-" in the key with "."
            allMismatchTags.put(RuleUtils.getMetaKey(requestTag.replace(RouterConstant.DASH, RouterConstant.POINT)),
                    null);
        }

        // All instances contain version, so null values cannot be stored
        allMismatchTags.remove(RouterConstant.META_VERSION_KEY);
    }

    @Override
    public Object handle(String targetService, List<Object> invokers, Object invocation, Map<String, String> queryMap,
            String serviceInterface) {
        if (!shouldHandle(invokers)) {
            return invokers;
        }
        List<Object> targetInvokers;
        if (routerConfig.isUseRequestRouter()) {
            targetInvokers = getTargetInvokersByRequest(targetService, invokers, invocation);
        } else {
            targetInvokers = getTargetInvokersByRules(invokers, invocation, queryMap, targetService, serviceInterface);
        }
        return super.handle(targetService, targetInvokers, invocation, queryMap, serviceInterface);
    }

    @Override
    public int getOrder() {
        return RouterConstant.FLOW_HANDLER_ORDER;
    }

    private List<Object> getTargetInvokersByRules(List<Object> invokers, Object invocation,
            Map<String, String> queryMap, String targetService,
            String serviceInterface) {
        RouterConfiguration configuration = ConfigCache.getLabel(RouterConstant.DUBBO_CACHE_NAME);
        if (RouterConfiguration.isInValid(configuration, RouterConstant.FLOW_MATCH_KIND)) {
            return invokers;
        }
        String interfaceName = getGroup(queryMap) + "/" + serviceInterface + RouterConstant.POINT
                + DubboReflectUtils.getMethodName(invocation) + ":" + getVersion(queryMap);
        List<Rule> rules = FlowRuleUtils
                .getFlowRules(configuration, targetService, interfaceName, DubboCache.INSTANCE.getAppName());
        if (CollectionUtils.isEmpty(rules)) {
            return invokers;
        }
        Optional<Rule> ruleOptional = getRule(rules, DubboReflectUtils.getArguments(invocation),
                parseAttachments(invocation));
        if (ruleOptional.isPresent()) {
            return RuleStrategyHandler.INSTANCE.getFlowMatchInvokers(targetService, invokers, ruleOptional.get());
        }
        return RuleStrategyHandler.INSTANCE
                .getMismatchInvokers(targetService, invokers, RuleUtils.getTags(rules), true);
    }

    private List<Object> getTargetInvokersByRequest(String targetName, List<Object> invokers, Object invocation) {
        Map<String, Object> attachments = parseAttachments(invocation);
        List<String> requestTags = routerConfig.getRequestTags();
        if (CollectionUtils.isEmpty(requestTags)) {
            return invokers;
        }

        // Set of tags used to match instances
        Map<String, String> tags = new HashMap<>();

        // A set of tags used to filter instances, where value is null, indicating that all instances containing the
        // tag are filtered without determining the value value
        Map<String, String> mismatchTags = new HashMap<>();
        for (Map.Entry<String, Object> entry : attachments.entrySet()) {
            String key = entry.getKey();
            if (!requestTags.contains(key)) {
                continue;
            }
            String replaceDashKey = key;
            if (replaceDashKey.contains(RouterConstant.DASH)) {
                // Dubbo will replace the "-" in the key with "."
                replaceDashKey = replaceDashKey.replace(RouterConstant.DASH, RouterConstant.POINT);
            }
            mismatchTags.put(RuleUtils.getMetaKey(replaceDashKey), null);
            String value = Optional.ofNullable(entry.getValue()).map(String::valueOf).orElse(null);
            if (StringUtils.isExist(value)) {
                tags.put(RuleUtils.getMetaKey(replaceDashKey), value);
            }
        }
        if (StringUtils.isExist(tags.get(RouterConstant.META_VERSION_KEY))) {
            mismatchTags.put(RouterConstant.META_VERSION_KEY, tags.get(RouterConstant.META_VERSION_KEY));
        } else {
            // All instances contain version, so null values cannot be stored
            mismatchTags.remove(RouterConstant.META_VERSION_KEY);
        }
        boolean isReturnAllInstancesWhenMismatch = false;
        if (CollectionUtils.isEmpty(mismatchTags)) {
            mismatchTags = allMismatchTags;
            isReturnAllInstancesWhenMismatch = true;
        }
        List<Object> result = RuleStrategyHandler.INSTANCE.getMatchInvokersByRequest(targetName, invokers, tags);
        if (CollectionUtils.isEmpty(result)) {
            result = RuleStrategyHandler.INSTANCE.getMismatchInvokers(targetName, invokers,
                    Collections.singletonList(mismatchTags), isReturnAllInstancesWhenMismatch);
        }
        return result;
    }

    /**
     * Analyze the attachment information of Dubbo
     *
     * @param invocation Dubbo's invoice
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    private Map<String, Object> parseAttachments(Object invocation) {
        Map<String, Object> attachments = DubboReflectUtils.getAttachments(invocation);
        return FlowContextUtils.decodeAttachments(attachments);
    }

    /**
     * Get Dubbo application group
     *
     * @param queryMap queryMap
     * @return value
     */
    private String getGroup(Map<String, String> queryMap) {
        String group = queryMap.get(RouterConstant.DUBBO_GROUP_KEY);
        return group == null ? "" : group;
    }

    /**
     * Get Dubbo application version
     *
     * @param queryMap queryMap
     * @return value
     */
    private String getVersion(Map<String, String> queryMap) {
        String version = queryMap.get(RouterConstant.DUBBO_VERSION_KEY);
        return version == null ? "" : version;
    }

    /**
     * Get matching routes
     *
     * @param list valid rules
     * @param arguments The arguments parameter of dubbo
     * @param attachments Dubbo's attachments parameter
     * @return Matching Routes
     */
    private static Optional<Rule> getRule(List<Rule> list, Object[] arguments, Map<String, Object> attachments) {
        for (Rule rule : list) {
            Match match = rule.getMatch();
            if (match == null) {
                return Optional.of(rule);
            }
            List<Route> routeList;
            if (!CollectionUtils.isEmpty(match.getAttachments()) && !CollectionUtils.isEmpty(attachments)) {
                routeList = getRoutesByAttachments(attachments, rule);
            } else if (!CollectionUtils.isEmpty(match.getArgs()) && arguments != null && arguments.length > 0) {
                routeList = getRoutesByArguments(arguments, rule);
            } else {
                routeList = Collections.emptyList();
            }
            if (!CollectionUtils.isEmpty(routeList)) {
                return Optional.of(rule);
            }
        }
        return Optional.empty();
    }

    /**
     * Obtain matching routes based on the arguments parameter
     *
     * @param arguments The argument parameter of dubbo
     * @param rule rule
     * @return matching routes
     */
    private static List<Route> getRoutesByArguments(Object[] arguments, Rule rule) {
        Match match = rule.getMatch();
        boolean isFullMatch = match.isFullMatch();
        Map<String, List<MatchRule>> args = match.getArgs();
        for (Map.Entry<String, List<MatchRule>> entry : args.entrySet()) {
            String key = entry.getKey();
            if (!key.startsWith(RouterConstant.DUBBO_SOURCE_TYPE_PREFIX)) {
                continue;
            }
            List<MatchRule> matchRuleList = entry.getValue();
            for (MatchRule matchRule : matchRuleList) {
                ValueMatch valueMatch = matchRule.getValueMatch();
                List<String> values = valueMatch.getValues();
                MatchStrategy matchStrategy = valueMatch.getMatchStrategy();
                String arg = TypeStrategyChooser.INSTANCE.getValue(matchRule.getType(), key, arguments).orElse(null);
                if (!isFullMatch && matchStrategy.isMatch(values, arg, matchRule.isCaseInsensitive())) {
                    // If it is not all matched, and one is matched, then return directly
                    return rule.getRoute();
                }
                if (isFullMatch && !matchStrategy.isMatch(values, arg, matchRule.isCaseInsensitive())) {
                    // If it is a full match and there is one mismatch, proceed to the next rule
                    return Collections.emptyList();
                }
            }
        }
        if (isFullMatch) {
            // If it's an all-match, go here, it means that there is no mismatch, just return
            return rule.getRoute();
        }

        // If it is not an all-match, if you go to this point, it means that none of the rules can be matched,
        // then move on to the next rule
        return Collections.emptyList();
    }

    /**
     * Get the matching route based on the attachment parameter
     *
     * @param attachments Dubbo's attachments parameter
     * @param rule rule
     * @return matching routes
     */
    private static List<Route> getRoutesByAttachments(Map<String, Object> attachments, Rule rule) {
        Match match = rule.getMatch();
        if (match == null) {
            return rule.getRoute();
        }
        boolean isFullMatch = match.isFullMatch();
        Map<String, List<MatchRule>> attachmentsRule = match.getAttachments();
        if (CollectionUtils.isEmpty(attachmentsRule)) {
            return rule.getRoute();
        }
        for (Map.Entry<String, List<MatchRule>> entry : attachmentsRule.entrySet()) {
            String key = entry.getKey();
            List<MatchRule> matchRuleList = entry.getValue();
            for (MatchRule matchRule : matchRuleList) {
                ValueMatch valueMatch = matchRule.getValueMatch();
                List<String> values = valueMatch.getValues();
                MatchStrategy matchStrategy = valueMatch.getMatchStrategy();
                String arg = Optional.ofNullable(attachments.get(key)).map(String::valueOf).orElse(null);
                if (!isFullMatch && matchStrategy.isMatch(values, arg, matchRule.isCaseInsensitive())) {
                    // If it is not all matched, and one is matched, it will be returned directly
                    return rule.getRoute();
                }
                if (isFullMatch && !matchStrategy.isMatch(values, arg, matchRule.isCaseInsensitive())) {
                    // If it's an all-match and another mismatch, move on to the next rule
                    return Collections.emptyList();
                }
            }
        }
        if (isFullMatch) {
            // If it's an all-match, go here and say that all are matched, and go back directly
            return rule.getRoute();
        }

        // If it's not an all-match, go here, it means that none of the rules can match, move on to the next rule
        return Collections.emptyList();
    }
}
