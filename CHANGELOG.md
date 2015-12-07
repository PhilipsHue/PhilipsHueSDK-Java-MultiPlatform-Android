# Android/Java SDK Changelog

## 1.10.1 (2015-12-07)
Changes
  - Can now create a PHGroup with an empty constructor (more logical as when you create a group the group identifier is not known).
  - Minor bug fix,  when renaming lights removed /name from the URL (works on the bridge anyway, but not correct).

## 1.10 (2015-11-09)
Changes
  - Implemented Bridge API 1.9 and 1.10 features.  Bridge Backup (Factorynew + replacesbridgeid),  Uniqueid, Luminaireuniqueid and touchlink.

## 1.8.3beta (2015-09-25)
Changes
  - Minor fix to huelocalsdk.jar so the IP Scan only finds Hue Bridges.

## 1.8.2beta (2015-09-03)
Changes
  - Fix to huelocalsdk.jar so DestroySDK method works if Pushlinking has been called (i.e. All Pushlinking Threads are now correctly killed).
	
## 1.8.1beta (2015-07-23)
Changes
  - Removed ability to create your own whitelist username (as will be removed in the bridge in the future).</br></br>
    <b>Important!!</b>&nbsp;If you are using the QuickStart app as your base code and updating the Java SDK, please check out the small 
	but important changes (to setting the username) in PHHomeActivity.java , HueSharedPreferences.java and PHPushlinkActivity.java

## 1.8beta (2015-06-30)
Changes
  - Delete devices added (sensors and lights)
  - New Clip Increment Feature (new increment* attributes in PHLightState)
  - New  bridge fields added (e.g .ModelId,  Type in PHGroup, Manufacturername, UniqueId in PHLight, Bridge Id)
  - New light Models added for Color Conversions (e.g .HueGo).  
  - UPNP search now returns BridgeId
  - All bug fixes since previous release. 

## 1.3.1beta (2015-03-20)
Changes
  - Fix to kill heartbeat manager when exiting the SDK (after calling destroySDKMethod).  
  This prevents a NPE when an Android App is destroyed but the GC has not fully released all SDK resources.
  
## 1.3.1beta (2015-02-19)
Changes
  - Fix to kill hanging timer thread when exiting the SDK (after calling destroySDKMethod).

## 1.3beta (2014-09-12)
Features:

  - Java SDK now supports 1.3 bridges.
  - Added support for sensors
  - Added support for rules
  - Added multi resources heartbeat (ie. can run full heartbeat or select your resource, e.g. lights).
  - Added Feature Based parsing (meaning the SDK checks which bridge version is running and sends commands appropriately).

Changes:

  - API improvements
  - Bug fixes
  - Removed sample app as no longer supported.  Code examples are now on our developer portal.
  
Notes:  
  - The SDK is fully backwards compatible but updating your app to use the new SDK will result in a few compilation errors.  However, this are all minor and can be easily fixed.  For example PHSDKListener now implements
  onParsingErrors, onCacheUpdated signature has changed (from an int to a List)

## 1.1.2beta (2014-01-02)
Changes:

  - Simplified the code/app.  PHAccessPointListActivity/Adapter class merged into PHHomeActivity, PHHomeActiviy now displays available bridges so now only 1 main PHSDKListener object.
  - Added IPScan fallback code in case bridge search (upnp) fails.  
  - Removed unused layouts.  
  
## 1.1.1beta (2013-12-05)

Features:

  - The SDK is now PLATFORM INDEPENDENT!   The 2.jars (huelocalsdk.jar and sdkresources.jar) can now be used in any Java Desktop project as well as in Android projects.
    
Changes:

  - QuickStart and SampleApp now persist data in Shared Preferences. 
  - QuickStart and SampleApp now automatically connect to the last connected bridge (last connected IP Address is persisted).
  - Whitelist username is now a 16 character random string.  This is persisted in the Shared Preferences.
  - Refactored PHHomeActivity, PHPushlinkActivity and PHAccessPointListActivity due to Platform Independence changes.
  - Removed all Android specific code from the SDK. As a result backwards compatibility with 1.1 has been lost, however it is easy to fix.
  - Added IPScan functionality which can be used as a fallback if the UPnP/portal search fails to find any bridges.
  
## 1.1 (2013-09-22)

The Android SDK 1.1 is publicly released.

Features:

  - Android SDK released. Includes all functionality to control your hue lights aswell as automatic bridge detection.
  - QuickStart App released, containing minimal functionality to connect to a bridge and for getting started.  Ideal for devs to start programming their Hue Apps.
  - Sample App released, showing all the bridge functionality. e.g. Groups, Schedules, Configuration, Lights, Scenes.