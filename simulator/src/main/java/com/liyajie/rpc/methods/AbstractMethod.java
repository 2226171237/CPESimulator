package com.liyajie.rpc.methods;

import com.liyajie.builder.ResponseBuilderFactory;
import com.liyajie.constants.CpeMethod;
import com.liyajie.model.Device;
import com.liyajie.model.Inform;
import com.liyajie.rpc.api.ICallback;
import com.liyajie.rpc.api.IRpcHandler;
import com.liyajie.utils.SoapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.*;
import java.util.Iterator;

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
            String headId = getHeadId(request);
            soapMessage = ResponseBuilderFactory.buildSoapMessage(response, headId, methodType());
            callback.run(soapMessage);
        } catch (Exception e) {
            LOGGER.error("handler: build soap message catch an exception: ", e);
        }
        LOGGER.info("{} Ending function {} handler.", device.getName(), methodType());
    }

    private String getHeadId(SOAPMessage request) throws SOAPException {
        if (request == null) {
            return "";
        }
        String headId = "";
        SOAPHeader soapHeader = request.getSOAPHeader();
        Iterator<Node> elements = soapHeader.getChildElements();
        while (elements.hasNext()) {
            Node next = elements.next();
            if (!(next instanceof SOAPElement)) {
                continue;
            }
            SOAPElement cur = (SOAPElement) next;
            if (cur.getLocalName().equals("ID")) {
                headId = cur.getValue();
                break;
            }
        }
        return headId;
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

