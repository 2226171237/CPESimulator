package com.liyajie.builder;

import com.liyajie.constants.CpeMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Liyajie
 */
public final class ResponseBuilderFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseBuilderFactory.class);

    private static final Map<CpeMethod, ResponseBuilder> BUILDERS = new HashMap<>();


    static {
        BUILDERS.put(CpeMethod.INFORM, new InformResponseBuilder());
        BUILDERS.put(CpeMethod.GET_PARAMETER_NAMES, new GetParameterNamesResponseBuilder());
        BUILDERS.put(CpeMethod.GET_PARAMETER_VALUES, new GetParameterValuesResponseBuilder());
        BUILDERS.put(CpeMethod.SET_PARAMETER_VALUES, new SetParameterValuesResponseBuilder());
    }

    private ResponseBuilderFactory() {
    }

    public static SOAPMessage buildSoapMessage(Object body, CpeMethod method) throws SOAPException {
        if (!BUILDERS.containsKey(method)) {
            LOGGER.error("buildSoapMessage: Method ={} is not error.", method.getName());
            throw new SOAPException("method is not error");
        }
        return BUILDERS.get(method).buildSoapMessage(body);
    }
}
