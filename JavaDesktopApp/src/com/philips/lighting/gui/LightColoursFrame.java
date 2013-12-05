package com.philips.lighting.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
/**
 *  LightColoursFrame.java                
 *  An example showing how to change Bulb Colours using a JColorChooser.
 *
 */
public class LightColoursFrame extends JFrame  {

  private static final long serialVersionUID = -3830092035262367974L;
  private PHHueSDK phHueSDK;  
    
  private JList <String> lightIdentifiersList;
  private List<PHLight> allLights;
  
  public LightColoursFrame() {
    super("Bulb Colour Changer");
    
    // The the HueSDK singleton.
    phHueSDK = PHHueSDK.getInstance();
    
    Container content = getContentPane();
   
    // Get the selected bridge.
    PHBridge bridge = phHueSDK.getSelectedBridge(); 
    
    // To get lights use the Resource Cache.  
    allLights = bridge.getResourceCache().getAllLights();
   
    DefaultListModel <String> sampleModel = new DefaultListModel<String>();
    
    for (PHLight light : allLights) {
        sampleModel.addElement(light.getIdentifier() + "  " + light.getName() );
    }

    lightIdentifiersList = new JList<String>(sampleModel);
    lightIdentifiersList.setVisibleRowCount(4);
    lightIdentifiersList.setSelectedIndex(0);

    JScrollPane listPane = new JScrollPane(lightIdentifiersList);
    listPane.setPreferredSize(new Dimension(300,100));
    
    JPanel listPanel = new JPanel();
    listPanel.setBackground(Color.white);
    
    Border listPanelBorder = BorderFactory.createTitledBorder("My Lights");
    listPanel.setBorder(listPanelBorder);
    listPanel.add(listPane);
    content.add(listPanel, BorderLayout.CENTER);
    
    JButton changeColourButton = new JButton("Change Bulb Colour");
    changeColourButton.addActionListener(new ColourChanger());
    
    Border buttonPanelBorder = BorderFactory.createTitledBorder("Change Selected");
    JPanel buttonPanel = new JPanel();
    buttonPanel.setBackground(Color.white);
    buttonPanel.setBorder(buttonPanelBorder);
    buttonPanel.add(changeColourButton);
    
    content.add(buttonPanel, BorderLayout.SOUTH);
    setPreferredSize(new Dimension(400,250));
    pack();
    setVisible(true);
  }

  private class ColourChanger implements ActionListener {
    public void actionPerformed(ActionEvent event) {
        
        Color lightColour  = JColorChooser.showDialog(getContentPane(), "Choose Bulb Color", getBackground());

        if (lightColour !=null) {
            int selectedBulb = lightIdentifiersList.getSelectedIndex();
            if (selectedBulb !=-1) {
                int selectedIndex= lightIdentifiersList.getSelectedIndex();
                String lightIdentifer = allLights.get(selectedIndex).getIdentifier();
                
                PHLightState lightState = new PHLightState();
                float xy[] = PHUtilities.calculateXYFromRGB(lightColour.getRed(), lightColour.getGreen(), lightColour.getBlue(), "LCT001");
                lightState.setX(xy[0]);
                lightState.setY(xy[1]);
                 
                phHueSDK.getSelectedBridge().updateLightState(lightIdentifer, lightState, null);  // null is passed here as we are not interested in the response from the Bridge. 
                
            }
        }
    }
  }
  
}