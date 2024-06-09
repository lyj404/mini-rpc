package sh.cloudns.lyj.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cloudns.lyj.rpc.api.HelloObject;
import sh.cloudns.lyj.rpc.api.HelloService;

/**
 * @Date 2024/6/9
 * @Author lyj
 */
public class HelloServiceImpl implements HelloService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        LOGGER.info("收到：", object.getMessage());
        return "这是调用的返回值，id:" + object.getId();
    }
}
