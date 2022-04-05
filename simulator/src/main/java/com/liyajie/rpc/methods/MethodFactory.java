package com.liyajie.rpc.methods;

import com.liyajie.rpc.api.IRpcHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Liyajie
 */
public final class MethodFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodFactory.class);

    private static final Map<String, IRpcHandler> METHOD_MAP = new HashMap<>();

    static {
        METHOD_MAP.put("Inform", new InformMethod());
        METHOD_MAP.put("GetParameterValues", new GetParameterValuesMethod());
        METHOD_MAP.put("GetParameterNames", new GetParameterNamesMethod());
        METHOD_MAP.put("SetParameterValues", new SetParameterValuesMethod());
    }

    public static Optional<IRpcHandler> getMethod(String method) {
        if (!METHOD_MAP.containsKey(method)) {
            LOGGER.error("method is not exist.");
            return Optional.empty();
        }
        return Optional.ofNullable(METHOD_MAP.get(method));
    }
}
