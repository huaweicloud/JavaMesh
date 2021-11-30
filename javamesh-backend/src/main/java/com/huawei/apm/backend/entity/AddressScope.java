package com.huawei.apm.backend.entity;

public enum AddressScope {
    /*
    内部地址
     */
    inner,

    /**
     * 对外的地址
     */
    outer;

    public static AddressScope getValue(String s) {
        try {
            return AddressScope.valueOf(s);
        } catch (Exception e) {
            return null;
        }
    }
}
