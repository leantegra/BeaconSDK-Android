# WiBeat SDK for Android #

The WiBeat SDK for Android is a library that allows interaction with [WiBeats](http://leantegra.com/wibeat-ble-beacon).
It works with Android 4.4 (API level 19) or above and requires Bluetooth Low Energy support.

Features:
- WiBeat management: connect\disconnect, read\write all info from nearby device;
- WiBeat ranging: scanning for nearby devices, filtering by specified properties, distance to device (IMMEDIATE, NEAR, FAR zones; distance in meters), distance tunning and calibration functionality;
- WiBeat monitoring: monitors Enter\Exit events for predefined regions that cyan be defined by multiple rules (UUID, distance, zone, Major, Minor etc.);
- Integration with Leantegra CMS (notifications, multi-rules, analytics, proximity analytics);
- Mobile RTLS: calculates user's phone position in location that is covered by WiBeats;
- Mobile Map: load and display location's map from CVO portal.

## Installation

### Manual installation

1. Put [leantegra-android-sdk_1.5.0.aar](https://github.com/leantegra/AndroidWiBeatSDK/blob/master/WiBeatSDK/leantegra-android-sdk_1.5.0.aar) into project's `libs` directory;
2. Add into `build.gradle`:

  ```groovy
  repositories {
      flatDir {
        dirs 'libs'
      }
  }
```
3. Add into `build.gradle` dependency to WiBeat SDK:

  ```groovy
  dependencies {
    compile(name:'leantegra-android-sdk_1.5.0', ext:'aar')
  }
```
4. All needed permissions (`BLUETOOTH`, `BLUETOOTH_ADMIN` and `INTERNET`) and services will be merged from SDK's `AndroidManifest.xml` to your application's `AndroidManifest.xml`;
5. Initialize WiBeat SDK without using Leantegra CMS:

  ```java
  LeantegraSDK.initialize(applicationContext);
  ```
6. Initialize WiBeat SDK with using Leantegra CMS:

  ```java
  LeantegraSDK.initialize(applicationContext);
  ApplicationCmsClient applicationCmsClient = CmsClientFactory.createApplicationCmsClient(applicationContext);
  ```

## How to use

### Documentation

JavaDocs for WiBeat SDK you can find [here](http://leantegra.github.io/AndroidWiBeatSDK/JavaDocs/).

### Demo Applications

Demo applications you can find [here](https://github.com/leantegra/AndroidWiBeatSDK/tree/master/Demos).

### Changelog

The list of changes in WiBeat SDK for Android located in [CHANGELOG](https://github.com/leantegra/AndroidWiBeatSDK/blob/master/CHANGELOG.md).

