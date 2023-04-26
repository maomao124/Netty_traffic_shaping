package mao.t3;

import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import lombok.extern.slf4j.Slf4j;

/**
 * Project name(项目名称)：Netty_traffic_shaping
 * Package(包名): mao.t3
 * Class(类名): ServerChannelTrafficShapingHandler
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/4/26
 * Time(创建时间)： 22:44
 * Version(版本): 1.0
 * Description(描述)： 无
 */

@Slf4j
public class ServerChannelTrafficShapingHandler extends ChannelTrafficShapingHandler
{
    /**
     * 服务器通道流量整形处理程序
     *
     * @param writeLimit    写限制
     * @param readLimit     阅读限制
     * @param checkInterval 检查间隔
     * @param maxTime       最大值时间
     */
    public ServerChannelTrafficShapingHandler(long writeLimit, long readLimit, long checkInterval, long maxTime)
    {
        super(writeLimit, readLimit, checkInterval, maxTime);
    }

    @Override
    protected void doAccounting(TrafficCounter counter)
    {
        log.info("上个监控周期读取了" + counter.lastReadBytes() + "个字节,发送了" + counter.lastWrittenBytes() + "个字节");
    }
}
