/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author Josh
 */
public class TransparentImageMaker {

    public static void main(String[] args){
        String imageFile = "C:/isometric sprites/trees2 tileset.bmp";

        BufferedImage i = ImageUtils.loadImageAndMakeBackgroundTransparent(imageFile);
         try {
            ImageIO.write(i, "PNG", new File("C:/isometric sprites/trees.png"));
         } catch(Exception e) {
             e.printStackTrace();
         }
    }
}
