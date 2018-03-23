import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Game extends JFrame implements ActionListener {

    private Puzzle puzzle;

    private JMenuBar menuBar;//菜单栏对象

    private JMenu menu, menu2;//菜单对象

    private JMenuItem menuItem, menuItem2, menuItem3, menuItem4, menuItem5, menuItem6;//菜单对象

    private static Logger LOGGER = LoggerFactory.getLogger(Game.class);

    String pictureUrl;

    public Game() {
        setTitle("我的拼图");
        setLayout(null);
        setBounds(400, 0, 600, 700);
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        puzzle = new Puzzle();
        puzzle.setBounds(0, 0, 600, 600);
        menu = new JMenu("开始");
        menuBar.add(menu);
        menuItem = new JMenuItem("开始游戏");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem2 = new JMenuItem("显示正确图片");
        menuItem2.addActionListener(this);
        menu.add(menuItem2);
        menuItem3 = new JMenuItem("退出");
        menuItem3.addActionListener(this);
        menu.add(menuItem3);


        menu2 = new JMenu("设置");
        menuBar.add(menu2);
        menuItem4 = new JMenuItem("设置行数");
        menuItem4.addActionListener(this);
        menu2.add(menuItem4);
        menuItem5 = new JMenuItem("设置列数");
        menuItem5.addActionListener(this);
        menu2.add(menuItem5);
        menuItem6 = new JMenuItem("选择图片");
        menuItem6.addActionListener(this);
        menu2.add(menuItem6);

        this.getContentPane().add(puzzle);
        this.getContentPane().setBackground(Color.white);
        setVisible(true);
        puzzle.setFocusable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == menuItem) {
            LOGGER.debug("开始游戏");
            try{
                puzzle.start();
            }catch (BusinessException ex){
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.ERROR_MESSAGE);
            }
        }
        if (e.getSource() == menuItem2) {
            LOGGER.debug("显示正确图片");
            new DisPlay("原图片",pictureUrl);

        }
        if (e.getSource() == menuItem3) {
            LOGGER.debug("退出游戏");
            System.exit(0);
        }
        if (e.getSource() == menuItem4) {
            LOGGER.debug("设置行数");
            String n = JOptionPane.showInputDialog(null, "请输入行数：\n", "title", JOptionPane.PLAIN_MESSAGE);
            try {
                puzzle.setN(Integer.valueOf(n));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "请输入数字", null, JOptionPane.ERROR_MESSAGE);
            }
        }
        if (e.getSource() == menuItem5) {
            LOGGER.debug("设置列数");
            String m = JOptionPane.showInputDialog(null, "请输入列数数：\n", "title", JOptionPane.PLAIN_MESSAGE);
            try {
                puzzle.setM(Integer.valueOf(m));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "请输入数字", null, JOptionPane.ERROR_MESSAGE);
            }
        }
        if (e.getSource() == menuItem6) {
            LOGGER.debug("选择图片");
            JFileChooser fileChooser;
            {
                fileChooser = new JFileChooser();
                FileFilter filter = new FileNameExtensionFilter("图像文件(只能是PNG或JPG)", "JPG", "PNG");
                fileChooser.setFileFilter(filter);
            }
            int i = fileChooser.showOpenDialog(getContentPane());
            if (i == JFileChooser.APPROVE_OPTION) {
                File SelectedFile = fileChooser.getSelectedFile();
                pictureUrl= SelectedFile.getAbsolutePath();
                puzzle.setPictureUrl(pictureUrl);
                puzzle.repaint();
            }
        }
    }

    public static void main(String[] args) {
        try {
          //  UIManager.setLookAndFeel("com.jtattoo.plaf.mint.MintLookAndFeel");
            new Game();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
