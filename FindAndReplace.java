package javaeditor;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Java编辑器
 * 作者：@author
 * 创建日期：2023年6月19日
 */

public class FindAndReplace {
    final JTextPane textPane;

    public FindAndReplace(JTextPane textPane) {
        this.textPane = textPane;
    }

    public void findText() {
        String searchText = JOptionPane.showInputDialog(textPane, "请输入要查找的字符串:");
        if (searchText != null && !searchText.isEmpty()) {
            String text = textPane.getText();
            Pattern pattern = Pattern.compile(Pattern.quote(searchText));
            Matcher matcher = pattern.matcher(text);
            Highlighter highlighter = textPane.getHighlighter();
            highlighter.removeAllHighlights();
            while (matcher.find()) {
                try {
                    int start = matcher.start();
                    int end = matcher.end();
                    highlighter.addHighlight(start, end, DefaultHighlighter.DefaultPainter);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void replaceText() {
        String searchText = JOptionPane.showInputDialog(textPane, "请输入要查找的字符串:");
        if (searchText != null && !searchText.isEmpty()) {
            String replaceText = JOptionPane.showInputDialog(textPane, "请输入要替换的字符串:");
            if (replaceText != null) {
                String text = textPane.getText();
                Pattern pattern = Pattern.compile(Pattern.quote(searchText));
                Matcher matcher = pattern.matcher(text);
                String replacedText = matcher.replaceAll(replaceText);
                textPane.setText(replacedText);
            }
        }
    }
}

