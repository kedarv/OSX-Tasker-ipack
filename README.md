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
icondir=$8 <- Change this to the directory path of the 'images' folder we created above
keystore="" <- Leave this for now
keyname="" <- Leave this for now
```

Next, open up ```local.properties```. In another Terminal window, navigate to your SDK folder. Then, enter the command `cd tools` to enter the tools directory. Run the command `./android list targets`. If you do not have any targets installed, you will need to launch the Android SDK Manager and install any build tools (eg 21.0.1).

Pick any build tools (I chose `android-21)`, and modify the first line of `local.properties` to reflect the proper build tools. Then, change the sdk.dir to the proper path. So, my `local.properties` would look like this:

```
target=android-21
sdk.dir=/Users/kedar/Documents/sdk
key.store.password=oooooo
key.alias.password=oooooo
key.store=/Users/Kedar/key.keystore
key.alias=release_alias
```
