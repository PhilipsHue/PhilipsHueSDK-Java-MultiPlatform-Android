# Android/Java SDK Changelog



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