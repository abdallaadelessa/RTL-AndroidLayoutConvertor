package com.abdallaadelessa.rtl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl;

/**
 * 
 * @author Abdalla
 * 
 */
public class RTLConvertor {
	private static final String HORIZONTAL = "horizontal";
	private static final String ANDROID_ORIENTATION = "android:orientation";
	private static final String LINEAR_LAYOUT = "LinearLayout";
	private static final String GRAVITY = "gravity";
	private static final boolean REVERSE_LINEARLAYOUT = true;
	private static final Map<String, String> RTL_MAP = new HashMap<>();
	static {
		RTL_MAP.put("layout_toRightOf", "layout_toLeftOf");
		RTL_MAP.put("layout_toLeftOf", "layout_toRightOf");
		RTL_MAP.put("layout_alignLeft", "layout_alignRight");
		RTL_MAP.put("layout_alignRight", "layout_alignLeft");
		RTL_MAP.put("layout_marginRight", "layout_marginLeft");
		RTL_MAP.put("layout_marginLeft", "layout_marginRight");
		RTL_MAP.put("layout_toEndOf", "layout_toStartOf");
		RTL_MAP.put("layout_toStartOf", "layout_toEndOf");
		RTL_MAP.put("layout_alignStart", "layout_alignEnd");
		RTL_MAP.put("layout_alignEnd", "layout_alignStart");
		RTL_MAP.put("layout_marginEnd", "layout_marginStart");
		RTL_MAP.put("layout_marginStart", "layout_marginEnd");
		RTL_MAP.put("layout_alignParentRight", "layout_alignParentLeft");
		RTL_MAP.put("layout_alignParentLeft", "layout_alignParentRight");
		RTL_MAP.put("layout_alignParentEnd", "layout_alignParentStart");
		RTL_MAP.put("layout_alignParentStart", "layout_alignParentEnd");
		RTL_MAP.put("paddingLeft", "paddingRight");
		RTL_MAP.put("paddingRight", "paddingLeft");
		RTL_MAP.put("paddingStart", "paddingEnd");
		RTL_MAP.put("paddingEnd", "paddingStart");
		RTL_MAP.put("left", "right");
		RTL_MAP.put("right", "left");
		RTL_MAP.put("start", "end");
		RTL_MAP.put("end", "start");
	}

	public static void convertXmlFiles(File[] srcFiles) {
		if (srcFiles != null) {
			for (File file : srcFiles) {
				if (!file.isDirectory()) {
					convertXmlFile(file);
				}
			}
		}
	}

	public static String convertXmlFile(File srcFile) {
		boolean stepOneSuccess = false;
		File destFile = null;
		BufferedReader br = null;
		BufferedWriter out = null;
		try {
			destFile = RTLConvertor.getDestFile(srcFile);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					srcFile)));
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(destFile, false), "UTF8"));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				out.write(convertXmlLine(strLine));
				out.newLine();
			}
			stepOneSuccess = true;
		} catch (Exception e) {
			Helper.logError(e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				Helper.logError(e);
			}
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				Helper.logError(e);
			}
		}
		if (stepOneSuccess && REVERSE_LINEARLAYOUT) {
			convertXmlLinearLayouts(destFile, destFile);
		}
		return destFile != null ? destFile.getPath() : null;
	}

	public static String convertXmlLine(String line) {
		String usedLine = line;
		String convertedLine = line;
		if (!Helper.isStringEmpty(line)) {
			int previousCuts = 0;
			Matcher matcher = Pattern.compile("android:(.*?)=\"(.*?)\"")
					.matcher(line);
			while (matcher.find()) {
				String group = matcher.group();
				String attrName = matcher.group(1);
				String attrValue = matcher.group(2);
				String convertedAttrName = convertXmlAttrName(attrName);
				String convertedAttrValue = convertXmlAttrValue(attrName,
						attrValue);
				int start = usedLine.indexOf(group) + previousCuts;
				int end = (start + group.length());
				String part1 = convertedLine.substring(0, start);
				String part2 = convertedLine.substring(end);
				String convertedAttr = String.format("android:%s=\"%s\"",
						convertedAttrName, convertedAttrValue);
				convertedLine = part1 + convertedAttr + part2;
				previousCuts = (part1 + convertedAttr).length();
				usedLine = part2;
			}
		}
		return convertedLine;
	}

	// ------------------------>

	private static void convertXmlLinearLayouts(File srcFile, File destFile) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(srcFile);
			NodeList childNodes = doc.getElementsByTagName(LINEAR_LAYOUT);
			if (childNodes != null && childNodes.getLength() > 0) {
				for (int i = 0; i < childNodes.getLength(); i++) {
					Node item = childNodes.item(i);
					NamedNodeMap attr = item.getAttributes();
					Node nodeAttr = attr.getNamedItem(ANDROID_ORIENTATION);
					if (nodeAttr == null
							|| nodeAttr.getTextContent().equalsIgnoreCase(
									HORIZONTAL)) {
						reverseChildren(item);
					}
				}
			}
			printXmlDocument(doc, destFile);
		} catch (Exception e) {
			Helper.logError(e);
		}
	}

	private static void reverseChildren(Node parent) {
		NodeList children = parent.getChildNodes();
		if (children.getLength() == 0) {
			return;
		}
		List<Node> nodes = new ArrayList<Node>();
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			nodes.add(n);
		}
		Collections.reverse(nodes);
		for (Node n : nodes) {
			parent.appendChild(n);
		}
	}

	private static void printXmlDocument(Document document, File destPath)
			throws TransformerFactoryConfigurationError, TransformerException {
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		Result output = new StreamResult(destPath);
		Source input = new DOMSource(document);
		transformer.transform(input, output);
	}

	// ------------------------>

	private static String convertXmlAttrName(String attrName) {
		return RTL_MAP.containsKey(attrName) ? RTL_MAP.get(attrName) : attrName;
	}

	private static String convertXmlAttrValue(String attrName, String attrValue) {
		String convertedAttrValue = attrValue;
		if (!Helper.isStringEmpty(attrName) && !Helper.isStringEmpty(attrValue)
				&& attrName.contains(GRAVITY)) {
			String[] gravityArray = { attrValue };
			if (attrValue.contains("|")) {
				gravityArray = attrValue.split("\\|");
			}
			if (gravityArray != null) {
				convertedAttrValue = "";
				for (String gravity : gravityArray) {
					convertedAttrValue += (!Helper
							.isStringEmpty(convertedAttrValue) ? "|" : "")
							+ (RTL_MAP.containsKey(gravity) ? RTL_MAP
									.get(gravity) : gravity);
				}
			}
		}
		return convertedAttrValue;
	}

	// ------------------------>

	private static File getDestFile(File srcFile) throws IOException {
		// TODO Validate that the file has valid xml
		String dirPath = srcFile.getParentFile().getPath() + File.separator
				+ "layout-ar" + File.separator;
		String destPath = dirPath + srcFile.getName();
		File dirFile = new File(dirPath);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		File destFile = new File(destPath);
		if (!destFile.exists()) {
			destFile.createNewFile();
		}
		return destFile;
	}
}
