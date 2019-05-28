package com.mypackage;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Date;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.BadLocationException;

public class Notepad implements ActionListener, MenuConstants {

	JFrame frame;
	JTextArea textArea;
	JLabel statusBar;

	private String fileName = "Untitled";
	//private boolean saved = true;
	String applicationName = "javapad";

	String searchString, replaceString;
	int lastSearchIndex;

	FileOperation fileHandler;
//	FontChooser fontDialog=null;  
//	FindDialog findReplaceDialog=null;   
	JColorChooser bcolorChooser = null;
	JColorChooser fcolorChooser = null;
	JDialog backgroundDialog = null;
	JDialog foregroundDialog = null;
	JMenuItem cutItem, copyItem, deleteItem, findItem, findNextItem, replaceItem, gotoItem, selectAllItem;

	/********************* Notepad Constructor *********************************/

	Notepad() {
		frame = new JFrame(fileName + " - " + applicationName);
		textArea = new JTextArea(30, 60);
		statusBar = new JLabel("||       Ln 1, Col 1 ", JLabel.RIGHT);
		frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
		frame.add(statusBar, BorderLayout.SOUTH);
		frame.add(new JLabel(" "), BorderLayout.EAST);
		frame.add(new JLabel(" "), BorderLayout.WEST);
		createMenubar(frame);
//		frame.setSize(350, 350);
		frame.pack();
		frame.setLocation(100, 50);
		frame.setVisible(true);
		frame.setLocation(150, 50);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		fileHandler = new FileOperation(this);

		textArea.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent event) {
				int lineNumber = 0, column = 0, pos = 0;
				try {
					pos = textArea.getCaretPosition();
					lineNumber = textArea.getLineOfOffset(pos);
					column = pos - textArea.getLineStartOffset(lineNumber);
				} catch (BadLocationException exception) {
					if (textArea.getText().length() == 0) {
						lineNumber = 0;
						column = 0;
					}
					statusBar.setText("||      Ln" + (lineNumber + 1) + ", Col " + (column + 1));
				} catch (Exception exception) {
					statusBar.setText(exception.toString());
				}
			}
		});

		DocumentListener documentListener = new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent event) {
				fileHandler.saved = false;
			}

			@Override
			public void insertUpdate(DocumentEvent event) {
				fileHandler.saved = false;
			}

			@Override
			public void changedUpdate(DocumentEvent event) {
				fileHandler.saved = false;
			}
		};

		textArea.getDocument().addDocumentListener(documentListener);

		WindowListener windowListener = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (fileHandler.confirmSave()) {
					System.exit(0);
				}
			}
		};

		frame.addWindowListener(windowListener);
	}

	/********************* End Notepad Constructor *****************************/

	/*********************
	 * Create ActionPerformed Method
	 *****************************/

	@Override
	public void actionPerformed(ActionEvent event) {
		String cmdText = event.getActionCommand();

		/////////////////////////////////////////////////////////

		if (cmdText.equals(fileNew))
			fileHandler.newFile();

		else if (cmdText.equals(fileOpen))
			fileHandler.openFile();

		else if (cmdText.equals(fileSave))
			fileHandler.saveThisFile();

		else if (cmdText.equals(fileSaveAs))
			fileHandler.saveAsFile();

		else if (cmdText.equals(filePrint))
			JOptionPane.showMessageDialog(Notepad.this.frame,
					"Get ur printer repaired first! It seems u dont have one!", "Bad Printer",
					JOptionPane.INFORMATION_MESSAGE);

		else if (cmdText.equals(fileExit)) {
			if (fileHandler.confirmSave())
				System.exit(0);
		}

		/////////////////////////////////////////////////////////

		else if (cmdText.equals(editCut))
			textArea.cut();

		else if (cmdText.equals(editCopy))
			textArea.copy();

		else if (cmdText.equals(editPaste))
			textArea.paste();

		else if (cmdText.equals(editDelete))
			textArea.replaceSelection("");

		else if (cmdText.equals(editFind)) {
			if (Notepad.this.textArea.getText().length() == 0)
				return;
			/*
			 * if(findReplaceDialog==null) findReplaceDialog = new
			 * FindDialog(Notepad.this.textArea);
			 * findReplaceDialog.showDialog(Notepad.this.frame,true);
			 */
		}

		else if (cmdText.equals(editFindNext)) {
			if (Notepad.this.textArea.getText().length() == 0)
				return;
			/*
			 * if(findReplaceDialog==null)
			 * statusBar.setText("Use Find option of Edit Menu first !!!!"); else
			 * findReplaceDialog.findNextWithSelection();
			 */
		}

		else if (cmdText.equals(editReplace)) {
			if (Notepad.this.textArea.getText().length() == 0)
				return;
			/*
			 * if(findReplaceDialog==null) findReplaceDialog = new
			 * FindDialog(Notepad.this.textArea);
			 * findReplaceDialog.showDialog(Notepad.this.frame,false);
			 */
		}

		else if (cmdText.equals(editGoTo)) {
			if (Notepad.this.textArea.getText().length() == 0)
				return;
			goTo();
		}

		else if (cmdText.equals(editSelectAll))
			textArea.selectAll();

		else if (cmdText.equals(editTimeDate))
			textArea.insert(new Date().toString(), textArea.getSelectionStart());

		/////////////////////////////////////////////////////////////////

		else if (cmdText.equals(formatWordWrap)) {
			JCheckBoxMenuItem item = (JCheckBoxMenuItem) event.getSource();
			textArea.setLineWrap(item.isSelected());
		}

		else if (cmdText.equals(formatFont)) {
			/*
			 * if(fontDialog==null) fontDialog= new FontChooser(textArea.getFont());
			 * 
			 * if(fontDialog.showDialog(Notepad.this.frame,"Choose a font"))
			 * Notepad.this.textArea.setFont(fontDialog.createFont());
			 */
		}

		else if (cmdText.equals(formatForeGround))
			showForegroundColorDialog();

		else if (cmdText.equals(formatBackGround))
			showBackgroundColorDialog();

		/////////////////////////////////////////////////////////////////

		else if (cmdText.equals(viewStatusBar)) {
			JCheckBoxMenuItem item = (JCheckBoxMenuItem) event.getSource();
			statusBar.setVisible(item.isSelected());
		}
		
		/////////////////////////////////////////////////////////////////
		
		else if (cmdText.equals(helpAboutNotepad)) {
			JOptionPane.showMessageDialog(Notepad.this.frame, aboutText, "Dedicated 2 U!!",
					JOptionPane.INFORMATION_MESSAGE);
		}
		
		else
			statusBar.setText("This "+cmdText+" command is yet to be implemented");
	}

	/********************* End ActionPerformed Method *****************************/

	/*********************
	 * Create BackGroundColorDialog Method
	 **************************/

	private void showBackgroundColorDialog() {
		if (bcolorChooser == null)
			bcolorChooser = new JColorChooser();
		if (backgroundDialog == null)
			backgroundDialog = JColorChooser.createDialog(Notepad.this.frame, formatBackGround, false, bcolorChooser,
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							Notepad.this.textArea.setBackground(bcolorChooser.getColor());
						}
					}, null);
		backgroundDialog.setVisible(true);
	}

	/*********************
	 * End BackGroundColorDialog Method
	 *****************************/

	/*********************
	 * Create ForeGroundColorDialog Method
	 **************************/

	private void showForegroundColorDialog() {
		if (fcolorChooser == null)
			fcolorChooser = new JColorChooser();
		if (foregroundDialog == null)
			foregroundDialog = JColorChooser.createDialog(Notepad.this.frame, formatForeGround, false, fcolorChooser,
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							Notepad.this.textArea.setForeground(fcolorChooser.getColor());
						}
					}, null);
		foregroundDialog.setVisible(true);
	}

	/*********************
	 * End ForeGroundColorDialog Method
	 *****************************/

	/********************* Create GoTo Method *****************************/

	private void goTo() {
		int lineNumber = 0;
		try {
			lineNumber = textArea.getLineOfOffset(textArea.getCaretPosition()) + 1;
			String str = JOptionPane.showInputDialog(frame, "Enter Line Number:", "" + lineNumber);
			if (str == null)
				return;
			lineNumber = Integer.parseInt(str);
			textArea.setCaretPosition(textArea.getLineStartOffset(lineNumber - 1));

		} catch (BadLocationException exception) {
			System.out.println(exception);
		}
	}

	/********************* End GoTo Method *****************************/

	/********************* Create MenuBar Method *******************************/

	private void createMenubar(JFrame frame) {
		JMenuBar mb = new JMenuBar();
		JMenuItem item;

		JMenu fileMenu = createMenu(fileText, KeyEvent.VK_F, mb);
		JMenu editMenu = createMenu(editText, KeyEvent.VK_E, mb);
		JMenu formatMenu = createMenu(formatText, KeyEvent.VK_O, mb);
		JMenu viewMenu = createMenu(viewText, KeyEvent.VK_V, mb);
		JMenu helpMenu = createMenu(helpText, KeyEvent.VK_H, mb);

		createMenuItem(fileNew, KeyEvent.VK_N, fileMenu, KeyEvent.VK_N, this);
		createMenuItem(fileOpen, KeyEvent.VK_O, fileMenu, KeyEvent.VK_O, this);
		createMenuItem(fileSave, KeyEvent.VK_S, fileMenu, KeyEvent.VK_S, this);
		createMenuItem(fileSaveAs, KeyEvent.VK_A, fileMenu, this);
		fileMenu.addSeparator();
		item = createMenuItem(filePageSetUp, KeyEvent.VK_U, fileMenu, this);
		item.setEnabled(false);
		createMenuItem(filePrint, KeyEvent.VK_P, fileMenu, KeyEvent.VK_P, this);
		fileMenu.addSeparator();
		createMenuItem(fileExit, KeyEvent.VK_X, fileMenu, this);

		item = createMenuItem(editUndo, KeyEvent.VK_U, editMenu, KeyEvent.VK_Z, this);
		item.setEnabled(false);
		editMenu.addSeparator();
		cutItem = createMenuItem(editCut, KeyEvent.VK_T, editMenu, KeyEvent.VK_X, this);
		copyItem = createMenuItem(editCopy, KeyEvent.VK_C, editMenu, KeyEvent.VK_C, this);
		createMenuItem(editPaste, KeyEvent.VK_P, editMenu, KeyEvent.VK_V, this);
		deleteItem = createMenuItem(editDelete, KeyEvent.VK_L, editMenu, this);
		deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		editMenu.addSeparator();
		findItem = createMenuItem(editFind, KeyEvent.VK_F, editMenu, KeyEvent.VK_F, this);
		findNextItem = createMenuItem(editFindNext, KeyEvent.VK_N, editMenu, this);
		findNextItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		replaceItem = createMenuItem(editReplace, KeyEvent.VK_R, editMenu, KeyEvent.VK_H, this);
		gotoItem = createMenuItem(editGoTo, KeyEvent.VK_G, editMenu, KeyEvent.VK_G, this);
		editMenu.addSeparator();
		selectAllItem = createMenuItem(editSelectAll, KeyEvent.VK_A, editMenu, KeyEvent.VK_A, this);
		createMenuItem(editTimeDate, KeyEvent.VK_D, editMenu, this)
				.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));

		createCheckBoxMenuItem(formatWordWrap, KeyEvent.VK_W, formatMenu, this);

		createMenuItem(formatFont, KeyEvent.VK_F, formatMenu, this);
		formatMenu.addSeparator();
		createMenuItem(formatBackGround, KeyEvent.VK_T, formatMenu, this);
		createMenuItem(formatForeGround, KeyEvent.VK_P, formatMenu, this);

		createCheckBoxMenuItem(viewStatusBar, KeyEvent.VK_S, viewMenu, this).setSelected(true);

		item = createMenuItem(helpViewHelp, KeyEvent.VK_H, helpMenu, this);
		item.setEnabled(false);
		helpMenu.addSeparator();
		createMenuItem(helpAboutNotepad, KeyEvent.VK_A, helpMenu, this);

		MenuListener editMenuListener = new MenuListener() {

			@Override
			public void menuSelected(MenuEvent event) {
				if (Notepad.this.textArea.getText().length() == 0) {
					findItem.setEnabled(false);
					findNextItem.setEnabled(false);
					replaceItem.setEnabled(false);
					selectAllItem.setEnabled(false);
					gotoItem.setEnabled(false);
				} else {
					findItem.setEnabled(true);
					findNextItem.setEnabled(true);
					replaceItem.setEnabled(true);
					selectAllItem.setEnabled(true);
					gotoItem.setEnabled(true);
				}

				if (Notepad.this.textArea.getSelectionStart() == textArea.getSelectionEnd()) {
					cutItem.setEnabled(false);
					copyItem.setEnabled(false);
					deleteItem.setEnabled(false);
				} else {
					cutItem.setEnabled(true);
					copyItem.setEnabled(true);
					deleteItem.setEnabled(true);
				}
			}

			@Override
			public void menuDeselected(MenuEvent event) {
			}

			@Override
			public void menuCanceled(MenuEvent event) {
			}
		};

		editMenu.addMenuListener(editMenuListener);
		frame.setJMenuBar(mb);
	}

	/********************* End MenuBar Method **********************************/

	/********************* Create Menu Method **********************************/

	private JMenu createMenu(String filetext, int key, JMenuBar mb) {
		JMenu menu = new JMenu(filetext);
		menu.setMnemonic(key);
		mb.add(menu);
		return menu;
	}

	/********************* End Menu Method **************************************/

	/********************* Create MenuItem Method *******************************/

	private JMenuItem createMenuItem(String file, int key, JMenu menu, ActionListener al) {
		JMenuItem item = new JMenuItem(file, key);
		item.addActionListener(al);
		menu.add(item);
		return item;
	}

	private JMenuItem createMenuItem(String file, int key, JMenu menu, int aclKey, ActionListener al) {
		JMenuItem item = new JMenuItem(file, key);
		item.addActionListener(al);
		item.setAccelerator(KeyStroke.getKeyStroke(aclKey, ActionEvent.CTRL_MASK));
		menu.add(item);
		return item;
	}

	/********************* End MenuItem Method **********************************/

	/********************* Create MenuItem Method *******************************/

	private JCheckBoxMenuItem createCheckBoxMenuItem(String format, int key, JMenu formatMenu, ActionListener al) {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(format);
		item.setMnemonic(key);
		item.addActionListener(al);
		item.setSelected(false);
		formatMenu.add(item);
		return item;
	}

	/********************* End MenuItem Method **********************************/
}