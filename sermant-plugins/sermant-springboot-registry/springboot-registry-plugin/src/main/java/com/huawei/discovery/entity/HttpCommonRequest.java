/*
 * Copyright (C) 2022-2024 Sermant Authors. All rights reserved.
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

package com.huawei.discovery.entity;

import org.apache.http.client.methods.HttpGet;

import java.net.URI;

/**
 * Define an HTTP request
 *
 * @author zhouss
 * @since 2022-10-11
 */
public class HttpCommonRequest extends HttpGet {
    private final String methodType;

    /**
     * Constructor
     *
     * @param methodType Method type
     * @param uri The path of the request
     */
    public HttpCommonRequest(String methodType, String uri) {
        this.methodType = methodType;
        setURI(URI.create(uri));
    }

    @Override
    public String getMethod() {
        return this.methodType;
    }
}
