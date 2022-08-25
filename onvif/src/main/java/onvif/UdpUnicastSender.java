package onvif;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * 单播发送
 */
public class UdpUnicastSender {
    public static void sendMessage(String message, InetSocketAddress target) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            byte[] data = message.getBytes(StandardCharsets.UTF_8);
            InetAddress inetAddress = InetAddress.getByName(target.getHostString());
            DatagramPacket packet = new DatagramPacket(data, data.length, inetAddress, target.getPort());
            socket.send(packet);
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
