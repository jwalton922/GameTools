/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package spritecreator;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 *
 * @author Josh
 */
public class SpriteCreator extends JFrame{

    public static Dimension size = new Dimension (800,600);
    public JMenuBar mb;
    private String LOAD_FILE_CHOOSER = "LOAD_FILE_CHOOSER";
    private SpriteCreatorPanel creatorPanel;

    public SpriteCreator()
    {
        init();
    }

    private void init()
    {
        MMActionListener actionListener = new MMActionListener(this);
        creatorPanel = new SpriteCreatorPanel();
        mb = new JMenuBar();
        JMenuItem imageSelector = new JMenuItem("Select First Image");
        imageSelector.setActionCommand(LOAD_FILE_CHOOSER);
        imageSelector.addActionListener(actionListener);
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(imageSelector);
        mb.add(fileMenu);

        this.setJMenuBar(mb);
        this.add(creatorPanel);
    }

    private class MMActionListener implements ActionListener
    {
        private SpriteCreator sc;

        public MMActionListener(SpriteCreator sc)
        {
            this.sc = sc;
        }
        public void actionPerformed(ActionEvent e)
        {
            if(e.getActionCommand().equalsIgnoreCase(LOAD_FILE_CHOOSER))
            {
                JFileChooser jfc = new JFileChooser();
                jfc.setCurrentDirectory(new File("C:/isometric sprites"));
                SpriteImageFileFilter filter = new SpriteImageFileFilter();
                //jfc.setFileFilter((FileFilter)filter);
                int returnVal = jfc.showOpenDialog(sc);
                if(returnVal == JFileChooser.APPROVE_OPTION)
                {
                    //open file
                    creatorPanel.setInitialFile(jfc.getSelectedFile());
                } else
                {
                    //do nothing?
                }
            }
        }
    }



    public static void main(String[] args)
    {
        Thread t = new Thread( new Runnable()
        {
           public void run()
           {
                SpriteCreator sc = new SpriteCreator();
                sc.setSize(size );
                sc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                sc.setVisible(true);
           }
        });

        t.start();
    }
}
