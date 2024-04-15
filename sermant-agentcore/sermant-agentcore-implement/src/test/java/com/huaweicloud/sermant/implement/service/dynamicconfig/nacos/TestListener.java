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

package com.huaweicloud.sermant.implement.service.dynamicconfig.nacos;

import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigEvent;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigListener;

/**
 * Test listener
 *
 * @author tangle
 * @since 2023-09-12
 */
public class TestListener implements DynamicConfigListener {
    /**
     * Listening success flag
     */
    private boolean isChange = false;

    /**
     * Listened configuration content
     */
    private String content;

    @Override
    public void process(DynamicConfigEvent event) {
        setContent(event.getContent());
        setChange(true);
    }

    public boolean isChange() {
        return isChange;
    }

    public void setChange(boolean change) {
        isChange = change;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}