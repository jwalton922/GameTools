/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SpriteCreatorPanel.java
 *
 * Created on Apr 17, 2011, 8:35:36 AM
 */

package spritecreator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import utils.ImageUtils;

/**
 *
 * @author Josh
 */
public class SpriteCreatorPanel extends javax.swing.JPanel {
    private ArrayList<String> directions = new ArrayList<String>();
    private HashMap<String, ArrayList<File>> directionToImagesMap = new HashMap<String, ArrayList<File>>();
    private SpriteImagePanel sip;
    private String action = "";

    /** Creates new form SpriteCreatorPanel */
    public SpriteCreatorPanel() {
        initComponents();
        init();
    }

    private void init()
    {
        MMActionListener al = new MMActionListener(this);
        chooseDirectoryButton.addActionListener(al);
        createSpriteButton.addActionListener(al);
        sip = new SpriteImagePanel(null);
        sip.setSize(new Dimension(800,300));
        System.out.println("Size of SpriteImagePanel: "+sip.getSize());
        sip.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        spriteHolderPanel.add(sip);
        sip.setVisible(true);
        directions.add("e");
        directions.add("se");
        directions.add("s");
        directions.add("sw");
        directions.add("w");
        directions.add("nw");
        directions.add("n");
        directions.add("ne");

    }

    public void setInitialFile(File f)
    {
        selectedImageLabel.setText("");
        String fileName= f.getPath();
        System.out.println("Trying to load image: "+fileName);
        try {
            BufferedImage bi = ImageUtils.loadImageAndMakeBackgroundTransparent(fileName);
            ImageIcon icon = new ImageIcon(bi);
            selectedImageLabel.setIcon(icon);
            imageNameLabel.setText(fileName);
            this.repaint();
        } catch(Exception e)
        {
            System.out.println("Error trying to load "+fileName);
            e.printStackTrace();
        }

        //try to find other files
        System.out.println("Parent file: "+f.getParent());
        String[] split = f.getName().split(" ");
        action = split[0];
        System.out.println("Action="+action);

        File dir = f.getParentFile();

        directionToImagesMap = new HashMap<String, ArrayList<File>>();
        for(int i = 0; i < directions.size(); i++)
        {
            ArrayList<File> imagesToMakeSpriteFrom = new ArrayList<File>();
            File[] files = dir.listFiles();
            String fileNameStart = action+" "+directions.get(i);
            for(int j = 0; j < files.length; j++)
            {
                if(files[j].getName().startsWith(fileNameStart))
                {
                    imagesToMakeSpriteFrom.add(files[j]);
                }
            }

            maxImagesField.setText(""+imagesToMakeSpriteFrom.size());
            directionToImagesMap.put(directions.get(i), imagesToMakeSpriteFrom);
        }
    }

    private class MMActionListener implements ActionListener {

        private SpriteCreatorPanel scp;

        public MMActionListener(SpriteCreatorPanel scp)
        {
            this.scp = scp;
        }

        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource().equals(chooseDirectoryButton))
            {
                JFileChooser jfc = new JFileChooser();
                jfc.setCurrentDirectory(new File("C:/createdSprites"));
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = jfc.showOpenDialog(scp);

                if(returnVal == JFileChooser.APPROVE_OPTION)
                {
                    directoryField.setText(jfc.getSelectedFile().getAbsolutePath());
                }
            }
            else if(e.getSource().equals(createSpriteButton))
            {
                Iterator<String> it = directionToImagesMap.keySet().iterator();
                while(it.hasNext())
                {
                    String direction = it.next();
                    String outputFileName = directoryField.getText()+"\\"+action+"_"+direction+"."+(String)imageTypeComboBox.getSelectedItem();
                    createSprite(outputFileName, (String)imageTypeComboBox.getSelectedItem(), directionToImagesMap.get(direction));
                }
//                sip.emptyImageHolder();
//                String outputSpriteName = directoryField.getText()+"\\"+spriteFileNameField.getText()+"."+(String)imageTypeComboBox.getSelectedItem();
//                System.out.println("Creating sprite: "+outputSpriteName);
//                scp.createSprite(outputSpriteName, (String)imageTypeComboBox.getSelectedItem(), imagesToMakeSpriteFrom);
            }
        }
    }

    protected void createSprite(String fileName, String type, ArrayList<File> frameImages)
    {
        try {
            String sizeString = (String)sizeComboBox.getSelectedItem();
            String[] sizeSplit = sizeString.split("x");
            int width = Integer.parseInt(sizeSplit[0]);
            int height = Integer.parseInt(sizeSplit[1]);
            int numFrames = Integer.parseInt(maxImagesField.getText());
            int numFramesPerRow = Integer.parseInt(numImagesAcrossField.getText());
            int numRows = (int)Math.ceil((numFrames / (1.0*numFramesPerRow)));
            numFramesPerRow = (numFrames >= numFramesPerRow ? numFramesPerRow : numFrames);
            int spriteImageWidth = numFramesPerRow * width;
            int spriteImageHeight = numRows * height;
            System.out.println("Creating sprite image with size: "+spriteImageWidth+"x"+spriteImageHeight);
            BufferedImage spriteImage = new BufferedImage(spriteImageWidth, spriteImageHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = spriteImage.createGraphics();
            int row = 0;
            int col = 0;
            for(int i = 0; i < frameImages.size(); i++)
            {
                int x = col*width;
                int y = row*height;
                BufferedImage frameImage = ImageUtils.loadImageAndMakeBackgroundTransparent(frameImages.get(i).getAbsolutePath());
                g2.drawImage(frameImage, x, y, x+width, y+height, 0, 0, frameImage.getWidth(), frameImage.getHeight(), null);
                System.out.println("drawing image of size "+frameImage.getWidth()+"x"+frameImage.getHeight()+" at "+x+"x"+y);
                sip.paintImageToComponent(frameImage, x, y, x+width, y+height, 0, 0, frameImage.getWidth(), frameImage.getHeight());
                col++;
                if(col >= numFramesPerRow)
                {
                    col = 0;
                    row++;
                }
            }
            spriteImage = ImageUtils.makeColorTransparent(spriteImage, ImageUtils.getColorOfPixel(spriteImage, 0, 0));
            ImageIO.write(spriteImage, type, new File(fileName));
        } catch(Exception e)
        {
            System.out.println("Error create sprite: "+fileName);
            e.printStackTrace();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        selectedImageLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        imageNameLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        numImagesAcrossField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        imageTypeComboBox = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        sizeComboBox = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        maxImagesField = new javax.swing.JTextField();
        createSpriteButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        spriteFileNameField = new javax.swing.JTextField();
        chooseDirectoryButton = new javax.swing.JButton();
        directoryField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        startingNumberField = new javax.swing.JTextField();
        spriteHolderPanel = new javax.swing.JPanel();

        setMaximumSize(new java.awt.Dimension(800, 600));
        setMinimumSize(new java.awt.Dimension(800, 600));

        selectedImageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        selectedImageLabel.setText("jLabel1");
        selectedImageLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        selectedImageLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectedImageLabel.setMaximumSize(new java.awt.Dimension(64, 64));
        selectedImageLabel.setMinimumSize(new java.awt.Dimension(64, 64));
        selectedImageLabel.setPreferredSize(new java.awt.Dimension(64, 64));

        jLabel2.setText("Selected Image:");

        imageNameLabel.setText("NO IMAGE SELECTED");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Sprite Options"));

        jLabel1.setText("Num Images Across:");

        numImagesAcrossField.setText("5");
        numImagesAcrossField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                numImagesAcrossFieldActionPerformed(evt);
            }
        });

        jLabel3.setText("Sprite Image Type:");

        imageTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BMP", "PNG", "JPG" }));
        imageTypeComboBox.setSelectedIndex(1);
        imageTypeComboBox.setAutoscrolls(true);

        jLabel4.setText("Size of each frame:");

        sizeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "32x32", "64x64", "96x96", "128x128" }));
        sizeComboBox.setSelectedIndex(2);

        jLabel5.setText("Max number of images:");

        maxImagesField.setText("##");

        createSpriteButton.setText("Create Sprite");
        createSpriteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createSpriteButtonActionPerformed(evt);
            }
        });

        jLabel6.setText("Sprite File Name:");

        spriteFileNameField.setText("NEWSPRITE");

        chooseDirectoryButton.setText("Choose Directory");

        directoryField.setText("C:/createdSprites");
        directoryField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                directoryFieldActionPerformed(evt);
            }
        });

        jLabel7.setText("Starting Number:");

        startingNumberField.setText("##");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(createSpriteButton, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(sizeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(imageTypeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(numImagesAcrossField))
                                .addGap(45, 45, 45)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(startingNumberField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(maxImagesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(spriteFileNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chooseDirectoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(directoryField, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(numImagesAcrossField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(maxImagesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(imageTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(startingNumberField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(sizeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(spriteFileNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chooseDirectoryButton)
                    .addComponent(directoryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                .addComponent(createSpriteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        spriteHolderPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout spriteHolderPanelLayout = new javax.swing.GroupLayout(spriteHolderPanel);
        spriteHolderPanel.setLayout(spriteHolderPanelLayout);
        spriteHolderPanelLayout.setHorizontalGroup(
            spriteHolderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 788, Short.MAX_VALUE)
        );
        spriteHolderPanelLayout.setVerticalGroup(
            spriteHolderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 275, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(spriteHolderPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(selectedImageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(imageNameLabel, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(selectedImageLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(imageNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spriteHolderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void numImagesAcrossFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_numImagesAcrossFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_numImagesAcrossFieldActionPerformed

    private void directoryFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_directoryFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_directoryFieldActionPerformed

    private void createSpriteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createSpriteButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_createSpriteButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton chooseDirectoryButton;
    private javax.swing.JButton createSpriteButton;
    private javax.swing.JTextField directoryField;
    private javax.swing.JLabel imageNameLabel;
    private javax.swing.JComboBox imageTypeComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField maxImagesField;
    private javax.swing.JTextField numImagesAcrossField;
    private javax.swing.JLabel selectedImageLabel;
    private javax.swing.JComboBox sizeComboBox;
    private javax.swing.JTextField spriteFileNameField;
    private javax.swing.JPanel spriteHolderPanel;
    private javax.swing.JTextField startingNumberField;
    // End of variables declaration//GEN-END:variables

}
