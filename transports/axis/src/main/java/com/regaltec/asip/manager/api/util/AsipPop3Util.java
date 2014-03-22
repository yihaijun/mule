/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2013</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.manager.api.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;

import com.regaltec.asip.manager.api.client.AsipEmailRecord;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2013-1-4 下午12:45:38</p>
 *
 * @author yihaijun
 */
public class AsipPop3Util {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private String host = "";
    private int port = 0;
    private String username = "";
    private String password = "";
    private String saveAttachPath = "";// 附件下载后的存放目录
    private AsipEmailRecord currEmail = null;

    private MimeMessage mimeMessage = null;
    private StringBuffer bodytext = new StringBuffer();
    // 存放邮件内容的StringBuffer对象
    private String dateformat = "yy-MM-dd HH:mm";// 默认的日前显示格式
    private StringBuffer attachmentList = new StringBuffer();
    private int attachmentIndex = 0;

    /**
     * 构造函数,初始化一个MimeMessage对象
     */
    public AsipPop3Util() {
    }

    public int init(String host, int port, String username, String password, String saveAttachPath) {
        setHost(host);
        setPort(port);
        setUsername(username);
        setPassword(password);
        setSaveAttachPath(saveAttachPath);
        return 0;
    }

    /**
     * 获得发件人的地址和姓名
     */
    public String getFrom() throws Exception {
        InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();
        String from = address[0].getAddress();
        if (from == null)
            from = "";
        String personal = address[0].getPersonal();
        if (personal == null)
            personal = "";
        String fromaddr = personal + "<" + from + ">";
        return fromaddr;
    }

    /**
    * 获得邮件的收件人，抄送，和密送的地址和姓名，根据所传递的参数的不同
    * "to"----收件人 "cc"---抄送人地址 "bcc"---密送人地址
    */

    public String getMailAddress(String type) throws Exception {
        String mailaddr = "";
        String addtype = type.toUpperCase();
        InternetAddress[] address = null;
        if (addtype.equals("TO") || addtype.equals("CC") || addtype.equals("BCC")) {
            if (addtype.equals("TO")) {
                address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.TO);
            } else if (addtype.equals("CC")) {
                address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.CC);
            } else {
                address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.BCC);
            }
            if (address != null) {
                for (int i = 0; i < address.length; i++) {
                    String email = address[i].getAddress();
                    if (email == null)
                        email = "";
                    else {
                        email = MimeUtility.decodeText(email);
                    }
                    String personal = address[i].getPersonal();
                    if (personal == null)
                        personal = "";
                    else {
                        personal = MimeUtility.decodeText(personal);
                    }
                    String compositeto = personal + "<" + email + ">";
                    mailaddr += "," + compositeto;
                }
                mailaddr = mailaddr.substring(1);
            }
        } else {
            throw new Exception("Error emailaddr type!");
        }
        return mailaddr;
    }

    /**
     * 获得邮件主题
     */

    public String getSubject() throws MessagingException {
        String subject = "";
        try {
            subject = MimeUtility.decodeText(mimeMessage.getSubject());
            if (subject == null)
                subject = "";
        } catch (Exception exce) {
        }
        return subject;
    }

    /**
     * 获得邮件发送日期
     */

    public String getSentDate() throws Exception {
        Date sentdate = mimeMessage.getSentDate();
        SimpleDateFormat format = new SimpleDateFormat(dateformat);
        return format.format(sentdate);
    }

    /**
     * 获得邮件正文内容
     */

    public String getBodyText() {
        return bodytext.toString();
    }

    /**
     * 解析邮件，把得到的邮件内容保存到一个StringBuffer对象中，解析邮件
     * 主要是根据MimeType类型的不同执行不同的操作，一步一步的解析
     */

    public void getMailContent(Part part) throws Exception {
        String contenttype = part.getContentType();
        int nameindex = contenttype.indexOf("name");
        boolean conname = false;
        if (nameindex != -1)
            conname = true;
        logger.debug("CONTENTTYPE: " + contenttype);
        if (part.isMimeType("text/plain") && !conname) {
            bodytext.append((String) part.getContent());
        } else if (part.isMimeType("text/html") && !conname) {
            bodytext.append((String) part.getContent());
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int counts = multipart.getCount();
            for (int i = 0; i < counts; i++) {
                getMailContent(multipart.getBodyPart(i));
            }
        } else if (part.isMimeType("message/rfc822")) {
            getMailContent((Part) part.getContent());
        } else {
        }
    }

    /**
     * 判断此邮件是否需要回执，如果需要回执返回"true",否则返回"false"
     */
    public boolean getReplySign() throws MessagingException {
        boolean replysign = false;
        String needreply[] = mimeMessage.getHeader("Disposition-Notification-To");
        if (needreply != null) {
            replysign = true;
        }
        return replysign;
    }

    /**
     * 获得此邮件的Message-ID
     */
    public String getMessageId() throws MessagingException {
        return mimeMessage.getMessageID();
    }

    /**
     * [判断此邮件是否已读，如果未读返回返回false,反之返回true]
     */
    public boolean isNew() throws MessagingException {
        boolean isnew = false;
        Flags flags = ((Message) mimeMessage).getFlags();
        Flags.Flag[] flag = flags.getSystemFlags();
        logger.debug("flags's length: " + flag.length);
        for (int i = 0; i < flag.length; i++) {
            if (flag[i] == Flags.Flag.SEEN) {
                isnew = true;
                logger.debug("seen Message.......");
                break;
            }
        }
        return isnew;
    }

    /**
     * 判断此邮件是否包含附件
     */
    public boolean isContainAttach(Part part) throws Exception {
        boolean attachflag = false;
        String contentType = part.getContentType();
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart mpart = mp.getBodyPart(i);
                String disposition = mpart.getDisposition();
                if ((disposition != null)
                        && ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE))))
                    attachflag = true;
                else if (mpart.isMimeType("multipart/*")) {
                    attachflag = isContainAttach((Part) mpart);
                } else {
                    String contype = mpart.getContentType();
                    if (contype.toLowerCase().indexOf("application") != -1)
                        attachflag = true;
                    if (contype.toLowerCase().indexOf("name") != -1)
                        attachflag = true;
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            attachflag = isContainAttach((Part) part.getContent());
        }
        return attachflag;
    }

    /**
     * [保存附件]
     */

    public void saveAttachMent(Part part) throws Exception {
        String fileName = "";
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart mpart = mp.getBodyPart(i);
                String disposition = mpart.getDisposition();
                if ((disposition != null)
                        && ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE)))) {
                    fileName = mpart.getFileName();
                    if ((fileName != null)
                            && ((fileName.toLowerCase().indexOf("gb2312") != -1) || (fileName.toLowerCase().indexOf(
                                    "gbk") != -1))) {
                        fileName = MimeUtility.decodeText(fileName);
                    }
                    saveFile(fileName, mpart.getInputStream());
                } else if (mpart.isMimeType("multipart/*")) {
                    saveAttachMent(mpart);
                } else {
                    fileName = mpart.getFileName();
                    if ((fileName != null)
                            && ((fileName.toLowerCase().indexOf("gb2312") != -1) || (fileName.toLowerCase().indexOf(
                                    "gbk") != -1))) {
                        fileName = MimeUtility.decodeText(fileName);
                        saveFile(fileName, mpart.getInputStream());
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachMent((Part) part.getContent());
        }
    }

    /**
     * [设置附件存放路径]
     */

    public void setAttachPath(String saveAttachPath) {
        String osName = System.getProperty("os.name");
        if (osName == null)
            osName = "";
        if (osName.toLowerCase().indexOf("win") != -1) {
            if (saveAttachPath == null || saveAttachPath.equals("")) {
                saveAttachPath = "c:\\tmp\\";
            }
        } else {
            if (saveAttachPath == null || saveAttachPath.equals("")) {
                saveAttachPath = "/tmp/";
            }
        }
        if (!saveAttachPath.equals("/") && saveAttachPath.equals("\\")) {
            saveAttachPath = saveAttachPath + "/";
        }
        this.saveAttachPath = saveAttachPath;
    }

    /**
     * [设置日期显示格式]
     */

    public void setDateFormat(String format) throws Exception {
        this.dateformat = format;
    }

    /**
     * [获得附件存放路径]
     */

    public String getAttachPath() {
        return saveAttachPath;
    }

    /**
     * [真正的保存附件到指定目录里]
     */

    private void saveFile(String fileName, InputStream in) throws Exception {
        String simpleMessageId = currEmail.getMessageId();
        if (simpleMessageId.indexOf("@") > 0) {
            simpleMessageId = simpleMessageId.substring(0, simpleMessageId.indexOf("@"));
        }
        if (simpleMessageId.indexOf("<") >= 0) {
            simpleMessageId = simpleMessageId.substring(simpleMessageId.indexOf("<") + 1, simpleMessageId.length());
        }
        attachmentIndex++;
        String storefilepath = saveAttachPath + simpleMessageId + "-" + attachmentIndex + "-" + fileName;
        if (attachmentList.toString().length() > 0) {
            attachmentList.append(";");
        }
        attachmentList.append(storefilepath);
        String storefilepath2 = saveAttachPath + simpleMessageId + "-" + attachmentIndex;
        saveFileExecute(storefilepath2, in);
//        saveFileExecute(storefilepath, in);
    }

    private void saveFileExecute(String storefilepath, InputStream in) throws Exception {
        File storefile = new File(storefilepath);
        logger.debug("storefile's path: " + storefile.toString());
        // for(int i=0;storefile.exists();i++){
        // storefile = new File(storedir+separator+fileName+i);
        // }
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(storefile));
            bis = new BufferedInputStream(in);
            int c;
            while ((c = bis.read()) != -1) {
                bos.write(c);
                bos.flush();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("文件保存失败!");
        } finally {
            bos.close();
            bis.close();
        }
    }

    /**
     * AsipPop3Component类测试
     */

    public AsipEmailRecord[] receive() {
        AsipEmailRecord[] emailList = null;
        try {
            Properties props = new Properties();
            // props.setProperty(key, value);
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("pop3");
            if (port <= 0) {
                store.connect(host, username, password);
            } else {
                store.connect(host, port, username, password);
            }
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
            Message message[] = folder.getMessages();
            logger.debug("Messages's length: " + message.length);
            emailList = new AsipEmailRecord[message.length];
            for (int i = 0; i < message.length; i++) {
                mimeMessage = (MimeMessage) message[i];
                mimeMessage.setFlag(Flags.Flag.DELETED, true);
                emailList[i] = new AsipEmailRecord();
                currEmail = emailList[i];
                emailList[i].setSubject(getSubject());
                emailList[i].setSentDate(getSentDate());
                emailList[i].setReplySign(getReplySign());
                emailList[i].setNewEmail(isNew());
                emailList[i].setContainAttachment(isContainAttach((Part) message[i]));
                emailList[i].setFrom(getFrom());
                emailList[i].setTo(getMailAddress("to"));
                emailList[i].setCc(getMailAddress("cc"));
                emailList[i].setBcc(getMailAddress("bcc"));
                emailList[i].setMessageId(getMessageId());
                getMailContent((Part) message[i]);
                emailList[i].setBodyContent(getBodyText());

                File file = new File(this.saveAttachPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                saveAttachMent((Part) message[i]);
                emailList[i].setAttachment(attachmentList.toString());
                logger.debug(emailList[i].toText());
                attachmentList.delete(0, attachmentList.length());
                bodytext.delete(0, bodytext.length());
                attachmentIndex = 0;
            }
            folder.close(true);
            store.close();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (emailList != null) {
            if (emailList.length > 0) {
                logger.info("emailList.length = " + emailList.length);
            } else {
                logger.debug("emailList.length = " + emailList.length);
            }
        } else {
            logger.debug("emailList.length = 0");
        }
        return emailList;
    }

    /**
     * @return the saveAttachPath
     */
    public String getSaveAttachPath() {
        return saveAttachPath;
    }

    /**
     * @param saveAttachPath the saveAttachPath to set
     */
    public void setSaveAttachPath(String saveAttachPath) {
        this.saveAttachPath = saveAttachPath;
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
}
