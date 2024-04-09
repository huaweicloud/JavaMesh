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

package com.huaweicloud.sermant.database.utils;

import com.huaweicloud.sermant.core.utils.StringUtils;

import java.util.regex.Pattern;

/**
 * sql parsing tool class
 *
 * @author daizhenyu
 * @since 2024-01-25
 **/
public class SqlParserUtils {
    private static Pattern writePattern =
            Pattern.compile("INSERT\\b|UPDATE\\b|DELETE\\b|CREATE\\b|ALTER\\b|DROP\\b|TRUNCATE\\b",
                    Pattern.CASE_INSENSITIVE);

    private SqlParserUtils() {
    }

    /**
     * Parse the sql and determine if it is a write operation
     *
     * @param sql sql
     * @return whether it is a write operation
     */
    public static boolean isWriteOperation(String sql) {
        if (StringUtils.isEmpty(sql)) {
            return false;
        }
        return writePattern.matcher(sql).find();
    }
}
