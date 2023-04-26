package mao.t3;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Project name(项目名称)：Netty_traffic_shaping
 * Package(包名): mao.t3
 * Class(类名): Client
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/4/26
 * Time(创建时间)： 22:29
 * Version(版本): 1.0
 * Description(描述)： 流量整形-ChannelTrafficShapingHandler-客户端，每秒发送3M的数据
 */

@Slf4j
public class Client
{
    @SneakyThrows
    public static void main(String[] args)
    {
        Channel channel = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>()
                {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception
                    {
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter()
                        {
                            private AtomicInteger SEQ = new AtomicInteger(0);

                            final byte[] ECHO_REQ = new byte[1024 * 1024];

                            ScheduledExecutorService scheduledExecutorService =
                                    Executors.newScheduledThreadPool(1);

                            @Override
                            public void channelActive(ChannelHandlerContext ctx)
                            {
                                scheduledExecutorService.scheduleAtFixedRate(() ->
                                {
                                    ByteBuf buf = null;
                                    for (int i = 0; i < 3; i++)
                                    {
                                        buf = Unpooled.copiedBuffer(ECHO_REQ);
                                        if (ctx.channel().isWritable())
                                        {
                                            SEQ.getAndAdd(buf.readableBytes());
                                            ctx.write(buf);
                                        }
                                    }
                                    ctx.flush();
                                    int counter = SEQ.getAndSet(0);
                                    log.info("写入速度 : " + (double) counter / (1024 * 1024) + " M/s");
                                }, 0, 1000, TimeUnit.MILLISECONDS);
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                            {
                                cause.printStackTrace();
                                ctx.close();
                            }
                        });
                    }
                })
                .connect(new InetSocketAddress(8080))
                .sync()
                .channel();
    }
}
