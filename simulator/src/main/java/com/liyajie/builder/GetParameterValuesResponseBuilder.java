package com.liyajie.builder;

import com.liyajie.model.GetParameterValuesResponse;
import com.liyajie.model.ParameterValueStruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.util.List;

/**
 * @author Liyajie
 */
public class GetParameterValuesResponseBuilder extends ResponseBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetParameterValuesResponseBuilder.class);

    @Override
    void buildBody(SOAPMessage message, Object body) throws SOAPException {
        if (!(body instanceof GetParameterValuesResponse)) {
            LOGGER.error("buildBody: input body is not instanceof GetParameterValuesResponse.");
            return;
        }
        GetParameterValuesResponse parameterValues = (GetParameterValuesResponse) body;
        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        SOAPBody soapBody = envelope.getBody();
        QName qName = new QName("", "GetParameterValuesResponse", "cwmp");
        SOAPBodyElement valuesResponse = soapBody.addBodyElement(qName);
        addParameterList(valuesResponse, parameterValues.getParameterValueStructList());
    }

    private void addParameterList(SOAPBodyElement valuesResponse, List<ParameterValueStruct> parameterValueStructList) throws SOAPException {
        SOAPElement parameterList = valuesResponse.addChildElement("ParameterList");
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
