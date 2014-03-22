/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.service;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * <p>asip代理综调向外发布的服务</p>
 * <p>创建日期：2010-9-19 下午06:24:15</p>
 *
 * @author yihaijun
 */
@WebService
public interface AsigService {

   /**
     * <p>asip代理综调向外发布的服务</p>
     * @param inputXml 服务请求参数组
     * @return xml字符串，它和inputXml 格式相同
     */
    @WebResult(name = "text")
    public String call(@WebParam(name = "text") String inputXml);

   /**
     * <p>asip代理综调向外发布的服务</p>
     * @param inputXml 服务请求参数组
     * @return xml字符串，它和inputXml 格式相同
     */
    @WebResult(name = "text")
    public String executeXML(@WebParam(name = "text") String inputXml);

   /**
     * <p>asip代理综调向旧接口平台发布的服务 </p>
     * @param inputXml 服务请求参数组
     * @return xml字符串，它和inputXml 格式相同
     */
    @WebResult(name = "text")
    public String faultSend(@WebParam(name = "text") String inputXml);
    /**
    *
    * <p>集团派单</p>
    * @param inputXml 传入的xml串
    * @return 统一格式返回
    * @throws BOException 业务处理异常
    */
    @WebResult(name = "text")
    public String blocDispatchBill(@WebParam(name = "text")String inputXml);

    /**
    *
    * <p>变更接口</p>
    * @param inputXml 传入的xml串
    * @return 统一格式返回
    * @throws BOException 业务处理异常
    */
    @WebResult(name = "text")
    public String blocUpdateInfo(@WebParam(name = "text")String inputXml);

    /**
    *
    * <p>供集团系统调用的挂起接口</p>
    * @param inputXml 传入的xml串
    * @return 统一格式返回
    * @throws BOException 业务处理异常
    */
    @WebResult(name = "text")
    public String blocHungup(@WebParam(name = "text")String inputXml);

    /**
    *
    * <p>供集团系统调用的解挂接口</p>
    * @param inputXml 传入的xml串
    * @return 统一格式返回
    * @throws BOException 业务处理异常
    */
    @WebResult(name = "text")
    public String blocHungupRelieve(@WebParam(name = "text")String inputXml);

       /**
    *
    * <p>供集团系统调用的结单接口</p>
    * @param inputXml 传入的xml串
    * @return 统一格式返回
    * @throws BOException 业务处理异常
    */
    @WebResult(name = "text")
    public String blocBillFinish(@WebParam(name = "text")String inputXml);

    /**
    *
    * <p>供集团系统调用的交接接口</p>
    * @param inputXml 传入的xml串
    * @return 统一格式返回
    * @throws BOException 业务处理异常
    */
    @WebResult(name = "text")
    public String blocHandOver(@WebParam(name = "text")String inputXml);

    /**
    *
    * <p>供集团系统调用的退单接口</p>
    * @param inputXml 传入的xml串
    * @return 统一格式返回
    * @throws BOException 业务处理异常
    */
    @WebResult(name = "text")
    public String blocBillReject(@WebParam(name = "text")String inputXml);

    /**
    *
    * <p>供集团系统调用的业务恢复接口</p>
    * @param inputXml 传入的xml串
    * @return 统一格式返回
    * @throws BOException 业务处理异常
    */
    @WebResult(name = "text")
    public String blocBusinessResume(@WebParam(name = "text")String inputXml);
}
