package com.liyajie.rpc.methods;

import com.liyajie.constants.CpeMethod;
import com.liyajie.model.Device;
import com.liyajie.model.GetParameterNamesResponse;
import com.liyajie.model.Parameter;
import com.liyajie.model.ParameterInfoStruct;
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
import java.util.Map;

/**
 * @author Liyajie
 */
public class GetParameterNamesMethod extends AbstractMethod<GetParameterNamesResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetParameterNamesMethod.class);

    @Override
    public GetParameterNamesResponse deviceProcess(Device device, SOAPMessage request) {
        List<Parameter> parameters = getParameters(device, request);
        List<ParameterInfoStruct> responses = new ArrayList<>();
        parameters.forEach(p -> {
            responses.add(new ParameterInfoStruct(p.getName(), p.isWriteable()));
        });
        return GetParameterNamesResponse.builder().parameterInfoStructList(responses).build();
    }

    @Override
    CpeMethod methodType() {
        return CpeMethod.GET_PARAMETER_NAMES;
    }

    private List<Parameter> getParameters(Device device, SOAPMessage request) {
        List<Parameter> params = new ArrayList<>();
        String parameterPath = "";
        boolean nextLevel = true;
        try {
            SOAPElement rpcElement = SoapUtil.getRpcElement(request);
            if (rpcElement == null) {
                LOGGER.error("{} getParameterKeys: rpcElement is null.", device.getName());
                return params;
            }
            Iterator<Node> elements = rpcElement.getChildElements();
            while (elements.hasNext()) {
                Node next = elements.next();
                if (!(next instanceof SOAPElement)) {
                    continue;
                }
                SOAPElement currentElem = (SOAPElement) next;
                if ("ParameterPath".equals(currentElem.getLocalName())) {
                    parameterPath = currentElem.getValue();
                }
                if ("NextLevel".equals(currentElem.getLocalName())) {
                    int value = Integer.parseInt(currentElem.getValue());
                    nextLevel = value != 0;
                }
            }
        } catch (SOAPException e) {
            LOGGER.error(device.getName() + " getParameterKeys: catch an exception: ", e);
            return params;
        }
        return getParametersByPath(device, parameterPath, nextLevel);
    }

    private List<Parameter> getParametersByPath(Device device, String parameterPath, boolean nextLevel) {
        List<Parameter> params = new ArrayList<>();
        if (parameterPath == null || "".equals(parameterPath)) {
            LOGGER.warn("{} getParametersByPath: parameterPath is empty", device.getName());
            return params;
        }
        Map<String, Parameter> parameterMap = device.getParameterMap();
        if (nextLevel) {
            for (String p : parameterMap.keySet()) {
                if (p.startsWith(parameterPath) && p.length() > parameterPath.length() + 1) {
                    int i = p.indexOf(".", parameterPath.length() + 1);
                    if (i == -1 || i == p.length() - 1) {
                        params.add(parameterMap.get(p));
                    }
                }
            }
        } else {
            for (String p : parameterMap.keySet()) {
                if (p.startsWith(parameterPath)) {
                    params.add(parameterMap.get(p));
                }
            }
        }
        return params;
    }
}
