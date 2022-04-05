package com.liyajie.rpc.methods;

import com.liyajie.builder.ResponseBuilderFactory;
import com.liyajie.constants.CpeMethod;
import com.liyajie.model.Device;
import com.liyajie.rpc.api.ICallback;
import com.liyajie.rpc.api.IRpcHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.SOAPMessage;

/**
 * @author Liyajie
 */
public class SetParameterValuesMethod extends AbstractMethod<Integer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetParameterValuesMethod.class);

    @Override
    public Integer deviceProcess(Device device, SOAPMessage request) {
//        try {
//            SOAPElement rpcElement = SoapUtil.getRpcElement(request);
//
//            return 0;
//        } catch () {
//
//        }
        return -1;
    }

    @Override
    CpeMethod methodType() {
        return CpeMethod.SET_PARAMETER_VALUES;
    }
}
