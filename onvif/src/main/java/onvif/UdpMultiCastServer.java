package onvif;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;

/**
 * 多播监听
 */
public class UdpMultiCastServer {

    public void start() {
        System.setProperty("java.net.preferIPv4Stack" , "true");
        InetSocketAddress groupAddress = new InetSocketAddress("239.255.255.250", 3702);
        // 192.168.2.250
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            InetAddress localAddress = InetAddress.getByName("192.168.2.250");
            NetworkInterface inetAddress = NetworkInterface.getByInetAddress(localAddress);
            System.out.println(localAddress);
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .localAddress(localAddress, groupAddress.getPort())
                    .option(ChannelOption.IP_MULTICAST_IF, inetAddress)
                    .option(ChannelOption.IP_MULTICAST_ADDR, localAddress)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel ch) throws Exception {
                            ch.pipeline().addLast(new UdpServerHandler());
                        }
                    });
            NioDatagramChannel channel = (NioDatagramChannel) bootstrap.bind(localAddress.getHostAddress(), groupAddress.getPort()).sync().channel();
            channel.joinGroup(groupAddress, inetAddress).sync();
            channel.closeFuture().await();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // group.shutdownGracefully();
        }
    }


    public static void main(String[] args) {
        UdpMultiCastServer multiCastServer = new UdpMultiCastServer();
        multiCastServer.start();
    }
}
