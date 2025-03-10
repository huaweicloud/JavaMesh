/*
 * Copyright (C) 2024-2024 Sermant Authors. All rights reserved.
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

package io.sermant.implement.service.metric;

import io.sermant.core.service.metric.api.Counter;

/**
 * metric counter implementation
 *
 * @author zwmagic
 * @since 2024-08-19
 */
public class MeterCounter implements Counter {
    private final io.micrometer.core.instrument.Counter counter;

    /**
     * Constructor to initialize the MeterCounter object.
     *
     * @param counter A Micrometer Counter instance used to create the MeterCounter instance.
     */
    public MeterCounter(io.micrometer.core.instrument.Counter counter) {
        this.counter = counter;
    }

    @Override
    public void increment() {
        counter.increment();
    }

    @Override
    public void increment(double amount) {
        counter.increment(amount);
    }

    @Override
    public double count() {
        return counter.count();
    }
}
