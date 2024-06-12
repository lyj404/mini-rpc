package sh.cloudns.lyj.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.api.HelloObject;
import sh.cloudns.lyj.rpc.api.HelloService;

/**
 * @Date 2024/6/12
 * @Author lyj
 */
public class HelloServiceImpl2 implements HelloService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloServiceImpl2.class);

    @Override
    public String hello(HelloObject object) {
        LOGGER.info("接收到消息：{}", object.getMessage());
        return "本次处理来自Socket服务";
    }
}
