package com.philips.lighting.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import layout.TableLayout;

import com.philips.lighting.Controller;
import com.philips.lighting.data.HueProperties;

/**
 * DesktopView.java
 * 
 * The main GUI showing last connected IP/Username and buttons for Finding Bridges and Changing the Hue Lights, once connected to a bridge.
 *
 */
public class DesktopView extends JFrame {

    private static final long serialVersionUID = -7469471678945429320L;  
    private Controller controller;
    private JButton setLightsButton;
    private JButton randomLightsButton;
    private JButton findBridgesButton;
    private JButton connectToLastBridgeButton;
    private JProgressBar findingBridgeProgressBar;
    
    private JTextField lastConnectedIP;
    private JTextField lastUserName;

    public DesktopView(){
        setTitle("Hue Desktop");
        JPanel mainPanel = new JPanel();
        
        // TODO - Move to another class
        JPanel controls = new JPanel();
        controls.setLayout(new GridLayout(2,3));
         
        findingBridgeProgressBar = new JProgressBar();
        findingBridgeProgressBar.setBorderPainted(false);
        findingBridgeProgressBar.setIndeterminate(true);
        findingBridgeProgressBar.setVisible(false);
        
        //Set up components preferred size
        String lastUsername = HueProperties.getUsername(); 
        String lastConnectedIPStr = HueProperties.getLastConnectedIP();
        
        JLabel labelLastConIP    = new JLabel("Last Connected IP:");
        lastConnectedIP = new JTextField(lastConnectedIPStr);
 
        lastConnectedIP.setEditable(false);
        JLabel labelLastUsername = new JLabel("Last UserName:");
        lastUserName = new JTextField(lastUsername);
        lastUserName.setEditable(false);
        findBridgesButton = new JButton("Find New Bridges");
        findBridgesButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                findBridgesButton.setEnabled(false);
                connectToLastBridgeButton.setEnabled(false);
                controller.findBridges();
                findingBridgeProgressBar.setBorderPainted(true);
                findingBridgeProgressBar.setVisible(true);
            }
        });
        
        connectToLastBridgeButton = new JButton("Auto Connect");
        connectToLastBridgeButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (controller.connectToLastKnownAccessPoint()) {
                    connectToLastBridgeButton.setEnabled(false);
                    findBridgesButton.setEnabled(false);
                    findingBridgeProgressBar.setBorderPainted(true);
                    findingBridgeProgressBar.setVisible(true);
                }
            }
        });
        
        
        setLightsButton = new JButton("Change Light Colours");
        setLightsButton.setEnabled(false);
        setLightsButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                controller.showControlLightsWindow();
            }
        });
        
        randomLightsButton = new JButton("Randomize Lights");
        randomLightsButton.setEnabled(false);
        randomLightsButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                controller.randomLights();
            }
        });
        
        double border = 10;
        double size[][] =
            {{border, 160, 20, 300, 20, 160},                 // Columns
             {border, 26,  10, 26, 26, 26,6,26}}; // Rows

        mainPanel.setLayout (new TableLayout(size));

        
        mainPanel.add(labelLastConIP,            " 1, 1");
        mainPanel.add(lastConnectedIP,           " 3, 1");

        mainPanel.add(labelLastUsername,         " 1, 3");
        mainPanel.add(lastUserName,              " 3, 3");
        
        mainPanel.add(findingBridgeProgressBar,  " 3, 5");
        
        mainPanel.add(connectToLastBridgeButton, " 5, 1");
        mainPanel.add(findBridgesButton,         " 5, 3");
        
        mainPanel.add(randomLightsButton,        " 5, 5");
        mainPanel.add(setLightsButton,           " 5, 7");
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(700,270));
        
        getContentPane().add(new JLabel("   An example Java/Swing Desktop Application to control your Hue Lights."), BorderLayout.NORTH);
        getContentPane().add(mainPanel, BorderLayout.CENTER);


        //4. Size the frame.
        pack();
        setLocationRelativeTo(null); // Centre the window.
        setVisible(true);        
        
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public JButton getSetLightsButton() {
        return setLightsButton;
    }

    public JButton getRandomLightsButton() {
        return randomLightsButton;
    } 
    
    public JButton getFindBridgesButton() {
        return findBridgesButton;
    } 

    public JButton getConnectToLastBridgeButton() {
        return connectToLastBridgeButton;
    } 
    
    public void showDialog(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public JProgressBar getFindingBridgeProgressBar() {
        return findingBridgeProgressBar;
    }

    public JTextField getLastConnectedIP() {
        return lastConnectedIP;
    }

    public JTextField getLastUserName() {
        return lastUserName;
    }
}