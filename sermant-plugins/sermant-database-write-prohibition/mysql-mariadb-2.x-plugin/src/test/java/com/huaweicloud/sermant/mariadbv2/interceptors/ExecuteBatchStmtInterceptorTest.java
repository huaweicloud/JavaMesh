/*
 *  Copyright (C) 2024-2024 Sermant Authors. All rights reserved.
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

package com.huaweicloud.sermant.mariadbv2.interceptors;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.database.config.DatabaseWriteProhibitionConfig;
import com.huaweicloud.sermant.database.config.DatabaseWriteProhibitionManager;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mariadb.jdbc.HostAddress;
import org.mariadb.jdbc.internal.protocol.MasterProtocol;
import org.mariadb.jdbc.internal.protocol.Protocol;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * executeBatchStmt方法拦截器单元测试
 *
 * @author daizhenyu
 * @since 2024-02-06
 **/
public class ExecuteBatchStmtInterceptorTest {
    private static DatabaseWriteProhibitionConfig globalConfig = new DatabaseWriteProhibitionConfig();

    private static ExecuteContext context;

    private static Method methodMock;

    private static Protocol protocolMock;

    private static Object[] argument;

    private static List<String> sqlList;

    private ExecuteBatchStmtInterceptor interceptor = new ExecuteBatchStmtInterceptor();

    @BeforeClass
    public static void setUp() {
        DatabaseWriteProhibitionManager.updateGlobalConfig(globalConfig);
        protocolMock = Mockito.mock(MasterProtocol.class);
        methodMock = Mockito.mock(Method.class);
        HostAddress serverAddress = new HostAddress("127.0.0.1", 8080);
        Mockito.when(protocolMock.getHostAddress()).thenReturn(serverAddress);
        Mockito.when(protocolMock.getDatabase()).thenReturn("database-test");
        sqlList = new ArrayList<>();
        sqlList.add("INSERT INTO table (name) VALUES ('test')");
        argument = new Object[]{null, null, sqlList};
    }

    @AfterClass
    public static void tearDown() {
        Mockito.clearAllCaches();
        DatabaseWriteProhibitionManager.updateGlobalConfig(null);
    }

    @Test
    public void testDoBefore() throws Exception {
        // the database write prohibition switch is disabled
        globalConfig.setEnableMySqlWriteProhibition(false);
        context = ExecuteContext.forMemberMethod(protocolMock, methodMock, argument, null, null);
        interceptor.before(context);
        Assert.assertNull(context.getThrowableOut());

        // The database write prohibition function is disabled.
        // The write prohibition database set contains the database that is blocked
        Set<String> databases = new HashSet<>();
        databases.add("database-test");
        globalConfig.setMySqlDatabases(databases);
        interceptor.before(context);
        Assert.assertNull(context.getThrowableOut());

        // The database write prohibition switch is enabled, and the database set contains the database that is blocked
        globalConfig.setEnableMySqlWriteProhibition(true);
        context = ExecuteContext.forMemberMethod(protocolMock, methodMock, argument, null, null);
        interceptor.before(context);
        Assert.assertEquals("Database prohibit to write, database: database-test",
                context.getThrowableOut().getMessage());

        //The database write prohibition switch is turned on, the sql does not write,
        // and the database set contains the blocked database
        sqlList = new ArrayList<>();
        argument = new Object[]{null, null, sqlList};
        context = ExecuteContext.forMemberMethod(protocolMock, methodMock, argument, null, null);
        interceptor.before(context);
        Assert.assertNull(context.getThrowableOut());

        //The database write prohibition switch is enabled. The database set does not contain the database that is blocked
        sqlList.add("INSERT INTO table (name) VALUES ('test')");
        globalConfig.setMySqlDatabases(new HashSet<>());
        context = ExecuteContext.forMemberMethod(protocolMock, methodMock, argument, null, null);
        interceptor.before(context);
        Assert.assertNull(context.getThrowableOut());
    }
}
