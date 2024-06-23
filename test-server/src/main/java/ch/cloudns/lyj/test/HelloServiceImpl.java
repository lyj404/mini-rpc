package ch.cloudns.lyj.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.cloudns.lyj.rpc.annotation.Service;
import ch.cloudns.lyj.rpc.api.HelloObject;
import ch.cloudns.lyj.rpc.api.HelloService;

/**
 * @Date 2024/6/9
 * @Author lyj
 */
@Service(group = "Group1")
public class HelloServiceImpl implements HelloService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        LOGGER.info("接收到消息：{}", object.getMessage());
        return "这是Hello方法";
    }
}
