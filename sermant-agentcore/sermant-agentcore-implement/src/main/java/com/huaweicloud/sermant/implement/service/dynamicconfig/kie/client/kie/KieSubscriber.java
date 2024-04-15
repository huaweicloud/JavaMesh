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

/**
 * Added whether the request is long request judgment
 *
 * @author zhouss
 * @since 2021-11-18
 */
public class KieSubscriber {
    /**
     * Maximum waiting time
     * 50S
     */
    private static final int MAX_WAIT = 50;

    private Boolean isLongConnectionRequest;

    private final KieRequest kieRequest;

    /**
     * constructor
     *
     * @param kieRequest kie request
     */
    public KieSubscriber(KieRequest kieRequest) {
        this.kieRequest = kieRequest;
    }

    /**
     * Whether it is a long request
     *
     * @return boolean
     */
    public boolean isLongConnectionRequest() {
        String wait = kieRequest.getWait();
        if (this.isLongConnectionRequest != null) {
            return this.isLongConnectionRequest;
        }
        if (wait == null || wait.trim().length() == 0) {
            this.isLongConnectionRequest = false;
            return false;
        }
        try {
            final int parseWait = Integer.parseInt(wait);
            this.isLongConnectionRequest = parseWait >= 1;
            if (parseWait > MAX_WAIT) {
                kieRequest.setWait(String.valueOf(MAX_WAIT));
            }
        } catch (Exception ex) {
            this.isLongConnectionRequest = false;
        }
        return this.isLongConnectionRequest;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        KieSubscriber that = (KieSubscriber) obj;

        if (!isLongConnectionRequest.equals(that.isLongConnectionRequest)) {
            return false;
        }
        return kieRequest != null ? kieRequest.equals(that.kieRequest) : that.kieRequest == null;
    }

    @Override
    public int hashCode() {
        int result = (isLongConnectionRequest == null || !isLongConnectionRequest) ? 1 : 0;
        result = 31 * result + (kieRequest != null ? kieRequest.hashCode() : 0);
        return result;
    }

    public KieRequest getKieRequest() {
        return kieRequest;
    }
}
