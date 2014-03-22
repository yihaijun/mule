/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.manager.api.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import com.regaltec.asip.manager.api.thread.IBaseThread;

/**
 * <p>提供一些与操作系统有关的函数。</p>
 * <p>创建日期：2010-11-17 上午09:30:18</p>
 *
 * @author yihaijun
 */
@SuppressWarnings("restriction")
public class SystemUtil {
    // private static Logger log = LoggerFactory.getLogger(SystemUtil.class.getName());
    private static Logger log = Logger.getLogger(SystemUtil.class.getName());
    private static String localHostAddress = "";

    /**
     * 
     * <p>取本机IP地址。</p>
     * @return 本机IP地址
     * -Djava.net.preferIPv4Stack=TRUE
     * 哈哈，ASIP-NODE加了这个参数，所以取的地址是对的,不会出现取到0:0:0:0:0:0:0:1%1这种IP了
     */
    @SuppressWarnings("unchecked")
    public static String getLocalHostAddress() {
        if (!localHostAddress.equals("")) {
            return localHostAddress;
        }
        String ip = "";
        String ipBak = "";
        java.net.InetAddress inet = null;
        try {
            inet = java.net.InetAddress.getLocalHost();
            log.info(" java.net.InetAddress.getLocalHost()=" + inet );           
        } catch (UnknownHostException e1) {
             e1.printStackTrace();
        }
        ip = inet.getHostAddress();
        if (!ip.equals("127.0.0.1") || ip.indexOf(":") >= 0) {
            localHostAddress = ip;
            log.info("java.net.InetAddress.getLocalHost().getHostAddress()=" + ip);
            return ip;
        }
        Enumeration netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress iAddress = null;
        try {
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
                iAddress = (InetAddress) ni.getInetAddresses().nextElement();
                if (!iAddress.isSiteLocalAddress() && !iAddress.isLoopbackAddress()
                        && iAddress.getHostAddress().indexOf(":") == -1) {
                    ip = iAddress.getHostAddress();
                    try {
                        log.info("iAddress.getHostAddress()=" + ip + ",getLocalHost()=" + iAddress.getLocalHost());
                    } catch (UnknownHostException e) {
                        log.info("iAddress.getHostAddress()=" + ip + ",getLocalHost()=" + e.toString());
                        e.printStackTrace();
                    }
                    break;
                } else {
                    ip = iAddress.getHostAddress();
                    try {
                        log.info("NetworkInterface.getNetworkInterfaces().nextElement().getInetAddresses().nextElement().getHostAddress()="
                                + ip + ",getLocalHost()=" + iAddress.getLocalHost());
                    } catch (UnknownHostException e) {
                        log.info("NetworkInterface.getNetworkInterfaces().nextElement().getInetAddresses().nextElement().getHostAddress()="
                                + ip + ",getLocalHost()=" + e.toString());
                        e.printStackTrace();
                    }
                    if (!ip.equals("127.0.0.1") && ip.split("\\.").length == 4 && ip.indexOf(":") < 0) {
                        ipBak = ip;
                    }
                    iAddress = null;
                }
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        if (!ip.equals("127.0.0.1") && ip.split("\\.").length == 4 && ip.indexOf(":") < 0) {
            localHostAddress = ip;
            return ip;
        }
        if (!ipBak.equals("127.0.0.1") && ipBak.split("\\.").length == 4 && ipBak.indexOf(":") < 0) {
            localHostAddress = ipBak;
            return ipBak;
        }
        try {
            Enumeration<?> e1 = (Enumeration<?>) NetworkInterface.getNetworkInterfaces();
            while (e1.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) e1.nextElement();
                if (!ni.getName().equals("eth0") && !ni.getName().equals("bond0")) {
                    log.info("NetworkInterface.getNetworkInterfaces().nextElement().getName()=" + ni.getName());
                    continue;
                } else {
                    Enumeration<?> e2 = ni.getInetAddresses();
                    while (e2.hasMoreElements()) {
                        InetAddress ia = (InetAddress) e2.nextElement();
                        if (ia instanceof Inet6Address)
                            continue;
                        ip = ia.getHostAddress();
                        try {
                            log.info("NetworkInterface.getNetworkInterfaces():" + ni.getName()
                                    + ".getInetAddresses().nextElement().getHostAddress()=" + ip + ",getLocalHost()="
                                    + ia.getLocalHost());
                        } catch (UnknownHostException e) {
                            log.info("NetworkInterface.getNetworkInterfaces():" + ni.getName()
                                    + ".getInetAddresses().nextElement().getHostAddress()=" + ip + ",getLocalHost()="
                                    + e.toString());
                            e.printStackTrace();
                        }
                        if (!ip.equals("127.0.0.1") && ip.split("\\.").length == 4 && ip.indexOf(":") < 0) {
                            localHostAddress = ip;
                            return ip;
                        }
                    }
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (!ip.equals("127.0.0.1") && ip.split("\\.").length == 4 && ip.indexOf(":") < 0) {
            localHostAddress = ip;
            return ip;
        }
        localHostAddress = ip;
        return ip;
    }

    /**
     * 
     * <p>捕获进程信号。</p>
     * @return
     */
    public static Thread captureSignalHandler(final IBaseThread asipThread) {
        Thread thHook = new Thread(asipThread) {
            private IBaseThread thread = (IBaseThread) asipThread;

            public void run() {
                // 添入你想在退出JVM之前要处理的必要操作代码
                log.info("ShutdownHook [" + asipThread.getName() + "] run!");
                thread.toBeginExit();
            }
        };
        Runtime.getRuntime().addShutdownHook(thHook);
        return thHook;
    }

    /**
     * 
     * <p>捕获进程信号。</p>
     * @return
     */
    public static void removeSignalHandler(final Thread th) {
        Runtime.getRuntime().removeShutdownHook(th);
    }

    public static class SophisticatedShutdownSequence {
        private static boolean running = true;

        public static void init() {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    System.out.println("reached point of no return ...");
                }
            });
            SignalHandler handler = new SignalHandler() {
                public void handle(Signal sig) {
                    if (running) {
                        running = false;
                        System.out.println("Signal " + sig);
                        System.out.println("Shutting down database...");
                    } else {
                        // only on the second attempt do we exit System.out.println(" database shutdown interrupted!");
                        System.exit(0);
                    }
                }
            };
            Signal.handle(new Signal("INT"), handler);
            Signal.handle(new Signal("TERM"), handler);
        }

        public static void main(String args[]) throws Exception {
            init();
            Object o = new Object();
            synchronized (o) {
                o.wait(10000);
            }
            System.exit(0);
        }
    }

    /**
     * 
     * <p>读取命令行输入。</p>
     * @return
     * @throws IOException
     */
    public static int readCharacter() throws IOException {
        byte[] buf = new byte[16];
        System.in.read(buf);
        return buf[0];
    }

    /**
     * <p>读取命令行输入</p>
     * @return String
     */
    public static String readCMDInput() {
        String ret = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(System.in));
            ret = reader.readLine();
        } catch (Exception e) {
            // e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                }
            }
        }
        return ret;
    }

    /**
     * 
     * <p>根据系统环境变量名称获取变量值</p>
     * @param envName
     * @return String 
     */
    public static String getEnvValueByName(String envName) {
        String OS = System.getProperty("os.name").toLowerCase();
        Process p = null;
        String envValue = "";
        /**
         * 以windows为例.
         */
        if (OS.indexOf("windows") > -1) {
            try {
                p = Runtime.getRuntime().exec("cmd /c set | find \"" + envName + "\"");
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = "";
                while ((line = br.readLine()) != null) {
                    if (StringUtils.isNotBlank(line) && StringUtils.isNotEmpty(line)) {
                        String[] str = line.split("=");
                        envValue = str[1];
                        break;
                    }

                }
            } catch (IOException ioe) {
                // ioe.printStackTrace();
            }
        } else {
            Map map = System.getenv();
            Iterator i = map.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                if (entry.getKey().equals(envName)) {
                    envValue = entry.getValue().toString();
                }
            }
        }
        return envValue;
    }

    /**
     * 
     * <p>根据系统环境变量集合</p>
     * @param envName
     * @return Map 
     */
    @SuppressWarnings("unchecked")
    public static Map getEnv() {
        Map map = new HashMap();
        String OS = System.getProperty("os.name").toLowerCase();
        Process p = null;
        /**
         * 以windows为例.
         */
        if (OS.indexOf("windows") > -1) {
            try {
                p = Runtime.getRuntime().exec("cmd /c set");
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] str = line.split("=");
                    map.put(str[0], str[1]);
                }
            } catch (IOException ioe) {
                // ioe.printStackTrace();
            }
        } else {
            // Linux 待定
        }
        return map;
    }

    public static int setSystemProperty(String propertyName, String propertyValue) {
        System.setProperty(propertyName, propertyValue);
        return 0;
    }

    public static String copyFileByOsCmd(String source, String dest) {
        String OS = System.getProperty("os.name").toLowerCase();
        String result = "";
        if (OS.indexOf("windows") > -1) {
            if (source.indexOf("/") > 0) {
                source = source.replace("/", "\\");
            }
            if (dest.indexOf("/") > 0) {
                dest = dest.replace("/", "\\");
            }
            String[] cmd = { "copy", source, dest, "/Y" };
            result = SystemUtil.callcmd(cmd, null);
        } else {
            // String[] cmd = {"alias cp=cp;cp", "-f", source, dest };
            String result1 = "";
            String result2 = "";
            String[] cmd1 = { "rm", "-rf", dest };
            result1 = SystemUtil.callcmd(cmd1, null);

            String[] cmd2 = { "cp", "-f", source, dest };
            result2 = SystemUtil.callcmd(cmd2, null);

            result = "copyFileByOsCmd:rm retun [" + result1 + "],cp return [" + result2 + "]";
        }
        return result;
    }

    public static String callcmd(String[] args, String charsetName) {
        if (charsetName == null || charsetName.equals("")) {
            charsetName = "ISO-8859-1"; // "US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE", "UTF-16","...?"
        }
        try {
            if (args.length == 0) {
                if (log.isDebugEnabled()) {
                    log.debug("no cmd.");
                }
                return "no cmd";
            }
            String cmd = args[0];
            String[] cmdParam = null;
            String osName = System.getProperty("os.name");
            int indexParam = 0;
            if (log.isDebugEnabled()) {
                log.debug("osName = " + osName + ",args.length=" + args.length + ",args[0]=" + args[0]);
            }
            StringBuffer logCmdBuf = new StringBuffer();
            logCmdBuf.delete(0, logCmdBuf.length());
            if (osName.toLowerCase().startsWith("windows")) {
                cmdParam = new String[args.length + 2];
                cmdParam[0] = "cmd";
                cmdParam[1] = "/c";

                if (args[0].endsWith(".sh")) {
                    cmd = args[0].substring(0, args[0].length() - 3);
                    cmd = cmd + ".bat";
                }
                cmd = cmd.replaceAll("/", "\\\\");
                cmdParam[2] = cmd;
                indexParam = 3;

                logCmdBuf.append(cmdParam[0]);
                logCmdBuf.append(" ");
                logCmdBuf.append(cmdParam[1]);
                logCmdBuf.append(" ");
                logCmdBuf.append(cmdParam[2]);
            } else if (args[0].indexOf(".sh") > 0 || args[0].indexOf(".bat") > 0) {
                cmdParam = new String[args.length + 1];
                cmdParam[0] = "sh";

                if (args[0].endsWith(".bat")) {
                    cmd = args[0].substring(0, args[0].length() - 4);
                    cmd = cmd + ".sh";
                }
                cmd = cmd.replaceAll("\\\\", "/");
                cmdParam[1] = cmd;
                indexParam = 2;

                logCmdBuf.append(cmdParam[0]);
                logCmdBuf.append(" ");
                logCmdBuf.append(cmdParam[1]);
            } else {
                cmdParam = new String[args.length];
                cmdParam[0] = args[0];
                indexParam = 1;
                logCmdBuf.append(cmdParam[0]);
            }
            if (log.isDebugEnabled()) {
                log.debug("cmd : " + cmd);
            }
            for (int i = 1; i < args.length; i++) {
                cmdParam[indexParam] = args[i];
                logCmdBuf.append(" " + cmdParam[indexParam]);
                indexParam++;
            }
            if (log.isDebugEnabled()) {
                log.debug("execcmd(" + logCmdBuf.toString() + ") begin...");
            }
            String result = execcmd(cmdParam, charsetName);
            if (log.isDebugEnabled()) {
                log.debug("execcmd return [" + result + "]");
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    private static String execcmd(String[] args, String charsetName) {
        StringBuffer buf = new StringBuffer();
        buf.delete(0, buf.length());
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        BufferedInputStream eis = null;
        InputStreamReader eisr = null;
        BufferedReader ebr = null;
        try {
            Process process = Runtime.getRuntime().exec(args);
            is = process.getInputStream();
            isr = new InputStreamReader(is, charsetName);
            br = new BufferedReader(isr);
            String line = null;

            buf.delete(0, buf.length());
            while ((line = br.readLine()) != null) {
                buf.append(line);
                buf.append("\r\n");
                if (buf.length() > 20 * 1024 * 1024) {
                    log.error("buf.length() = " + buf.length() + " > 20*1024*1024 !");
                }
            }

            StringBuffer ebuf = new StringBuffer();
            ebuf.delete(0, ebuf.length());
            eis = new BufferedInputStream(process.getErrorStream());
            eisr = new InputStreamReader(eis, charsetName);
            ebr = new BufferedReader(isr);
            while ((line = ebr.readLine()) != null) {
                ebuf.append(line);
                ebuf.append("\r\n");
                if (ebuf.length() > 20 * 1024 * 1024) {
                    log.error("ebuf.length() = " + ebuf.length() + " > 20*1024*1024 !");
                }
            }
            if (ebuf.toString().length() > 0) {
                log.error(ebuf.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
            buf.append(e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            buf.append(e.toString());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (isr != null) {
                    isr.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (ebr != null) {
                    ebr.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (eisr != null) {
                    eisr.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (eis != null) {
                    eis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return buf.toString();
    }
}
