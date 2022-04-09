package com.liyajie.rpc.methods;

import com.liyajie.builder.ResponseBuilderFactory;
import com.liyajie.constants.CpeMethod;
import com.liyajie.model.Device;
import com.liyajie.model.Inform;
import com.liyajie.rpc.api.ICallback;
import com.liyajie.rpc.api.IRpcHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.SOAPMessage;

/**
 * @author Liyajie
 */
public abstract class AbstractMethod<T> implements IRpcHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMethod.class);

    @Override
    public void handler(Device device, SOAPMessage request, ICallback callback) {
        LOGGER.info("{} Entering {} function handler.", device.getName(), methodType());
        Object response = deviceProcess(device, request);
        SOAPMessage soapMessage = null;
        try {
            soapMessage = ResponseBuilderFactory.buildSoapMessage(response, methodType());
            callback.run(soapMessage);
        } catch (Exception e) {
            LOGGER.error("handler: build soap message catch an exception: ", e);
        }
        LOGGER.info("{} Ending function {} handler.", device.getName(), methodType());
    }

    /**
     * 设备处理，从soapMessage中构建response model
     *
     * @param device  设备信息
     * @param request ACS响应的SoapMessage
     * @return Object 设备处理结果
     */
    abstract T deviceProcess(Device device, SOAPMessage request);

    /**
     * @return CpeMethod
     */
    abstract CpeMethod methodType();
}

