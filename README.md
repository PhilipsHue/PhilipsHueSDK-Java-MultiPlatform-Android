The Hue SDK by Philips
===============
 (c) Copyright Philips 2012-2013
Introduction
----------------
The Android Hue SDK is a set of tools that are designed to make it easy to access the Hue system through the Hue Wi-Fi network connected bridge and control an associated set of connected lamps. The aim of the SDK is to enable you to create your own applications for the Hue system.
The tools are provided with documentation for the SDK and example code. They are designed to be flexible, whilst easing the use of the more complex components of the system.

The Hue SDK Components
-------------------------------------
###The SDK API
The SDK library is the main API of the Hue SDK. It contains all of the main tools to access the Hue system
###The Wizards
The Wizards are user interface components that are provided as source and access the Hue SDK to operate. They are provided to cover functionality such as configuration that is best given as source. Providing the source and graphics resources grants you the option to change the look and feel to match your own application's design.

###The Documentation
Documentation is provided in documents such as this, other media, and code comments. 

###The Sample Apps
2 Sample Apps are provided to show the usage of the Hue SDK.

* QuickStart Sample App - This is a skeleton/bare bones app with the minimal functionality needed to connect to a bridge (with authentication/pushlink) including some basic code to update lights.

* Example App - A more complete app showing a lot more Hue functionality such as timers, groups, find new lights and gui components for controlling Hue.

How to structure your app for the Hue SDK
---------------------------------------------------------
These are the key things you need to consider when building your app for the Hue SDK.
###The bridge controls the lights
The hardware bridge that you connect to your Wi-Fi network controls the connected lights. This means that all communication with the lights goes via the bridge
###The SDK connects to a bridge
To operate, the SDK must connect to a bridge
###Find a bridge
The SDK has functionality to find local bridges and the Example App contains a Wizard UI component for the user to select the bridge to use
###Push Linking
The button on the bridge itself must be pressed to connect to the SDK.  The SDK provides the code and Example App Wizard UI component for this.
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
    
Add Internet permission and any activities your app requires.
	
    <uses-permission android:name="android.permission.INTERNET" />

	<activity
       android:name="com.xxxx.xxx.MyApplicationActivity"
       android:label="@string/app_name" >
    </activity>

###PHHomeActivity.java


**Startup:**
**The PHHueSDK singleton instance is created.**

**We also start the searches for available bridges. The PHSDKListener notifies us of the connection status.**

    
	private phHueSDK;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    // Create the HueSDK singleton
	    phHueSDK = PHHueSDK.create(getApplicationContext());
		
		// Register the PHHueListener to receive callback notifications on Bridge events.
		phHueSDK.getNotificationManager().registerSDKListener(listener);

	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.find_new_bridge:
            // Display Search Progress animation.
            PHWizardAlertDialog.getInstance().showProgressDialog(R.string.search_progress, this);
            PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
            // Start the UPNP Searching of local bridges.
            sm.search(true, true);
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
		public void onBridgeConnected(PHBridge bridge) {
			phHueSDK.getLastHeartbeat().put(b.readResourceCache().getBridgeConfiguration().getIpAddress(),System.currentTimeMillis());
			
			// Set the selected Bridge
			phHueSDK.setSelectedBridge(bridge);
			// Start your own Activity and have fun with Philips Hue.
		    Intent intent = new Intent(getApplicationContext(),MyApplicationActivity.class);
			startActivity(intent);
		}

		@Override
		public void onAuthenticationRequired(PHAccessPoint accessPoint) {
			Log.w(TAG, "Authentication Required: ");			
			
			Activity act = phHueSDK.getCurrentActivty();
			PHWizardAlertDialog.getInstance().closeProgressDialog();
			
			// As username is not Authenticated we display the PushLink Image instructing user to push the Bridge Button.
			act.startActivity(new Intent(act,PHPushlinkActivity.class));
			phHueSDK.startPushlinkAuthentication(accessPoint);
			if(act instanceof PHAccessPointListActivity){
				act.finish(); // it  finishes bridge list 
			}		
		}

		@Override
		public void onAccessPointsFound(ArrayList<PHAccessPoint> accessPoints) {			
		
			Activity act = phHueSDK.getCurrentActivty();
			
			PHWizardAlertDialog.getInstance().closeProgressDialog();
			if(accessPoint!=null && accessPoint.size()>0){
				phHueSDK.getAccessPointsFound().addAll(accessPoint);

				// show list of bridges
				if(act instanceof PHAccessPointListActivity) {
					((PHAccessPointListActivity) act).refreshList();
				}else{
					act.startActivity(new Intent(act,PHAccessPointListActivity.class));
				}

			}else{
				//show error dialog
				PHWizardAlertDialog.showErrorDialog(act, phHueSDK.getApplicationContext().getString(R.string.could_not_find_bridge), R.string.btn_retry);
			}

		@Override
		public void onError(int code, String message) {
			Activity act = phHueSDK.getCurrentActivty();
			
			if(code==PHMessageType.BRIDGE_NOT_FOUND){
				PHWizardAlertDialog.getInstance().closeProgressDialog();
				PHWizardAlertDialog.showErrorDialog(act, message, R.string.btn_ok);
			} else if(code==PHMessageType.PUSHLINK_BUTTON_NOT_PRESSED){
				if(act!=null && (act instanceof PHPushlinkActivity)){
					((PHPushlinkActivity) act).incrementProgress();
				}
			} else if(code==PHMessageType.PUSHLINK_AUTHENTICATION_FAILED){
				if(act!=null && (act instanceof PHPushlinkActivity)){
					((PHPushlinkActivity) act).incrementProgress();
				}
				PHWizardAlertDialog.showAuthenticationErrorDialog(act, message,  R.string.btn_ok);

			} else if(code==PHHueError.BRIDGE_NOT_RESPONDING){
				PHWizardAlertDialog.getInstance().closeProgressDialog();
				PHWizardAlertDialog.showErrorDialog(act, message, R.string.btn_ok);

			} else if(code==PHHueError.NO_CONNECTION){ // connection lost to the bridge

			} else if(code ==PHHueError.AUTHENTICATION_FAILED){
				PHWizardAlertDialog.getInstance().closeProgressDialog();

				if(act!=null && act instanceof MainActivity){
					PHWizardAlertDialog.showErrorDialog(act, message, R.string.btn_ok);
					((MainActivity) act).refresh();					
				}else{
					goBackToHome();
				}
			} else{
				// For any other error
				PHWizardAlertDialog.getInstance().closeProgressDialog();
				Toast.makeText(phHueSDK.getApplicationContext(), message, Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onConnectionResumed(PHBridge bridge) {	
		}

		@Override
		public void onConnectionLost(PHAccessPoint accessPoints) {	
		}
		
	};

###PHAccessPointListActivity.java

**Once a Local Access Point is found, a list of available bridges is displayed.**

	private phHueSDK;
	private BridgeListAdapter adapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.txt_selectbridges);
        setContentView(R.layout.bridgelistlinear);

        ListView lampList = (ListView) findViewById(R.id.bridge_list);
        lampList.setOnItemClickListener(this);
        phHueSDK = PHHueSDK.getInstance(getApplicationContext());
        adapter = new BridgeListAdapter(this, phHueSDK.getAccessPointsFound());
        lampList.setAdapter(adapter);
    }
	
	private class BridgeListAdapter extends BaseAdapter{
		private LayoutInflater mInflater;
		ArrayList<PHAccessPoint> accessPoints;

        class BridgeListItem {
            private TextView bridgeIp;
            private TextView bridgeMac;
        }

		public BridgeListAdapter(Context context, ArrayList<PHAccessPoint> accessPoints) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
			this.accessPoints=accessPoints;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {

			BridgeListItem item;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.selectbridge_item, null);
				item = new BridgeListItem();
                item.bridgeMac = (TextView) convertView.findViewById(R.id.bridge_mac);
                item.bridgeIp = (TextView) convertView.findViewById(R.id.bridge_ip);

				convertView.setTag(item);
			} else {
				item = (BridgeListItem) convertView.getTag();
			}
			PHAccessPoint accessPoint=accessPoints.get(position);
            item.bridgeIp.setText(accessPoint.getIpAddress());
            item.bridgeMac.setText(accessPoint.getMacAddress());

			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public int getCount() {
			return accessPoints.size();
		}

		@Override
		public Object getItem(int position) {
			return accessPoints.get(position);
		}
		void updateData(ArrayList<PHAccessPoint> accessPoints){
			this.accessPoints=accessPoints;
			notifyDataSetChanged();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		PHAccessPoint accessPoint=(PHAccessPoint) adapter.getItem(position);
		phHueSDK.connect(accessPoint);
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

    }

**Once connected to a bridge your application activity is started and the fun begins.**	
	
###MyApplicationActivity
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