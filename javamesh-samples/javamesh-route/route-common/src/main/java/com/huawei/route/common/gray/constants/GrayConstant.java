package com.huawei.route.common.gray.constants;

/**
 * 常量
 *
 * @author pengyuyi
 * @date 2021/10/13
 */
public class GrayConstant {
    /**
     * dubbo参数索引的前缀
     */
    public static final String DUBBO_SOURCE_TYPE_PREFIX = "args";

    /**
     * 地域属性，属于哪个机房
     */
    public static final String GRAY_LDC = "GRAY_LDC";

    /**
     * 上游携带标签
     */
    public static final String GRAY_TAG = "GRAY_TAG";

    /**
     * 灰度发布默认ldc
     */
    public static final String GRAY_DEFAULT_LDC = "DEFAULT_LDC";

    /**
     * 灰度发布默认版本
     */
    public static final String GRAY_DEFAULT_VERSION = "DEFAULT_VERSION";

    /**
     * isEnabled匹配的方法名
     */
    public static final String ENABLED_METHOD_NAME = ".isEnabled()";

    /**
     * dubbo的版本字段
     */
    public static final String URL_VERSION_KEY = "version";

    /**
     * dubbo接口的默认版本
     */
    public static final String DUBBO_DEFAULT_VERSION = "0.0.0";

    /**
     * dubbo的分组字段
     */
    public static final String URL_GROUP_KEY = "group";

    /**
     * dubbo的集群名字段
     */
    public static final String URL_CLUSTER_NAME_KEY = "clusterName";

    /**
     * 注册时灰度版本的key
     */
    public static final String GRAY_VERSION_KEY = "gray.version";

    /**
     * 注册时ldc的key
     */
    public static final String GRAY_LDC_KEY = "ldc";

    /**
     * DUBBO协议前缀
     */
    public static final String DUBBO_PREFIX = "dubbo://";

    /**
     * 灰度配置servicecomb的key
     */
    public static final String GRAY_CONFIG_SERVICECOMB_KEY = "servicecomb";

    /**
     * 灰度配置routeRule的key
     */
    public static final String GRAY_CONFIG_ROUTE_RULE_KEY = "routeRule";

    /**
     * 灰度配置versionFrom的key
     */
    public static final String GRAY_CONFIG_VERSION_FROM_KEY = "versionFrom";
}