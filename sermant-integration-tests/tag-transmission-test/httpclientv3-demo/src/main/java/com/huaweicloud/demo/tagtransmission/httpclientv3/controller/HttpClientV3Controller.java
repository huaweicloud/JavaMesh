/*
 *   Copyright (C) 2023-2024 Sermant Authors. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.huaweicloud.demo.tagtransmission.httpclientv3.controller;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * controller，使用httpclient4.x调用http 服务端
 *
 * @author daizhenyu
 * @since 2023-10-14
 **/
@RestController
@RequestMapping(value = "httpClientV3")
public class HttpClientV3Controller {
    @Value("${common.server.url}")
    private String commonServerUrl;

    /**
     * 验证httpclient3.x透传流量标签
     *
     * @return 流量标签值
     * @throws IOException
     */
    @RequestMapping(value = "testHttpClientV3", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String testHttpClientV3() throws IOException {
        return doHttpClientV3Get(commonServerUrl);
    }

    private String doHttpClientV3Get(String url) throws IOException {
        // 创建 HttpClient 实例
        HttpClient httpClient = new HttpClient();
        GetMethod getMethod = new GetMethod(url);

        // 执行 GET 请求
        httpClient.executeMethod(getMethod);
        String responseContext = getMethod.getResponseBodyAsString();
        getMethod.releaseConnection();
        return responseContext;
    }
}
