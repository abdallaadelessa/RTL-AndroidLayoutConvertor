package com.abdallaadelessa.rtl.lib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	private static final String XML_EXTENSION = ".xml";
	private static final String RADIO_GROUP = "RadioGroup";
	private static final String PERCENT_FRAME_LAYOUT = "android.support.percent.PercentFrameLayout";
	private static final String TEXT_VIEW = "TextView";
	private static final String EDIT_TEXT = "EditText";
	private static final String CENTER_HORIZONTAL = "center_horizontal";
	private static final String CENTER = "center";
	private static final String FRAME_LAYOUT = "FrameLayout";
	private static final String HORIZONTAL = "horizontal";
	private static final String ANDROID_ORIENTATION = "android:orientation";
	private static final String ANDROID_GRAVITY = "android:gravity";
	private static final String ANDROID_LAYOUT_GRAVITY = "android:layout_gravity";
	private static final String LINEAR_LAYOUT = "LinearLayout";
	private static final String GRAVITY = "gravity";
	private static final String UTF8 = "UTF8";
	// ---->
	public static final int MODE_RTL = 0;
	public static final int MODE_LTR = 1;
	private static int mode = MODE_RTL;
	private static boolean reverseLinearLayout = false;
	private static Set<String> textViewClassNames = new HashSet<>();
	private static Set<String> frameClassNames = new HashSet<>();
	private static Set<String> linearLayoutClassNames = new HashSet<>();
	private static final Map<String, String> RTL_ATTR_MAP = new HashMap<>();
	private static final Map<String, String> RTL_GRAVITY_MAP = new HashMap<>();
	static {
		RTL_ATTR_MAP.put("layout_toRightOf", "layout_toLeftOf");
		RTL_ATTR_MAP.put("layout_toLeftOf", "layout_toRightOf");
		RTL_ATTR_MAP.put("layout_alignLeft", "layout_alignRight");
		RTL_ATTR_MAP.put("layout_alignRight", "layout_alignLeft");
		RTL_ATTR_MAP.put("layout_marginRight", "layout_marginLeft");
		RTL_ATTR_MAP.put("layout_marginLeft", "layout_marginRight");
		RTL_ATTR_MAP.put("layout_toEndOf", "layout_toStartOf");
		RTL_ATTR_MAP.put("layout_toStartOf", "layout_toEndOf");
		RTL_ATTR_MAP.put("layout_alignStart", "layout_alignEnd");
		RTL_ATTR_MAP.put("layout_alignEnd", "layout_alignStart");
		RTL_ATTR_MAP.put("layout_marginEnd", "layout_marginStart");
		RTL_ATTR_MAP.put("layout_marginStart", "layout_marginEnd");
		RTL_ATTR_MAP.put("layout_alignParentRight", "layout_alignParentLeft");
		RTL_ATTR_MAP.put("layout_alignParentLeft", "layout_alignParentRight");
		RTL_ATTR_MAP.put("layout_alignParentEnd", "layout_alignParentStart");
		RTL_ATTR_MAP.put("layout_alignParentStart", "layout_alignParentEnd");
		RTL_ATTR_MAP.put("paddingLeft", "paddingRight");
		RTL_ATTR_MAP.put("paddingRight", "paddingLeft");
		RTL_ATTR_MAP.put("paddingStart", "paddingEnd");
		RTL_ATTR_MAP.put("paddingEnd", "paddingStart");
		RTL_ATTR_MAP.put("layout_marginEndPercent", "layout_marginStartPercent");
		RTL_ATTR_MAP.put("layout_marginRightPercent", "layout_marginLeftPercent");
		RTL_ATTR_MAP.put("layout_marginStartPercent", "layout_marginEndPercent");
		RTL_ATTR_MAP.put("layout_marginLeftPercent", "layout_marginRightPercent");
		RTL_GRAVITY_MAP.put("left", "right");
		RTL_GRAVITY_MAP.put("right", "left");
		RTL_GRAVITY_MAP.put("start", "end");
		RTL_GRAVITY_MAP.put("end", "start");
		RTL_ATTR_MAP.putAll(RTL_GRAVITY_MAP);
		// ---->
		textViewClassNames.add(TEXT_VIEW);
		textViewClassNames.add(EDIT_TEXT);
		frameClassNames.add(FRAME_LAYOUT);
		frameClassNames.add(PERCENT_FRAME_LAYOUT);
		linearLayoutClassNames.add(LINEAR_LAYOUT);
		linearLayoutClassNames.add(RADIO_GROUP);
	}

	// ------------------------>

	public static void setMode(int mode) {
		RTLConvertor.mode = mode;
	}

	public static void setReverseLinearLayout(boolean reverseLinearLayout) {
		RTLConvertor.reverseLinearLayout = reverseLinearLayout;
	}

	public static void addTextViewClass(String fullClassName) {
		if (!Helper.isStringEmpty(fullClassName)) {
			textViewClassNames.add(fullClassName);
		}
	}

	public static void addFrameLayoutClass(String fullClassName) {
		if (!Helper.isStringEmpty(fullClassName)) {
			frameClassNames.add(fullClassName);
		}
	}

	public static void addLinearLayoutClass(String fullClassName) {
		if (!Helper.isStringEmpty(fullClassName)) {
			linearLayoutClassNames.add(fullClassName);
		}
	}

	// ------------------------>

	public static String convertXmlFiles(File srcDir, File destDir) throws Exception {
		if (srcDir != null) {
			File[] listFiles = srcDir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(XML_EXTENSION);
				}
			});
			for (File file : listFiles) {
				convertXmlFile(file, Helper.getDestFile(file, destDir));
			}
		}
		return destDir != null ? destDir.getPath() : null;
	}

	public static String convertXmlFile(File srcFile, File destFile) throws Exception {
		BufferedReader br = null;
		BufferedWriter out = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile)));
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFile, false), UTF8));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				out.write(convertXmlLine(strLine));
				out.newLine();
			}
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
		updateXmlProperties(destFile, destFile);
		return destFile != null ? destFile.getPath() : null;
	}

	private static String convertXmlLine(String line) {
		String usedLine = line;
		String convertedLine = line;
		if (!Helper.isStringEmpty(line)) {
			int previousCuts = 0;
			Matcher matcher = Pattern.compile("android:(.*?)=\"(.*?)\"").matcher(line);
			while (matcher.find()) {
				String group = matcher.group();
				String attrName = matcher.group(1);
				String attrValue = matcher.group(2);
				String convertedAttrName = convertXmlAttrName(attrName);
				String convertedAttrValue = convertXmlAttrValue(attrName, attrValue);
				int start = usedLine.indexOf(group) + previousCuts;
				int end = (start + group.length());
				String part1 = convertedLine.substring(0, start);
				String part2 = convertedLine.substring(end);
				String convertedAttr = String.format("android:%s=\"%s\"", convertedAttrName, convertedAttrValue);
				convertedLine = part1 + convertedAttr + part2;
				previousCuts = (part1 + convertedAttr).length();
				usedLine = part2;
			}
		}
		return convertedLine;
	}

	// ------------------------>

	private static void updateXmlProperties(File srcFile, File destFile) throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(srcFile);
		for (String className : linearLayoutClassNames) {
			updateLinearLayout(doc, className);
		}
		for (String className : frameClassNames) {
			updateFrameLayout(doc, className);
		}
		for (String className : textViewClassNames) {
			updateNodeListGravity(doc.getElementsByTagName(className));
		}
		printXmlDocument(doc, destFile);
	}

	private static void updateLinearLayout(Document doc, String linearLayoutClassName) {
		NodeList nodes = doc.getElementsByTagName(linearLayoutClassName);
		if (nodes != null && nodes.getLength() > 0) {
			for (int i = 0; i < nodes.getLength(); i++) {
				Node item = nodes.item(i);
				NamedNodeMap attr = item.getAttributes();
				// Check Orientation
				Node orientationAttr = attr.getNamedItem(ANDROID_ORIENTATION);
				if (orientationAttr == null
						|| orientationAttr.getTextContent().equalsIgnoreCase(HORIZONTAL) && reverseLinearLayout) {
					reverseChildren(item);
				}
				// Add Gravity Support
				addDirectionGravitySupport(ANDROID_GRAVITY, item);
			}
		}
	}

	private static void updateFrameLayout(Document doc, String frameLayoutClassName) {
		NodeList nodes = doc.getElementsByTagName(frameLayoutClassName);
		if (nodes != null && nodes.getLength() > 0) {
			for (int i = 0; i < nodes.getLength(); i++) {
				Node item = nodes.item(i);
				// Add Layout Gravity Support to children
				if (item != null && item.getChildNodes() != null && item.getChildNodes().getLength() > 0) {
					for (int j = 0; j < item.getChildNodes().getLength(); j++) {
						Node childItem = item.getChildNodes().item(j);
						addDirectionGravitySupport(ANDROID_LAYOUT_GRAVITY, childItem);
					}
				}
			}
		}
	}

	private static void updateNodeListGravity(NodeList nodes) {
		if (nodes != null && nodes.getLength() > 0) {
			for (int i = 0; i < nodes.getLength(); i++) {
				Node item = nodes.item(i);
				addDirectionGravitySupport(ANDROID_GRAVITY, item);
			}
		}
	}

	private static void addDirectionGravitySupport(String attrName, Node item) {
		if (item != null && item.getNodeType() == Node.ELEMENT_NODE) {
			Node GravityAttr = item.getAttributes() != null ? item.getAttributes().getNamedItem(attrName) : null;
			if (GravityAttr == null) {
				((Element) item).setAttribute(attrName, mode == MODE_RTL ? "right|end" : "left|start");
			} else {
				List<String> gravityArray = new ArrayList<>(
						Arrays.asList(getGravityArray(GravityAttr.getTextContent())));
				if (gravityArray != null && !gravityArray.contains(CENTER)
						&& !gravityArray.contains(CENTER_HORIZONTAL)) {
					boolean containsRightOrLeftGravity = false;
					for (String gravity : gravityArray) {
						if (RTL_GRAVITY_MAP.keySet().contains(gravity)) {
							containsRightOrLeftGravity = true;
						}
					}
					if (!containsRightOrLeftGravity) {
						gravityArray.add(mode == MODE_RTL ? "left" : "right");
						gravityArray.add(mode == MODE_RTL ? "start" : "end");
						((Element) item).setAttribute(attrName,
								convertGravityArrayToString(gravityArray.toArray(new String[gravityArray.size()])));
					}
				}
			}
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
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		Result output = new StreamResult(destPath);
		Source input = new DOMSource(document);
		transformer.transform(input, output);
	}

	// ------------------------>

	private static String convertXmlAttrName(String attrName) {
		return RTL_ATTR_MAP.containsKey(attrName) ? RTL_ATTR_MAP.get(attrName) : attrName;
	}

	private static String convertXmlAttrValue(String attrName, String attrValue) {
		String convertedAttrValue = attrValue;
		if (!Helper.isStringEmpty(attrName) && !Helper.isStringEmpty(attrValue) && attrName.contains(GRAVITY)) {
			convertedAttrValue = convertGravityArrayToString(getGravityArray(attrValue));
		}
		return convertedAttrValue;
	}

	private static String convertGravityArrayToString(String[] gravityArray) {
		String convertedAttrValue = "";
		if (gravityArray != null) {
			for (String gravity : gravityArray) {
				convertedAttrValue += (!Helper.isStringEmpty(convertedAttrValue) ? "|" : "")
						+ (RTL_ATTR_MAP.containsKey(gravity) ? RTL_ATTR_MAP.get(gravity) : gravity);
			}
		}
		return convertedAttrValue;
	}

	private static String[] getGravityArray(String attrValue) {
		String[] gravityArray = { attrValue };
		if (attrValue.contains("|")) {
			gravityArray = attrValue.split("\\|");
		}
		return gravityArray;
	}

	// ------------------------>

}
