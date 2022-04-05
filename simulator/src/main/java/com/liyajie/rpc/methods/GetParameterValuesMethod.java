package com.liyajie.rpc.methods;

import com.liyajie.builder.ResponseBuilderFactory;
import com.liyajie.constants.CpeMethod;
import com.liyajie.model.Device;
import com.liyajie.model.GetParameterValuesResponse;
import com.liyajie.model.Parameter;
import com.liyajie.model.ParameterValueStruct;
import com.liyajie.rpc.api.ICallback;
import com.liyajie.rpc.api.IRpcHandler;
import com.liyajie.utils.SoapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * @author Liyajie
 */
public class GetParameterValuesMethod extends AbstractMethod<GetParameterValuesResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetParameterValuesMethod.class);

    @Override
    public GetParameterValuesResponse deviceProcess(Device device, SOAPMessage request) {
        GetParameterValuesResponse response = new GetParameterValuesResponse(new ArrayList<>());
        try {
            SOAPElement rpcElement = SoapUtil.getRpcElement(request);
            if (rpcElement == null) {
                LOGGER.error("deviceProcess: getRpcElement is null.");
                return response;
            }
            Optional<SOAPElement> childElement = SoapUtil.getChildElement(rpcElement, "ParameterNames");
            if (childElement.isPresent()) {
                SOAPElement parameterNames = childElement.get();
                response.setParameterValueStructList(getParameters(device, parameterNames));
                return response;
            }
        } catch (SOAPException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    CpeMethod methodType() {
        return CpeMethod.GET_PARAMETER_VALUES;
    }

    private List<ParameterValueStruct> getParameters(Device device, SOAPElement parameterNames) {
        List<ParameterValueStruct> parameterValueStructList = new ArrayList<>();
        Iterator<Node> nameIt = parameterNames.getChildElements();
        while (nameIt.hasNext()) {
            Node next = nameIt.next();
            if (next instanceof SOAPElement) {
                String name = next.getValue();
                Parameter parameter = device.getParameter(name);
                if (parameter == null) {
                    continue;
                }
                ParameterValueStruct valueStruct = ParameterValueStruct.builder().name(name).
                        value(parameter.getValue()).
                        type(parameter.getValueType())
                        .build();
                parameterValueStructList.add(valueStruct);
            }
        }
        return parameterValueStructList;
    }
}
