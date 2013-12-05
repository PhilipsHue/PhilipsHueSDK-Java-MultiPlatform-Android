package com.philips.lighting.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.philips.lighting.Controller;

/**
 *   PushLinkFrame.java
 *   For first time access to the Bridge the user must PushLink their bridge to prove they have physical access to the bridge.
 *   
 *   For more information see:
 *   http://developers.meethue.com
 */

public class PushLinkFrame extends JDialog {
    
    private static final long serialVersionUID = -8806693739602901100L;
    private Font font;
    private JProgressBar progressBar;
    
    public PushLinkFrame(final Controller controller) {
        font = new Font("Verdana", Font.BOLD, 16);
        setBounds(100, 100, 520, 600);
        add(new PushLinkImagePanel(), BorderLayout.CENTER);
        
        // Add the progress bar.  The user has 30 seconds to Pushlink the bridge.
        progressBar = new JProgressBar(1, 30);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        
        add(progressBar, BorderLayout.SOUTH);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                  controller.enableFindBridgesButton();  // Enable the Find New Bridges button if the Frame is closed!
            }
        });
    }
    
    public void incrementProgress() {
        int value = progressBar.getValue();
        value++;
       
        progressBar.setValue(value);
        progressBar.repaint();
    }

    public class PushLinkImagePanel extends JPanel {

        private static final long serialVersionUID = -581903426574990512L;
        private BufferedImage image;

        public PushLinkImagePanel() {
            try {
                image = ImageIO.read(PushLinkFrame.class.getResourceAsStream("/pushlink_image.png"));
            } catch (IOException ex) {
                // Handle the IOException
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, null); 
            g.setFont(font);
            g.drawString("Press the link button on the bridge", 20, 60);
        }

    }
}