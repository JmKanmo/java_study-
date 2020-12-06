package netty.echo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import netty.echo.Config.EchoConfig;
import netty.echo.server.EchoServerHandler;

import java.net.InetSocketAddress;

public class EchoClient {
    public static void main(String[] args) throws Exception {
        EchoClient echoClient = new EchoClient();
        echoClient.start();
    }

    public void start() throws Exception {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        final AttributeKey<Integer> id = AttributeKey.newInstance("id");

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .remoteAddress(new InetSocketAddress(EchoConfig.host, EchoConfig.port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("firstHandler", new EchoClientHandler1())
                                    .addLast("secondHandler", new EchoClientHandler())
                                    .addLast("outboundHandler1", new EchoClientOutBountHandler())
                                    .addLast("outboundHandler2", new EchoClientOutBoundHandler2());
                        }
                    });

            for (int i = 1; i <= 3; i++) {
                bootstrap.attr(id, i);
                ChannelFuture channelFuture = bootstrap.connect().sync();
                channelFuture.channel().closeFuture().sync();
            }
        } finally {
            eventLoopGroup.shutdownGracefully().sync();
            System.out.println("Echo Client 닫힘");
        }
    }
}
