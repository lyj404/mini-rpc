package sh.cloudns.lyj.rpc.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @Description 用于在运行时扫描和加载指定包名下的所有类
 * @Author: lyj
 * @Date: 2024/6/14 11:34
 */
public class ReflectUtil {

    /**
     * 用于获取当前调用者的方法所在的类名。
     * 该方法通过创建一个Throwable实例来访问调用栈，然后返回调用栈最后一个元素的类名。
     * @return 返回调用者类名的字符串表示。
     */
    public static String getStackTrace() {
        StackTraceElement[] stack = new Throwable().getStackTrace(); // 获取当前的调用栈
        return stack[stack.length - 1].getClassName(); // 返回调用栈最后一个元素的类名
    }

    /**
     * 用于扫描并返回指定包名下的所有类。
     * @param packageName 要扫描的包名。
     * @return 返回一个包含所有找到的类的Set集合。
     */
    public static Set<Class<?>> getClasses(String packageName) {
        // 创建一个LinkedHashSet用于存储类
        Set<Class<?>> classes = new LinkedHashSet<>();
        // 是否递归扫描子包
        boolean recursive = true;
        // 将点分隔的包名转换为路径
        String packageDirName = packageName.replace('.', '/');
        // 用于存储获取到的资源URL列表
        Enumeration<URL> dirs;
        try {
            // 获取包名对应的资源URL列表
            dirs = Thread.currentThread()
                    .getContextClassLoader()
                    .getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                // 获取下一个资源URL
                URL url = dirs.nextElement();
                // 获取URL的协议类型
                String protocol = url.getProtocol();
                // 如果资源是文件系统上的文件或目录
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    // 定义一个JarFile
                    JarFile jar;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection())
                                .getJarFile();
                        // 获取Jar包中所有条目的枚举
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            // 获取条目的名称
                            String name = entry.getName();
                            // 如果是以/开头
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名把斜杠替换成"."
                                    packageName = name.substring(0, idx)
                                            .replace('/', '.');
                                }
                                if ((idx != -1) || recursive) {
                                    // 如果可以迭代下去 并且是一个包
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        // 去掉后面的".class" 获取真正的类名
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            // 添加到classes
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    /**
     * 通过文件系统递归查找并添加指定包下的所有类。
     * @param packageName 包名。
     * @param packagePath 包的物理路径。
     * @param recursive 是否递归扫描子目录。
     * @param classes 用于存储找到的类的集合。
     */
    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录，建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
        File[] dirfiles = dir.listFiles(file -> (recursive && file.isDirectory())
                || (file.getName().endsWith(".class")));
        // 确保文件数组不为null
        assert dirfiles != null;
        // 循环所有文件
        for (var file : dirfiles) {
            // 如果是目录，则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else {
                // 如果是java类文件，去掉后面的.class只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 添加到集合
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
