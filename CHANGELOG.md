# Android/Java SDK Changelog

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