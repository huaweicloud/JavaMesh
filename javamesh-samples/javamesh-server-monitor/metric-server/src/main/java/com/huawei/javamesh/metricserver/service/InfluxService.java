/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.javamesh.metricserver.service;

import com.huawei.javamesh.metricserver.dao.influxdb.InfluxDao;
import com.huawei.javamesh.metricserver.dao.influxdb.request.InfluxQueryRequest;
import com.influxdb.annotations.Measurement;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Influxdb通用基类服务
 */
public abstract class InfluxService {

    protected static final String TAG_SERVICE = "service";
    protected static final String TAG_SERVICE_INSTANCE = "service_instance";

    private final InfluxDao influxDao;

    private final Map<Class<?>, String> measurementCache = new ConcurrentHashMap<>();

    public InfluxService(InfluxDao influxDao) {
        this.influxDao = influxDao;
    }

    protected <E> void insert(Supplier<E> entitySupplier, Object dto) {
        E entity = entitySupplier.get();
        BeanUtils.copyProperties(dto, entity);
        influxDao.asyncInsert(entity);
    }

    protected <M> List<M> query(String start, String end, Class<M> metricClass) {
        return query(start, end, Collections.emptyMap(), metricClass);
    }

    protected <M> List<M> queryByService(String start, String end, String service, Class<M> metricClass) {
        return query(start, end, Collections.singletonMap(TAG_SERVICE, service), metricClass);
    }

    protected <M> List<M> queryByServiceInstance(String start, String end, String service,
                                                 String serviceInstance, Class<M> metricClass) {
        Map<String, String> tags = new HashMap<>();
        tags.put(TAG_SERVICE, service);
        tags.put(TAG_SERVICE_INSTANCE, serviceInstance);
        return query(start, end, tags, metricClass);
    }

    protected <M> List<M> query(String start, String end, Map<String, String> tags, Class<M> metricClass) {
        final InfluxQueryRequest request = InfluxQueryRequest.builder()
            .measurement(resolveMeasurement(metricClass))
            .start(start)
            .end(end)
            .tags(tags)
            .build();
        return influxDao.query(request, metricClass);
    }

    protected InfluxDao getInfluxDao() {
        return influxDao;
    }

    private <M> String resolveMeasurement(final Class<M> metricClass) {
        return measurementCache.computeIfAbsent(metricClass, clazz -> {
            final Measurement measurement = clazz.getAnnotation(Measurement.class);
            Assert.notNull(measurement, "Not a measurement type.");
            final String name = measurement.name();
            Assert.hasText(name, "The name of measurement must be provided.");
            return name;
        });
    }
}
