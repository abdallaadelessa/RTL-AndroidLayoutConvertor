package com.abdallaadelessa.rtl;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.awt.event.ActionEvent;
import javax.swing.JToolBar;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.abdallaadelessa.rtl.lib.RTLConvertor;

import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import java.awt.Dialog.ModalExclusionType;
import javax.swing.DefaultComboBoxModel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class HomeScreen {

	private JFrame frame;
	private JTextField txtSrc;
	private JTextField txtDest;
	private File srcFile, destFile;
	private JCheckBox cbReverse;
	private JButton btnStart;
	private JComboBox comboBoxMode;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HomeScreen window = new HomeScreen();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public HomeScreen() {
		initialize();
	}

	public void start() {
		if (validate(true)) {
			try {
				RTLConvertor.setMode(comboBoxMode.getSelectedIndex()==0? RTLConvertor.MODE_RTL:RTLConvertor.MODE_LTR);
				RTLConvertor.setReverseLinearLayout(cbReverse.isSelected());
				// RTLConvertor.addTextViewClass("com.linkdev.soutalkhaleej.views.customWidgets.TextViewWithMovingMarquee");
				// RTLConvertor.addTextViewClass("com.rengwuxian.materialedittext.MaterialEditText");
				if (srcFile.isDirectory()) {
					RTLConvertor.convertXmlFiles(srcFile, destFile);
				} else {
					RTLConvertor.convertXmlFile(srcFile, destFile);
				}
			} catch (Exception e) {
				showErrorMessage("Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public boolean validate(boolean showDialog) {
		boolean isValid = true;
		if (srcFile == null || !srcFile.exists()) {
			isValid = false;
			if (showDialog)
				showErrorMessage("Error", "Please Select a Valid Source Path", JOptionPane.ERROR_MESSAGE);
		} else if (destFile == null || !destFile.exists()) {
			isValid = false;
			if (showDialog)
				showErrorMessage("Error", "Please Select a Valid Destination Folder", JOptionPane.ERROR_MESSAGE);
		} else if (srcFile != null && srcFile.isDirectory()) {
			File[] files = srcFile.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".xml");
				}
			});
			if (files.length == 0) {
				isValid = false;
				if (showDialog)
					showErrorMessage("Error", "Folder doesn't contain any xml files", JOptionPane.ERROR_MESSAGE);
			}
		}
		return isValid;
	}

	private File openPicker(int fileMode, FileNameExtensionFilter extensionFilter) {
		File selectedFile = null;
		JFileChooser fc = new JFileChooser();
		if (extensionFilter != null)
			fc.setFileFilter(extensionFilter);
		fc.setCurrentDirectory(new java.io.File("."));
		fc.setFileSelectionMode(fileMode);
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			selectedFile = fc.getSelectedFile();
		}
		return selectedFile;
	}

	public void showErrorMessage(String title, String message, int messageType) {
		JOptionPane.showMessageDialog(frame, message, "Dialog", messageType);
	}

	// ------------------------>

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		frame.setBounds(100, 100, 724, 254);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel lblNewLabel = new JLabel(
				"<html>A Java tool created to save wasted time in converting any android layout from LTR to RTL and viceversa</html>");
		lblNewLabel.setHorizontalAlignment(SwingConstants.TRAILING);

		JLabel lblSrc = new JLabel("Source Path (Folder or File)");

		txtSrc = new JTextField();
		txtSrc.setColumns(10);

		JLabel lblDestinationPath = new JLabel("Destination Folder");

		btnStart = new JButton("Start");
		btnStart.setEnabled(false);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				start();
			}
		});

		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srcFile = openPicker(JFileChooser.FILES_AND_DIRECTORIES,
						new FileNameExtensionFilter("xml files or folders (*.xml)", "xml"));
				if (srcFile != null) {
					txtSrc.setText(srcFile.getPath());
				}
				btnStart.setEnabled(validate(false));
			}
		});

		txtDest = new JTextField();
		txtSrc.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				srcFile = new File(txtSrc.getText());
				btnStart.setEnabled(validate(false));
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				srcFile = new File(txtSrc.getText());
				btnStart.setEnabled(validate(false));
				
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				srcFile = new File(txtSrc.getText());
				btnStart.setEnabled(validate(false));
		
			}
		});
		txtDest.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				destFile = new File(txtDest.getText());
				btnStart.setEnabled(validate(false));
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				destFile = new File(txtDest.getText());
				btnStart.setEnabled(validate(false));
				
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				destFile = new File(txtDest.getText());
				btnStart.setEnabled(validate(false));
		
			}
		});
		txtDest.setColumns(10);

		JButton button = new JButton("Browse");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				destFile = openPicker(JFileChooser.DIRECTORIES_ONLY, null);
				if (destFile != null) {
					txtDest.setText(destFile.getPath());
				}
				btnStart.setEnabled(validate(false));
			}
		});

		cbReverse = new JCheckBox("Reverse LinearLayouts");
		cbReverse.setSelected(true);
		
		comboBoxMode = new JComboBox();
		comboBoxMode.setModel(new DefaultComboBoxModel(new String[] {"RTL", "LTR"}));

		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(26)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblNewLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 526, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblSrc, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblDestinationPath, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(txtDest, GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(cbReverse, GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(comboBoxMode, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
									.addGap(34)
									.addComponent(btnStart, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addComponent(txtSrc, GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(button, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnBrowse, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))))
					.addGap(50))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
					.addGap(7)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSrc)
						.addComponent(txtSrc, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBrowse))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDestinationPath)
						.addComponent(txtDest, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(button))
					.addGap(13)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(cbReverse)
						.addComponent(btnStart, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBoxMode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(50, Short.MAX_VALUE))
		);
		frame.getContentPane().setLayout(groupLayout);
	}
}
