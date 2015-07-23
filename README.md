# Tasker ipack with OSX icons
This guide assumes that you are running OSX, have Ruby installed, have [Homebrew](http://brew.sh) installed, and have [Android Studio (and SDK) installed.](http://developer.android.com/tools/studio/index.html)

### Installing Dependencies
Run the following commands:
```
brew update
brew install imagemagick
brew install ant
```

### Pulling OS X Emoji
Download the following file: https://raw.githubusercontent.com/raincoats/emoji-extractor/master/emoji_extractor.rb

(Remember to maintain the .rb extension)

Navigate to the downloaded file in Terminal, and run `ruby emoji_extractor.rb`
You should now see a folder called `images` created, with all the emoji extracted.
Navigate to the images folder, and the resolution you want (eg 160x160) in Terminal.
The following command will rename every file inside the 160x160 directory (or whichever resolution folder you are in) from `<number>.png` to `i<number>.png`. This is done so that the build file can process the image names.
```
for f in *.png; do mv "$f" "i$f"; done
```

### Configuring your Build files
Open up ```ipacker.sh```

```
mypath=$0 <- Leave this
sdkdir= "SDK DIRECTORY" <- CHANGE THIS
version="1" <- Leave this
packagename= "com.example.ipack.OSX" <- CHANGE THIS
label="OSX" <- Leave this
allsamesize="true" <- Leave this
backcolour="00FFFFFF" <- Leave this
attribution="Apple" <- Leave this
icondir="/Users/user/images/20x20" <- Change this to the directory path of the 'images' folder we created above, with the proper resolution that you want (eg 160x160)
```

Next, open up ```local.properties```. In another Terminal window, navigate to your SDK folder. Then, enter the command `cd tools` to enter the tools directory. Run the command `./android list targets`. If you do not have any targets installed, you will need to launch the Android SDK Manager and install any build tools (eg 21.0.1).

Pick any build tools (I chose `android-21)`, and modify the first line of `local.properties` to reflect the proper build tools. Then, change the sdk.dir to the proper path. So, my `local.properties` would look like this:

```
target=android-21
sdk.dir=/Users/kedar/Documents/sdk
key.store.password=oooooo
key.alias.password=oooooo
key.store=/Users/Kedar/my-release-key.keystore
key.alias=myApp
```

### Generating Keystore
Next, navigate to a directory that is easily accessible (such as /Users/username/Downloads). **When prompted for a password, enter `oooooo`. Use the same password for the alias!**

**WARNING:** If you get the following error: `Signing key myApp not found`, re-execute the command below, except type out every character instead of copy and pasting.
```
keytool -genkey -v -keystore my-release-key.keystore
-alias myApp -keyalg RSA -keysize 2048 -validity 10000
```

You should now see a file called my-release-key.keystore. Open up `local.properties` and change `key.store` to the full path of the `my-release-key.keystore` file.

Finally, execute ./ipacker.sh. If everything goes well, you will see the generated APK.
Plug in your device, and execute ./adb install OSX
Once the APK has successfully installed, open up Tasker, and the new icons will be listed under "OSX"
