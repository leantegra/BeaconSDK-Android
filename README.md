# PowerMote SDK for Android #

The PowerMote SDK for Android is a library that allows interaction with [PowerMotes](http://leantegra.com/pm).
It works with Android 4.4 (API level 19) or above and requires Bluetooth Low Energy support.

Features:
- PowerMote management: connect\disconnect, read\write all info from nearby device;
- PowerMote ranging: scanning for nearby devices, filtering by specified properties, distance to device (IMMEDIATE, NEAR, FAR zones; distance in meters), distance tunning and calibration functionality;
- PowerMote monitoring: monitors Enter\Exit events for predefined regions that can be defined by multiple rules (UUID, distance, zone, Major, Minor etc.)
- Integration with Leantegra CMS (notifications, multi-rules, analytics);

## Installation

### Manual installation

1. Put [leantegra-android-sdk_1.0.1.aar](https://github.com/leantegra/AndroidPowerMoteSDK/blob/master/PowerMoteSDK/leantegra-android-sdk_1.0.1.aar) into project's `libs` directory; 
2. Add into `build.gradle`:

  ```groovy
  repositories {
      flatDir {
        dirs 'libs'
      }
  }
```
3. Add into `build.gradle` dependency to PowerMote SDK:

  ```groovy
  dependencies {
    compile(name:'leantegra-android-sdk_1.0.1', ext:'aar')
  }
```
4. All needed permissions (`BLUETOOTH`, `BLUETOOTH_ADMIN` and `INTERNET`) and services will be merged from SDK's `AndroidManifest.xml` to your application's `AndroidManifest.xml`;
5. Initialize PowerMote SDK without using Leantegra CMS:

  ```java
  LeantegraSDK.initialize(applicationContext);
  ```
6. Initialize PowerMote SDK with using Leantegra CMS:

  ```java
  LeantegraSDK.initialize(applicationContext);
  LeantegraCmsClient mLeantegraCmsClient = new LeantegraCmsClient(applicationContext);
  ```

## How to use

### Documentation

JavaDocs for PowerMote SDK you can find [here](http://leantegra.github.io/AndroidPowerMoteSDK/JavaDocs/).

### Demo Applications

Demo applications you can find [here](https://github.com/leantegra/AndroidPowerMoteSDK/tree/master/Demos).

### Changelog

The list of changes in PowerMote SDK for Android located in [CHANGELOG](https://github.com/leantegra/AndroidPowerMoteSDK/blob/master/PowerMoteSDK/CHANGELOG.md).

