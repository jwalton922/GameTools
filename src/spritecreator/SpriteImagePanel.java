/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package spritecreator;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author Josh
 */
public class SpriteImagePanel extends JPanel{

    private BufferedImage image;
    private ArrayList<ImageHolder> imageHolder = new ArrayList<ImageHolder>();

    public SpriteImagePanel(BufferedImage spriteImage)
    {
        this.image = spriteImage;
    }

    public void emptyImageHolder()
    {
        imageHolder.clear();
        imageHolder = new ArrayList<ImageHolder>();
    }

    @Override
    public void paintComponent(Graphics g)
    {

        Graphics2D g2 = (Graphics2D)g;
        for(int i = 0; i < imageHolder.size(); i++)
        {
            g2.drawImage(imageHolder.get(i).bi, imageHolder.get(i).x, imageHolder.get(i).y, imageHolder.get(i).dx, imageHolder.get(i).dy, imageHolder.get(i).sx, imageHolder.get(i).sy, imageHolder.get(i).sdx, imageHolder.get(i).sdy, null);
        }
    }

    public void paintImageToComponent(BufferedImage bi, int x, int y, int dx, int dy, int sx, int sy, int sdx, int sdy)
    {

        ImageHolder ih = new ImageHolder(image, x, y, dx, dy, sx, sy, sdx, sdy);
        imageHolder.add(ih);
        this.repaint();
    }

    private class ImageHolder {
        public BufferedImage bi;
        public int x;
        public int y;
        public int dx;
        public int dy;
        public int sx;
        public int sy;
        public int sdx;
        public int sdy;

        public ImageHolder(BufferedImage bi, int x, int y, int dx, int dy, int sx, int sy, int sdx, int sdy)
        {
            this.x = x;
            this.y = y;
            this.dx = dx;
            this.dy = dy;
            this.sx = sx;
            this.sy = sy;
            this.sdx = sdx;
            this.sdy = sdy;
        }

    }

}
