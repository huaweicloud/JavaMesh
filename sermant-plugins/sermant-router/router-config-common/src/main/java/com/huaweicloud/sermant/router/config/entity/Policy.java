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

package com.huaweicloud.sermant.router.config.entity;

/**
 * Threshold policy
 *
 * @author robotLJW
 * @since 2023-02-28
 */
public class Policy {
    /**
     * Per cent
     */
    private static final float ONE_PERCENT = 0.01f;

    /**
     * The trigger threshold is the same as the proportion of the same AZ
     */

    private float triggerThreshold;

    /**
     * The minimum available threshold for all instances
     */
    private int minAllInstances;

    public double getTriggerThreshold() {
        return triggerThreshold;
    }

    public void setTriggerThreshold(float triggerThreshold) {
        // Need to do a percentage conversion, multiplied by 0.01
        this.triggerThreshold = triggerThreshold * ONE_PERCENT;
    }

    public int getMinAllInstances() {
        return minAllInstances;
    }

    public void setMinAllInstances(int minAllInstances) {
        this.minAllInstances = minAllInstances;
    }
}
