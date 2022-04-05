package com.liyajie.rpc.methods;

import com.liyajie.builder.ResponseBuilderFactory;
import com.liyajie.constants.CpeMethod;
import com.liyajie.model.*;
import com.liyajie.rpc.api.ICallback;
import com.liyajie.rpc.api.IRpcHandler;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Liyajie
 */

@Data
public class InformMethod extends AbstractMethod<Inform> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InformMethod.class);

    private final static String[] INFORM_PARAMS = {
            "Device.DeviceInfo.SpecVersion",
            "InternetGatewayDevice.DeviceInfo.SpecVersion",
            "Device.DeviceInfo.HardwareVersion",
            "InternetGatewayDevice.DeviceInfo.HardwareVersion",
            "Device.DeviceInfo.SoftwareVersion",
            "InternetGatewayDevice.DeviceInfo.SoftwareVersion",
            "Device.DeviceInfo.ProvisioningCode",
            "InternetGatewayDevice.DeviceInfo.ProvisioningCode",
            "Device.ManagementServer.ParameterKey",
            "InternetGatewayDevice.ManagementServer.ParameterKey",
            "Device.ManagementServer.ConnectionRequestURL",
            "InternetGatewayDevice.ManagementServer.ConnectionRequestURL",
            "Device.WANDevice.1.WANConnectionDevice.1.WANPPPConnection.1.ExternalIPAddress",
            "InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANPPPConnection.1.ExternalIPAddress",
            "Device.WANDevice.1.WANConnectionDevice.1.WANIPConnection.1.ExternalIPAddress",
            "InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection.1.ExternalIPAddress"
    };

    private List<String> events;

    @Override
    public Inform deviceProcess(Device device, SOAPMessage request) {
        DeviceStruct deviceStruct = getDeviceStruct(device);
        List<EventStruct> eventStructList = new ArrayList<>();
        for (String event : events) {
            eventStructList.add(EventStruct.builder().eventCode(event).build());
        }
        List<ParameterValueStruct> parameterValueStructs = new ArrayList<>();
        for (String name : INFORM_PARAMS) {
            Parameter parameter = device.getParameter(name);
            if (parameter != null) {
                ParameterValueStruct valueStruct = ParameterValueStruct.builder().name(name)
                        .value(parameter.getValue())
                        .type(parameter.getValueType()).build();
                parameterValueStructs.add(valueStruct);
            }
        }
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return Inform.builder().deviceStruct(deviceStruct)
                .eventStructs(eventStructList)
                .currentTime(ft.format(new Date()))
                .maxEnvelopes(1)
                .retryCount(0)
                .parameterValueStructList(parameterValueStructs).build();
    }

    @Override
    CpeMethod methodType() {
        return CpeMethod.INFORM;
    }

    private DeviceStruct getDeviceStruct(Device device) {
        String oui = device.containsParameter("DeviceID.OUI") ?
                device.getParameter("DeviceID.OUI").getValue() : "";
        String manufacturer = device.containsParameter("DeviceID.Manufacturer") ?
                device.getParameter("DeviceID.OUI").getValue() : "";
        String productClass = device.containsParameter("DeviceID.ProductClass") ?
                device.getParameter("DeviceID.OUI").getValue() : "";
        String serialNumber = device.containsParameter("DeviceID.SerialNumber") ?
                device.getParameter("DeviceID.OUI").getValue() : "";
        return DeviceStruct.builder().oui(oui)
                .manufacturer(manufacturer)
                .productClass(productClass)
                .serialNumber(serialNumber)
                .build();
    }

    public static void main(String[] args) throws SOAPException, IOException {

        SOAPFactory soapFactory = SOAPFactory.newInstance();
        // DeviceStruct
        SOAPElement deviceId = soapFactory.createElement("DeviceId");
        SOAPElement manufacturer = soapFactory.createElement("Manufacturer").addTextNode("Bellmann");
        deviceId.addChildElement(manufacturer);
        SOAPElement oui = soapFactory.createElement("OUI").addTextNode("001F8F");
        deviceId.addChildElement(oui);
        SOAPElement productClass = soapFactory.createElement("ProductClass").addTextNode("HA9330e");
        deviceId.addChildElement(productClass);
        SOAPElement serialNumber = soapFactory.createElement("SerialNumber").addTextNode("0000FFFFFF");
        deviceId.addChildElement(serialNumber);

        // Event
        SOAPElement eventElement = soapFactory.createElement("Event");
        eventElement.addAttribute(new QName("", "arrayType", "SOAP-ENC"), "cwmp:EventStruct[1]");
        SOAPElement eventStruct = eventElement.addChildElement("EventStruct");
        eventStruct.addChildElement("EventCode").addTextNode("0 BootStrap");
        eventStruct.addChildElement("CommandKey");

        // MaxEnvelopes
        SOAPElement maxEnvelopes = soapFactory.createElement("MaxEnvelopes").addTextNode("1");

        // CurrentTime
        SOAPElement currentTime = soapFactory.createElement("CurrentTime").addTextNode(new Date().toString());

        // RetryCount
        SOAPElement retryCount = soapFactory.createElement("RetryCount").addTextNode("0");

        // ParameterList
        SOAPElement parameterList = soapFactory.createElement("ParameterList");
        parameterList.addAttribute(new QName("", "arrayType", "SOAP-ENC"), "cwmp:ParameterValueStruct[10]");
        SOAPElement param1 = parameterList.addChildElement("ParameterValueStruct");
        param1.addChildElement("Name").addTextNode("Device.DeviceInfo.SpecVersion");
        param1.addChildElement("Value").addAttribute(new QName("", "type", "xsi"), "xsd:string").addTextNode("1.0");
        SOAPElement param2 = parameterList.addChildElement("ParameterValueStruct");
        param2.addChildElement("Name").addTextNode("Device.DeviceInfo.ProvisioningCode");
        param2.addChildElement("Value").addAttribute(new QName("", "type", "xsi"), "xsd:string").addTextNode("");
        SOAPElement param3 = parameterList.addChildElement("ParameterValueStruct");
        param3.addChildElement("Name").addTextNode("Device.DeviceInfo.Manufacturer");
        param3.addChildElement("Value").addAttribute(new QName("", "type", "xsi"), "xsd:string").addTextNode("easycwmp");
        SOAPElement param4 = parameterList.addChildElement("ParameterValueStruct");
        param4.addChildElement("Name").addTextNode("Device.DeviceInfo.ManufacturerOUI");
        param4.addChildElement("Value").addAttribute(new QName("", "type", "xsi"), "xsd:string").addTextNode("FFFFFF");
        SOAPElement param5 = parameterList.addChildElement("ParameterValueStruct");
        param5.addChildElement("Name").addTextNode("Device.DeviceInfo.ProductClass");
        param5.addChildElement("Value").addAttribute(new QName("", "type", "xsi"), "xsd:string").addTextNode("easycwmp");
        SOAPElement param6 = parameterList.addChildElement("ParameterValueStruct");
        param6.addChildElement("Name").addTextNode("Device.DeviceInfo.SerialNumber");
        param6.addChildElement("Value").addAttribute(new QName("", "type", "xsi"), "xsd:string").addTextNode("FFFFFF123456");
        SOAPElement param7 = parameterList.addChildElement("ParameterValueStruct");
        param7.addChildElement("Name").addTextNode("Device.DeviceInfo.HardwareVersion");
        param7.addChildElement("Value").addAttribute(new QName("", "type", "xsi"), "xsd:string").addTextNode("example_hw_version");
        SOAPElement param8 = parameterList.addChildElement("ParameterValueStruct");
        param8.addChildElement("Name").addTextNode("Device.DeviceInfo.SoftwareVersion");
        param8.addChildElement("Value").addAttribute(new QName("", "type", "xsi"), "xsd:string").addTextNode("example_sw_version");
        SOAPElement param9 = parameterList.addChildElement("ParameterValueStruct");
        param9.addChildElement("Name").addTextNode("Device.ManagementServer.ConnectionRequestURL");
        param9.addChildElement("Value").addAttribute(new QName("", "type", "xsi"), "xsd:string").addTextNode("");
        SOAPElement param10 = parameterList.addChildElement("ParameterValueStruct");
        param10.addChildElement("Name").addTextNode("Device.ManagementServer.ParameterKey");
        param10.addChildElement("Value").addAttribute(new QName("", "type", "xsi"), "xsd:string").addTextNode("unsetCommandKey");

        SOAPMessage message = MessageFactory.newInstance().createMessage();
        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        // namespace
        envelope.addNamespaceDeclaration("SOAP-ENC", "http://schemas.xmlsoap.org/soap/encoding/");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        envelope.addNamespaceDeclaration("cwmp", "urn:dslforum-org:cwmp-1-2");

        SOAPBody soapBody = envelope.getBody();
        SOAPHeader soapHeader = envelope.getHeader();

        // Header
        QName idName = new QName("urn:dslforum-org:cwmp-1-2", "ID", "cwmp");
        SOAPHeaderElement headerElement = soapHeader.addHeaderElement(idName);
        headerElement.addAttribute(new QName("", "mustUnderstand", "SOAP-ENV"), "1");
        headerElement.addTextNode("2044897763");

        // Inform
        QName informName = new QName("", "Inform", "cwmp");
        SOAPBodyElement inform = soapBody.addBodyElement(informName);
        inform.addChildElement(deviceId);
        inform.addChildElement(eventElement);
        inform.addChildElement(maxEnvelopes);
        inform.addChildElement(currentTime);
        inform.addChildElement(retryCount);
        inform.addChildElement(parameterList);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        message.writeTo(out);
        System.out.println(out);
    }
}
