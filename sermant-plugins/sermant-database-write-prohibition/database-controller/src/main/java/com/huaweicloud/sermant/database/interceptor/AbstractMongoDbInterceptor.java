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

package com.huaweicloud.sermant.database.interceptor;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.database.config.DatabaseWriteProhibitionManager;

/**
 * MongoDB Abstract Interceptor
 *
 * @author daizhenyu
 * @since 2024-02-02
 **/
public abstract class AbstractMongoDbInterceptor extends AbstractDatabaseInterceptor {
    @Override
    public ExecuteContext doBefore(ExecuteContext context) {
        String database = getDataBaseInfo(context).getDatabaseName();
        handleWriteOperationIfWriteDisabled(database,
                DatabaseWriteProhibitionManager.getMongoDbProhibitionDatabases(), context);
        return context;
    }
}
