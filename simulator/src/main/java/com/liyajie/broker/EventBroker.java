package com.liyajie.broker;

import com.liyajie.model.Device;
import com.liyajie.service.CpeCenterService;
import com.liyajie.service.DeviceProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Liyajie
 */
public class EventBroker {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventBroker.class);

    private static final EventBroker BROKER = new EventBroker();

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 6000,
            TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(1000));

    private EventBroker() {
    }

    public static EventBroker getInstance() {
        return BROKER;
    }

    public void broker(int deviceId, String eventCodes) {
        executor.execute(() -> {
            Device device = CpeCenterService.getInstance().getDeviceById(deviceId);
            if (device != null) {
                device.getDeviceProcess().addEvent(eventCodes);
                LOGGER.info("broker: add Event {} into deviceId {}", eventCodes, deviceId);
            }
        });
    }
}
