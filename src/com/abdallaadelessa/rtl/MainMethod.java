package com.abdallaadelessa.rtl;

import java.io.File;

public class MainMethod {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			RTLConvertor.setMode(RTLConvertor.MODE_RTL);
			RTLConvertor.setReverseLinearLayout(true);
			RTLConvertor.addTextViewClass("com.abdallaadelessa.demo.views.customWidgets.TextViewWithMovingMarquee");
			RTLConvertor.addTextViewClass("com.rengwuxian.materialedittext.MaterialEditText");
			
			String srcDir = "D:\\Workspace\\Projects\\AndroidDemoProject\\app\\src\\main\\res\\layout";
			String destDir = "D:\\Workspace\\Projects\\AndroidDemoProject\\app\\src\\main\\res\\layout-ar";
			RTLConvertor.convertXmlFiles(new File(srcDir),new File(destDir));
			
		} catch (Exception e) {
			Helper.logError(e);
		}
		// String path = "C:\\Users\\Abdalla\\Desktop\\layout.xml";
		// RTLConvertor.convertXmlFile(new File(path));

		// System.out.println(RTLConvertor.convertXmlLine("android:layout_alignTop=\"@+id/tvAddress\""
		// + "  android:gravity=\"top|left|start\""));
	}
}
