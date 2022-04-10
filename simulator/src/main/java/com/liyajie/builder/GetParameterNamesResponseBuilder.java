package com.liyajie.builder;

import com.liyajie.model.GetParameterNamesResponse;
import com.liyajie.model.GetParameterValuesResponse;
import com.liyajie.model.ParameterInfoStruct;
import com.liyajie.model.ParameterValueStruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.util.List;

/**
 * @author Liyajie
 */
public class GetParameterNamesResponseBuilder extends ResponseBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetParameterNamesResponseBuilder.class);

    @Override
    void buildBody(SOAPMessage message, Object body) throws SOAPException {
        if (!(body instanceof GetParameterNamesResponse)) {
            LOGGER.error("buildBody: input body is not instanceof GetParameterNamesResponse.");
            return;
        }
        GetParameterNamesResponse parameterNames = (GetParameterNamesResponse) body;
        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        SOAPBody soapBody = envelope.getBody();
        QName qName = new QName("", "GetParameterNamesResponse", "cwmp");
        SOAPBodyElement namesResponse = soapBody.addBodyElement(qName);
        addParameterList(namesResponse, parameterNames.getParameterInfoStructList());
    }

    private void addParameterList(SOAPBodyElement namesResponse, List<ParameterInfoStruct> parameterInfoStructList) throws SOAPException {
        SOAPElement parameterList = namesResponse.addChildElement("ParameterList");
        parameterList.addAttribute(new QName("", "arrayType", "SOAP-ENC"), "cwmp:ParameterInfoStruct["
                + parameterInfoStructList.size() + "]");
        for (ParameterInfoStruct info : parameterInfoStructList) {
            SOAPElement param = parameterList.addChildElement("ParameterInfoStruct");
            param.addChildElement("Name").addTextNode(info.getName());
            param.addChildElement("Writable").addTextNode(String.valueOf(info.isWriteable()));
        }
    }
}
