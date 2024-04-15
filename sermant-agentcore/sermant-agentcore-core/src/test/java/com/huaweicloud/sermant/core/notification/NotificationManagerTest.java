/*
 * Copyright (C) 2023-2024 Sermant Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huaweicloud.sermant.core.notification;

import com.huaweicloud.sermant.core.config.ConfigManager;
import com.huaweicloud.sermant.core.config.common.BaseConfig;
import com.huaweicloud.sermant.core.config.common.ConfigTypeKey;
import com.huaweicloud.sermant.core.notification.config.NotificationConfig;
import com.huaweicloud.sermant.core.utils.ReflectUtils;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NotificationManagerTest {
    private static final String LISTENER_MAP = "NOTIFICATION_LISTENER_MAP";

    private boolean isSendNotificationFlag = false;

    @BeforeClass
    public static void setUp() {
        NotificationConfig notificationConfig = new NotificationConfig();
        notificationConfig.setEnable(true);
        Optional<?> optional = ReflectUtils.getStaticFieldValue(ConfigManager.class, "CONFIG_MAP");
        if (optional.isPresent()) {
            Map<String, BaseConfig> configMap = (Map<String, BaseConfig>) optional.get();
            configMap.put(NotificationConfig.class.getAnnotation(ConfigTypeKey.class).value(), notificationConfig);
        }
    }

    @Test
    public void registry() {
        NotificationManager.registry(new ListenerTest(), NettyNotificationType.class);
        Optional<?> mapOptional = ReflectUtils.getStaticFieldValue(NotificationManager.class, LISTENER_MAP);
        Assert.assertTrue(mapOptional.isPresent());
        Map<String, List<NotificationListener>> map = (Map<String, List<NotificationListener>>) mapOptional.get();
        Assert.assertTrue(map.containsKey(NettyNotificationType.class.getCanonicalName()));
    }

    @Test
    public void unRegistry() {
        ListenerTest listenerTest = new ListenerTest();
        NotificationManager.registry(listenerTest, NettyNotificationType.class);
        NotificationManager.unRegistry(listenerTest, NettyNotificationType.class);
        Optional<?> mapOptional = ReflectUtils.getStaticFieldValue(NotificationManager.class, LISTENER_MAP);
        Assert.assertTrue(mapOptional.isPresent());
        Map<String, List<NotificationListener>> map = (Map<String, List<NotificationListener>>) mapOptional.get();
        List<NotificationListener> notificationListeners =
                map.get(NettyNotificationType.class.getCanonicalName());
        Assert.assertFalse(notificationListeners.contains(listenerTest));

    }

    @Test
    public void sendNotification() throws InterruptedException {
        NotificationManager.registry(new ListenerTest(), NettyNotificationType.class);
        NotificationManager.doNotify(new NotificationInfo(NettyNotificationType.CONNECTED, null));
        Thread.sleep(1000);
        Assert.assertTrue(isSendNotificationFlag);
    }

    class ListenerTest implements NotificationListener {
        @Override
        public void process(NotificationInfo notificationInfo) {
            isSendNotificationFlag = true;
        }
    }
}