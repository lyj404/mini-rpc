package ch.cloudns.lyj.rpc.compress.impl;

import ch.cloudns.lyj.rpc.compress.Compress;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: liyj
 * @date: 2024/6/18 15:59
 */
public class CompressFactory {
    private static final Map<Integer, Compress> map = new HashMap<>(16);

    static {
        map.put(0, new DefaultCompress());
        map.put(1, new GzipCompress());
    }

    public static Compress getCompressInstance(int type){
        if (type == 1) {
            return map.get(type);
        }
        return map.get(0);
    }
}
