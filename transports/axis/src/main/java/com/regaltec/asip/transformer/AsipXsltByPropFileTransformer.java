/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2012</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.transformer;

import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.mule.api.transformer.TransformerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.regaltec.asip.utils.PropertiesMapping;

/**
 * <p>
 * 概述该类作用，请详细描述。
 * </p>
 * <p>
 * 创建日期：2012-4-1 下午04:51:58
 * </p>
 * 
 * @author yihaijun
 */
public class AsipXsltByPropFileTransformer extends AsipTransformer {
	private Logger logger = LoggerFactory.getLogger(AsipXsltTransformer.class);

	// 属性文件路径
	private String propFileName = "";
	// 根节点名称。默认为"root"
	private String rootName = "";

	@Override
	public Object doTransform(Object srcData, String encoding)
			throws TransformerException {

		if (isBlank(propFileName)) {
			throw new TransformerException(this, new Exception("属性文件路径为空"));
		}

		Document doc = null;
		try {
			doc = DocumentHelper.parseText((String) srcData);
		} catch (Exception e) {
			throw new TransformerException(this, e);
		}
		PropertiesMapping pm = new PropertiesMapping(propFileName); // 转换配置文件
		Properties properties = pm.getProperties();
		try {
			return doCustTransform(doc, properties);
		} catch (Exception e) {
			throw new TransformerException(this, e);
		}
	}

	/**
	 * <p>
	 * 转换xml文件。
	 * </p>
	 * 
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private String doCustTransform(Document srcDoc, Properties properties)
			throws Exception {

		if (srcDoc == null) {
			throw new Exception("待转换的xml文档为空");
		}
		if (properties == null) {
			throw new Exception("转换配置属性为空");
		}

		/**
		 * 1,条目名称作为转换后XML的节点名称 2，条目值作为xpath表达式。改表达是用逗号分隔，标识是多个节点进行拼接
		 */
		Document newDoc = DocumentHelper.createDocument();
		String rootName = "root";
		if (isNotBlank(this.rootName)) {
			rootName = this.rootName;
		}
		Element root = newDoc.addElement(rootName);
		Set<Entry<Object, Object>> keyMapSet = properties.entrySet();
		for (Entry<Object, Object> entry : keyMapSet) {
			String nodeName = (String) entry.getKey();
			String nodeXpath = (String) entry.getValue();
			if (isNotBlank(nodeName) && isNotBlank(nodeXpath)) {
				String name = nodeName.trim();
				String value = getExperssionValue(srcDoc, nodeXpath);
				int index = name.indexOf(".");
				if (index >= 0) {
					name = name.substring(index + 1);
				}
				Element newElemnet = root.addElement(name);
				newElemnet.setText(value);
			}
		}
		return root.asXML();
	}

	/**
	 * <p>>
	 * 通过xpath获取值
	 * </p>
	 * 
	 * @param doc
	 *            DOM文档
	 * @param xpath
	 *            xpath
	 * @param isMany
	 *            是否多值拼接(多个xpath表达式值进行拼接)
	 * @param separator
	 *            多值分隔符
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String getValueByXpath(Document doc, String xpath, boolean isMany,
			String separator) {
		if (isBlank(xpath)) {
			return "";
		}
		String value = "";
		xpath = xpath.trim();

		if (isMany) {// 多值拼接
			separator = defaultIfEmpty(separator, "");
			xpath = xpath.trim();
			List<Node> nodes = doc.selectNodes(xpath);
			if (nodes != null) {
				for (Node node : nodes) {
					String temp = node.getStringValue();
					temp = temp.trim();
					if (value.length() > 0) {
						value += separator + temp;
					} else {
						value += temp;
					}
				}
			}

		} else { // 取单值。如果是组合表达式则从第一个表达式开始取，取到有值时立即退出循环
			String[] xpaths = null;
			if (xpath.indexOf("|") >= 0) {
				xpaths = xpath.split("\\|");
			} else {
				xpaths = new String[] { xpath };
			}
			top: for (String string : xpaths) {
				List<Node> nodes = null;
				try {
					nodes = doc.selectNodes(string);
				} catch (Exception e) {
					if (logger.isDebugEnabled()) {
						e.printStackTrace();
					}
					logger.error("DOM4j取值异常", e);
				}
				if (nodes != null) {
					for (Node node2 : nodes) {
						if (node2 != null) {
							String temp = node2.getStringValue();
							temp = temp.trim();
							if (isNotBlank(temp)) {
								value = temp;
								break top;
							}
						}
					}
				}
			}
		}
		return value;
	}

	/**
	 * <p>
	 * 获取表达式值
	 * </p>
	 * 目前表达式值支持
	 * 
	 * @param doc
	 * @param express
	 * @return
	 * @throws Exception
	 */
	public String getExperssionValue(Document doc, String express)
			throws Exception {
		if (express.contains("?")) { // 包含问号时，才是取值表达式
			int index = express.indexOf("?");
			String xpathExpress = express.substring(0, index); // xpath 表达式部分
			String valueExpress = express.substring(index + 1); // 取值表达式

			String expressValue = getXmlValue(doc, xpathExpress); // 获取xpath表达式值

			String[] val2vals = valueExpress.split(","); // 取值对逗号分隔
			for (String val2val : val2vals) {
				String[] temp = val2val.split(":", 2); //
				if (temp.length == 2) {
					String exValue = temp[0].trim(); // 匹配值
					String acValue = temp[1].trim(); // 替换值
					if (isNotBlank(exValue) && isNotBlank(acValue)) {
						if ("*".equals(exValue)) { // 为*号，只有有值就返回
							if (isNotBlank(expressValue)) {
								return acValue;
							}
						}
						if ("!*".equals(exValue)) { // 为NULL时，只有无值或空值就返回
							if (isBlank(expressValue)) {
								return acValue;
							}
						}
						if (exValue.equals(expressValue)) { // 一般性比较。
							return acValue;
						}
					}
				}
			}
			return "";
		} else {
			return getXmlValue(doc, express);
		}
	}

	/**
	 * <p>
	 * 功能的简单描述，参数、返回值及异常必须注明。
	 * </p>
	 * 
	 * @param args
	 */
	public void main(String[] args) {
		String aa = "放回第一个值不为空的节点值?";
		System.out.println(aa.contains("?"));
	}

	/**
	 * <p>
	 * 通过属性配置xpath获取DOM文档值。
	 * </p>
	 * 1，可获取单值 2，也可xpath + 逗号 + 分隔符。可进行多值拼接，分隔符可为空。</p>
	 * 
	 * 单值例子： 1,xpath表达式："//adderss|//telphone" 放回第一个值不为空的节点值。 如：
	 * 如果adderss有值，则返回：广州是天河区。adderss值为空时，返回：1380000000 多值拼接例子：
	 * 1，xpath表达式："//adderss|//telphone,;" 拼接多节点值 返回结果：
	 * 广州是天河区;1380000000。如//address有多个节点，同样也会多值进行拼接
	 * 2，xpath表达式："//adderss|//telphone,#" 拼接多节点值 返回结果： 广州是天河区#1380000000
	 * 3，xpath表达式："//adderss|//telphone," 拼接多节点值 返回结果： 广州是天河区1380000000
	 * 
	 * @param doc
	 *            DOM文档
	 * @param xpathExpress
	 *            xpath表达式
	 * @return
	 * @throws Exception
	 */
	private String getXmlValue(Document doc, String xpathExpress)
			throws Exception {
		try {
			if (doc != null && isNotBlank(xpathExpress)) {
				// 是否多值拼接,如果有逗号存在则说明需要进行多值拼接
				if (xpathExpress.indexOf(",") >= 0) {
					String[] xpaths = xpathExpress.split(",", 2);
					String xpath = xpaths[0];
					String separator = "";
					if (xpaths.length > 1) {
						separator = xpaths[1];
					}
					String temp = getValueByXpath(doc, xpath, true, separator);
					if (isNotBlank(temp)) {
						return temp;
					}
				} else { // 单值
					return getValueByXpath(doc, xpathExpress, false, null);
				}
			}
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				e.printStackTrace();
			}
			logger.error("取Xpath值出差", e);
		}
		return "";
	}

	/**
	 * <p>
	 * 获取文档节点值。
	 * </p>
	 * 
	 * @param doc
	 *            文档
	 * @param xpath
	 *            节点路径
	 * @return
	 */
	public String getValueByXpath(Element ele, String xpath) {
		if (ele != null && isNotBlank(xpath)) {
			Node node = ele.selectSingleNode(xpath);
			if (node != null) {
				return node.getText().trim();
			}
		}
		return "";
	}

	/**
	 * <p>
	 * 获取文档节点值。
	 * </p>
	 * 
	 * @param doc
	 *            文档
	 * @param xpath
	 *            节点路径
	 * @return
	 */
	public String getValueByXpath(Document doc, String xpath) {
		if (doc != null && isNotBlank(xpath)) {
			Node node = doc.selectSingleNode(xpath);
			if (node != null) {
				return node.getText().trim();
			}
		}
		return "";
	}

	/**
	 * <p>
	 * 判断字符串是否为空。
	 * </p>
	 * 
	 * @param string
	 * @return
	 */
	private boolean isNotBlank(String string) {
		return string != null && !"".equals(string);
	}

	/**
	 * <p>
	 * 判断字符串为空。
	 * </p>
	 * 
	 * @param string
	 * @return
	 */
	private boolean isBlank(String string) {
		return string == null || "".equals(string);
	}

	/**
	 * <p>
	 * 设置字符串默认值。
	 * </p>
	 * 
	 * @param string
	 * @return
	 */
	private String defaultIfEmpty(String string, String defalut) {
		if (isBlank(string)) {
			return defalut;
		}
		return string;
	}

	/**
	 * @return the propFileName
	 */
	public String getPropFileName() {
		return propFileName;
	}

	/**
	 * @param propFileName
	 *            the propFileName to set
	 */
	public void setPropFileName(String propFileName) {
		this.propFileName = propFileName;
	}

	/**
	 * @return the rootName
	 */
	public String getRootName() {
		return rootName;
	}

	/**
	 * @param rootName
	 *            the rootName to set
	 */
	public void setRootName(String rootName) {
		this.rootName = rootName;
	}

}
