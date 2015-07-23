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
