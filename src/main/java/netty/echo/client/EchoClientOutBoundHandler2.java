package netty.echo.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

public class EchoClientOutBoundHandler2 extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println(ctx.name() + " write 메소드 호출");
        ByteBuf byteBuf = (ByteBuf) msg;
        byteBuf.writeInt(49343);

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
    }
}
