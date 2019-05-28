package com.mypackage;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class FileOperation {

	protected boolean saved;
	private boolean newFileFlag;
	private String fileName;
	private String applicationTitle = "Notepad - Javapad";

	private Notepad notepad;
	private File file;
	private JFileChooser fileChooser;

	/////////////////////////////////////////////////////////

	public boolean isSaved() {
		return saved;
	}

	public void setSaved(boolean saved) {
		this.saved = saved;
	}

	public String getFileName() {
		return new String(fileName);
	}

	public void setFileName(String fileName) {
		this.fileName = new String(fileName);
	}

	/////////////////////////////////////////////////////////

	/******************************
	 * FileOperation Constructor
	 *******************************/

	public FileOperation(Notepad notepad) {
		this.notepad = notepad;
		saved = true;
		newFileFlag = true;
		fileName = new String("Untitled");
		file = new File(fileName);
		this.notepad.frame.setTitle(fileName + " - " + applicationTitle);

		fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return null;
			}

			@Override
			public boolean accept(File f) {
				return false;
			}
		});

		fileChooser.addChoosableFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return null;
			}

			@Override
			public boolean accept(File f) {
				return false;
			}
		});
		fileChooser.setCurrentDirectory(new File("."));
	}

	/****************************
	 * End FileOperation Constructor
	 *****************************/

	/********************************
	 * ConfirmSave Method
	 ************************************/

	public boolean confirmSave() {
		String message = "<html>The text in the " + fileName + " file has been changed.<br>"
				+ "Do you want to save the changes?<html>";
		if (!saved) {
			int x = JOptionPane.showConfirmDialog(this.notepad.frame, message, applicationTitle,
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (x == JOptionPane.CANCEL_OPTION)
				return false;
			if (x == JOptionPane.YES_OPTION && !saveAsFile())
				return false;
		}
		return true;
	}

	/******************************
	 * End ConfirmSave Method
	 **********************************/

	/**********************************
	 * NewFile Method
	 **************************************/

	public void newFile() {
		if (!confirmSave())
			return;
		this.notepad.textArea.setText("");
		fileName = new String("Untitled");
		file = new File(fileName);
		saved = true;
		newFileFlag = true;
		this.notepad.frame.setTitle(fileName + " - " + applicationTitle);
	}

	/********************************
	 * End NewFile Method
	 ************************************/

	/**********************************
	 * OpenFile Method
	 *************************************/

	public void openFile() {
		if (!confirmSave())
			return;
		fileChooser.setDialogTitle("Open File...");
		fileChooser.setApproveButtonText("Open this");
		fileChooser.setApproveButtonMnemonic(KeyEvent.VK_O);
		fileChooser.setApproveButtonToolTipText("Click me to open selected file.");

		File tempFile = null;

		do {
			if (fileChooser.showOpenDialog(this.notepad.frame) != JFileChooser.APPROVE_OPTION)
				return;
			tempFile = fileChooser.getSelectedFile();

			if (tempFile.exists())
				break;
			JOptionPane.showMessageDialog(this.notepad.frame,
					"<html>" + tempFile.getName() + "<br> File not found.<br>"
							+ "Please verify the correct file name was given.<html>",
					"Open", JOptionPane.INFORMATION_MESSAGE);
		} while (true);

		this.notepad.textArea.setText("");

		if (!openFile(tempFile)) {
			fileName = "Untitled";
			saved = true;
			this.notepad.frame.setTitle(fileName + " - " + applicationTitle);
		}
		if (!tempFile.canWrite())
			newFileFlag = true;
	}

	private boolean openFile(File tempFile) {
		FileInputStream fin = null;
		BufferedReader br = null;

		try {
			fin = new FileInputStream(tempFile);
			br = new BufferedReader(new InputStreamReader(fin));
			String str = "";
			while (str != null) {
				str = br.readLine();
				if (str == null)
					break;
				this.notepad.textArea.append(str + "\n");
			}
		} catch (FileNotFoundException exception) {
			updateStatus(tempFile, false);
			return false;
		} catch (IOException exception) {
			updateStatus(tempFile, false);
			return false;
		} finally {
			try {
				br.close();
				fin.close();
			} catch (IOException exception) {
				updateStatus(tempFile, true);
				this.notepad.textArea.setCaretPosition(0);
				return true;
			}
		}
		return false;
	}

	/********************************
	 * End OpenFile Method
	 **********************************/

	/**********************************
	 * SaveThisFile Method
	 * 
	 * @return
	 ************************************/

	public boolean saveThisFile() {
		if (!newFileFlag)
			return saveFile(this.file);
		return saveAsFile();
	}

	/********************************
	 * End SaveThisFile Method
	 **********************************/

	/*********************************
	 * SaveAsFile Method
	 ***********************************/

	public boolean saveAsFile() {
		File file = null;
		fileChooser.setDialogTitle("Save As..");
		fileChooser.setApproveButtonText("Save Now");
		fileChooser.setApproveButtonMnemonic(KeyEvent.VK_S);
		fileChooser.setApproveButtonToolTipText("Click me to save!");

		do {
			if (fileChooser.showSaveDialog(this.notepad.frame) != JFileChooser.APPROVE_OPTION)
				return false;
			file = fileChooser.getSelectedFile();
			if (!file.exists())
				break;
			if (JOptionPane.showConfirmDialog(this.notepad.frame,
					"<html>" + file.getPath() + " already exists.<br>Do you want to replace it?<html>", "Save As",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				break;
		} while (true);

		return saveFile(file);
	}

	/*******************************
	 * End SaveAsFile Method
	 *********************************/

	/*********************************
	 * SaveFile Method
	 ***********************************/

	private boolean saveFile(File file) {
		FileWriter fileWriter = null;

		try {
			fileWriter = new FileWriter(file);
			fileWriter.write(notepad.textArea.getText());
		} catch (IOException exception) {
			updateStatus(file, false);
			return false;
		}

		finally {
			try {
				fileWriter.close();
			} catch (IOException exception) {
			}
		}
		updateStatus(file, true);
		return true;
	}

	/*********************************
	 * End SaveFile Method
	 ***********************************/

	/*********************************
	 * UpdateStatus Method
	 ***********************************/

	private void updateStatus(File file, boolean saved) {
		if (saved) {
			this.saved = true;
			fileName = new String(file.getName());
			if (!file.canWrite()) {
				fileName += "(Read Only)";
				this.newFileFlag = true;
			}
			this.file = file;
			notepad.frame.setTitle(fileName + " - " + applicationTitle);
			notepad.statusBar.setText("File: " + file.getPath() + " saved/opened successfully.");
			this.newFileFlag = false;
		} else {
			notepad.statusBar.setText("Failed to save/open : " + file.getPath());
		}
	}

	/*********************************
	 * End UpdateStatus Method
	 ***********************************/

}
