/*
 * Copyright (C) 2021-2024 Sermant Authors. All rights reserved.
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

package com.huaweicloud.sermant.implement.service.dynamicconfig.kie.client.kie;

import com.huaweicloud.sermant.core.config.ConfigManager;
import com.huaweicloud.sermant.core.service.dynamicconfig.config.KieDynamicConfig;
import com.huaweicloud.sermant.implement.service.dynamicconfig.kie.client.AbstractClient;
import com.huaweicloud.sermant.implement.service.dynamicconfig.kie.client.ClientUrlManager;
import com.huaweicloud.sermant.implement.service.dynamicconfig.kie.client.http.HttpClient;
import com.huaweicloud.sermant.implement.service.dynamicconfig.kie.client.http.HttpResult;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Kie Client
 *
 * @author zhouss
 * @since 2021-11-17
 */
public class KieClient extends AbstractClient {
    /**
     * Default version
     */
    private static final String ABSENT_REVISION = "0";

    private static final String KIE_API_TEMPLATE = "/v1/%s/kie/kv?";

    private final ResultHandler<KieResponse> defaultHandler = new ResultHandler.DefaultResultHandler();

    private String kieApi;

    /**
     * Kie client constructor
     *
     * @param clientUrlManager kie url manager
     * @param timeout timeout
     */
    public KieClient(ClientUrlManager clientUrlManager, int timeout) {
        this(clientUrlManager, ConfigManager.getConfig(KieDynamicConfig.class).getProject(), timeout);
    }

    /**
     * Kie client constructor
     *
     * @param clientUrlManager kie url manager
     * @param project namespace
     * @param timeout timeout
     */
    public KieClient(ClientUrlManager clientUrlManager, String project, int timeout) {
        this(clientUrlManager, null, project, timeout);
    }

    /**
     * Kie client constructor
     *
     * @param clientUrlManager kie url manager
     * @param httpClient HttpClient
     * @param project namespace
     * @param timeout timeout
     */
    public KieClient(ClientUrlManager clientUrlManager, HttpClient httpClient, String project, int timeout) {
        super(clientUrlManager, httpClient, timeout);
        kieApi = String.format(KIE_API_TEMPLATE, project);
    }

    /**
     * Set the kieApi to project
     *
     * @param project namespace
     */
    public void setProject(String project) {
        this.kieApi = String.format(KIE_API_TEMPLATE, project);
    }

    /**
     * Querying Kie Configuration
     *
     * @param request Kie request
     * @return KieResponse
     */
    public KieResponse queryConfigurations(KieRequest request) {
        return queryConfigurations(request, defaultHandler);
    }

    /**
     * Querying Kie Configuration
     *
     * @param request Kie request
     * @param responseHandler http result handler
     * @param <T> The converted target type
     * @return Response result
     */
    public <T> T queryConfigurations(KieRequest request, ResultHandler<T> responseHandler) {
        if (request == null || responseHandler == null) {
            return null;
        }
        final StringBuilder requestUrl = new StringBuilder().append(clientUrlManager.getUrl()).append(kieApi);
        requestUrl.append(formatNullString(request.getLabelCondition()))
                .append("&revision=")
                .append(formatNullString(request.getRevision()));
        if (request.isAccurateMatchLabel()) {
            requestUrl.append("&match=exact");
        }
        if (request.getWait() != null) {
            requestUrl.append("&wait=").append(formatNullString(request.getWait())).append("s");
        }
        final HttpResult httpResult = httpClient.doGet(requestUrl.toString(), request.getRequestConfig());
        return responseHandler.handle(httpResult);
    }

    /**
     * Publish configuration
     *
     * @param key request key
     * @param labels labels
     * @param content configuration content
     * @param enabled configuration switch status
     * @return publish result
     */
    public boolean publishConfig(String key, Map<String, String> labels, String content, boolean enabled) {
        final Map<String, Object> params = new HashMap<>();
        params.put("key", key);
        params.put("value", content);
        params.put("labels", labels);
        params.put("status", enabled ? "enabled" : "disabled");
        final HttpResult httpResult = this.httpClient.doPost(clientUrlManager.getUrl() + kieApi, params);
        return httpResult.getCode() == HttpStatus.SC_OK;
    }

    /**
     * Update configuration
     *
     * @param keyId key id
     * @param content update content
     * @param enabled enable or not
     * @return update result
     */
    public boolean doUpdateConfig(String keyId, String content, boolean enabled) {
        final Map<String, Object> params = new HashMap<>();
        params.put("value", content);
        params.put("status", enabled ? "enabled" : "disabled");
        final HttpResult httpResult = this.httpClient.doPut(buildKeyIdUrl(keyId), params);
        return httpResult.getCode() == HttpStatus.SC_OK;
    }

    private String buildKeyIdUrl(String keyId) {
        return String.format(Locale.ENGLISH, "%s/%s",
                clientUrlManager.getUrl() + kieApi.substring(0, kieApi.length() - 1), keyId);
    }

    /**
     * Delete configuration
     *
     * @param keyId key id
     * @return delete result
     */
    public boolean doDeleteConfig(String keyId) {
        final HttpResult httpResult = this.httpClient.doDelete(buildKeyIdUrl(keyId));
        return httpResult.getCode() == HttpStatus.SC_OK;
    }

    private String formatNullString(String val) {
        if (val == null || val.trim().length() == 0) {
            // When the version number is empty, the default version number is set to "0". When the version is
            // updated, the data is returned immediately to avoid blocking problems
            return ABSENT_REVISION;
        }
        return val;
    }
}
