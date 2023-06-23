package javaeditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Java编辑器
 * 作者：@author
 * 创建日期：2023年6月19日
 */

public class JavaEditor {
    private JFrame frame;// 主窗口
    private JTextPane textPane;// 文本编辑区域
    private JFileChooser fileChooser;// 文件选择器
    private Timer autoSaveTimer;// 自动保存定时器
    private File currentFile; // 当前打开的文件
    private FindAndReplace findAndReplace;// 查找和替换对象

    public JavaEditor() {
        frame = new JFrame("Java 编辑器");

        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        // 创建文件菜单
        JMenu fileMenu = new JMenu("文件");
        menuBar.add(fileMenu);

        // 创建文件菜单项：打开
        JMenuItem openMenuItem = new JMenuItem("打开");
        openMenuItem.addActionListener(new OpenFileListener());
        fileMenu.add(openMenuItem);

        // 创建文件菜单项：保存
        JMenuItem saveMenuItem = new JMenuItem("保存");
        saveMenuItem.addActionListener(new SaveFileListener());
        fileMenu.add(saveMenuItem);

        // 创建文件菜单项：退出
        JMenuItem exitMenuItem = new JMenuItem("退出");
        exitMenuItem.addActionListener(new ExitListener());
        fileMenu.add(exitMenuItem);

        // 创建编辑菜单
        JMenu editMenu = new JMenu("编辑");
        menuBar.add(editMenu);

        // 创建编辑菜单项：查找
        JMenuItem findMenuItem = new JMenuItem("查找");
        findMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findAndReplace.findText();
            }
        });
        editMenu.add(findMenuItem);

        // 创建编辑菜单项：替换
        JMenuItem replaceMenuItem = new JMenuItem("替换");
        replaceMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findAndReplace.replaceText();
            }
        });
        editMenu.add(replaceMenuItem);

        // 创建文本编辑区域
        textPane = new JTextPane();

        // 将文本编辑区域添加到主界面的中心位置，并支持滚动
        frame.add(new JScrollPane(textPane), BorderLayout.CENTER);

        // 创建语法高亮器对象，并将其作为文本编辑区域的文档监听器
        textPane.getDocument().addDocumentListener(new javaeditor.SyntaxHighlighter(textPane));

        // 设置窗口关闭操作，即当用户关闭窗口时，程序将退出
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 设置主界面的尺寸为800x600像素 ,设置主界面可见，使其显示在屏幕上
        frame.setSize(800, 600);
        frame.setVisible(true);

        // 创建文件选择器对象，用于文件选择和打开/保存对话框的显示
        // 创建查找和替换功能模块对象，并传入文本编辑区域参数
        fileChooser = new JFileChooser();
        findAndReplace = new FindAndReplace(textPane);

        // 创建定时器，每隔一定时间触发自动保存操作
        autoSaveTimer = new Timer(3000, new AutoSaveListener()); // 3000毫秒 = 3秒
        autoSaveTimer.setInitialDelay(3000); // 延迟3000毫秒 = 3秒开始执行
        autoSaveTimer.setRepeats(true); // 设置定时器重复执行

        autoSaveTimer.start(); // 启动定时器
    }

    private class OpenFileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int returnVal = fileChooser.showOpenDialog(frame);// 显示文件选择对话框，返回用户选择的操作结果
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();// 获取用户选择的文件
                currentFile = file;// 将当前打开的文件设置为所选文件
                try {
                    // 读取文件内容到文本编辑区域
                    FileReader reader = new FileReader(file);
                    textPane.read(reader, null);
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private class SaveFileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveFile();
        }
    }

    private class ExitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    private class AutoSaveListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveFileSilently(); // 无弹窗保存
            createOrUpdateBackupFile();
        }
    }

    private void saveFile() {
        File file = getSaveFile();
        if (file != null) {
            currentFile = file;
            try {
                // 将文本编辑区域的内容保存到文件
                FileWriter writer = new FileWriter(file);
                textPane.write(writer);
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void saveFileSilently() {
        if (currentFile != null) {
            try {
                // 将文本编辑区域的内容保存到文件
                FileWriter writer = new FileWriter(currentFile);
                textPane.write(writer);
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

//检查当前文件是否存在，如果存在则创建或更新备份文件。
//如果当前文件不存在，则不执行任何操作。
    private void createOrUpdateBackupFile() {
        if (currentFile != null) {
            Path sourcePath = currentFile.toPath();// 获取当前文件的路径
            Path backupPath = getBackupFilePath(sourcePath);// 获取备份文件的路径
            try {
                // 复制当前文件到备份文件路径，如果备份文件已存在则替换它
                // StandardCopyOption.REPLACE_EXISTING：表示替换已存在的目标文件
                Files.copy(sourcePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private Path getBackupFilePath(Path sourceFilePath) {
        // 获取源文件路径中的文件名部分，并将其作为字符串赋值给 sourceFileName 变量
        String sourceFileName = sourceFilePath.getFileName().toString();

        // 源文件名与后缀 .back 进行拼接，生成备份文件的文件名
        String backupFileName = sourceFileName + ".back";

        // 使用备份文件名替换源文件名，并保持相同的父路径，生成备份文件的完整路径。
        // 然后将备份文件的路径作为结果返回。
        return sourceFilePath.resolveSibling(backupFileName);
    }

    private File getSaveFile() {
        int returnVal = fileChooser.showSaveDialog(frame);// 显示文件保存对话框，并将返回值保存到returnVal变量中
        // 如果用户点击了"确认"或"保存"按钮
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();// 返回用户选择的文件
        }
        return null;
    }

    public static void main(String[] args) {
        new JavaEditor();
    }
}
