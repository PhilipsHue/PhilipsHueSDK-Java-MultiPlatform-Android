The Hue SDK by Philips
===============
 (c) Copyright Philips 2012-2014
Introduction
----------------
The Java/Android Hue SDK is a set of tools that are designed to make it easy to access the Hue system through the Hue Wi-Fi network connected bridge and control an associated set of connected lamps. The aim of the SDK is to enable you to create your own applications for the Hue system.
The tools are provided with documentation for the SDK and example code. They are designed to be flexible, whilst easing the use of the more complex components of the system.

The Hue SDK Components
-------------------------------------
###The SDK API
The SDK libraries (huelocalsdk.jar and sdkresources.jar) are the main API libraries for using the Hue SDK. They contain all of the main tools to access the Hue system.  
<b>Note</b> that as of v1.1.1 they are platform independent so they can be used on Android or Java Desktop systems alike.
###The Wizards
The Wizards are user interface components that are provided as source and access the Hue SDK to operate. They are provided to cover functionality such as configuration that is best given as source. Providing the source and graphics resources grants you the option to change the look and feel to match your own application's design.

###The Documentation
Documentation is provided in documents such as this, other media, and code comments. 

###The Sample Apps
3 Sample Apps are provided to show the usage of the Hue SDK.

* QuickStart Sample App - This is a skeleton/bare bones app with the minimal functionality needed to connect to a bridge (with authentication/pushlink) including some basic code to update lights. Ideal starting point for a developer wanting to write their own Hue app.

* Example App - A more complete app showing a lot more Hue functionality such as timers, groups, find new lights and gui components for controlling Hue.

* Java Desktop App - A Java Desktop Application written in Swing showing how to connect to your Hue Lights.

How to structure your app for the Hue SDK
---------------------------------------------------------
These are the key things you need to consider when building your app for the Hue SDK.
###The bridge controls the lights
The hardware bridge that you connect to your Wi-Fi network controls the connected lights. This means that all communication with the lights goes via the bridge
###The SDK connects to a bridge
To operate, the SDK must connect to a bridge
###Find a bridge
The SDK has functionality to find local bridges and the provided Apps contain Wizard UI components for the user to select the bridge to use
###Push Linking
The button on the bridge itself must be pressed to connect to the SDK.  The SDK provides the code and App Wizard UI components for this.
###Find the lights
The bridge should be instructed to find any unallocated lights in the locality. It will search using Zigbee to find them
###Identifiers for lights
The found lights are given unique identifiers by the bridge. These identifiers can be used to reference specific lights in SDK method calls
###The Bridge Send API sends commands to the lights 
This API allows the app to command the bridge to set light states (colours etc), group commands to lights, set schedules for light events etc.
###The heartbeat gets the state of the bridge and light settings
The Heartbeat runs at regular intervals in the SDK and each interval the latest state of the lights, configuration etc, is collected from the bridge and stored in the Bridge Resources cache.
###Use the Bridge Resources Cache to read the state of the lights etc.
The Bridge keeps itself in sync with the lights and the Bridge Resources Cache is a copy of the last read setting.
The app should read the Bridge Resources Cache objects to get the latest settings for lights, schedules etc.
###Notifications
Android Notifications are used by the SDK. The App can receive notifications as events occur such as bridge connection notification or upon updating of a light state.
The 1-2-3 Quick Start for your SDK app
-----------------------------------------------------
1. Connect the SDK to the Bridge and Find Lights
2. Send commands via the Bridge Send API
3. Read the Bridge Resources Cache to see current light settings.


Hue SDK Components
------------------------------
###The Heartbeat
The Heartbeat is a regular timed event that occurs on the SDK once it is running and initialized. The Heartbeat is used to collect the latest status of the bridge and Hue Lamps. The Heartbeat serves the purpose of allowing for other bridge clients, both local and remote, to make changes to settings of lamps via the bridge. During the Heartbeat, the SDK caches the current status returned from the bridge
###Notifications
Notifications are used by the SDK. The notification architecture allows the SDK to post events as they occur. Clients can register to receive notifications in order to process them. An example would be that the cache has updated information (changes) on the lamp settings. -Registering for this notification will allow a UI component to update its display settings for the lamps.
###Bridge Send API
The Bridge Send API provides methods to set and get the status of Bridge resources(lights, schedules etc). Calls through the Bridge Send API typically result in changes in state of the lamps connected to the bridge. These state changes are then shown in the Bridge Resources Cache when it updates.
###PHBridgeResourcesCache
The SDK provides visibility of the bridge and the resources it is controlling through the PHBridgeResourcesCache object.
This object contains the following objects that show the current bridge state:
###PHBridgeConfiguration
This object contains information on the configuration and settings of the bridge itself
###PHLights
This collection of PHLight objects contains details of each light connected to the bridge
###PHGroups
Each group in this collection of groups contains a set of PHLight objects
###PHSchedules
The collection of PHSchedules held in the bridge. Each schedule contains a date/time and the bridge action to be carried out at that time.
###PHLightState
Represents a light state setting for the lights properties.

##Bridge Discovery and Connection
When initializing, the SDK can optionally find a bridge on the local network and connect to it. If no local bridge is found, the SDK, will timeout and return a notification.

##The Wizards
The Wizards are supplied in the Example App as source code components that use the SDK. You may adapt and modify them, but they will make it easier to:
1. Discover the bridge
2. Connect to the bridge
3. Push link to the bridge 
4. Bridge Configuration
5. Find lights

##Build Path.

The Android SDK requires both huelocalsdk.jar and sdkresources.jar to be on your project build path.

##Walking through the Quick Start App code

The below code covers the implementation only.  For a more complete set of UI components check the Example App.

###AndroidManifest.xml
    
Add Internet and Get Tasks permission and any activities your app requires.
	
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.GET_TASKS"/>

	<activity
       android:name="com.xxxx.xxx.MyApplicationActivity"
       android:label="@string/app_name" >
    </activity>

###PHHomeActivity.java
Main purpose of the PHHomeActivity is to: handle automatic connection to the last connected bridge, stop/start the heartbeat, display a list of available bridges and calling the PushLink and main activities.

**Startup:**
**The PHHueSDK singleton instance is created.**

**We also start the searches for available bridges. The PHSDKListener notifies us of the connection status.**

    
	private phHueSDK;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    // Create the HueSDK singleton
	    phHueSDK = PHHueSDK.create();
	    
	    // Set the Device Name (name of your app). This will be stored in your bridge whitelist entry.
        phHueSDK.setDeviceName("QuickStartApp");
		
		// Register the PHHueListener to receive callback notifications on Bridge events.
		phHueSDK.getNotificationManager().registerSDKListener(listener);
		
		// Try to automatically connect to the last known bridge.
        prefs = HueSharedPreferences.getInstance(getApplicationContext());
        String lastIpAddress   = prefs.getLastConnectedIPAddress();
        String lastUsername    = prefs.getUsername();

        // Automatically try to connect to the last connected IP Address.  For multiple bridge support a different implementation is required.
        if (lastIpAddress !=null && !lastIpAddress.equals("")) {
            PHAccessPoint lastAccessPoint = new PHAccessPoint();
            lastAccessPoint.setIpAddress(lastIpAddress);
            lastAccessPoint.setUsername(lastUsername);
           
            if (!phHueSDK.isAccessPointConnected(lastAccessPoint)) {
               PHWizardAlertDialog.getInstance().showProgressDialog(R.string.connecting, PHHomeActivity.this);
               phHueSDK.connect(lastAccessPoint);
            }
        }
        else {  // First time use, so perform a bridge search.
            doBridgeSearch();
        }

	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.find_new_bridge:
            doBridgeSearch();
            break;
        }
        return true;
    }
	
	// Create a Listener to receive bridge notifications.
	public PHSDKListener listener=new PHSDKListener() {

		@Override
		public void onCacheUpdated(int flags, PHBridge bridge) {
		}

        @Override
        public void onBridgeConnected(PHBridge b) {
            phHueSDK.setSelectedBridge(b);
            phHueSDK.enableHeartbeat(b, PHHueSDK.HB_INTERVAL);
            phHueSDK.getLastHeartbeat().put(b.getResourceCache().getBridgeConfiguration() .getIpAddress(), System.currentTimeMillis());
            prefs.setLastConnectedIPAddress(b.getResourceCache().getBridgeConfiguration().getIpAddress());
            prefs.setUsername(prefs.getUsername());
            PHWizardAlertDialog.getInstance().closeProgressDialog();
            startMainActivity();
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
            Log.w(TAG, "Authentication Required.");
            
            phHueSDK.startPushlinkAuthentication(accessPoint);
            startActivity(new Intent(PHHomeActivity.this, PHPushlinkActivity.class));
           
        }

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPoint) {
            Log.w(TAG, "Access Points Found. " + accessPoint.size());

            PHWizardAlertDialog.getInstance().closeProgressDialog();
            if (accessPoint != null && accessPoint.size() > 0) {
                    phHueSDK.getAccessPointsFound().clear();
                    phHueSDK.getAccessPointsFound().addAll(accessPoint);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.updateData(phHueSDK.getAccessPointsFound());
                       }
                   });
                   
            } else {
                // FallBack Mechanism.  If a UPNP Search returns no results then perform an IP Scan. 
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    PHWizardAlertDialog.getInstance().showProgressDialog(R.string.search_progress, PHHomeActivity.this);
                    PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
                    // Start the IP Scan Search if the UPNP and NPNP return 0 results.
                    sm.search(false, false, true);
                }
            }
            
        }

        @Override
        public void onError(int code, final String message) {
            Log.e(TAG, "on Error Called : " + code + ":" + message);

            if (code == PHHueError.NO_CONNECTION) {
                Log.w(TAG, "On No Connection");
            } 
            else if (code == PHHueError.AUTHENTICATION_FAILED || code==1158) {  
                PHWizardAlertDialog.getInstance().closeProgressDialog();
            } 
            else if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
                Log.w(TAG, "Bridge Not Responding . . . ");
                PHWizardAlertDialog.getInstance().closeProgressDialog();
                PHHomeActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PHWizardAlertDialog.showErrorDialog(PHHomeActivity.this, message, R.string.btn_ok);
                    }
                }); 

            } 
            else if (code == PHMessageType.BRIDGE_NOT_FOUND) {
                PHWizardAlertDialog.getInstance().closeProgressDialog();

                PHHomeActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PHWizardAlertDialog.showErrorDialog(PHHomeActivity.this, message, R.string.btn_ok);
                    }
                });                
            }
        }


		@Override
		public void onConnectionResumed(PHBridge bridge) {	
		}

		@Override
		public void onConnectionLost(PHAccessPoint accessPoints) {	
		}
		
	};
	
	public void doBridgeSearch() {
      PHWizardAlertDialog.getInstance().showProgressDialog(R.string.search_progress, PHHomeActivity.this);
      PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
      // Start the UPNP Searching of local bridges.
      sm.search(true, true);
    }
	
###PHPushlinkActivity.java
**If first time authentication is required the PushLink activity is called. A progress bar is displayed allowing the user 30 seconds to push the button on the bridge.**

    public class PHPushlinkActivity extends Activity{
		ProgressBar pbar;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.pushlink);
			setTitle(R.string.txt_pushlink);
			pbar = (ProgressBar) findViewById(R.id.countdownPB);
			pbar.setMax(30);
			
		    phHueSDK.getNotificationManager().registerSDKListener(listener);
		}
		
		@Override
		protected void onStart() {
			PHHueSDK phHueSDK = PHHueSDK.getInstance(getApplicationContext());
			phHueSDK.setCurrentActivty(this);
			super.onStart();
		}

		public void  incrementProgress(){
			pbar.incrementProgressBy(1);
		}
		
	// Create a listener to listen to responses from the bridge and increment the progress bar. 	
    private PHSDKListener listener = new PHSDKListener() {

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> arg0) {}

        @Override
        public void onAuthenticationRequired(PHAccessPoint arg0) {}

        @Override
        public void onBridgeConnected(PHBridge arg0) {}

        @Override
        public void onCacheUpdated(int arg0, PHBridge arg1) {}

        @Override
        public void onConnectionLost(PHAccessPoint arg0) {}

        @Override
        public void onConnectionResumed(PHBridge arg0) {}

        @Override
        public void onError(int code, final String message) {
            if (code == PHMessageType.PUSHLINK_BUTTON_NOT_PRESSED) {
                incrementProgress();
            }
            else if (code == PHMessageType.PUSHLINK_AUTHENTICATION_FAILED) {
                incrementProgress();

                if (!isDialogShowing) {
                    isDialogShowing=true;
                    PHPushlinkActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PHPushlinkActivity.this);
                            builder.setMessage(message).setNeutralButton(R.string.btn_ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            finish();
                                        }
                                    });

                            builder.create();
                            builder.show();
                        }
                    });
                }
                
            }

        } // End of On Error
    };		

    }

**Once connected to a bridge your application activity is started and the fun begins.**	
	
###MyApplicationActivity
This is where you can create your own app.  If you rename the Activity don't forget to update your AndroidManfiest.xml!

**Once connected to a bridge there are various ways to change a light state**

    private static final int MAX_HUE=65535;
	
	PHBridge bridge = phHueSDK.getSelectedBridge();
    ArrayList<PHLight> allLights = bridge.getResourceCache().getAllLights();
    Random rand = new Random();
        
    for (PHLight light : allLights) {
        PHLightState lightState = new PHLightState();
        lightState.setHue(rand.nextInt(MAX_HUE));
        bridge.updateLightState(light, lightState, listener);
        //  bridge.updateLightState(light, lightState);   // If no bridge response is required then use this simpler form.
    }
	
    // If you want to handle the response from the bridge, create a PHLightListener object.
    PHLightListener listener = new PHLightListener() {
        
        @Override
        public void onSuccess() {  
        }
        
        @Override
        public void onStateUpdate(Hashtable<String, String> arg0, ArrayList<PHHueError> arg1) {
           Log.w(TAG", "Light has updated");
        }
        
        @Override
        public void onError(int arg0, String arg1) {  
        }
    };


    
Philips releases this SDK with friendly house rules. These friendly house rules are part of a legal framework; this to protect both the developers and hue. The friendly house rules cover e.g. the naming of Philips and of hue which can only be used as a reference (a true and honest statement) and not as a an brand or identity. Also covered is that the hue SDK and API can only be used for hue and for no other application or product. Very common sense friendly rules that are common practice amongst leading brands that have released their SDK’s.


Copyright (c) 2012- 2013, Philips Electronics N.V. All rights reserved.
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 
* Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 
* Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 
* Neither the name of Philips Electronics N.V. , nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOTLIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FORA PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER ORCONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, ORPROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OFLIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDINGNEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THISSOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.