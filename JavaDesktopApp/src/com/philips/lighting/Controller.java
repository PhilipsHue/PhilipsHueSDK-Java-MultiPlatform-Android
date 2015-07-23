package com.philips.lighting;

import java.util.List;
import java.util.Random;

import javax.swing.JDialog;

import com.philips.lighting.data.HueProperties;
import com.philips.lighting.gui.AccessPointList;
import com.philips.lighting.gui.DesktopView;
import com.philips.lighting.gui.LightColoursFrame;
import com.philips.lighting.gui.PushLinkFrame;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

public class Controller {

    private PHHueSDK phHueSDK;
    private DesktopView desktopView;
    
    private PushLinkFrame pushLinkDialog;
    private LightColoursFrame lightColoursFrame;
    
    private static final int MAX_HUE=65535;
    private Controller instance;

    public Controller(DesktopView view) {
        this.desktopView = view;
        this.phHueSDK = PHHueSDK.getInstance();
        this.instance = this;
    }

    public void findBridges() {
        phHueSDK = PHHueSDK.getInstance();
        PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        sm.search(true, true);
    }

    private PHSDKListener listener = new PHSDKListener() {

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPointsList) {
            desktopView.getFindingBridgeProgressBar().setVisible(false);
            AccessPointList accessPointList = new AccessPointList(accessPointsList, instance);
            accessPointList.setVisible(true);
            accessPointList.setLocationRelativeTo(null);  // Centre the AccessPointList Frame
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
            // Start the Pushlink Authentication.
            desktopView.getFindingBridgeProgressBar().setVisible(false);
            phHueSDK.startPushlinkAuthentication(accessPoint);
            
            pushLinkDialog = new PushLinkFrame(instance);
            pushLinkDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            pushLinkDialog.setModal(true);
            pushLinkDialog.setLocationRelativeTo(null); // Center the dialog.
            pushLinkDialog.setVisible(true);

        }

        @Override
        public void onBridgeConnected(PHBridge bridge, String username) {
            phHueSDK.setSelectedBridge(bridge);
            phHueSDK.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
            desktopView.getFindingBridgeProgressBar().setVisible(false);
            String lastIpAddress =  bridge.getResourceCache().getBridgeConfiguration().getIpAddress();   
            HueProperties.storeUsername(username);
            HueProperties.storeLastIPAddress(lastIpAddress);
            HueProperties.saveProperties();
            // Update the GUI.
            desktopView.getLastConnectedIP().setText(lastIpAddress);
            desktopView.getLastUserName().setText(username);
            // Close the PushLink dialog (if it is showing).
            if (pushLinkDialog!=null && pushLinkDialog.isShowing()) {
                pushLinkDialog.setVisible(false);
            }
            // Enable the Buttons/Controls to change the hue bulbs.s
            desktopView.getRandomLightsButton().setEnabled(true);
            desktopView.getSetLightsButton().setEnabled(true);

        }

        @Override
        public void onCacheUpdated(List<Integer> arg0, PHBridge arg1) {
        }

        @Override
        public void onConnectionLost(PHAccessPoint arg0) {
        }

        @Override
        public void onConnectionResumed(PHBridge arg0) {
        }

        @Override
        public void onError(int code, final String message) {

            if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
                desktopView.getFindingBridgeProgressBar().setVisible(false);
                desktopView.getFindBridgesButton().setEnabled(true);
                desktopView.getConnectToLastBridgeButton().setEnabled(true);
                desktopView.showDialog(message);
            }
            else if (code == PHMessageType.PUSHLINK_BUTTON_NOT_PRESSED) {
                pushLinkDialog.incrementProgress();
            }
            else if (code == PHMessageType.PUSHLINK_AUTHENTICATION_FAILED) {
                if (pushLinkDialog.isShowing()) {
                    pushLinkDialog.setVisible(false);
                    desktopView.showDialog(message);
                }
                else {
                    desktopView.showDialog(message);
                }
                desktopView.getFindBridgesButton().setEnabled(true);
            }
            else if (code == PHMessageType.BRIDGE_NOT_FOUND) {
                desktopView.getFindingBridgeProgressBar().setVisible(false);
                desktopView.getFindBridgesButton().setEnabled(true);
                desktopView.showDialog(message);
            }
        }

        @Override
        public void onParsingErrors(List<PHHueParsingError> parsingErrorsList) {  
            for (PHHueParsingError parsingError: parsingErrorsList) {
                System.out.println("ParsingError : " + parsingError.getMessage());
            }
        } 
    };

    public PHSDKListener getListener() {
        return listener;
    }

    public void setListener(PHSDKListener listener) {
        this.listener = listener;
    }

    public void randomLights() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        PHBridgeResourcesCache cache = bridge.getResourceCache();

        List<PHLight> allLights = cache.getAllLights();
        Random rand = new Random();

        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setHue(rand.nextInt(MAX_HUE));
            bridge.updateLightState(light, lightState); // If no bridge response is required then use this simpler form.
        }
    }

    public void showControlLightsWindow() {
        if (lightColoursFrame == null) {
            lightColoursFrame = new LightColoursFrame(); 
        }
        lightColoursFrame.setLocationRelativeTo(null); // Centre window
        lightColoursFrame.setVisible(true);
    }
    
    /**
     * Connect to the last known access point.
     * This method is triggered by the Connect to Bridge button but it can equally be used to automatically connect to a bridge.
     * 
     */
    public boolean connectToLastKnownAccessPoint() {
        String username = HueProperties.getUsername();
        String lastIpAddress =  HueProperties.getLastConnectedIP();     
        
        if (username==null || lastIpAddress == null) {
            desktopView.showDialog("Missing Last Username or Last IP.  Last known connection not found.");
            return false;
        }
        PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(lastIpAddress);
        accessPoint.setUsername(username);
        phHueSDK.connect(accessPoint);
        return true;
    }

    public void enableFindBridgesButton() {
        desktopView.getFindBridgesButton().setEnabled(true);
    }
    
    public void showProgressBar() {
        desktopView.getFindingBridgeProgressBar().setVisible(true);
    }
}
