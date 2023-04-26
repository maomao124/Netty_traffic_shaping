package mao.t1;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Project name(项目名称)：Netty_traffic_shaping
 * Package(包名): mao.t1
 * Class(类名): Client
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/4/26
 * Time(创建时间)： 20:36
 * Version(版本): 1.0
 * Description(描述)： 高低水位机制-客户端
 */

@Slf4j
public class Client
{
    @SneakyThrows
    public static void main(String[] args)
    {
        //10kb
        byte[] data = new byte[1024 * 10 * 8];

        Channel channel = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>()
                {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception
                    {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter()
                        {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception
                            {
                                //设置低水位
                                ctx.channel().config().setWriteBufferLowWaterMark(20 * 1024);
                                //设置高水位
                                ctx.channel().config().setWriteBufferHighWaterMark(100 * 1024);
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception
                            {
                                System.exit(1);
                            }
                        });
                    }
                })
                .connect(new InetSocketAddress(8080))
                .sync()
                .channel();

        int total = 0;

        while (true)
        {
            if (channel.isWritable())
            {
                ByteBuf buffer = channel.alloc().buffer(1024 * 10 * 8);
                buffer.writeBytes(data);
                channel.write(buffer);
                total += 10;
                log.info("写入10kb数据，已发送：" + total + "kb");
            }
            else
            {
                log.info("服务端不可写");
                channel.flush();
                Thread.sleep(1000);
            }

        }
    }
}
