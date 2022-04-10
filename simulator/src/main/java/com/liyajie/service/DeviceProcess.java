package com.liyajie.service;

import com.liyajie.constants.EventCode;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Liyajie
 */
public class DeviceProcess implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceProcess.class);

    private final Device device;

    private volatile boolean closed = false;

    private final BlockingQueue<String> eventCodeQueue = new LinkedBlockingQueue<>(100);

    private final Object waitObject = new Object();

    public DeviceProcess(Device device) {
        this.device = device;
    }

    @Override
    public void run() {
        while (!closed) {
            try {
                processOneSession();
                synchronized (waitObject) {
                    waitObject.wait();
                }
            } catch (InterruptedException e) {
                closed = true;
                return;
            }
        }

    }

    public void notifyNew() {
        synchronized (waitObject) {
            waitObject.notifyAll();
        }
    }

    public void addEvent(String eventCodes) {
        eventCodeQueue.offer(eventCodes);
        LOGGER.info("{} addEvent \"{}\"", device.getName(), eventCodes);
        notifyNew();
    }

    synchronized public void close() {
        closed = true;
        LOGGER.info("Device {} is stopping...", device.getName());
    }

    private void processOneSession() throws InterruptedException {
        String eventCodes = eventCodeQueue.take();
        LOGGER.info("{} processOneSession: eventCode is \"{}\"", device.getName(), eventCodes);
        InformMethod informMethod = new InformMethod();
        informMethod.setEvents(EventCode.splitEvents(eventCodes));
        informMethod.handler(device, null, (soapMessage) -> {
            try {
                String body = soapMessageToString(soapMessage);
                String response = CpeHttpClient.getInstance().sendRequest(body);
                if ("".equals(response)) {
                    LOGGER.error("{} processOneSession: ACS response content is empty.", device.getName());
                    return;
                }
                response = CpeHttpClient.getInstance().sendRequest("");
                if ("".equals(response)) {
                    LOGGER.warn("{} processOneSession: ACS response content is empty.", device.getName());
                    return;
                }
                cpeRequest(response);
            } catch (Exception e) {
                LOGGER.error(device.getName() + " processOneSession: catch an exception：", e);
            }
        });
    }

    private String soapMessageToString(SOAPMessage soapMessage) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes(StandardCharsets.UTF_8));
        soapMessage.writeTo(out);
        return out.toString(StandardCharsets.UTF_8);
    }

    private void cpeRequest(String response) throws Exception {
        if ("".equals(response)) {
            LOGGER.error("{} cpeRequest: ACS response content is empty.", device.getName());
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
            LOGGER.error("{} handleMethod: rpc is null.", device.getName());
            return;
        }
        String rpcMethod = rpc.getLocalName();
        LOGGER.info("{} handleMethod: executing rpcMethod {}.", device.getName(), rpcMethod);
        Optional<IRpcHandler> method = MethodFactory.getMethod(rpcMethod);
        method.ifPresent(handler -> handler.handler(device, soapMessage, (message) -> {
            String body = soapMessageToString(message);
            String response = CpeHttpClient.getInstance().sendRequest(body);
            cpeRequest(response);
        }));
    }
}