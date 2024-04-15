/*
 * Copyright (C) 2023-2024 Sermant Authors. All rights reserved.
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

package com.huaweicloud.sermant.premain.utils;

/**
 * String tool
 *
 * @author luanwenfei
 * @since 2023-09-23
 */
public class StringUtils {
    private StringUtils() {
    }

    /**
     * isBlank
     *
     * @param charSequence charSequence
     * @return boolean
     */
    public static boolean isBlank(final CharSequence charSequence) {
        int charSequenceLen = charSequence == null ? 0 : charSequence.length();
        if (charSequenceLen == 0) {
            return true;
        }
        for (int i = 0; i < charSequenceLen; i++) {
            if (!Character.isWhitespace(charSequence.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
