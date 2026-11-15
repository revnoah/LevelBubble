# Level Bubble

Level Bubble turns an Android phone into a simple spirit level for checking flat surfaces and upright alignment. A green circular vial and two tube vials respond to the phone’s tilt, with live angle readings to help you find level or plumb.

## Features

- **Automatic mode selection:** switches between surface and plumb readings as you move the phone from flat to upright.
- **Three bubble displays:** a circular level, a horizontal vial, and a vertical vial in a dark instrument-style case.
- **Live angle readings:** shows overall tilt and signed readings for individual axes, displayed to two decimal places.
- **Centering indicator:** approaches 100% as the phone approaches level or plumb.
- **Sensor fallback:** uses the gravity sensor when available, or smooths accelerometer readings to reduce jitter.
- **Screen stays awake:** keeps the display on while the app is in the foreground.
- **Offline operation:** no account, internet connection, or app permissions required.

## Getting started

### Prerequisites

- Android 7.0 (API 24) or newer.
- A gravity sensor or accelerometer. If neither is available, the app displays **Sensor unavailable**.

### Installation

Open the project in Android Studio with support for Android Gradle Plugin 8.13.2. Use JDK 17 to run Gradle and install Android SDK Platform 36 through the SDK Manager.

The included wrapper selects Gradle 8.13; Kotlin is pinned to 2.0.21. The app compiles Kotlin and Java to JVM 11 bytecode, independently of the JDK used to run Gradle. First sync downloads build tools and dependencies from the configured Google, Maven Central, and Gradle plugin repositories.

Let Gradle sync, select the **app** run configuration and a connected device, and click **Run**. Launch **Level Bubble** after a command-line installation. A physical phone lets you try the level with real motion sensor readings.

You can also build a debug APK from the project root:

```sh
./gradlew :app:assembleDebug
```

The APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

To build and install on a connected device with USB debugging enabled:

```sh
./gradlew :app:installDebug
```

On Windows, replace `./gradlew` with `gradlew.bat`. For command-line builds, configure the Android SDK path through `local.properties` (`sdk.dir`) or the `ANDROID_HOME` environment variable.

## Configuration

Android Studio can create the machine-local `local.properties` SDK setting. Keep this file out of version control; no API keys, account, server, or environment file is needed. Shared build settings live in `gradle.properties` and dependency versions in `gradle/libs.versions.toml`.

There is no settings screen, calibration setting, or measurement history. Readings remain in memory; the app does not save or upload measurements. Sensor updates stop when the activity is paused. Keeping the display awake while using the app consumes battery.

## Usage

### Check a flat surface

Place the phone flat on the surface and let the readings settle. The app automatically selects **SURFACE** mode. Adjust the surface until the bubble in the circular vial sits over the center crosshair and the main angle approaches **0°**.

The **X** and **Y** readings show tilt along the phone’s two surface axes. The **CENTER** percentage gives a visual indication of how close the phone is to level.

### Check upright alignment

Hold the phone upright with its long edge against the object you want to check. The app automatically selects **PLUMB** mode. Watch the vertical vial and adjust the alignment until the main angle approaches **0°**.

The **SIDE** reading shows sideways lean, while **FACE** shows forward or backward lean. The **CENTER** percentage approaches 100% as the phone becomes upright.

### Reading accuracy

Keep the phone still while taking a reading, especially when the app is using the accelerometer. A case or camera bump can affect how the phone rests on a surface. There is no manual calibration control; accuracy depends on the device’s sensors and contact with the surface. Two decimal places indicate display resolution, not guaranteed measurement accuracy.

## Development & testing

Run these commands from the project root after configuring the JDK and Android SDK:

```sh
./gradlew :app:testDebugUnitTest
./gradlew :app:connectedDebugAndroidTest
./gradlew :app:lintDebug
```

The instrumented test requires a connected Android device or running emulator. Existing tests only check sample arithmetic and the application package name; they do not verify tilt calculations, sensor fallback, mode switching, or drawing. Use a physical phone to check those behaviors. These commands are documented from the project configuration and have not been run as part of repository setup.

## How it works

[MainActivity.kt](app/src/main/java/com/revnoah/levelbubble/MainActivity.kt) selects the sensor, listens for motion while the app is active, and normalizes the gravity vector. When using the accelerometer, a low-pass filter smooths the readings. The direction of gravity determines whether the phone is mostly flat or upright.

[LevelView.kt](app/src/main/java/com/revnoah/levelbubble/LevelView.kt) draws the instrument directly with Android Canvas. It converts the sensor readings into bubble positions, tilt angles, and centering indicators. Sensor updates stop when the app is paused.

## License

The original application code is licensed under the [MIT License](LICENSE).
Copyright © 2026 [Noah Stewart](https://noahjstewart.com).

The Gradle wrapper scripts retain their existing Apache License 2.0 notices; third-party dependencies retain their own licenses.