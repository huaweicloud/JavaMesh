/*
 * Copyright (C) 2021-2021 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.sermant.core.lubanops.bootstrap.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.huawei.sermant.core.lubanops.bootstrap.config.ConfigManager;

/**
 * 针对异常消息进行处理的类 <br>
 * @author
 * @since 2020年3月10日
 */
public class ExceptionUtil {

    /*
     * 异常堆栈的最大长度
     */
    public final static int MAX_ERROR_STACK_LENGTH = 4096;

    /**
     * 针对异常消息堆栈进行获取 <br>
     * @param t
     * @param hideExceptionMessage
     *            是否过滤message
     * @return
     * @author
     * @since 2020年3月10日
     */
    public static String getThrowableStackTrace(Throwable t, boolean hideExceptionMessage) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String s = sw.toString();
        String lineSep = System.getProperty("line.separator");
        if (hideExceptionMessage) {
            int endIndex = s.indexOf(":");
            int startIndex = s.indexOf(lineSep);
            if (endIndex > -1 && startIndex > -1) {
                s = s.substring(0, endIndex) + s.substring(startIndex);
            }
        }
        if (s.length() > ConfigManager.getMaxExceptionLength()) {
            s = s.substring(0, ConfigManager.getMaxExceptionLength());
        }
        return s;
    }

    /**
     * 针对异常堆栈进行处理，有最大堆栈长度 <br>
     * @param t
     * @param maxStackLength
     * @param hideExceptionMessage
     * @return
     * @author
     * @since 2020年3月10日
     */
    public static String getThrowableStackTrace(Throwable t, int maxStackLength, boolean hideExceptionMessage) {
        if (maxStackLength <= 0) {
            maxStackLength = ConfigManager.getMaxExceptionLength();
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String s = sw.toString();
        String lineSep = System.getProperty("line.separator");
        if (hideExceptionMessage) {
            s = s.substring(0, s.indexOf(":")) + s.substring(s.indexOf(lineSep));
        }
        if (s.length() > maxStackLength) {
            s = s.substring(0, maxStackLength);
        }
        return s;
    }

    public static String getThrowableStackTrace(Throwable t) {
        return getThrowableStackTrace(t, ConfigManager.isHideExceptionMessage());
    }

    public static void main(String[] args) {
        try {
            Map<String, String> m = new ConcurrentHashMap<String, String>();
            m.put(null, "1");
        } catch (Throwable e) {
        }

    }

}
