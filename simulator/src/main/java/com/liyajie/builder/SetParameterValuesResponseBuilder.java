package com.liyajie.builder;

import com.liyajie.model.GetParameterValuesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.soap.*;

/**
 * @author Liyajie
 */
public class SetParameterValuesResponseBuilder extends ResponseBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetParameterValuesResponseBuilder.class);

    @Override
    void buildBody(SOAPMessage message, Object body) throws SOAPException {
        if (!(body instanceof Integer)) {
            LOGGER.error("buildBody: input body is not instanceof Integer.");
            return;
        }
        Integer status = (Integer) body;
        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        SOAPBody soapBody = envelope.getBody();
        QName qName = new QName("", "SetParameterValuesResponse", "cwmp");
        SOAPBodyElement valuesResponse = soapBody.addBodyElement(qName);
        valuesResponse.addChildElement("Status").addTextNode(String.valueOf(status));
    }
}
