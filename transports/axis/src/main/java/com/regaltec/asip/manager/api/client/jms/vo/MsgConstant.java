package com.regaltec.asip.manager.api.client.jms.vo;

/**
 * <p>JMS Message常数定义</p>
 * <p>创建日期：2011-2-28 10:31:27</p>
 * @author 戈亮锋
 */
public class MsgConstant {
    /**
     * 本机测试环境(127.0.0.1)
     */
    public static final String CONTEXT_LOCALHOST = "localhost";
    
    /**
     * 公司测试环境(172.16.29.104)
     */
    public static final String CONTEXT_ALPHA = "alpha";
    
    /**
     * 江苏测试环境(132.228.169.160 ~ 132.228.169.161)
     */
    public static final String CONTEXT_MANAGER_BETA_JS = "manager_beta_js";
    
    /**
     * 江苏正式环境(132.228.169.165 ~ 132.228.169.169 和 132.228.169.174 ~ 132.228.169.176)
     */
    public static final String CONTEXT_BETA_JS = "beta_js";
    
    /**
     * 苏州正式环境(132.228.169.174)
     */
    public static final String CONTEXT_BETA_SZ = "beta_sz";
    
    /**
     * 江苏??环境(132.228.169.174 ~ 132.228.169.176)
     */
    public static final String CONTEXT_BETA_JS_TMP = "beta_js_tmp";
    
    

	public static  String ASIP_TOPIC_ALARM = "ASIP.TOPIC.ALARM";  		  	   //告警(Topic)
	public static  String ASIP_TOPIC_IFCALL = "ASIP.TOPIC.IFCALL";  		   //接口调用(Topic)
	public static  String ASIP_QUEUE_OPERATION = "ASIP.QUEUE.OPERATION";   		//OAM操作(Queue)

		
	public static final String ASIP_TOPICS[] = {"ASIP.TOPIC.ALARM","ASIP.TOPIC.IFCALL"}; 	//告警、接口调用
	public static final String ASIP_QUEUES[] = {"ASIP.QUEUE.OPERATION"};					//操作
	
	
    public static final int MSG_TYPE_LOG4j_OPENDEBUG = 1;			//打开ASIP Node log4j Debug日志
    public static final int MSG_TYPE_LOG4j_CLOSEDEBUG = 2;			//关闭ASIP Node log4j Debug日志
    public static final int MSG_TYPE_ALARM_CLEAR = 3;				//告警清除
    public static final int MSG_TYPE_ALARM_ACKNOWLEDGE = 4;			//告警确认
    
}