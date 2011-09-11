/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.PixelGrabber;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author Josh
 */
public class ImageUtils {

    public static void clearPixels(BufferedImage image, ArrayList<Point> pixels) {
        for (int i = 0; i < pixels.size(); i++) {

            image.setRGB(pixels.get(i).x, pixels.get(i).y, 0);
        }

    }

    public static Image rotateImage(Image i, double degrees){
        BufferedImage rotatedImage= new BufferedImage(i.getWidth(null),i.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = rotatedImage.createGraphics();
        AffineTransform xform= new AffineTransform();
        xform.setToRotation(Math.toRadians(degrees));
        g2.drawImage(i, xform, null);
        return rotatedImage;
    }

    public static Image duplicateImage(Image image) {
        return image.getScaledInstance(image.getWidth(null), image.getHeight(null), Image.SCALE_DEFAULT);
    }

    public static ArrayList<ArrayList<Point>> getRandomPixelPoints(Image image, int numImages) {
        ArrayList<Point> randomPoints = new ArrayList<Point>();
        for (int i = 0; i < image.getWidth(null); i++) {
            for (int j = 0; j < image.getHeight(null); j++) {
                randomPoints.add(new Point(i, j));
            }
        }

        int numPoints = randomPoints.size() / numImages;
        int remainderPoints = randomPoints.size() % numImages;

        Collections.shuffle(randomPoints);
//        System.out.println("there are "+randomPoints.size()+ " points");
//        System.out.println("There will be "+numPoints+" removed per image with "+remainderPoints+" at the end.");
        ArrayList<ArrayList<Point>> points = new ArrayList<ArrayList<Point>>();
        for (int i = 0; i < numImages; i++) {
            ArrayList<Point> listPoints = new ArrayList<Point>();
            int index = i * numPoints;
            for (int j = 0; j < listPoints.size(); j++) {
                index += j;
                listPoints.add(randomPoints.get(index));
            }
            points.add(listPoints);
        }
        int index = numImages * numPoints;
        ArrayList<Point> lastPoints = new ArrayList<Point>();
        for (int i = 0; i < remainderPoints; i++) {

            lastPoints.add(randomPoints.get(index+i));
        }

        points.add(lastPoints);
        return points;
    }

    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        } // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
        // Determine if the image has transparent pixels; for this method's
        // implementation, see Determining If an Image Has Transparent Pixels
        boolean hasAlpha = hasAlpha(image);
        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }
            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null),
                    transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }
        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }


        // Copy image to buffered image
        Graphics g = bimage.createGraphics();
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }

    // This method returns true if the specified image has transparent pixels
    public static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage) image;

            return bimage.getColorModel().hasAlpha();
        }
        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }
        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }

    /**
     * Gets pixel color of input coordinates
     * @param x
     * @param y
     * @return
     */
    public static Color getColorOfPixel(BufferedImage image, int x, int y) {
        int c = image.getRGB(x, y);
        int red = (c & 0x00ff0000) >> 16;
        int green = (c & 0x0000ff00) >> 8;
        int blue = c & 0x000000ff;
        return new Color(red, green, blue);
    }

    /** reads in an image
     *
     */
    public static BufferedImage readImage(String f) {
        System.out.println("Reading in image: " + f);
        return readImage(new File(f));
    }

    public static BufferedImage readImage(File f) {
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(f);
            System.out.println("Success reading in image");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bi;
    }

    public static BufferedImage readImage(URL url) {
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(url);
            System.out.println("Success reading in image");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bi;
    }

    /**
     * Makes input color transparent for input image
     *
     * @param im
     * @param color
     * @return
     */
    public static BufferedImage makeColorTransparent(Image im, final Color color) {
        ImageFilter filter = new RGBImageFilter() {
            // the color we are looking for... Alpha bits are set to opaque

            public int markerRGB = color.getRGB() | 0xFF000000;

            public final int filterRGB(int x, int y, int rgb) {
                if ((rgb | 0xFF000000) == markerRGB) {
                    // Mark the alpha bits as zero - transparent
                    return 0x00FFFFFF & rgb;
                } else {
                    // nothing to do
                    return rgb;
                }
            }
        };
        if (im.getSource() == null) {
            System.out.println("Image source is null");
        }
        ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return toBufferedImage(Toolkit.getDefaultToolkit().createImage(ip));
    }

     public static BufferedImage invertImageHorizontally(BufferedImage img){
         if(img == null){
             System.out.println("Image is null!");
         }
        BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

        Graphics2D g2 = newImage.createGraphics();

        g2.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), img.getWidth(), 0, 0, img.getHeight(), null);

        return newImage;
    }

     /**
      * Makes the color of pixel at 1,1 transparent, then returns the image
      *
      * @param fileName
      * @return
      */
     public static BufferedImage loadImageAndMakeBackgroundTransparent(String fileName)
     {
         BufferedImage image = readImage(fileName);
         return ImageUtils.makeColorTransparent(image, ImageUtils.getColorOfPixel(image, 1, 1));
     }

     /**
      * Makes the color of pixel at 1,1 transparent, then returns the image
      *
      * @param fileName
      * @return
      */
     public static BufferedImage loadImageAndMakeBackgroundTransparent(URL file)
     {
         System.out.println("Loading image and making background transparent for: "+file);
         BufferedImage image = readImage(file);
         return ImageUtils.makeColorTransparent(image, ImageUtils.getColorOfPixel(image, 1, 1));
     }


}
