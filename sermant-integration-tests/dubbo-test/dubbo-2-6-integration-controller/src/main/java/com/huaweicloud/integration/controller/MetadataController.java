/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huaweicloud.integration.controller;

import com.huaweicloud.integration.service.MetadataService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 测试接口
 *
 * @author provenceee
 * @since 2022-04-28
 */
@RestController
@RequestMapping("/controller")
public class MetadataController {
    @Resource(name = "metadataService")
    private MetadataService metadataService;

    /**
     * 获取metadata
     *
     * @return metadata
     */
    @GetMapping("/getMetadataByDubbo")
    public String getMetadata() {
        return metadataService.getMetadataByDubbo();
    }

    /**
     * 获取metadata
     *
     * @return metadata
     */
    @GetMapping("/getMetadataByFeign")
    public String getMetadataByFeign() {
        return metadataService.getMetadataByFeign();
    }

    /**
     * 获取metadata
     *
     * @return metadata
     */
    @GetMapping("/getMetadataByRest")
    public String getMetadataByRest() {
        return metadataService.getMetadataByRest();
    }
}