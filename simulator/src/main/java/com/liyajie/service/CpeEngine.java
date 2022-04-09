package com.liyajie.service;

import com.liyajie.config.CpeConfig;
import com.liyajie.constants.EventCode;
import com.liyajie.http.CpeHttpClient;
import com.liyajie.http.CpeHttpServer;
import com.liyajie.model.Device;
import com.liyajie.model.Parameter;
import com.liyajie.utils.ReaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Liyajie
 */
public class CpeEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(CpeEngine.class);

    private static final CpeEngine ENGINE = new CpeEngine();

    private final CpeHttpServer cpeHttpServer;

    private final CpeConfig cpeConfig = CpeConfig.getInstance();

    private final CpeCenterService cpeCenter = CpeCenterService.getInstance();

    private final ThreadPoolExecutor deviceExecutor;

    public static CpeEngine getInstance() {
        return ENGINE;
    }

    private CpeEngine() {
        initConfig();
        cpeHttpServer = new CpeHttpServer(cpeConfig.getDeviceListenPort());
        deviceExecutor = new ThreadPoolExecutor(cpeConfig.getDeviceNum() + 5, cpeConfig.getDeviceNum() + 5, 6000,
                TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(cpeConfig.getDeviceNum() * 2));
        initDevices();
    }

    public void start() {
        cpeHttpServer.start();
        cpeCenter.getAllDevices().forEach(device -> {
            deviceExecutor.execute(device.getDeviceProcess());
            LOGGER.info("Device {} is starting....", device.getName());
        });
    }

    private void initConfig() {
        LOGGER.info("Begin to initConfig.");
        ReaderUtil.readConfig(cpeConfig);
        LOGGER.info("initConfig is finished.");
    }

    private void initDevices() {
        LOGGER.info("Begin to initDevices.");
        Device device = new Device();
        device.setParameterMap(ReaderUtil.readDeviceParams());
        device.setName(cpeConfig.getDeviceName());
        device.setServerUrl(cpeConfig.getConnectionRequestUrl());
        for (int i = 0; i < cpeConfig.getDeviceNum(); i++) {
            Device deviceNew = copyDevice(device);
            deviceNew.setDevId(i);
            // url以设备号为标识
            deviceNew.setServerUrl(device.getServerUrl() + "/" + i);
            // 设备名以设备号为标识
            deviceNew.setName(device.getName() + "-" + i);
            DeviceProcess deviceProcess = new DeviceProcess(deviceNew);
            // 初始首次上电和启动事件
            String events = EventCode.combineEvents(EventCode.BOOT_0, EventCode.BOOTSTRAP_1);
            deviceProcess.addEvent(events);
            deviceNew.setDeviceProcess(deviceProcess);
            cpeCenter.addDevice(deviceNew);
            LOGGER.info("Device {}  is initialed.", deviceNew.getName());
        }
        LOGGER.info("initDevices is finished.");
    }

    public void stop() {
        cpeHttpServer.close();
        CpeHttpClient.getInstance().close();
        cpeCenter.getAllDevices().forEach(device -> device.getDeviceProcess().close());
        deviceExecutor.shutdown();
    }

    private Device copyDevice(Device device) {
        Device deviceNew = new Device();
        deviceNew.setName(device.getName());
        deviceNew.setServerUrl(device.getServerUrl());
        Map<String, Parameter> parameterMap = device.getParameterMap();
        Map<String, Parameter> newParameterMap = new HashMap<>();
        for (String key : parameterMap.keySet()) {
            Parameter param = parameterMap.get(key);
            Parameter newParam = new Parameter();
            newParam.setName(param.getName());
            newParam.setObject(param.isObject());
            newParam.setValue(param.getValue());
            newParam.setValueType(param.getValueType());
            newParam.setWriteable(param.isWriteable());
            newParameterMap.put(key, newParam);
        }
        deviceNew.setParameterMap(newParameterMap);
        return deviceNew;
    }
}
