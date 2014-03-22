package com.regaltec.ida40.asip.client;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;

import com.regaltec.asip.manager.api.client.AsipClientConfig;
import com.regaltec.asip.manager.api.client.AsipClientConfigItem;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
/**
 * 
 * <p>接口平台客户端界面</p>
 * <p>创建日期：2010-9-26 上午11:46:55</p>
 *
 * @author 封加华
 */
public class AsipClientGUI extends javax.swing.JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 3853098207553689325L;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel4;
    private JTextArea responseInfo;
    private JTextArea infParam;
    private JLabel jLabel8;
    private JLabel timeShow;
    private JLabel jLabel11;
    private JScrollPane jScrollPane2;
    private JLabel jLabel10;
    private JTextField serviceName;
    private JLabel jLabel9;
    private JCheckBox isTestFlag;
    private JTextField businessModuleName;
    private JTextField receiveTimeout;
    private JLabel jLabel14;
    private JTextField connectionTimeout;
    private JLabel jLabel13;
    private JLabel jLabel7;
    private JButton exitBtn;
    private JPanel jPanel1;
    private JButton callBtn;
    private JPanel jPanel2;
    private JLabel jLabel6;
    private JLabel jLabel5;
    private JComboBox protocolHandler;
    private JTextField asipUri;
    private JTextField asipPort;
    private JTextField asipIp;
    private JComboBox sender;
    private JLabel jLabel12;
    private JScrollPane jScrollPane1;
    private JLabel jLabel3;
    private static boolean isGuiClose = false;

    private JLabel labelAsipClientConfig;
    private JTextField textAsipClientConfig;

    private String asipIpValue = "";
    private String asipPortValue = "";
    private String asipUriValue = "";
    private String protocolHandlerValue = "";
    private String connectionTimeoutValue = "";
    private String receiveTimeoutValue = "";
    private boolean asipConfigModified = false;
    private String configfileName = "";

    /**
     * 
     * <p>入口函数</p>
     * @param args 初始参数
     */
    public static void main(String[] args) {
        org.apache.log4j.xml.DOMConfigurator.configure("asipclientlog4j.xml");
        Runnable runnable = new Runnable() {
            public void run() {
                AsipClientGUI inst = new AsipClientGUI();
                inst.setLocationRelativeTo(null);
                inst.setVisible(true);
                try {
                    javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }
                SwingUtilities.updateComponentTreeUI(inst);
            }
        };
        // SwingUtilities.invokeLater(runnable);
        try {
            SwingUtilities.invokeAndWait(runnable);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        while (!isGuiClose) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     * 构造函数
     */
    public AsipClientGUI() {
        super();
        this.setTitle("接口平台客户端GUI-v1.0");
        this.setResizable(false);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        initGUI();
        // AsipConfig asipConfig = new AsipClient().getAsipConfig(); // 得到一个默认的接口平台配置
        this.asipIp.setText("127.0.0.1");
        this.asipPort.setText("8000");
        this.asipUri.setText("/asip/services/AsipService");
        this.infParam.setLineWrap(true);
        this.textAsipClientConfig.setText("not load asip-client-config.properties");
    }

    /**
     * 
     * <p>初始化用户界面</p>
     */
    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
            thisLayout.rowHeights = new int[] { 24, 23, 25, 26, 24, -1, 194, 132, 18, 27 };
            thisLayout.columnWeights = new double[] { 0.0, 0.1 };
            thisLayout.columnWidths = new int[] { 122, 7 };
            getContentPane().setLayout(thisLayout);
            {
                jLabel1 = new JLabel();
                getContentPane().add(
                        jLabel1,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                                new Insets(0, 0, 0, 0), 0, 0));
                jLabel1.setText("\u63a5\u53e3\u5e73\u53f0IP\uff1a");
            }
            {
                jLabel2 = new JLabel();
                getContentPane().add(
                        jLabel2,
                        new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                                new Insets(0, 0, 0, 0), 0, 0));
                jLabel2.setText("\u63a5\u53e3\u5e73\u53f0\u7aef\u53e3\uff1a");
            }
            {
                jLabel3 = new JLabel();
                getContentPane().add(
                        jLabel3,
                        new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                                new Insets(0, 0, 0, 0), 0, 0));
                jLabel3.setText("\u63a5\u53e3\u5e73\u53f0Uri\uff1a");
            }
            {
                asipIp = new JTextField();
                getContentPane().add(
                        asipIp,
                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 350), 0, 0));
            }
            {
                asipPort = new JTextField();
                getContentPane().add(
                        asipPort,
                        new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 350), 0, 0));
            }
            {
                asipUri = new JTextField();
                getContentPane().add(
                        asipUri,
                        new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 200), 0, 0));
            }
            {
                jLabel4 = new JLabel();
                getContentPane().add(
                        jLabel4,
                        new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                                new Insets(0, 0, 0, 0), 0, 0));
                jLabel4.setText("\u901a\u8baf\u534f\u8bae\uff1a");
            }

            {
                ComboBoxModel jComboBox1Model = new DefaultComboBoxModel(new String[] {
                        "com.regaltec.ida40.asip.client.HTTPProtocolHandler",
                        "com.regaltec.ida40.asip.client.SOAPProtocolHandler",
                        "com.regaltec.ida40.asip.client.TCPProtocolHandler" });
                protocolHandler = new JComboBox();
                getContentPane().add(
                        protocolHandler,
                        new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 200), 0, 0));
                protocolHandler.setModel(jComboBox1Model);
            }
            {
                jPanel1 = new JPanel();
                jPanel1.setOpaque(false);
                GridBagLayout jPanel1Layout = new GridBagLayout();
                getContentPane().add(
                        jPanel1,
                        new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(3, 3, 3, 10), 0, 0));
                jPanel1.setBorder(new LineBorder(new java.awt.Color(0, 0, 0), 1, false));
                jPanel1Layout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.1 };
                jPanel1Layout.rowHeights = new int[] { 22, 25, 21, 25, 7 };
                jPanel1Layout.columnWeights = new double[] { 0.0, 0.1 };
                jPanel1Layout.columnWidths = new int[] { 103, 7 };
                jPanel1.setLayout(jPanel1Layout);
                {
                    jLabel7 = new JLabel();
                    jPanel1.add(jLabel7, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                            GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                    jLabel7.setText("\u670d\u52a1\u540d\uff1a");
                }
                {
                    jLabel8 = new JLabel();
                    jPanel1.add(jLabel8, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                            GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                    jLabel8.setText("\u6d4b\u8bd5\u6a21\u5f0f\uff1a");
                }
                {
                    businessModuleName = new JTextField();
                    jPanel1.add(businessModuleName, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 0, 0, 150), 0, 0));
                    businessModuleName.setText("predeal");
                }
                {
                    isTestFlag = new JCheckBox();
                    jPanel1.add(isTestFlag, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                            GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                }
                {
                    jLabel9 = new JLabel();
                    jPanel1.add(jLabel9, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                            GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                    jLabel9.setText("\u4e1a\u52a1\u6a21\u5757\u540d\u79f0\uff1a");
                }
                {
                    serviceName = new JTextField();
                    jPanel1.add(serviceName, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 150), 0, 0));
                    serviceName.setText("crmQuery");
                }
                {
                    jLabel10 = new JLabel();
                    jPanel1.add(jLabel10, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                            GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                    jLabel10.setText("\u63a5\u53e3\u53c2\u6570\uff1a");
                }
                {
                    jScrollPane1 = new JScrollPane();
                    jPanel1.add(jScrollPane1, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 8, 10), 0, 0));
                    {
                        infParam = new JTextArea();
                        jScrollPane1.setViewportView(infParam);
                    }
                }
                {
                    jLabel12 = new JLabel();
                    jPanel1.add(jLabel12, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                            GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                    jLabel12.setText("\u53d1\u9001\u8005\uff1a");
                }
                {
                    ComboBoxModel jComboBox1Model = new DefaultComboBoxModel(new String[] { "ida40", "ida30", "ida20",
                            "other" });
                    sender = new JComboBox();
                    jPanel1.add(sender, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 150), 0, 0));
                    sender.setModel(jComboBox1Model);
                }
            }
            {
                jLabel5 = new JLabel();
                getContentPane().add(
                        jLabel5,
                        new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                                new Insets(0, 0, 0, 0), 0, 0));
                jLabel5.setText("\u8bf7\u6c42\u4fe1\u606f\uff1a");
            }
            {
                jLabel6 = new JLabel();
                getContentPane().add(
                        jLabel6,
                        new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                                new Insets(0, 0, 0, 0), 0, 0));
                jLabel6.setText("\u54cd\u5e94\u4fe1\u606f\uff1a");
            }
            {
                jPanel2 = new JPanel();
                jPanel2.setOpaque(false);
                BorderLayout jPanel2Layout = new BorderLayout();
                jPanel2.setLayout(jPanel2Layout);
                getContentPane().add(
                        jPanel2,
                        new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(3, 3, 3, 10), 0, 0));
                {
                    jScrollPane2 = new JScrollPane();
                    jPanel2.add(jScrollPane2, BorderLayout.CENTER);
                    {
                        responseInfo = new JTextArea();
                        jScrollPane2.setViewportView(responseInfo);
                    }
                }
            }
            {
                callBtn = new JButton();
                getContentPane().add(
                        callBtn,
                        new GridBagConstraints(1, 10, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                                new Insets(0, 10, 0, 0), 0, 0));
                callBtn.setText("\u8c03\u7528");
                callBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        callBtnActionPerformed(evt);
                    }
                });
            }
            {
                exitBtn = new JButton();
                getContentPane().add(
                        exitBtn,
                        new GridBagConstraints(1, 10, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                                new Insets(0, 0, 0, 10), 0, 0));
                exitBtn.setText("\u9000\u51fa");
                exitBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        exitBtnActionPerformed(evt);
                    }
                });
            }
            {
                jLabel11 = new JLabel();
                getContentPane().add(
                        jLabel11,
                        new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                                new Insets(0, 0, 0, 10), 0, 0));
                jLabel11.setText("\u8017\u65f6\uff1a");
            }
            {
                timeShow = new JLabel();
                getContentPane().add(
                        timeShow,
                        new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));
                timeShow.setText("0 毫秒");
            }
            {
                jLabel13 = new JLabel();
                getContentPane().add(
                        jLabel13,
                        new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                                new Insets(0, 0, 0, 0), 0, 0));
                jLabel13.setText("\u8fde\u63a5\u8d85\u65f6\uff1a");
            }
            {
                connectionTimeout = new JTextField();
                getContentPane().add(
                        connectionTimeout,
                        new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 200), 0, 0));
                connectionTimeout.setText("1000");
            }
            {
                jLabel14 = new JLabel();
                getContentPane().add(
                        jLabel14,
                        new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                                new Insets(0, 0, 0, 0), 0, 0));
                jLabel14.setText("\u54cd\u5e94\u8d85\u65f6\uff1a");
            }
            {
                receiveTimeout = new JTextField();
                getContentPane().add(
                        receiveTimeout,
                        new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 200), 0, 0));
                receiveTimeout.setText("30000");
            }

            {
                labelAsipClientConfig = new JLabel();
                getContentPane().add(
                        labelAsipClientConfig,
                        new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                                new Insets(0, 0, 0, 0), 0, 0));
                labelAsipClientConfig.setText("AsipClientConfig:");
            }
            {
                textAsipClientConfig = new JTextField();
                getContentPane().add(
                        textAsipClientConfig,
                        new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 3, 0, 10), 0, 0));
            }

            pack();
            this.setSize(690, 580);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * <p>取消按钮事件处理。</p>
     * @param evt 事件
     */
    private void exitBtnActionPerformed(ActionEvent evt) {
        isGuiClose = true;
        this.dispose();
        System.exit(0);
    }

    /**
     * 
     * <p>调用按钮事件处理。</p>
     * @param evt  事件
     */
    private void callBtnActionPerformed(ActionEvent evt) {
        if (!asipIpValue.equals(asipIp.getText())) {
            asipIpValue = asipIp.getText();
            asipConfigModified = true;
        }
        if (!asipPortValue.equals(asipPort.getText())) {
            asipPortValue = asipPort.getText();
            asipConfigModified = true;
        }
        if (!asipUriValue.equals(asipUri.getText())) {
            asipUriValue = asipUri.getText();
            asipConfigModified = true;
        }
        if (!protocolHandlerValue.equals(protocolHandler.getSelectedItem().toString())) {
            protocolHandlerValue = protocolHandler.getSelectedItem().toString();
            asipConfigModified = true;
        }
        if (!connectionTimeoutValue.equals(connectionTimeout.getText())) {
            connectionTimeoutValue = connectionTimeout.getText();
            asipConfigModified = true;
        }
        if (!receiveTimeoutValue.equals(receiveTimeout.getText())) {
            receiveTimeoutValue = receiveTimeout.getText();
            asipConfigModified = true;
        }
        if (!configfileName.equals(this.textAsipClientConfig.getText())) {
            configfileName=this.textAsipClientConfig.getText();
            asipConfigModified = true;
        }

        if (isEmpty(asipIpValue)) {
            JOptionPane.showMessageDialog(this, "接口平台ip不能为空", "输入验证", JOptionPane.OK_OPTION);
            return;
        }
        if (isEmpty(asipPortValue)) {
            JOptionPane.showMessageDialog(this, "接口平台端口不能为空", "输入验证", JOptionPane.OK_OPTION);
            return;
        }
        if (!isNumeric(asipPortValue)) {
            JOptionPane.showMessageDialog(this, "接口平台端口不是一个整数", "输入验证", JOptionPane.OK_OPTION);
            return;
        }
        if (isEmpty(connectionTimeoutValue)) {
            JOptionPane.showMessageDialog(this, "连接超时不能为空", "输入验证", JOptionPane.OK_OPTION);
            return;
        }
        if (!isNumeric(connectionTimeoutValue)) {
            JOptionPane.showMessageDialog(this, "连接超时不是一个整数", "输入验证", JOptionPane.OK_OPTION);
            return;
        }
        if (isEmpty(receiveTimeoutValue)) {
            JOptionPane.showMessageDialog(this, "响应超时不能为空", "输入验证", JOptionPane.OK_OPTION);
            return;
        }
        if (!isNumeric(receiveTimeoutValue)) {
            JOptionPane.showMessageDialog(this, "响应超时不是一个整数", "输入验证", JOptionPane.OK_OPTION);
            return;
        }

        ProtocolHandler protocolHandlerInstance = null;
        try {
            protocolHandlerInstance = (ProtocolHandler) Class.forName(protocolHandlerValue).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            protocolHandlerInstance = new SOAPProtocolHandler();
            responseInfo.setText(protocolHandlerValue + "协议不可用自动转换成" + SOAPProtocolHandler.class.getPackage().getName()
                    + ".SOAPProtocolHandler");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            protocolHandlerInstance = new SOAPProtocolHandler();
            responseInfo.setText(protocolHandlerValue + "协议不可用自动转换成" + SOAPProtocolHandler.class.getPackage().getName()
                    + ".SOAPProtocolHandler");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            protocolHandlerInstance = new SOAPProtocolHandler();
            responseInfo.setText(protocolHandlerValue + "协议不可用自动转换成" + SOAPProtocolHandler.class.getPackage().getName()
                    + ".SOAPProtocolHandler");
        }
        if (asipConfigModified) {
            AsipClientConfig
                    .loadConfig(configfileName, asipIpValue, Integer.parseInt(asipPortValue),
                            asipUriValue, Integer.valueOf(connectionTimeoutValue), Integer
                                    .valueOf(receiveTimeoutValue), "", 0, "");
            asipConfigModified = false;
        }
        final AsipClient asipClient = new AsipClient(protocolHandlerInstance);

        final String businessModuleNameValue = businessModuleName.getText();
        String serviceNameValue = serviceName.getText();
        boolean isTestFlagValue = isTestFlag.isSelected();
        String dataInfo = infParam.getText();
        final RequestMessage requestMessage = new RequestMessage(serviceNameValue, dataInfo);
        requestMessage.setSimulateFlag(isTestFlagValue);
        requestMessage.setSender(sender.getSelectedItem().toString());
        System.out.println("请求内容：\n" + requestMessage);
        if (isEmpty(businessModuleNameValue)) {
            JOptionPane.showMessageDialog(this, "业务模块名称不能为空", "输入验证", JOptionPane.OK_OPTION);
            return;
        }
        if (isEmpty(serviceNameValue)) {
            JOptionPane.showMessageDialog(this, "服务名称不能为空", "输入验证", JOptionPane.OK_OPTION);
            return;
        }
        if (isEmpty(dataInfo)) {
            JOptionPane.showMessageDialog(this, "接口参数不能为空", "输入验证", JOptionPane.OK_OPTION);
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ResponseMessage responseMessage = null;
                long startTime = System.currentTimeMillis();
                responseMessage = asipClient.call(businessModuleNameValue, requestMessage);
                System.out.println("响应内容：\n" + responseMessage.toString());
                long endTime = System.currentTimeMillis();
                timeShow.setText(String.valueOf(endTime - startTime) + " 毫秒");
                if (!responseMessage.isFault()) {
                    responseInfo.setText(responseMessage.toString());
                } else {
                    responseInfo.setText("接口调用出错，请根据以下信息分析原因。\n接口平台错误码：" + responseMessage.getErrorCode()
                            + "\n接口平台错误描述：" + responseMessage.getErrorInfo());
                }
            }
        });
    }

    /**
     * 判断<b>对象</b>和<b>内容</b>是否为空，参数可以传任意类型。
     * @param obj 任意具有空特征的对象
     * @return 返回boolean标识
     */
    @SuppressWarnings("unchecked")
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof String) {
            return ((String) obj).equals("");
        } else if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        } else if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        } else {
            return false;
        }
    }

    /**
     * 
     *  判断<b>对象</b>和<b>内容</b>是否不为空，参数可以传任意类型。
     * @param obj 任意具有空特征的对象
     * @return 返回boolean标识
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 
     * <p>验证是否是一个数字</p>
     * @param str 字符
     * @return boolean
     */
    private static boolean isNumeric(String str) {
        if (str.matches("\\d*")) {
            return true;
        } else {
            return false;
        }
    }
}
