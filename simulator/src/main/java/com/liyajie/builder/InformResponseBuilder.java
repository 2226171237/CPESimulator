package com.liyajie.builder;

import com.liyajie.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.util.List;

/**
 * @author Liyajie
 */
public class InformResponseBuilder extends ResponseBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(InformResponseBuilder.class);

    @Override
    void buildBody(SOAPMessage message, Object body) throws SOAPException {
        if (!(body instanceof Inform)) {
            LOGGER.error("buildBody: input body is not instanceof Inform.");
            return;
        }
        Inform inform = (Inform) body;
        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        SOAPBody soapBody = envelope.getBody();
        QName informName = new QName("", "Inform", "cwmp");
        SOAPBodyElement informElem = soapBody.addBodyElement(informName);
        addDeviceStruct(informElem, inform.getDeviceStruct());
        addEventCode(informElem, inform.getEventStructs());
        informElem.addChildElement("MaxEnvelopes").addTextNode(String.valueOf(inform.getMaxEnvelopes()));
        informElem.addChildElement("CurrentTime").addTextNode(inform.getCurrentTime());
        informElem.addChildElement("RetryCount").addTextNode(String.valueOf(inform.getRetryCount()));
        addParameterList(informElem, inform.getParameterValueStructList());
    }

    private void addDeviceStruct(SOAPBodyElement informElem, DeviceStruct deviceStruct) throws SOAPException {
        SOAPElement deviceId = informElem.addChildElement("DeviceId");
        deviceId.addChildElement("Manufacturer").addTextNode(deviceStruct.getManufacturer());
        deviceId.addChildElement("OUI").addTextNode(deviceStruct.getOui());
        deviceId.addChildElement("ProductClass").addTextNode(deviceStruct.getProductClass());
        deviceId.addChildElement("SerialNumber").addTextNode(deviceStruct.getSerialNumber());
    }

    private void addEventCode(SOAPBodyElement informElem, List<EventStruct> eventStructList) throws SOAPException {
        SOAPElement event = informElem.addChildElement("Event");
        event.addAttribute(new QName("", "arrayType", "SOAP-ENC"), "cwmp:EventStruct["
                + eventStructList.size() + "]");
        for (EventStruct eventStruct : eventStructList) {
            SOAPElement eventElem = event.addChildElement("EventStruct");
            eventElem.addChildElement("EventCode").addTextNode(eventStruct.getEventCode());
            String commandKey = eventStruct.getCommandKey() == null ? "" : eventStruct.getCommandKey();
            eventElem.addChildElement("CommandKey").addTextNode(commandKey);
        }
    }

    private void addParameterList(SOAPBodyElement informElem, List<ParameterValueStruct> parameterValueStructList) throws SOAPException {
        SOAPElement parameterList = informElem.addChildElement("ParameterList");
        parameterList.addAttribute(new QName("", "arrayType", "SOAP-ENC"), "cwmp:ParameterValueStruct["
                + parameterValueStructList.size() + "]");
        QName typeName = new QName("", "type", "xsi");
        for (ParameterValueStruct valueStruct : parameterValueStructList) {
            SOAPElement param = parameterList.addChildElement("ParameterValueStruct");
            param.addChildElement("Name").addTextNode(valueStruct.getName());
            param.addChildElement("Value").addAttribute(typeName, valueStruct.getType()).addTextNode(valueStruct.getValue());
        }
    }
}
