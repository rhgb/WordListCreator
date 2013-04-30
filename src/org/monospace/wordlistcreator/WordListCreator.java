package org.monospace.wordlistcreator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class WordListCreator extends JDialog {
	private JPanel contentPane;
	private JTextField titleField;
	private JTextArea wordListArea;
	private JSpinner splitNumSpinner;
	private JButton createButton;
	private JCheckBox splitLinesCheckBox;
	private final JFileChooser fileChooser;

	public WordListCreator() {
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(createButton);
		splitNumSpinner.setValue(100);
		splitLinesCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				splitNumSpinner.setEnabled(splitLinesCheckBox.isSelected());
			}
		});
		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generateFile();
			}
		});
	}

	private void generateFile() {
		int res = fileChooser.showSaveDialog(this);
		if (res != JFileChooser.APPROVE_OPTION) return;
		fileChooser.getCurrentDirectory();
		File dir = fileChooser.getSelectedFile();

		String source = wordListArea.getText();
		String[] lines = source.split("\n");
		ArrayList<String> list = new ArrayList<String>(lines.length);
		for (String line1 : lines) {
			String line = line1.trim();
			if (line.length() > 0) {
				list.add(line);
			}
		}
		String titlePre = "wordlist";
		String titleInput = titleField.getText().trim();
		if (titleInput.length() > 0) titlePre = titleInput;
		boolean split = splitLinesCheckBox.isSelected();
		int splitNum = (Integer)splitNumSpinner.getValue();

		try {
			if (!split || list.size() < splitNum) {
				File file = new File(dir, titlePre + ".xml");
				PrintWriter writer = new PrintWriter(file, "UTF-8");
				writer.println(format(titlePre, list));
				writer.close();
			} else {
				for (int i = 0; i < list.size() / splitNum; i++) {
					String title = titlePre + "_" + (i + 1);
					File file = new File(dir, title + ".xml");
					PrintWriter writer = new PrintWriter(file, "UTF-8");
					writer.println(format(title, list.subList(i * splitNum, (i + 1) * splitNum - 1)));
					writer.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String format(String title, List<String> lines) {
		String PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<themes xmlns=\"http://9spikes.com/schemas/lsteacher/themes/2012\">\n" +
				"<theme title=\"" + title + "\" lang=\"fr\">\n";
		String SUFFIX = "</theme>\n" + "</themes>";
		String WRAPPER_PREFIX = "<sentence>";
		String WRAPPER_SUFFIX = "</sentence>\n";
		String result = PREFIX;
		for (String line : lines) {
			result += WRAPPER_PREFIX + line + WRAPPER_SUFFIX;
		}
		result += SUFFIX;
		return result;
	}

	public static void main(String[] args) {
		WordListCreator dialog = new WordListCreator();
		dialog.pack();
		dialog.setVisible(true);
		System.exit(0);
	}
}
