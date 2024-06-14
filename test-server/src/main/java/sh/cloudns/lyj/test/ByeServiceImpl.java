package sh.cloudns.lyj.test;

import sh.cloudns.lyj.rpc.annotation.Service;
import sh.cloudns.lyj.rpc.api.ByeService;

/**
 * @author: liyj
 * @date: 2024/6/14 14:02
 */
@Service
public class ByeServiceImpl implements ByeService {
    @Override
    public String bye(String name) {
        return "bye, " + name;
    }
}
