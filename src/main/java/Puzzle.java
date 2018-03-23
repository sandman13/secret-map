import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Random;

public class Puzzle extends JPanel implements MouseListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Puzzle.class);

    private ImageIcon imageIcon;

    /**
     * 确定绘制模式
     */
    private String type;

    /**
     * 数组里存储着第i个位置存放着第几张切割的图片,如果是第9张则空着.
     */
    private int icon[];

    /**
     * 切割后的图片
     */
    private ImageIcon[] image;

    private int n = 3;

    private int m = 3;

    private String pictureUrl;

    private int currentX;

    private int currentY;

    private int nullIndex;

    public Puzzle() {
        LOGGER.info(this.getClass().getResource("/").getPath() + "image/bg.png");
        imageIcon = new ImageIcon(this.getClass().getResource("/").getPath() + "image/bg.png");
        type = TypeEnum.BACKGROUND.name();
        repaint();
    }

    public void start() throws BusinessException {
        LOGGER.info(MessageFormat.format("game is start,图片url:{0},m:{1},n:{2}", pictureUrl, m, n));
        checkisValidate(n, m, pictureUrl);
        initIcon(n, m);
        initImage(n, m, pictureUrl);
        type = TypeEnum.GAME_PROCESSING.name();
        this.addMouseListener(this);
        repaint();
    }


    /**
     * 校验是否符合规范
     *
     * @param n
     * @param m
     * @param pictureUrl
     */
    private void checkisValidate(int n, int m, String pictureUrl) {
        if (n < 0) {
            throw new BusinessException("行数不能为负数");
        }
        if (m < 0) {
            throw new BusinessException("列数不能为负数");
        }
        if (pictureUrl == null || pictureUrl.length() == 0) {
            throw new BusinessException("请点击选择图片提供图片来源");
        }
    }

    /**
     * 初始化图片片段的信息,保证一定有解
     *
     * @param n
     * @param m
     */
    private void initIcon(int n, int m) {
        icon = new int[n * m + 1];
        boolean isOk = false;
        while (!isOk) {
            for (int i = 1; i <= n * m; i++) {
                icon[i] = new Random().nextInt(n * m) + 1;
                boolean isRepeat = false;
                for (int j = 1; j < i; j++) {
                    if (icon[i] == icon[j]) {
                        isRepeat = true;
                    }
                }
                if (isRepeat) {
                    i--;
                }
            }
            int sum = 0;
            //记录下第几个为空,除去为空的其余的计算逆序对
            nullIndex = 0;
            for (int i = 1; i <= n * m; i++) {
                if (icon[i] == n * m) {
                    nullIndex = i;
                    continue;
                }
                for (int j = 1; j < i; j++) {
                    if (j==nullIndex){
                        continue;
                    }
                    if (icon[j] > icon[i]) {
                        sum++;
                    }
                }
            }
            if ((sum % 2 == 0) && (m % 2 == 1))//如果列数为奇数并且逆序对为偶数(默认的逆序对为0)
                isOk = true;
            else if ((m % 2 == 0) && (n - (nullIndex - 1) / m - 1) % 2 == sum % 2)//如果列数为偶数,并且逆序对奇偶性和空白到最后一行一样
                isOk = true;
            for (int i = 1; i <= n * m; i++) {
                if (icon[i] != i) {
                    break;
                }
                //如果完全一样也需要排除
                if (i == n * m) {
                    isOk = false;
                }
            }
        }
        LOGGER.debug(Arrays.toString(icon));
    }

    /**
     * 生成图片片段
     *
     * @param n
     * @param m
     */
    private void initImage(int n, int m, String pictureUrl) {
        try {
            image = new ImageIcon[n * m + 1];
            BufferedImage bufferedImage = ImageIO.read(new File(pictureUrl));
            int srcWidth = bufferedImage.getWidth();
            int srcHeight = bufferedImage.getHeight();
            Image imageInstance = bufferedImage.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
            double destWidth = srcWidth / m;
            double destHeight = srcHeight / n;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    ImageFilter imageFilter = new CropImageFilter((int) (j * destWidth), (int) (i * destHeight),
                            (int) ((j + 1) * destWidth), (int) ((i + 1) * destHeight));
                    Image img = Toolkit.getDefaultToolkit().createImage(
                            new FilteredImageSource(imageInstance.getSource(), imageFilter));
                    BufferedImage tag = new BufferedImage((int) destWidth, (int) destHeight,
                            BufferedImage.TYPE_INT_RGB);
                    Graphics g = tag.getGraphics();
                    g.drawImage(img, 0, 0, null); // 绘制缩小后的图
                    g.dispose();
                    image[j + 1 + i * m] = new ImageIcon(tag);
                }
            }
        } catch (Exception e) {
            LOGGER.error("裁剪图片失败", e);
        }
    }

    @Override
    public void paint(Graphics g){
        switch (type) {
            case "BACKGROUND":
                LOGGER.debug("背景图片");
                Image background = imageIcon.getImage();
                g.drawImage(background, 0, 0, 600, 600, this);
                break;
            case "GAME_PROCESSING":
                LOGGER.debug("游戏进行中");
                g.clearRect(0, 0, 600, 600);
                for (int i = 1; i <= n * m; i++) {
                    if (icon[i] != n * m) {
                        int temp = icon[i];
                        Image r = image[temp].getImage();
                        g.drawImage(r, (i - 1) % m * (600 / m), (i - 1) / m * (600 / n), 600 / m, 600 / n, this);
                    } else {
                        currentX = (i - 1) % m * (600 / m);
                        currentY = (i - 1) / m * (600 / n);//空格的横纵左上角坐标
                        g.clearRect((i - 1) % m * (600 / m), (i - 1) / m * (600 / n), 600 / m, 600 / n);//设置第9为空格
                    }
                }
                break;
            case "WIN":
                LOGGER.debug("胜利");
                g.clearRect(0,0,600,600);
                ImageIcon imageIcon=new ImageIcon(pictureUrl);
                g.drawImage(imageIcon.getImage(),0,0,600,600,this);
        }
    }


    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     *
     * @param e
     */
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     * @param e
     */
    public void mousePressed(MouseEvent e) {
        int tempx = e.getX();
        int tempy = e.getY();
        LOGGER.debug("鼠标触摸,原来的长度为:" + currentX + "原来的高度为:" + currentY + "点击的长度为:" + tempx + "点击的高度为:" + tempy);
        if ((tempx - currentX >= 0) && (tempx - currentX <= 600 / m) && (currentY - tempy <= 600 / n) && (currentY - tempy >= 0)) {//如果点击按钮在当前的上方
            int temp = icon[nullIndex];//上下交换自己的图片
            icon[nullIndex] = icon[nullIndex - m];
            icon[nullIndex - m] = temp;
            nullIndex = nullIndex - m;
            //更新到左上角
            currentX = tempx / (600 / m) * (600 / m);
            currentY = tempy / (600 / n) * (600 / n);
            LOGGER.debug("向上" + currentX + " " + currentY);
        } else if ((currentX - tempx >= 0) && (currentX - tempx <= 600 / m) && (tempy - currentY >= 0) && (tempy - currentY <= 600 / n)) {//如果点击按钮在当前的左边
            int temp = icon[nullIndex];
            icon[nullIndex] = icon[nullIndex - 1];
            icon[nullIndex - 1] = temp;
            nullIndex--;
            currentX = tempx / (600 / m) * (600 / m);
            currentY = tempy / (600 / n) * (600 / n);
            LOGGER.debug("向左边" + currentX + " " + currentY);
        } else if ((tempx - currentX <= 2 * (600 / m)) && (tempx - currentX >= 600 / m) && (tempy - currentY >= 0) && (tempy - currentY <= 600 / n)) {//如果点击按钮在当前的右边
            int temp = icon[nullIndex];
            icon[nullIndex] = icon[nullIndex + 1];
            icon[nullIndex + 1] = temp;
            nullIndex++;
            currentX = tempx / (600 / m) * (600 / m);
            currentY = tempy / (600 / n) * (600 / n);
            LOGGER.debug("向右边" + currentX + " " + currentY);
        } else if ((tempy - currentY >= 600 / n) && (tempy - currentY <= 2 * (600 / n)) && (tempx - currentX >= 0) && (tempx - currentX <= 600 / m)) {//如果点击按钮在当前的下边
            int temp = icon[nullIndex];
            icon[nullIndex] = icon[nullIndex + m];
            icon[nullIndex + m] = temp;
            nullIndex = nullIndex + m;
            currentX = tempx / (600 / m) * (600 / m);
            currentY = tempy / (600 / n) * (600 / n);
            LOGGER.debug("向下边" + currentX + " " + currentY);
        }
        repaint();
        if (isFinished()){
             new DisPlay("恭喜",this.getClass().getResource("/").getPath()+"/image/win.png");
             type=TypeEnum.WIN.name();
        }
    }

    /**
     * 判断是否胜利
     * @return
     */
    public boolean isFinished() {
        for (int i=1;i<=n*m;i++){
            if (icon[i]!=i){
                return false;
            }
        }
        return true;
    }
    /**
     * Invoked when a mouse button has been released on a component.
     *
     * @param e
     */
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e
     */
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e
     */
    public void mouseExited(MouseEvent e) {

    }

    public void setN(int n) {
        this.n = n;
    }

    public void setM(int m) {
        this.m = m;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }


}
