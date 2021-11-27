/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 */

package com.huawei.apm.core.service.dynamicconfig.kie.listener;

import com.huawei.apm.core.service.dynamicconfig.kie.client.kie.KieConfigEntity;
import com.huawei.apm.core.service.dynamicconfig.kie.client.kie.KieResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 监听键响应数据
 * 用于对比新旧数据并保留旧数据
 *
 * @author zhouss
 * @since 2021-11-18
 */
public class KvDataHolder {
    /**
     * 当前数据
     */
    private Map<String, String> currentData;

    /**
     * 分析最新的数据
     *
     * @param response 最新数据
     * @return EventDataHolder
     */
    public EventDataHolder analyzeLatestData(KieResponse response) {
        final Map<String, String> latestData = formatKieResponse(response);
        final EventDataHolder eventDataHolder = new EventDataHolder();
        if (currentData != null) {
            if (latestData.isEmpty()) {
                eventDataHolder.deleted.putAll(currentData);
            } else {
                Map<String, String> temp = new HashMap<String, String>(currentData);
                for (Map.Entry<String, String> entry : latestData.entrySet()) {
                    final String value = currentData.get(entry.getKey());
                    if (value == null) {
                        // 增加的键
                        eventDataHolder.added.put(entry.getKey(), entry.getValue());
                    } else {
                        // 如果存在该键，则比对值是否相等
                        if (!value.equals(entry.getValue())) {
                            // 修改
                            eventDataHolder.modified.put(entry.getKey(), entry.getValue());
                        }
                    }
                    temp.remove(entry.getKey());
                }
                // temp留下的键即为删除的
                eventDataHolder.deleted.putAll(temp);
            }
        } else {
            eventDataHolder.added.putAll(latestData);
        }
        currentData = latestData;
        return eventDataHolder;
    }

    private Map<String, String> formatKieResponse(KieResponse response) {
        final HashMap<String, String> latestData = new HashMap<String, String>();
        if (response == null || response.getData() == null || response.getData().isEmpty()) {
            return latestData;
        }
        for (KieConfigEntity entity : response.getData()) {
            latestData.put(entity.getKey(), entity.getValue());
        }
        return latestData;
    }

    /**
     * 数据变更
     */
    public static class EventDataHolder {
        /**
         * 修改的key
         */
        private Map<String, String> modified;

        /**
         * 删除的key
         */
        private Map<String, String> deleted;

        /**
         * 新增key
         */
        private Map<String, String> added;

        public EventDataHolder() {
            modified = new HashMap<String, String>();
            deleted = new HashMap<String, String>();
            added = new HashMap<String, String>();
        }

        public Map<String, String> getModified() {
            return modified;
        }

        public void setModified(Map<String, String> modified) {
            this.modified = modified;
        }

        public Map<String, String> getDeleted() {
            return deleted;
        }

        public void setDeleted(Map<String, String> deleted) {
            this.deleted = deleted;
        }

        public Map<String, String> getAdded() {
            return added;
        }

        public void setAdded(Map<String, String> added) {
            this.added = added;
        }

        public boolean isChanged() {
            return !added.isEmpty() || !deleted.isEmpty() || !modified.isEmpty();
        }
    }
}
