package com.liyajie.service;

import com.liyajie.model.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设备管理中心服务
 *
 * @author Liyajie
 */
public class CpeCenterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CpeCenterService.class);

    private static final CpeCenterService SERVICE = new CpeCenterService();

    /**
     * deviceID：Device
     */
    private static final Map<Integer, Device> DEVICE_CACHE = new ConcurrentHashMap<>();

    /**
     * deviceUrl:deviceID
     */
    private static final Map<String, Integer> DEVICE_URL_CACHE = new ConcurrentHashMap<>();


    public static CpeCenterService getInstance() {
        return SERVICE;
    }

    private CpeCenterService() {
    }

    public Device getDeviceById(int id) {
        return DEVICE_CACHE.get(id);
    }

    public Integer getDeviceIdByUrl(String url) {
        return DEVICE_URL_CACHE.get(url);
    }

    public Device getDeviceByUrl(String url) {
        Integer deviceId = DEVICE_URL_CACHE.get(url);
        if (deviceId != null) {
            return getDeviceById(deviceId);
        }
        return null;
    }

    public boolean contains(int id) {
        return DEVICE_CACHE.containsKey(id);
    }

    public boolean contains(String url) {
        return DEVICE_URL_CACHE.containsKey(url);
    }

    public void addDevice(Device device) {
        DEVICE_CACHE.put(device.getDevId(), device);
        DEVICE_URL_CACHE.put(device.getServerUrl(), device.getDevId());
    }

    public List<Device> getAllDevices() {
        Collection<Device> values = DEVICE_CACHE.values();
        return new ArrayList<>(values);
    }
}
