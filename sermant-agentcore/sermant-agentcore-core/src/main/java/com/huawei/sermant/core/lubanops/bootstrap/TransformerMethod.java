/*
 * Copyright (C) 2021-2021 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.sermant.core.lubanops.bootstrap;

import java.util.List;
import java.util.Set;

public class TransformerMethod {

    private String method;

    private List<String> params;

    private Set<String> excludeMethods;

    private boolean isConstructor = false;

    private boolean interceptorGetAndSet = false;

    private String interceptor;

    public TransformerMethod(String method, List<String> params, String interceptor) {
        this.interceptor = interceptor;
        this.method = method;
        this.params = params;
    }

    public TransformerMethod(String method, List<String> params, String interceptor, boolean isConstructor) {
        this.interceptor = interceptor;
        this.method = method;
        this.params = params;
        this.setConstructor(isConstructor);
    }

    public TransformerMethod(String method, String interceptor, Set<String> excludeMethods,
            boolean interceptorGetAndSet) {
        this.interceptor = interceptor;
        this.method = method;
        this.excludeMethods = excludeMethods;
        this.interceptorGetAndSet = interceptorGetAndSet;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public String getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(String interceptor) {
        this.interceptor = interceptor;
    }

    public boolean isConstructor() {
        return isConstructor;
    }

    public void setConstructor(boolean isConstructor) {
        this.isConstructor = isConstructor;
    }

    public Set<String> getExcludeMethods() {
        return excludeMethods;
    }

    public void setExcludeMethods(Set<String> excludeMethods) {
        this.excludeMethods = excludeMethods;
    }

    public boolean isInterceptorGetAndSet() {
        return interceptorGetAndSet;
    }

    public void setInterceptorGetAndSet(boolean interceptorGetAndSet) {
        this.interceptorGetAndSet = interceptorGetAndSet;
    }

}
