package com.liyajie.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.*;
import java.util.Iterator;
import java.util.Optional;

/**
 * @author Liyajie
 */
public final class SoapUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(SoapUtil.class);

    public static SOAPElement getRpcElement(SOAPMessage soapMessage) throws SOAPException {
        SOAPBody soapBody = soapMessage.getSOAPBody();
        Iterator<Node> elements = soapBody.getChildElements();
        while (elements.hasNext()) {
            Node next = elements.next();
            if (!(next instanceof SOAPElement)) {
                continue;
            }
            return (SOAPElement) next;
        }
        return null;
    }

    public static Optional<SOAPElement> getChildElement(SOAPElement element, String name) {
        Iterator<Node> elements = element.getChildElements();
        while (elements.hasNext()) {
            Node next = elements.next();
            if (!(next instanceof SOAPElement)) {
                continue;
            }
            SOAPElement cur = (SOAPElement) next;
            if (cur.getLocalName().equals(name)) {
                return Optional.of(cur);
            }
        }
        return Optional.empty();
    }
}
