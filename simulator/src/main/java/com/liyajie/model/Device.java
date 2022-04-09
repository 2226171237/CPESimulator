package com.liyajie.model;

import com.liyajie.service.DeviceProcess;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author Liyajie
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    private int devId;
    /**
     * connectionRequestUrl
     */
    private String serverUrl;
    private String name;
    private DeviceProcess deviceProcess;
    private Map<String, Parameter> parameterMap;

    /**
     * 获取参数
     *
     * @param parameterName 参数名
     * @return 参数
     */
    public Parameter getParameter(String parameterName) {
        return parameterMap.get(parameterName);
    }

    /**
     * 增加参数
     *
     * @param parameter 参数
     */
    public void addParameter(Parameter parameter) {
        if (parameter == null) {
            return;
        }
        parameterMap.put(parameter.getName(), parameter);
    }

    public boolean containsParameter(String name) {
        return parameterMap.containsKey(name);
    }
}
