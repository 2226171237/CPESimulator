package com.liyajie.service;

import com.liyajie.config.CpeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


/**
 * @author Liyajie
 */
public class CpeSimulator {
    private static final Logger LOGGER = LoggerFactory.getLogger(CpeSimulator.class);

    private static final CpeSimulator SIMULATOR = new CpeSimulator();

    private final CpeEngine engine = CpeEngine.getInstance();

    private CpeSimulator() {
    }

    public static CpeSimulator getInstance() {
        return SIMULATOR;
    }

    public void start() {
        engine.start();
        LOGGER.info("++++++++++++++ CpeSimulator Start ++++++++++++++++++++");
    }

    public void stop() {
        engine.stop();
        LOGGER.info("++++++++++++++ CpeSimulator Stop ++++++++++++++++++++");
    }

    public static void main(String[] args) throws InterruptedException {
        CpeSimulator simulator = CpeSimulator.getInstance();
        simulator.start();
        TimeUnit.SECONDS.sleep(10);
        simulator.stop();
    }
}
