
Hackathon - Quick Start Developers Guide
===============
<h2>Prerequisites</h2>

 <ul>
  <li>Android Develpment environment (IDE, AndroidSDK, Java SDK, USB Cables, Android Device)</li>
  <li>A Hue Kit (Bridge/Router/Lights)</li>
 </ul>

##Source Code
Either download the source as a .zip or use your favourite git client:-

https://github.com/PhilipsHue/PhilipsHueSDKAndroid

Here you have 2 Sample Apps (Android projects). Pick either to use as a starting point.
 <ul>
  <li>QuickStart App.  Contains a skeleton app with minimal functionality for connecting to a bridge and setting lights.</li>
  <li>Example App.  A more complete app containing schedules, groups, scenes.</li>
 </ul>
 Import either (as an Andriod project) into your favourite IDE.
 
##Deployment
For installing the ADB drivers for the Amazon Kindle it is recommended to follow the below 2 links.

https://developer.amazon.com/sdk/fire/setup.html  (Installing SDK Add-Ons)

https://developer.amazon.com/sdk/fire/connect-adb.html

##Connecting to/finding a Bridge
On your mobile device in your Wi-Fi settings connect to the router that corresponds to the label on your bridge (e.g.  For "hue dev 009" Wi-Fi name would be "hue dev 009 2.4GHz"). 

Password is: <b>philipshue</b>
 
##Classpath
 The <b>huelocalsdk.jar</b> and the <b>sdkresources.jar</b> should be placed in your project buildpath (these are located under the /libs folder in either of the sample apps).
 
##Coding Tips

To obtain an instance of the SDK:
   
    phHueSDK = PHHueSDK.getInstance(getApplicationContext());

   
To get bridge information (e.g. Lights, Config, Scenes, Groups) use the Bridge Resource Cache. For example:

    PHBridge bridge = phHueSDK.getSelectedBridge();
	PHBridgeResourcesCache cache = bridge.getResourceCache();
    ArrayList<PHLight> allLights = cache.getAllLights();


For updates (e.g. change light state) use the Bridge directly. For example:

    PHBridge bridge = phHueSDK.getSelectedBridge();
	bridge.updateLightState(light, lightState, listener);
	
To set lights to specific RGB Colours:

    float xy[] = PHUtilities.calculateXYFromRGB(255, 0, 255, light.getModelNumber());
    PHLightState lightState = new PHLightState();
    lightState.setX(xy[0]);
	lightState.setY(xy[1]);
     
For more information see:

https://github.com/PhilipsHue/PhilipsHueSDKAndroid/blob/master/README.md	 
	 
	 
Have fun & happy coding!

