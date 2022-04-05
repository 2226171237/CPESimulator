package com.liyajie.builder;

import com.liyajie.model.Device;

import javax.xml.namespace.QName;
import javax.xml.soap.*;

/**
 * @author Liyajie
 */
public abstract class ResponseBuilder {

    public SOAPMessage buildSoapMessage(Object body) throws SOAPException {
        SOAPMessage message = MessageFactory.newInstance().createMessage();
        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        // namespace
        envelope.addNamespaceDeclaration("SOAP-ENC", "http://schemas.xmlsoap.org/soap/encoding/");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        envelope.addNamespaceDeclaration("cwmp", "urn:dslforum-org:cwmp-1-2");
        SOAPHeader soapHeader = envelope.getHeader();
        QName idName = new QName("urn:dslforum-org:cwmp-1-2", "ID", "cwmp");
        SOAPHeaderElement headerElement = soapHeader.addHeaderElement(idName);
        headerElement.addAttribute(new QName("", "mustUnderstand", "SOAP-ENV"), "1");
        headerElement.addTextNode("2044897763");
        buildBody(message, body);
        return message;
    }

    /**
     * 构建body
     *
     * @param message SOAPMessage
     * @param body    body
     */
    abstract void buildBody(SOAPMessage message, Object body) throws SOAPException;
}
