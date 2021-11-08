package com.huawei.example.demo.service;

import com.huawei.apm.core.config.ConfigLoader;
import com.huawei.apm.core.lubanops.bootstrap.log.LogFactory;
import com.huawei.apm.core.service.CoreServiceManager;
import com.huawei.apm.core.service.PluginService;
import com.huawei.apm.core.service.configServer.zookeeper.ZookeeperServer;
import com.huawei.example.demo.config.DemoConfig;
import org.apache.curator.framework.CuratorFramework;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ��ʾ��չʾ�ڲ����ʹ���������ģ���zookeeperΪ��
 */
public class DemoConfigServerService implements PluginService {
    private static final Logger LOGGER = LogFactory.getLogger();
    private static final DemoConfig demoConfig = ConfigLoader.getConfig(DemoConfig.class);
    private static CuratorFramework zkClient;


    @Override
    public void init() {
        try {
            ZookeeperServer zkServer = CoreServiceManager.INSTANCE.getService(demoConfig.getConfigServerClassName());
            zkClient = zkServer.getClient();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.toString());
        }
        System.out.println(zkClient.getState());
    }

    @Override
    public void stop() {

    }
}
