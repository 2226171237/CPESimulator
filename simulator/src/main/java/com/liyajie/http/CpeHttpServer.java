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
     * HttpServer
     */
    private final HttpServer server;

    /**
     * Server端口
     */
    private final int serverPort;

    public CpeHttpServer(int serverPort) {
        this.serverPort = serverPort;
        server = createServer();
        init();
    }

    private void init() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 6000,
                TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(1000));
        server.setExecutor(executor);
        server.createContext("/", new RequestHandler());
    }

    private HttpServer createServer() {
        try {
            return HttpServer.create(new InetSocketAddress(this.serverPort), 0);
        } catch (IOException e) {
            LOGGER.error("createServer: catch an exception.", e);
        }
        return null;
    }

    /**
     * 开启服务
     */
    public void start() {
        server.start();
        LOGGER.info("Http Server is started at port {}", serverPort);
    }

    /**
     * 关闭服务
     */
    public void close() {
        server.stop(100);
        LOGGER.info("Http Server is stoped at port {}", serverPort);
    }
}
