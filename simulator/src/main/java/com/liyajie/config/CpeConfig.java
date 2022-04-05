package com.liyajie.config;

import lombok.Data;

import java.util.List;

/**
 * @author Liyajie
 */
@Data
public final class CpeConfig {
    private static final CpeConfig INSTANCE = new CpeConfig();
    /**
     * ACS服务的IP地址
     */
    private String acsHost;
    /**
     * ACS服务的端口
     */
    private int acsPort;
    /**
     * ACS 服务URL
     */
    private String acsUrl;
    /**
     * ACS用户名
     */
    private String acsUserName;
    /**
     * ACS用户名密码
     */
    private String acsPassword;
    /**
     * CPE设备序列号
     */
    private String serialNumber;
    /**
     * 连接CPE的URL
     */
    private String connectionRequestUrl;
    /**
     * CPE的认证方式
     */
    private String cpeAuthType;
    /**
     * 请求类型HTTP或HTTPS
     */
    private String httpType;
    /**
     * 摘要认证的realm
     */
    private String realm;
    /**
     * 上报心跳的周期,单位s
     */
    private int heartbeatPeriod;
    /**
     * 设备包含的告警事件
     */
    private List<String> alarms;

    public void addAlarm(String alarmCode) {
        alarms.add(alarmCode);
    }

    private CpeConfig() {
    }

    public static CpeConfig getInstance() {
        return INSTANCE;
    }
}
