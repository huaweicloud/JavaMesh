/*
 * Copyright (C) 2022-2024 Sermant Authors. All rights reserved.
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

package com.huaweicloud.sermant.implement.service.dynamicconfig.zookeeper;

import com.huaweicloud.sermant.core.config.ConfigManager;
import com.huaweicloud.sermant.core.service.dynamicconfig.config.DynamicConfig;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

/**
 * ZooKeeper Unit Test
 *
 * @author xzc
 * @since 2022-10-08
 */
public class ZooKeeperBufferedClientTest {
    private static final String ZOOKEEPER_ADDRESS = "127.0.0.1:2181";

    private static final int SESSION_TIMEOUT = 30000;

    private static final String PARENT_PATH = "/path";

    private static final String CHILD_ONE_PATH = "/path/1";

    private static final String CHILE_TWO_PATh = "/path/2";

    private static final String NODE_CONTENT = "data";

    private MockedStatic<ConfigManager> configManagerMockedStatic;

    ZooKeeperBufferedClient zooKeeperBufferedClient;

    @Before
    public void setUp() {
        configManagerMockedStatic = Mockito.mockStatic(ConfigManager.class);
        configManagerMockedStatic.when(() -> ConfigManager.getConfig(DynamicConfig.class))
                .thenReturn(new DynamicConfig());
        zooKeeperBufferedClient = new ZooKeeperBufferedClient(ZOOKEEPER_ADDRESS, SESSION_TIMEOUT);
    }

    @After
    public void tearDown() {
        zooKeeperBufferedClient.close();
        configManagerMockedStatic.close();
    }

    @Test
    public void testCreateParent() {
        boolean result = zooKeeperBufferedClient.createParent(PARENT_PATH);
        Assert.assertTrue(result);
    }

    @Test
    public void testIfNodeExist() {
        boolean result = zooKeeperBufferedClient.ifNodeExist(PARENT_PATH);
        Assert.assertTrue(result);
    }

    @Test
    public void testUpdateNode() {
        boolean result = zooKeeperBufferedClient.updateNode(PARENT_PATH, NODE_CONTENT);
        Assert.assertTrue(result);
    }

    @Test
    public void testGetNode() {
        String result = zooKeeperBufferedClient.getNode(PARENT_PATH);
        Assert.assertEquals(NODE_CONTENT, result);
    }

    @Test
    public void testCreateParentNode1() {
        boolean result = zooKeeperBufferedClient.updateNode(CHILD_ONE_PATH, NODE_CONTENT);
        Assert.assertTrue(result);
    }

    @Test
    public void testCreateParentNode2() {
        boolean result = zooKeeperBufferedClient.updateNode(CHILE_TWO_PATh, NODE_CONTENT);
        Assert.assertTrue(result);
    }

    @Test
    public void testListAllNodes() {
        List<String> result = zooKeeperBufferedClient.listAllNodes(PARENT_PATH);
        Assert.assertEquals(Arrays.<String>asList(CHILD_ONE_PATH, CHILE_TWO_PATh), result);
    }

    @Test
    public void testRemoveNode() {
        boolean deleteChileOne = zooKeeperBufferedClient.removeNode(CHILD_ONE_PATH);
        Assert.assertTrue(deleteChileOne);
        boolean deleteChileTwo = zooKeeperBufferedClient.removeNode(CHILE_TWO_PATh);
        Assert.assertTrue(deleteChileTwo);
        boolean deleteParentNode = zooKeeperBufferedClient.removeNode(PARENT_PATH);
        Assert.assertTrue(deleteParentNode);
    }
}