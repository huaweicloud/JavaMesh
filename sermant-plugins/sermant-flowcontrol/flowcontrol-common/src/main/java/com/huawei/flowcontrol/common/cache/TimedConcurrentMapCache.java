/*
 * Copyright (C) 2022-2024 Sermant Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huawei.flowcontrol.common.cache;

import com.huawei.flowcontrol.common.factory.FlowControlThreadFactory;

import com.huaweicloud.sermant.core.common.LoggerFactory;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * periodic cache clearing
 *
 * @param <K> key
 * @param <V> value
 * @author zhouss
 * @since 2022-07-21
 */
public class TimedConcurrentMapCache<K extends Timed, V> extends ConcurrentMapCache<K, V> {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    /**
     * default check interval
     */
    private static final long CHECK_INTERVAL = 60000L;

    /**
     * maximum cache number
     */
    private final int maxSize;

    /**
     * cache expiration time
     */
    private final long evictTimeMs;

    /**
     * the oldest key
     */
    private K lastOldestKey;

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    /**
     * timed cache constructor
     *
     * @param maxSize maximum cache number
     * @param evictTimeMs expiration time in milliseconds
     */
    public TimedConcurrentMapCache(int maxSize, long evictTimeMs) {
        this.maxSize = maxSize;
        this.evictTimeMs = evictTimeMs;
        initEvictTask();
    }

    @Override
    public void put(K key, V value) {
        if (super.size() >= maxSize) {
            if (!checkEvict()) {
                LOGGER.fine(String.format(Locale.ENGLISH,
                        "[TimedConcurrentMapCache] can not put key, because capacity has been max (%s)", maxSize));
                return;
            }
        }
        key.setTimestamp(System.currentTimeMillis());
        super.put(key, value);
    }

    private boolean checkEvict() {
        if (lastOldestKey == null) {
            return false;
        }

        // remove the last oldest key
        evict(lastOldestKey);
        lastOldestKey = null;
        return super.size() <= maxSize;
    }

    @Override
    public V get(K key) {
        final V value = super.get(key);
        if (value != null) {
            key.setTimestamp(System.currentTimeMillis());
        }
        return value;
    }

    @Override
    public V evict(K key) {
        return super.evict(key);
    }

    private void initEvictTask() {
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, new FlowControlThreadFactory(
                "TimedConcurrentMapCache-thread"));
        scheduledThreadPoolExecutor.scheduleAtFixedRate(this::removeEvictedCache, 0, Math.min(CHECK_INTERVAL,
                evictTimeMs), TimeUnit.MILLISECONDS);
    }

    private void removeEvictedCache() {
        final Object cacheTarget = getCacheTarget();
        if (!(cacheTarget instanceof ConcurrentHashMap)) {
            return;
        }
        ConcurrentHashMap<K, V> cache = (ConcurrentHashMap<K, V>) cacheTarget;
        final long currentTimeMillis = System.currentTimeMillis();
        AtomicLong timestamp = new AtomicLong(Long.MAX_VALUE);
        AtomicReference<K> curOldestKey = new AtomicReference<>();
        cache.forEach((key, value) -> {
            if (currentTimeMillis - key.getTimestamp() >= evictTimeMs) {
                cache.remove(key);
                return;
            }
            if (key.getTimestamp() < timestamp.get()) {
                timestamp.set(key.getTimestamp());
                curOldestKey.set(key);
            }
        });
        lastOldestKey = curOldestKey.get();
    }

    @Override
    public void release() {
        super.release();
        if (scheduledThreadPoolExecutor != null) {
            scheduledThreadPoolExecutor.shutdown();
        }
    }
}
