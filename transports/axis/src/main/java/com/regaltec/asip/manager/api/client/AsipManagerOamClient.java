/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.manager.api.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>manager模块向oam提供的客户端。</p>
 * <p>创建日期：2010-11-30 下午03:32:29</p>
 *
 * @author yihaijun
 */
public class AsipManagerOamClient {

    /**
     * 
     * <p>读取系统命令。</p>
     * @return
     */
    public String[] getAsipnodePromptArry() {
        Object service = null;
        try {
            service = Class.forName("com.regaltec.asip.manager.core.conf.NodeConf").newInstance();
            Method method = service.getClass().getMethod("getAsipnodePromptArry");
            return (String[]) method.invoke(service);
        } catch (Exception e) {
            // e.printStackTrace();
            return null;
        }

    }

    /**
     * 
     * <p>读取系统命令。</p>
     * @return
     */
    public String getAsipnodePrompt(String context) {
        Object service = null;
        try {
            service = Class.forName("com.regaltec.asip.manager.core.conf.NodeConf").newInstance();
            Method method = service.getClass().getMethod("getAsipnodePrompt", String.class);
            return (String) method.invoke(service, context);
        } catch (Exception e) {
            // e.printStackTrace();
            return e.toString();
        }

    }

    /**
     * 
     * <p>功能的简单描述，参数、返回值及异常必须注明。</p>
     * @param context
     * @param testType
     * @param appName
     * @param serviceName
     * @return
     */
    public String patrol(String context, String testType, String appName, String serviceName) {
        Object service = null;
        try {
            service = Class.forName("com.regaltec.asip.manager.core.conf.maintain.patrol.ServicePatrol").newInstance();
            Method method =
                    service.getClass().getMethod("patrol", String.class, String.class, String.class, String.class);
            return (String) method.invoke(service, context, testType, appName, serviceName);
            // } catch (InstantiationException e) {
            // e.printStackTrace();
            // } catch (IllegalAccessException e) {
            // e.printStackTrace();
            // } catch (ClassNotFoundException e) {
            // e.printStackTrace();
            // } catch (SecurityException e) {
            // e.printStackTrace();
            // } catch (NoSuchMethodException e) {
            // e.printStackTrace();
            // } catch (IllegalArgumentException e) {
            // e.printStackTrace();
            // } catch (InvocationTargetException e) {
            // e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    /**
     * 
     * <p>从数据T_ASIP_HOST中读取主机信息并执行系统命令。</p>
     * @param ipPrefix String IP前缀,形如： xxx.xxx.xxx
     * @param begin  int  起始地址
     * @param end    int  终止地址
     * @param port   int  端口
     * @param command   String 操作系统命令
     * @return       int  执行命令失败的主机数
     */
    public int executeCmd(String context, String command) {
        return -1; // 未实现
    }

    /**
     * <p>向指定IP网段执行Linux命令(含执行报告)</p>
     * @param ipPrefix String IP前缀,形如： xxx.xxx.xxx
     * @param begin  int  起始地址
     * @param end    int  终止地址
     * @param port   int  端口
     * @param userName  String 用户名
     * @param password  String 密码
     * @param command   Linux操作系统命令
     * @return       int  执行命令失败的主机数
     */
    public int executeLinuxCommand(String context, String command) {
        Object service = null;
        try {
            service = Class.forName("com.regaltec.asip.manager.core.thread.main.SSH2Main").newInstance();
            Method method = service.getClass().getMethod("init", String.class, String.class);
            method.invoke(service, context, command);
            method = service.getClass().getMethod("execute");
            return (Integer) method.invoke(service);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String executeLinuxCommand(String host, int port, String username, String password, String command) {
        Object service = null;
        try {
            service = Class.forName("com.regaltec.asip.manager.core.thread.main.SSH2Main").newInstance();
            Method method =
                    service.getClass().getMethod("call", String.class, int.class, String.class, String.class,
                            String.class);
            method.invoke(service, host, port, username, password, command);
            method = service.getClass().getMethod("execute");
            return (String) method.invoke(service);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return "Exception";
    }

    /**
     * <p>向指定IP网段执行Windows命令(含执行报告)</p>
     * @param ipPrefix String IP前缀,形如： xxx.xxx.xxx
     * @param begin  int  起始地址
     * @param end    int  终止地址
     * @param port   int  端口
     * @param userName  String 用户名
     * @param password  String 密码
     * @param command   String windows操作系统命令
     * @return       int  执行命令失败的主机数
     */
    public int executeWindowsCmd(String ipPrefix, int begin, int end, String command) {
        return -1;
    }

    /**
     * <p>向指定IP网段执行Windows命令(含执行报告)</p>
     * @param ipPrefix String IP前缀,形如： xxx.xxx.xxx
     * @param begin  int  起始地址
     * @param end    int  终止地址
     * @param port   int  端口
     * @param userName  String 用户名
     * @param password  String 密码
     * @param command   String windows操作系统命令
     * @return       int  执行命令失败的主机数
     */
    public int executeWindowsCmd(String ipPrefix, int begin, int end, int port, String username, String password,
            String command) {
        return -1;
    }

    /**
     * <p>向指定IP网段上传文件(有上传报告)</p>
     * @param ipPrefix  String IP前缀,形如： xxx.xxx.xxx
     * @param begin    int  起始地址
     * @param end      int  终止地址
     * @param port     int  端口
     * @param userName String 用户名
     * @param password String 密码
     * @param remoteDirectory String 远程上传目录
     * @param remoteNewFile String   远程文件名称(重命名时使用)为空时，取上传文件名称
     * @param uploadFile  String 本地上传文件
     * @return       int  执行上传失败的主机数
     */
    public int upload(String context, String remoteDirectory, String remoteNewFile, String uploadFile) {
        // super(ipPrefix, begin, end, port, userName, password, remoteDirectory, remoteNewFile, uploadFile);
        /*
         * File file = new File(uploadFile); if (!file.exists()) { log.debug("上传文件不存在!!!"); return; } if
         * (!file.isFile()) { log.debug("请检查上传文件!!!"); return; } if (begin <0 || end < 0 || begin > end) {
         * log.debug("起始地址或结束地址配置错误!!!!"); return; }
         */
        Object service = null;
        try {
            service = Class.forName("com.regaltec.asip.manager.core.thread.main.SftpUploadMain").newInstance();
            Method method =
                    service.getClass().getMethod("init", String.class, String.class, String.class, String.class);
            method.invoke(service, context, remoteDirectory, remoteNewFile, uploadFile);
            method = service.getClass().getMethod("execute");
            return (Integer) method.invoke(service);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * <p>向指定IP网段上传文件(有上传报告)</p>
     * @param ipPrefix  String IP前缀,形如： xxx.xxx.xxx
     * @param begin    int  起始地址
     * @param end      int  终止地址
     * @param port     int  端口
     * @param userName String 用户名
     * @param password String 密码
     * @param remoteDirectory String 远程上传目录
     * @param remoteNewFile String   远程文件名称(重命名时使用)为空时，取上传文件名称
     * @param uploadFile  String 本地上传文件
     * @return       int  执行上传失败的主机数
     */
    public int download(String context, String remoteDirectory, String downloadFile, String localDirectory) {
        // super(ipPrefix, begin, end, port, userName, password, remoteDirectory, remoteNewFile, uploadFile);
        Object service = null;
        try {
            service = Class.forName("com.regaltec.asip.manager.core.thread.main.SftpDownloadMain").newInstance();
            Method method =
                    service.getClass().getMethod("init", String.class, String.class, String.class, String.class);
            method.invoke(service, context, remoteDirectory, downloadFile, localDirectory);
            method = service.getClass().getMethod("execute");
            return (Integer) method.invoke(service);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * <p>用指定帐号收邮件。</p>
     * @param context
     * @param host
     * @param username
     * @param password
     * @param saveAttachPath
     * @return
     */
    public AsipEmailRecord[] receiveEmail(String host, int port,String username, String password, String saveAttachPath) {
        // super(ipPrefix, begin, end, port, userName, password, remoteDirectory, remoteNewFile, uploadFile);
        Object service = null;
        try {
            service = Class.forName("com.regaltec.asip.manager.api.util.AsipPop3Util").newInstance();
            Method method =
                    service.getClass().getMethod("init", String.class, int.class, String.class, String.class, String.class);
            method.invoke(service,host, port,username, password, saveAttachPath);
            method = service.getClass().getMethod("receive");
            return (AsipEmailRecord[]) method.invoke(service);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p>获取远端服务列表。</p>
     * @param context
     * @param uri
     * @return
     */
    public ArrayList<ArrayList<String>> getRemoteServiceList(String context,String uri) {
        // super(ipPrefix, begin, end, port, userName, password, remoteDirectory, remoteNewFile, uploadFile);
        Object service = null;
        try {
            service = Class.forName("com.regaltec.asip.manager.core.adapter.soapui.SfipSoapUiApapter").newInstance();
            Method method =
                    service.getClass().getMethod("getRemoteServiceList", String.class,String.class);
            method.invoke(service,uri);
            method = service.getClass().getMethod("receive");
            return (ArrayList<ArrayList<String>>) method.invoke(service);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
        
    }
}
