package com.liyajie.rpc.api;

import com.liyajie.model.Device;

import javax.xml.soap.SOAPMessage;

/**
 * @author Liyajie
 */
public interface IRpcHandler {

    /**
     * 处理
     *
     * @param device   设备信息
     * @param request  ACS响应的SoapMessage
     * @param callback 回调
     */
    void handler(Device device, SOAPMessage request, ICallback callback);
}
