package com.liyajie.utils;

import com.liyajie.config.CpeConfig;
import com.liyajie.model.Device;
import com.liyajie.model.Parameter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.lf5.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 文件读取工具类
 *
 * @author Liyajie
 */
public class ReaderUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReaderUtil.class);

    /**
     * 模拟器配置文件
     */
    private static final String CONFIG_FILE = ReaderUtil.class.getResource("/").getPath()
            + File.separator + "config" + File.separator + "cpeConfig.properties";

    /**
     * 设备参数csv文件
     */
    private static final String DEVICE_CVS_FILE = ReaderUtil.class.getResource("/").getPath()
            + File.separator + "device" + File.separator + "data_model.csv";


    public static void readConfig(CpeConfig cpeConfig) {
        try {
            Properties properties = new Properties();
            properties.load(new FileReader(CONFIG_FILE));
            cpeConfig.setAcsHost(properties.getProperty("acs.host"));
            cpeConfig.setAcsPort(Integer.parseInt(properties.getProperty("acs.port")));
            cpeConfig.setAcsUrl(properties.getProperty("acs.url"));
            cpeConfig.setAcsUserName(properties.getProperty("acs.username"));
            cpeConfig.setAcsPassword(properties.getProperty("acs.password"));
            cpeConfig.setAcsRealm(properties.getProperty("acs.realm"));
            cpeConfig.setAcsHttpType(properties.getProperty("acs.httpType"));
            cpeConfig.setOui(properties.getProperty("device.oui"));
            cpeConfig.setSerialNumber(properties.getProperty("device.serialNumber"));
            cpeConfig.setDeviceListenPort(Integer.parseInt(properties.getProperty("device.listenPort")));
            cpeConfig.setDeviceName(properties.getProperty("device.name"));
            cpeConfig.setCpeAuthType(properties.getProperty("device.authType"));
            cpeConfig.setConnectionRequestUrl(properties.getProperty("device.connectionRequestUrl"));
            cpeConfig.setDeviceNum(Integer.parseInt(properties.getProperty("device.number")));
            cpeConfig.setHeartbeatPeriod(Integer.parseInt(properties.getProperty("device.heartbeatPeriod")));
            cpeConfig.setAlarms((Arrays.asList(properties.getProperty("device.alarms").split(";"))));
        } catch (IOException e) {
            LOGGER.error("readConfig: catch an exception. ", e);
        }
    }

    public static Map<String, Parameter> readDeviceParams() {
        Map<String, Parameter> parameterMap = new HashMap<>();
        try (Reader reader = new FileReader(DEVICE_CVS_FILE)) {
            CSVParser parse = CSVFormat.DEFAULT.parse(reader);
            List<CSVRecord> records = parse.getRecords();
            for (CSVRecord record : records) {
                Parameter param = new Parameter();
                param.setName(record.get(0));
                param.setObject(Boolean.parseBoolean(record.get(1)));
                param.setWriteable(Boolean.parseBoolean(record.get(2)));
                param.setValue(record.get(3));
                param.setValueType(record.get(4));
                parameterMap.put(param.getName(), param);
            }
        } catch (IOException e) {
            LOGGER.error("readDeviceParams: catch an exception. ", e);
        }
        return parameterMap;
    }

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        Map<String, Parameter> parameterMap = readDeviceParams();
        System.out.println(parameterMap.get("DeviceID.Manufacturer"));
        readConfig(CpeConfig.getInstance());
        System.out.println(CpeConfig.getInstance());
        Device device = new Device();
        device.setParameterMap(parameterMap);
        Device deviceNew = new Device();
        deviceNew.setParameterMap(new HashMap<>(device.getParameterMap()));

        Map<String, Parameter> map1 = device.getParameterMap();
        Map<String, Parameter> map2 = deviceNew.getParameterMap();
        map1.get("DeviceID.Manufacturer").setValue("LYJ");
        System.out.println(map1.get("DeviceID.Manufacturer"));
        System.out.println(map2.get("DeviceID.Manufacturer"));
    }
}
