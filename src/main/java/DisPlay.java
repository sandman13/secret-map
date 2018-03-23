import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class DisPlay extends JDialog {

    public DisPlay(String title,String url) {
        setTitle(title);
        setLayout(null);
        setBounds(500, 200, 400, 400);
        Container container = getContentPane();
        container.setBackground(Color.white);
        JPanel jPanel=new JPanel(){
            @Override
            public void paint(Graphics g) {
                try {
                    BufferedImage image = ImageIO.read(new File(url));
                    g.drawImage(image, 0, 0, 400, 400, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };
        jPanel.setBounds(0,0,400,400);
        jPanel.repaint();
        container.add(jPanel);
        setVisible(true);
    }

}
