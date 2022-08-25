package onvif;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.internal.StringUtil;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final String EMPTY_PROBE_MATCH = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
            "                   xmlns:a=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\"\n" +
            "                   xmlns:d=\"http://schemas.xmlsoap.org/ws/2005/04/discovery\"\n" +
            "                   xmlns:dn=\"http://www.onvif.org/ver10/network/wsdl\">\n" +
            "    <SOAP-ENV:Header>\n" +
            "    </SOAP-ENV:Header>\n" +
            "    <SOAP-ENV:Body>\n" +
            "    </SOAP-ENV:Body>\n" +
            "</SOAP-ENV:Envelope>";

    private static final String A_NAMESPACE = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
    private static final String D_NAMESPACE = "http://schemas.xmlsoap.org/ws/2005/04/discovery";
    private static final String DB_NAMESPACE = "http://www.onvif.org/ver10/network/wsdl";

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        System.out.println("收到组播一条消息:");
        String message = msg.content().toString(Charset.defaultCharset());
        System.out.println(message);
        System.out.println("");
        InetSocketAddress sender = msg.sender();
        if (message != null && message.length() > 0) {
            processMessage(message, sender);
        }
    }

    private void processMessage(String message, InetSocketAddress sender) {
        try {
            SOAPMessage soapMessage = stringToSoapMessage(message);
            String action = getSoapMethod(soapMessage);
            if ("Probe".equals(action)) {
                String reId = parseMessageIDFromProbe(soapMessage);
                sendProbeMatch(reId, sender);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendProbeMatch(String reId, InetSocketAddress sender) throws Exception {
        SOAPMessage soapMessage = buildProbeMatch(reId, "http://192.168.2.250:10086");
        InetSocketAddress groupAddress = new InetSocketAddress("239.255.255.250", 3702);
        UdpUnicastSender.sendMessage(soapMessageToString(soapMessage), groupAddress);
        UdpUnicastSender.sendMessage(soapMessageToString(soapMessage), sender);
    }


    private String soapMessageToString(SOAPMessage soapMessage) throws Exception {
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            soapMessage.writeTo(out);
            return out.toString(String.valueOf(StandardCharsets.UTF_8));
        } finally {
            out.close();
        }
    }

    private SOAPMessage stringToSoapMessage(String response) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8));
        return messageFactory.createMessage(new MimeHeaders(), inputStream);
    }


    private String getSoapMethod(SOAPMessage soapMessage) throws SOAPException {
        SOAPBody soapBody = soapMessage.getSOAPBody();
        Iterator<?> elements = soapBody.getChildElements();
        while (elements.hasNext()) {
            Object next = elements.next();
            if (!(next instanceof SOAPElement)) {
                continue;
            }
            return ((SOAPElement) next).getLocalName();
        }
        return "";
    }

    public SOAPMessage buildProbeMatch(String reId, String xaddrs) throws Exception {
        SOAPMessage soapMessage = stringToSoapMessage(EMPTY_PROBE_MATCH);

        SOAPHeader soapHeader = soapMessage.getSOAPHeader();
        QName messageId = new QName(A_NAMESPACE, "MessageID", "a");
        soapHeader.addHeaderElement(messageId).addTextNode("urn:uuid:" + UUID.randomUUID());
        QName relatesTo = new QName(A_NAMESPACE, "RelatesTo", "a");
        soapHeader.addHeaderElement(relatesTo).addTextNode("uuid:" + reId);
        QName replayTo = new QName(A_NAMESPACE, "ReplyTo", "a");
        SOAPHeaderElement replayToElem = soapHeader.addHeaderElement(replayTo);
        replayToElem.addChildElement("Address", "a").addTextNode("http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous");
        QName to = new QName(A_NAMESPACE, "To", "a");
        soapHeader.addHeaderElement(to).addTextNode("http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous");
        QName action = new QName(A_NAMESPACE, "Action", "a");
        soapHeader.addHeaderElement(action).addTextNode("http://schemas.xmlsoap.org/ws/2005/04/discovery/ProbeMatches");
        QName appSequence = new QName(D_NAMESPACE, "AppSequence", "d");
        SOAPHeaderElement appSequenceElement = soapHeader.addHeaderElement(appSequence);
        appSequenceElement.addAttribute(new QName("MessageNumber"), "0");
        appSequenceElement.addAttribute(new QName("InstanceId"), "0");

        SOAPBody soapBody = soapMessage.getSOAPBody();
        SOAPBodyElement probeMatches = soapBody.addBodyElement(new QName(D_NAMESPACE, "ProbeMatches", "d"));
        SOAPElement probeMatch = probeMatches.addChildElement("ProbeMatch", "d");
        SOAPElement endpointReference = probeMatch.addChildElement("EndpointReference", "a");
        endpointReference.addChildElement("Address", "a").addTextNode("urn:uuid:" + UUID.randomUUID());
        probeMatch.addChildElement("Types", "d").addTextNode("dn:NetworkVideoTransmitter");
        probeMatch.addChildElement("Scopes", "d").addTextNode("onvif://www.onvif.org/Profile/Streaming " +
                "onvif://www.onvif.org/hardware/NetworkVideoTransmitter " +
                "onvif://www.onvif.org/location/country/china " +
                "onvif://www.onvif.org/location/city/shenzhen " +
                "onvif://www.onvif.org/name/NVT");
        probeMatch.addChildElement("XAddrs", "d").addTextNode(xaddrs);
        probeMatch.addChildElement("MetadataVersion", "d").addTextNode("1");
        return soapMessage;
    }

    private String parseMessageIDFromProbe(SOAPMessage probe) throws SOAPException {
        SOAPHeader soapHeader = probe.getSOAPHeader();
        Iterator<?> iterator = soapHeader.getChildElements();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof SOAPElement) {
                SOAPElement elem = (SOAPElement) next;
                if (elem.getLocalName().equals("MessageID")) {
                    return elem.getValue().substring("uuid:".length());
                }
            }
        }
        return "";
    }
}
