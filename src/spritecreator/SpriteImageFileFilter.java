/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package spritecreator;

import java.io.File;
import javax.swing.filechooser.FileFilter;


/**
 *
 * @author Josh
 */
public class SpriteImageFileFilter extends FileFilter{

    public boolean accept(File f)
    {
        boolean isAcceptable = false;

        String fileType = f.getName().split(".")[1];

        if(fileType.equalsIgnoreCase("bmp") ||
           fileType.equalsIgnoreCase("png") ||
           fileType.equalsIgnoreCase("jpg"))
        {
            isAcceptable = true;
        }

        return isAcceptable;
    }

    public String getDescription()
    {
        return "Image Filter";
    }



}
