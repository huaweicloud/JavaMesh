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

package com.huaweicloud.sermant.mongodbv3.interceptors;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.database.constant.DatabaseType;
import com.huaweicloud.sermant.database.entity.DatabaseInfo;
import com.huaweicloud.sermant.database.handler.DatabaseHandler;
import com.huaweicloud.sermant.database.interceptor.AbstractMongoDbInterceptor;

import com.mongodb.ServerAddress;
import com.mongodb.binding.ConnectionSource;
import com.mongodb.binding.WriteBinding;
import com.mongodb.connection.ServerDescription;
import com.mongodb.operation.MixedBulkWriteOperation;

import java.util.Optional;

/**
 * MixedBulkWriteOperation Interceptor
 *
 * @author daizhenyu
 * @since 2024-01-16
 **/
public class MixedBulkWriteOperationInterceptor extends AbstractMongoDbInterceptor {
    /**
     * No-argument constructor
     */
    public MixedBulkWriteOperationInterceptor() {
    }

    /**
     * Parametric constructor
     *
     * @param handler write operation handler
     */
    public MixedBulkWriteOperationInterceptor(DatabaseHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void createAndCacheDatabaseInfo(ExecuteContext context) {
        DatabaseInfo info = new DatabaseInfo(DatabaseType.MONGODB);
        context.setLocalFieldValue(DATABASE_INFO, info);
        info.setDatabaseName(((MixedBulkWriteOperation) context.getObject()).getNamespace().getDatabaseName());
        ServerAddress serverAddress = Optional.ofNullable((WriteBinding) context.getArguments()[0])
                .map(WriteBinding::getWriteConnectionSource)
                .map(ConnectionSource::getServerDescription)
                .map(ServerDescription::getAddress)
                .orElse(null);
        if (serverAddress != null) {
            info.setHostAddress(serverAddress.getHost());
            info.setPort(serverAddress.getPort());
        }
    }
}
