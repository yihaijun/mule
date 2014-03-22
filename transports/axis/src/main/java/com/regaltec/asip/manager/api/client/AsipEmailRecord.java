/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2013</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.manager.api.client;

import java.io.Serializable;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2013-1-4 下午03:22:02</p>
 *
 * @author yihaijun
 */
public class AsipEmailRecord implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -6843883830841402980L;

    private String subject = "";
    private String sentDate = "";
    private boolean replySign = false;
    private String hasRead = "";
    private boolean containAttachment = false;
    private String from = "";
    private String to = "";
    private String cc = "";
    private String bcc = "";
    private String messageId = "";
    private String bodyContent = "";
    private boolean newEmail = true;
    private String attachment = "";

    private String appId = "";
    private String itemId = "";
    private String affixFileServerIp = "";
    private String queueSubPath = "";
    private String reader = "";
    private String mark = "";
    private String host = "";
    private int port = 0;
    private String username = "";
    private String password = "";

    public AsipEmailRecord() {

    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the sentdate
     */
    public String getSentDate() {
        return sentDate;
    }

    /**
     * @param sentdate the sentdate to set
     */
    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    /**
     * @return the replysign
     */
    public boolean getReplySign() {
        return replySign;
    }

    /**
     * @param replysign the replysign to set
     */
    public void setReplySign(boolean replySign) {
        this.replySign = replySign;
    }

    /**
     * @return the hasRead
     */
    public String getHasRead() {
        return hasRead;
    }

    /**
     * @param hasRead the hasRead to set
     */
    public void setHasRead(String hasRead) {
        this.hasRead = hasRead;
    }

    /**
     * @return the containAttachment
     */
    public boolean getContainAttachment() {
        return containAttachment;
    }

    /**
     * @param containAttachment the containAttachment to set
     */
    public void setContainAttachment(boolean containAttachment) {
        this.containAttachment = containAttachment;
    }

    /**
     * @return the form
     */
    public String getFrom() {
        return from;
    }

    /**
     * @return the to
     */
    public String getTo() {
        return to;
    }

    /**
     * @param to the to to set
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * @return the cc
     */
    public String getCc() {
        return cc;
    }

    /**
     * @param cc the cc to set
     */
    public void setCc(String cc) {
        this.cc = cc;
    }

    /**
     * @return the bcc
     */
    public String getBcc() {
        return bcc;
    }

    /**
     * @param bcc the bcc to set
     */
    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @param messageId the messageId to set
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * @return the bodycontent
     */
    public String getBodyContent() {
        return bodyContent;
    }

    /**
     * @param bodycontent the bodycontent to set
     */
    public void setBodyContent(String bodyContent) {
        this.bodyContent = bodyContent;
    }

    /**
     * @return the newEmail
     */
    public boolean isNewEmail() {
        return newEmail;
    }

    /**
     * @param newEmail the newEmail to set
     */
    public void setNewEmail(boolean newEmail) {
        this.newEmail = newEmail;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * @return the attachment
     */
    public String getAttachment() {
        return attachment;
    }

    /**
     * @param attachment the attachment to set
     */
    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    /**
     * @return the appId
     */
    public String getAppId() {
        return appId;
    }

    /**
     * @param appId the appId to set
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * @return the itemId
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * @param itemId the itemId to set
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * @return the affixFileServerIp
     */
    public String getAffixFileServerIp() {
        return affixFileServerIp;
    }

    /**
     * @param affixFileServerIp the affixFileServerIp to set
     */
    public void setAffixFileServerIp(String affixFileServerIp) {
        this.affixFileServerIp = affixFileServerIp;
    }

    /**
     * @return the queueSubPath
     */
    public String getQueueSubPath() {
        return queueSubPath;
    }

    /**
     * @param queueSubPath the queueSubPath to set
     */
    public void setQueueSubPath(String queueSubPath) {
        this.queueSubPath = queueSubPath;
    }

    /**
     * @return the reader
     */
    public String getReader() {
        return reader;
    }

    /**
     * @param reader the reader to set
     */
    public void setReader(String reader) {
        this.reader = reader;
    }

    /**
     * @return the mark
     */
    public String getMark() {
        return mark;
    }

    /**
     * @param mark the mark to set
     */
    public void setMark(String mark) {
        this.mark = mark;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public String toText() {
        StringBuffer out = new StringBuffer();
        out.delete(0, out.length());

        out.append("\r\nsubject=" + getSubject());
        out.append("\r\nsentDate=" + getSentDate());
        out.append("\r\nreplySign=" + getReplySign());
        out.append("\r\nhasRead=" + isNewEmail());
        out.append("\r\nfrom=" + getFrom());
        out.append("\r\nto=" + getTo());
        out.append("\r\ncc=" + getCc());
        out.append("\r\nbcc=" + getBcc());
        out.append("\r\nmessageID=" + getMessageId());
        out.append("\r\nbodycontent: \r\n" + getBodyContent());
        out.append("\r\nattachment=" + getAttachment());
        return out.toString();
    }

    public String[] toStringArry() {
        String[] out =
                { getAppId(), getItemId(), getMessageId(), getQueueSubPath(), getHost(), "" + getPort(), getUsername(),
                        getPassword(), getFrom(), getTo(), getCc(), getBcc(), getSubject(), getSentDate(), "未知",
                        getBodyContent(), getAffixFileServerIp(), getAttachment(), getReader(), getMark() };
        return out;
    }

    public String[] toStringArryTitle() {
        String[] excelTitle =
                { "系统ID", "项目ID", "邮件序列号", "子文件夹", "邮件服务器IP", "邮件服务器端口", "邮件服务帐号", "邮件服务密码", "发件人", "收件人", "抄送", "暗送",
                        "标题", "日期", "正文格式", "正文内容", "文件服务器IP", "附件", "阅读器", "处理标记" };
        return excelTitle;
    }
}
