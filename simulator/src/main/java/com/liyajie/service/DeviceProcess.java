package com.liyajie.service;

import com.liyajie.http.CpeHttpClient;
import com.liyajie.model.Device;
import com.liyajie.rpc.api.ICallback;
import com.liyajie.rpc.api.IRpcHandler;
import com.liyajie.rpc.methods.InformMethod;
import com.liyajie.rpc.methods.MethodFactory;
import com.liyajie.utils.SoapUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
class DeviceProcess implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceProcess.class);

    private final Device device;

    private volatile boolean closed = false;

    @Override
    public void run() {
        while (!closed) {
            processOneSession();
            try {
                synchronized (this) {
                    this.wait();
                }
            } catch (InterruptedException e) {
                closed = true;
                return;
            }
        }

    }

    public void notifyNew() {
        synchronized (this) {
            this.notifyAll();
        }
    }

    synchronized public void close() {
        closed = true;
    }

    private void processOneSession() {
        InformMethod informMethod = new InformMethod();
        informMethod.setEvents(Arrays.asList("0 Boot", "1 BootStrap"));
        informMethod.handler(device, null, (soapMessage) -> {
            try {
                String body = soapMessageToString(soapMessage);
                String response = CpeHttpClient.getInstance().sendRequest(body);
                if ("".equals(response)) {
                    LOGGER.error("processOneSession: ACS response content is empty.");
                    return;
                }
                response = CpeHttpClient.getInstance().sendRequest("");
                if ("".equals(response)) {
                    LOGGER.warn("processOneSession: ACS response content is empty.");
                    return;
                }
                cpeRequest(response);
            } catch (Exception e) {
                LOGGER.error("processOneSession: catch an exception： ", e);
            }
        });
    }

    private String soapMessageToString(SOAPMessage soapMessage) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        soapMessage.writeTo(out);
        return out.toString(StandardCharsets.UTF_8);
    }

    private void cpeRequest(String response) throws Exception {
        if ("".equals(response)) {
            LOGGER.error("cpeRequest: ACS response content is empty.");
            return;
        }
        sendMessage(response, this::handleMethod);
    }

    private void sendMessage(String response, ICallback callback) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8));
        SOAPMessage message = messageFactory.createMessage(new MimeHeaders(), inputStream);
        callback.run(message);
    }

    private void handleMethod(SOAPMessage soapMessage) throws SOAPException {
        SOAPElement rpc = SoapUtil.getRpcElement(soapMessage);
        if (rpc == null) {
            LOGGER.error("handleMethod: rpc is null.");
            return;
        }
        String rpcMethod = rpc.getLocalName();
        Optional<IRpcHandler> method = MethodFactory.getMethod(rpcMethod);
        method.ifPresent(handler -> handler.handler(device, soapMessage, (message) -> {
            String body = soapMessageToString(message);
            String response = CpeHttpClient.getInstance().sendRequest(body);
            cpeRequest(response);
        }));
    }
}