package com.philips.lighting.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.MouseInputAdapter;

import com.philips.lighting.Controller;
import com.philips.lighting.data.HueProperties;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;

public class AccessPointList extends  JFrame {

 private static final long serialVersionUID = -8224855537302531427L;
 private JList<DefaultListModel<String>>     listbox;

 
 public AccessPointList(final List<PHAccessPoint> accessPointsList, final Controller controller)
 {
     // Set the frame characteristics
     setTitle( "PHAccess Points List" );
     setSize( 400, 200 );
     setBackground( Color.gray );

     // Create a panel to hold all other components
     JPanel topPanel = new JPanel();
     topPanel.setLayout( new BorderLayout() );
     getContentPane().add( topPanel );

     DefaultListModel <String> listModel = new DefaultListModel<String>();
     
     for (PHAccessPoint accessPoint: accessPointsList) {
         listModel.addElement(accessPoint.getIpAddress());
     }
     
     // Create a new listbox control
     listbox = new JList( listModel );
     listbox.setVisibleRowCount(10);
     JScrollPane listPane = new JScrollPane(listbox);

     topPanel.add( listPane, BorderLayout.CENTER );
     
     listbox.addMouseListener(new MouseInputAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
              int mouseClickedIndex = listbox.getSelectedIndex();
             
              controller.showProgressBar();
              PHHueSDK phHueSDK = PHHueSDK.getInstance();
              phHueSDK.connect(accessPointsList.get(mouseClickedIndex));
              setVisible(false);
         }
         
     }); 
     
     
     addWindowListener(new java.awt.event.WindowAdapter() {
         @Override
         public void windowClosing(java.awt.event.WindowEvent e) {
               controller.enableFindBridgesButton();  // Enable the Find New Bridges button if the Frame is closed!
         }
     });
  }

}