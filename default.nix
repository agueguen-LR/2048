#`nix-shell -A shell` to enter shell
#`nix-build -A emulate` to generate emulation script in ./result/bin/run-test-emulator
# apk must be built beforehand, with `./gradlew assembleDebug` for example
#`adb logcat --pid=$(adb shell pidof com.agueguen.clafout1s.game2048)` to enter logcat for this project
#`adb -s emulator-<port> install app/build/outputs/apk/debug/app-debug.apk` to reinstall app without relaunching emulator
# port is shown when launching the emulator
let
  pkgs = import <nixpkgs> {
    config = {
      allowUnfree = true;
      android_sdk.accept_license = true;
    };
  };

  androidComposition = pkgs.androidenv.composeAndroidPackages {
    platformVersions = ["36"];
    buildToolsVersions = ["35.0.0"];
    includeEmulator = true;
    includeSystemImages = true;
    systemImageTypes = ["google_apis"];
    abiVersions = ["x86_64"];
  };

  myEmulateApp = pkgs.androidenv.emulateApp.override {
    composeAndroidPackages = args: androidComposition;
  };
in {
  shell = pkgs.mkShell {
    buildInputs = [
      pkgs.zulu17 #jdk
      pkgs.gradle
      androidComposition.androidsdk
    ];

    shellHook = ''
      export ANDROID_HOME=${androidComposition.androidsdk}/libexec/android-sdk
      export ANDROID_SDK_ROOT=${androidComposition.androidsdk}/libexec/android-sdk
      export PATH=$ANDROID_HOME/platform-tools:$PATH
    '';
  };

  emulate = myEmulateApp {
    name = "emulate-app";
    systemImageType = "google_apis";
    platformVersion = "36";
    abiVersion = "x86_64";
    app = ./app/build/outputs/apk/debug/app-debug.apk;
    package = "com.agueguen.clafout1s.game2048";
    activity = "MainActivity";
  };
}
