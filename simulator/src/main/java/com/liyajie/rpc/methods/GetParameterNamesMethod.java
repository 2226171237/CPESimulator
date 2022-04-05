package com.liyajie.rpc.methods;

import com.liyajie.constants.CpeMethod;
import com.liyajie.model.Device;
import com.liyajie.model.GetParameterNamesResponse;
import com.liyajie.rpc.api.ICallback;
import com.liyajie.rpc.api.IRpcHandler;

import javax.xml.soap.SOAPMessage;

/**
 * @author Liyajie
 */
public class GetParameterNamesMethod extends AbstractMethod<GetParameterNamesResponse> {

    @Override
    public GetParameterNamesResponse deviceProcess(Device device, SOAPMessage request) {
        return null;
    }

    @Override
    CpeMethod methodType() {
        return CpeMethod.GET_PARAMETER_NAMES;
    }
}
