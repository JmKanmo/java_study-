package netty.echo.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

import java.net.SocketAddress;

public class EchoClientOutBountHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        System.out.println("connect메소드 호출");
        ctx.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println(ctx.name() + " write 메소드 호출");
        ByteBuf byteBuf = (ByteBuf) msg;
        byteBuf.writeShort((short) 5555);

        ChannelFuture channelFuture = ctx.write(byteBuf, promise);

        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    future.cause().printStackTrace();
                    future.channel().close();
                }
            }
        });
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.name() + " flush 메소드 호출");
        ctx.flush();
        ctx.close();
    }
}
