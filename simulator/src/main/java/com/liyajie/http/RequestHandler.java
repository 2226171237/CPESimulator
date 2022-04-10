package com.liyajie.http;

import com.liyajie.broker.EventBroker;
import com.liyajie.constants.EventCode;
import com.liyajie.service.CpeCenterService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Liyajie
 */
public class RequestHandler implements HttpHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);

    private static final String GET_METHOD = "GET";


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (!GET_METHOD.equals(method)) {
            LOGGER.error("request method is not allowed.");
            exchange.sendResponseHeaders(HttpStatus.SC_METHOD_NOT_ALLOWED, 0);
            return;
        }
        dispatchRequest(exchange);
        exchange.sendResponseHeaders(HttpStatus.SC_NO_CONTENT, 0);
    }

    private void dispatchRequest(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        CpeCenterService cpeCenter = CpeCenterService.getInstance();
        if (!cpeCenter.contains(path)) {
            LOGGER.warn("dispatchRequest: path {} is not exist.", path);
            return;
        }
        int deviceId = cpeCenter.getDeviceIdByUrl(path);
        EventBroker eventBroker = EventBroker.getInstance();
        eventBroker.broker(deviceId, EventCode.combineEvents(EventCode.CONNECTION_REQUEST_6));
    }
}
