package com.liyajie.http;

import com.liyajie.model.Device;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.PrimitiveIterator;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Liyajie
 */
public class CpeHttpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CpeHttpServer.class);

    /**
     * 设备的serverUrl到设备Id的映射表
     */
    private final Map<String, Integer> deviceMap = new HashMap<>();

    /**
     * HttpServer
     */
    private final HttpServer server;

    /**
     * Server端口
     */
    private final int serverPort;

    public CpeHttpServer(int serverPort) throws IOException {
        this.serverPort = serverPort;
        server = HttpServer.create(new InetSocketAddress(this.serverPort), 0);
        init();
    }

    private void init() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 6000,
                TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(1000));
        server.setExecutor(executor);
        server.createContext("/", new RequestHandler(deviceMap));
    }

    /**
     * 添加启动的设备
     *
     * @param device 设备
     */
    public void addDevice(Device device) {
        deviceMap.put(device.getServerUrl(), device.getDevId());
    }

    /**
     * 开启服务
     */
    public void start() {
        server.start();
    }

    /**
     * 关闭服务
     */
    public void close() {
        server.stop(100);
    }
}
