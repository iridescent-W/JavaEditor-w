package javaeditor;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Java编辑器
 *
 *
 * 作者：@author
 * 创建日期：2023年6月19日
 */
public class SyntaxHighlighter implements DocumentListener {
    private final Set<String> keywords;
    private final Style keywordStyle;
    private final Style normalStyle;
    public SyntaxHighlighter(JTextPane editor) {
// 准备着色使用的样式
        keywordStyle = ((StyledDocument) editor.getDocument()).addStyle("Keyword_Style", null);
        normalStyle = ((StyledDocument) editor.getDocument()).addStyle("normalword_Style", null);
        StyleConstants.setForeground(keywordStyle, Color.RED);
        StyleConstants.setForeground(normalStyle, Color.BLACK);
// 准备关键字
        keywords = new HashSet<>();
        keywords.add("abstract");
        keywords.add("assert");
        keywords.add("boolean");
        keywords.add("break");
        keywords.add("byte");
        keywords.add("case");
        keywords.add("catch");
        keywords.add("char");
        keywords.add("class");
        keywords.add("const");
        keywords.add("continue");
        keywords.add("default");
        keywords.add("do");
        keywords.add("double");
        keywords.add("else");
        keywords.add("enum");
        keywords.add("extends");
        keywords.add("final");
        keywords.add("finally");
        keywords.add("float");
        keywords.add("for");
        keywords.add("if");
        keywords.add("implements");
        keywords.add("import");
        keywords.add("instanceof");
        keywords.add("int");
        keywords.add("interface");
        keywords.add("long");
        keywords.add("native");
        keywords.add("new");
        keywords.add("package");
        keywords.add("private");
        keywords.add("protected");
        keywords.add("public");
        keywords.add("return");
        keywords.add("short");
        keywords.add("static");
        keywords.add("strictfp");
        keywords.add("super");
        keywords.add("switch");
        keywords.add("synchronized");
        keywords.add("this");
        keywords.add("throw");
        keywords.add("throws");
        keywords.add("transient");
        keywords.add("try");
        keywords.add("void");
        keywords.add("volatile");
        keywords.add("while");
    }
    public void colouring(StyledDocument doc, int pos, int len) throws BadLocationException {
        int start = indexOfWordStart(doc, pos);
        int end = indexOfWordEnd(doc, pos + len);
        char ch;
        while (start < end) {
            ch = getCharAt(doc, start);
            if (Character.isLetter(ch) || ch == '_') {
// 如果是以字母或者下划线开头, 说明是单词
// pos为处理后的最后一个下标
                start = colouringWord(doc, start);
            } else {
                SwingUtilities.invokeLater(new ColouringTask(doc, start, 1, normalStyle));
                ++start;
            }
        }
    }
    //是否着色
    public int colouringWord(StyledDocument doc, int pos) throws BadLocationException {
        int wordEnd = indexOfWordEnd(doc, pos);
        String word = doc.getText(pos, wordEnd - pos);
        if (keywords.contains(word)) {
            SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, keywordStyle));
        } else {
            SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, normalStyle));
        }
        return wordEnd;
    }
    public char getCharAt(Document doc, int pos) throws BadLocationException {
        return doc.getText(pos, 1).charAt(0);
    }
    public int indexOfWordStart(Document doc, int pos) throws BadLocationException {
// 从pos开始向前找到第一个非单词字符.
        while (pos > 0 && isWordCharacter(doc, pos - 1)) {
            --pos;
        }
        return pos;
    }
    public int indexOfWordEnd(Document doc, int pos) throws BadLocationException {
// 从pos开始向后找到第一个非单词字符.

        //noinspection StatementWithEmptyBody
        for (; isWordCharacter(doc, pos); ++pos) {
        }
        return pos;
    }
    public boolean isWordCharacter(Document doc, int pos) throws BadLocationException {
        char ch = getCharAt(doc, pos);
        return Character.isLetter(ch) || Character.isDigit(ch) || ch == '_';
    }
    @Override
    public void changedUpdate(DocumentEvent e) {
    }
    @Override
    public void insertUpdate(DocumentEvent e) {
        try {
            colouring((StyledDocument) e.getDocument(), e.getOffset(), e.getLength());
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }
    }
    @Override
    public void removeUpdate(DocumentEvent e) {
        try {
// 因为删除后光标紧接着影响的单词两边, 所以长度就不需要了
            colouring((StyledDocument) e.getDocument(), e.getOffset(), 0);
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }
    }
    private static class ColouringTask implements Runnable {
        private final StyledDocument doc;
        private final Style style;
        private final int pos;
        private final int len;
        public ColouringTask(StyledDocument doc, int pos, int len, Style style) {
            this.doc = doc;
            this.pos = pos;
            this.len = len;
            this.style = style;
        }
        @Override
        public void run() {
            try {
// 这里就是对字符进行着色
                doc.setCharacterAttributes(pos, len, style, true);
            } catch (Exception ignored) {}
        }
    }
}
