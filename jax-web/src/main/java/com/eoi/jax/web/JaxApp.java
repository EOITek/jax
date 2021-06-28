/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eoi.jax.web;

import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;

/**
 * 获取应用的相关信息
 *
 */
public class JaxApp {
    public static final String PID_FILE_NAME = "jax.pid.lock";

    /**
     * 将进程pid写入文件，并使用文件锁锁住该文件
     * 如果其他进程锁住了该文件，则直接退出进程
     * pid文件在进程退出后自动删除
     *
     * like {@link org.springframework.boot.context.ApplicationPidFileWriter}
     */
    public static void lockPid() {
        println("");
        try {
            String pid = getPid();
            boolean locked = writeAndLock(PID_FILE_NAME, pid.getBytes(StandardCharsets.UTF_8));
            if (!locked) {
                println("请勿重复启动JAX");
                System.exit(0);
            }
            deleteOnExit(PID_FILE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        printInfo();
    }

    /**
     * 打印应用相关信息
     * 包括 目录、宿主机、进程pid 等
     *
     * like {@link org.springframework.boot.StartupInfoLogger}
     */
    public static void printInfo() {
        println(String.format("Start Jax with PID %s on %s in %s", getPid(), getHostName(), getJaxHome()));
    }

    public static String getJaxHome() {
        String home = System.getenv("JAX_HOME");
        return StrUtil.isEmpty(home) ? System.getProperty("JAX_HOME") : home;
    }

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getPid() {
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        return jvmName.split("@")[0];
    }

    /**
     * 锁定文件并写入内容
     * 依赖系统的文件锁功能，可以跨进程锁
     *
     * @param path 目标文件
     * @param bytes 写入内容
     * @return 是否获得锁，true获得，false未获得
     */
    private static boolean writeAndLock(String path, byte[] bytes) throws IOException {
        // rw打开以便读取和写入，如果该文件尚不存在，则自动创建该文件
        RandomAccessFile file = new RandomAccessFile(path, "rw");
        FileChannel channel = file.getChannel();
        //1. 对于一个只读文件通过任意方式加锁时，会报NonWritableChannelException异常
        //2. 无参lock()默认为独占锁，不会报NonReadableChannelException异常
        //3. 有参lock()为共享锁，也只能读共享，写还是独占的，当有写冲突时会报NonWritableChannelException异常
        //4. lock()，阻塞的方法，当文件锁不可用时，当前进程会被挂起 lock = channel.lock()
        //5. tryLock()，非阻塞的方法，当文件锁不可用时，tryLock()会得到null值
        FileLock lock = channel.tryLock();
        if (lock != null) {
            file.write(bytes); // 不能关闭文件流，关闭会导致lock释放！
            return true;
        }
        return false;
    }

    private static void deleteOnExit(String path) {
        File file = new File(path);
        file.deleteOnExit();
    }

    public static void println(String line) {
        System.out.println(line);
    }
}
