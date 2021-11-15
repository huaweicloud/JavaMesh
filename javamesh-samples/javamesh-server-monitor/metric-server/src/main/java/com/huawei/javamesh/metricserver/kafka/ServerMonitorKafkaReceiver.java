/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.javamesh.metricserver.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import com.huawei.javamesh.metricserver.dto.servermonitor.CpuDTO;
import com.huawei.javamesh.metricserver.dto.servermonitor.DiskDTO;
import com.huawei.javamesh.metricserver.dto.servermonitor.MemoryDTO;
import com.huawei.javamesh.metricserver.dto.servermonitor.NetworkDTO;
import com.huawei.javamesh.metricserver.service.ServerMetricService;
import com.huawei.javamesh.sample.servermonitor.entity.CpuMetric;
import com.huawei.javamesh.sample.servermonitor.entity.DiskMetric;
import com.huawei.javamesh.sample.servermonitor.entity.MemoryMetric;
import com.huawei.javamesh.sample.servermonitor.entity.NetworkMetric;
import com.huawei.javamesh.sample.servermonitor.entity.ServerMetric;
import com.huawei.javamesh.sample.servermonitor.entity.ServerMetricCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ServerMetric kafka接收处理类
 */
@Component
public class ServerMonitorKafkaReceiver {

    private final ServerMetricService serverMetricService;

    @Autowired
    public ServerMonitorKafkaReceiver(ServerMetricService serverMetricService) {
        this.serverMetricService = serverMetricService;
    }

    @KafkaListener(topics = "topic-server-monitor", groupId = "monitor-server")
    public void onMessage(byte[] record) {
        final ServerMetricCollection collection;
        try {
            collection = ServerMetricCollection.parseFrom(record);
        } catch (InvalidProtocolBufferException e) {
            return;
        }
        final String service = collection.getService();
        final String serviceInstance = collection.getServiceInstance();
        for (ServerMetric metric : collection.getMetricsList()) {
            final Instant time = Instant.ofEpochMilli(metric.getTime());
            addCpuMetric(service, serviceInstance, time, metric.getCpu());
            addMemoryMetric(service, serviceInstance, time, metric.getMemory());
            addNetworkMetric(service, serviceInstance, time, metric.getNetwork());
            addDisksMetric(service, serviceInstance, time, metric.getDisksList());
        }
    }

    private void addCpuMetric(String service, String serviceInstance, Instant time, CpuMetric cpu) {
        serverMetricService.addCpuMetric(CpuDTO.builder()
            .service(service)
            .serviceInstance(serviceInstance)
            .time(time)
            .userPercentage((long) cpu.getUserPercentage())
            .sysPercentage((long) cpu.getSysPercentage())
            .ioWaitPercentage((long) cpu.getIoWaitPercentage())
            .idlePercentage((long) cpu.getIdlePercentage())
            .build());
    }

    private void addMemoryMetric(String service, String serviceInstance, Instant time, MemoryMetric memory) {
        serverMetricService.addMemoryMetric(MemoryDTO.builder()
            .service(service)
            .serviceInstance(serviceInstance)
            .time(time)
            .memoryTotal(memory.getMemoryTotal())
            .swapCached(memory.getSwapCached())
            .memoryUsed(memory.getMemoryUsed())
            .cached(memory.getCached())
            .buffers(memory.getBuffers())
            .build());
    }

    private void addNetworkMetric(String service, String serviceInstance, Instant time, NetworkMetric network) {
        serverMetricService.addNetworkMetric(NetworkDTO.builder()
            .service(service)
            .serviceInstance(serviceInstance)
            .time(time)
            .readBytesPerSec(network.getReadBytesPerSec())
            .writeBytesPerSec(network.getWriteBytesPerSec())
            .readPackagesPerSec(network.getReadPackagesPerSec())
            .writePackagesPerSec(network.getWritePackagesPerSec())
            .build());
    }

    private void addDisksMetric(String service, String serviceInstance, Instant time, List<DiskMetric> disks) {
        Map<String, Object> readRateOfEachDevice = new HashMap<>();
        Map<String, Object> writeRateOfEachDevice = new HashMap<>();
        Map<String, Object> ioSpentPercentageOfEachDevice = new HashMap<>();
        for (DiskMetric disk : disks) {
            String deviceName = disk.getDeviceName();
            readRateOfEachDevice.put(deviceName, disk.getReadBytesPerSec());
            writeRateOfEachDevice.put(deviceName, disk.getWriteBytesPerSec());
            ioSpentPercentageOfEachDevice.put(deviceName, disk.getIoSpentPercentage());
        }
        serverMetricService.addDiskMetric(DiskDTO.builder()
            .service(service)
            .serviceInstance(serviceInstance)
            .time(time)
            .type(DiskDTO.ValueType.READ_RATE)
            .deviceAndValueMap(readRateOfEachDevice).build());
        serverMetricService.addDiskMetric(DiskDTO.builder()
            .service(service)
            .serviceInstance(serviceInstance)
            .time(time)
            .type(DiskDTO.ValueType.WRITE_RATE)
            .deviceAndValueMap(writeRateOfEachDevice).build());
        serverMetricService.addDiskMetric(DiskDTO.builder()
            .service(service)
            .serviceInstance(serviceInstance)
            .time(time)
            .type(DiskDTO.ValueType.IO_SPENT_PERCENTAGE)
            .deviceAndValueMap(ioSpentPercentageOfEachDevice).build());
    }
}
